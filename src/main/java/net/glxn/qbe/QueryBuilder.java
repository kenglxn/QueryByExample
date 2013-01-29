package net.glxn.qbe;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.xml.bind.annotation.XmlElement;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.glxn.qbe.reflection.*;
import org.jboss.query.reflection.api.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.glxn.qbe.reflection.ReflectionUtil.hierarchy;

public class QueryBuilder<T, E> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final T example;
    private final Class<E> entityClass;
    private final EntityManager entityManager;
    private net.glxn.qbe.MatchType matchType;
    private JunctionType junctionType;
    private CriteriaBuilder cb;
    private CriteriaQuery<E> criteriaQuery;
    private Root<E> root;
    private final LinkedList<QBEOrder> qbeOrders = new LinkedList<QBEOrder>();

    QueryBuilder(T example, Class<E> entityClass, EntityManager entityManager, MatchType matchType, JunctionType junctionType) {
        this.example = example;
        this.entityClass = entityClass;
        this.entityManager = entityManager;
        this.matchType = matchType;
        this.junctionType = junctionType;
    }

    TypedQuery<E> build() {
        createCriteriaQueryAndRoot();
        HashMap<Field, Object> exampleFields = createFieldMapForExample();
        buildCriteria(exampleFields);
        return createQueryAndSetParameters(exampleFields);
    }

    private void buildCriteria(HashMap<Field, Object> exampleFields) {
        criteriaQuery.select(root);
        List<Predicate> criteria = buildCriteriaBasedOnFieldsAndMatchType(exampleFields, cb, root);
        if (criteria.size() == 0) {
            log.warn("query by example running with no criteria");
        } else if (criteria.size() == 1) {
            criteriaQuery.where(criteria.get(0));
        } else {
            addJunctionedCriteria(criteria);
        }
        addOrderByToCriteria();
    }

    private void addOrderByToCriteria() {
        ArrayList<Order> orders = new ArrayList<Order>(qbeOrders.size());
        for (QBEOrder qbeOrder : qbeOrders) {
            Order order;
            Path<Object> path = root.get(qbeOrder.getFieldToOrderBy());
            switch (qbeOrder.getOrderType()) {
                case ASC:
                    order = cb.asc(path);
                    break;
                case DESC:
                    order = cb.desc(path);
                    break;
                default:
                    throw new UnsupportedOperationException("no handling implemented for orderType" + qbeOrder.getOrderType());
            }
            orders.add(order);
        }
        if(orders.size() > 0) {
            criteriaQuery.orderBy(orders);
        }
    }

    private void addJunctionedCriteria(List<Predicate> criteria) {
        switch (junctionType) {
            case AND:
                criteriaQuery.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
                break;
            case OR:
                criteriaQuery.where(cb.or(criteria.toArray(new Predicate[criteria.size()])));
                break;
            default:
                throw new UnsupportedOperationException("no case for " + JunctionType.class.getSimpleName() + " " + junctionType + " in switch");
        }
    }

    private void createCriteriaQueryAndRoot() {
        cb = entityManager.getCriteriaBuilder();
        criteriaQuery = cb.createQuery(entityClass);
        root = criteriaQuery.from(entityClass);
    }

    private TypedQuery<E> createQueryAndSetParameters(HashMap<Field, Object> exampleFields) {
        String wildcardPrefix = "";
        String wildcardPostfix = "";

        TypedQuery<E> query = entityManager.createQuery(criteriaQuery);

        if (MatchType.MIDDLE == matchType || MatchType.END == matchType) {
            wildcardPrefix = "%";
        }
        if (MatchType.MIDDLE == matchType || MatchType.START == matchType) {
            wildcardPostfix = "%";
        }

        for (Field field : exampleFields.keySet()) {
            query.setParameter(field.getName(), wildcardPrefix + exampleFields.get(field) + wildcardPostfix);
        }
        return query;
    }

    private List<Predicate> buildCriteriaBasedOnFieldsAndMatchType(HashMap<Field, Object> exampleFields, CriteriaBuilder cb, Root<E> ent) {
        List<Predicate> criteria = new ArrayList<Predicate>();
        for (Field field : exampleFields.keySet()) {
            if (matchType == MatchType.EXACT) {
                criteria.add(cb.equal(ent.get(field.getName()), cb.parameter(field.getType(), field.getName())));
            } else {
                if (String.class.equals(field.getType())) {
                    criteria.add(cb.like(ent.<String>get(field.getName()), cb.parameter(String.class, field.getName())));
                } else {
                    throw new UnsupportedOperationException("can not do " + matchType + " matching on field " + field.getName() + " of type " + field.getType());
                }
            }
        }
        return criteria;
    }

    private HashMap<Field, Object> createFieldMapForExample() {
        HashMap<Field, Object> exampleFields = new HashMap<Field, Object>();

        HashMap<String, Field> entityFields = fieldMapForEntity(entityClass);
        Collection<Field> exampleFieldCollection = Query.forField().run(hierarchy(example.getClass()));
        for (Field field : exampleFieldCollection) {
            if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(example);
            } catch (IllegalAccessException e) {
                log.debug("FAILED TO ACCESS FIELD " + field.getName() + " ON CLASS " + example.getClass());
            }
            if (value != null && entityFields.containsKey(field.getName())) {
                exampleFields.put(field, value);
            }
        }
        return exampleFields;
    }

    private HashMap<String, Field> fieldMapForEntity(Class<E> entityClass) {
        HashMap<String, Field> entityFields = new HashMap<String, Field>();
        for (Field field : Query.forField().run(hierarchy(entityClass))) {
            entityFields.put(field.getName(), field);
        }
        return entityFields;
    }

    public void setJunctionType(JunctionType junctionType) {
        this.junctionType = junctionType;
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    public void order(String orderBy, OrderType orderType) {
        String nameOfFieldToOrderBy = findFieldNameToOrderByForXmlElementAnnotation(orderBy);

        if(nameOfFieldToOrderBy == null) {
            nameOfFieldToOrderBy = findFieldNameToOrderByForColumnAnnotation(orderBy);
        }

        if(nameOfFieldToOrderBy == null) {
            nameOfFieldToOrderBy = findFieldNameToOrderByForFieldNameOnClass(orderBy);
        }

        if(nameOfFieldToOrderBy == null) {
            String message = "" +
                "Unable to create order parameters for the supplied order by argument [" + orderBy + "] " +
                "You must use on of the following: " +
                "name property of the " + Column.class.getCanonicalName() + " annotation, " +
                "name property of the " + XmlElement.class.getCanonicalName() + " annotation, " +
                "or the name of the field on the class you are querying ";
            throw new OrderCreationException(message);
        }

        qbeOrders.add(new QBEOrder(nameOfFieldToOrderBy, orderType));
    }

    private String findFieldNameToOrderByForFieldNameOnClass(String orderBy) {
        String nameOfFieldToOrderBy = null;
        Field field = fieldMapForEntity(entityClass).get(orderBy);
        if(field != null) {
            nameOfFieldToOrderBy = field.getName();
        }
        return nameOfFieldToOrderBy;
    }

    private String findFieldNameToOrderByForXmlElementAnnotation(String fieldToOrderBy) {
        String nameOfFieldToOrderBy = null;
        Collection<Field> fields = Query.forField().withAnnotation(XmlElement.class).run(hierarchy(example.getClass()));
        for (Field field : fields) {
            XmlElement xe = field.getAnnotation(XmlElement.class);
            if (xe.name().equals(fieldToOrderBy)) {
                nameOfFieldToOrderBy = field.getName();
            }
        }
        return nameOfFieldToOrderBy;
    }

    private String findFieldNameToOrderByForColumnAnnotation(String fieldToOrderBy) {
        String nameOfFieldToOrderBy = null;
        Collection<Field> fields = Query.forField().withAnnotation(Column.class).run(hierarchy(entityClass));
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column.name().equals(fieldToOrderBy)) {
                nameOfFieldToOrderBy = field.getName();
            }
        }
        return nameOfFieldToOrderBy;
    }
}