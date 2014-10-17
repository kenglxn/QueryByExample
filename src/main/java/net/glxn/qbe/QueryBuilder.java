package net.glxn.qbe;

import net.glxn.qbe.exception.*;
import net.glxn.qbe.types.*;
import org.slf4j.*;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.lang.reflect.*;
import java.util.*;

import static java.lang.String.*;
import static net.glxn.qbe.reflection.Reflection.*;

public class QueryBuilder<T, E> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final T example;
    private final Class<E> entityClass;
    private final EntityManager entityManager;
    private CriteriaBuilder cb;
    private CriteriaQuery<E> criteriaQuery;
    private Root<E> root;
    private final LinkedList<QBEOrder> ordering = new LinkedList<QBEOrder>();
    Matching matching;
    Junction junction;

    QueryBuilder(T example, Class<E> entityClass, EntityManager entityManager, Matching matching, Junction junction) {
        this.example = example;
        this.entityClass = entityClass;
        this.entityManager = entityManager;
        this.matching = matching;
        this.junction = junction;
    }

    TypedQuery<E> build() {
        createCriteriaQueryAndRoot();
        HashMap<Field, Object> exampleFields = createFieldMapForExample();
        buildCriteria(exampleFields);
        return createQueryAndSetParameters(exampleFields);
    }

    private void buildCriteria(HashMap<Field, Object> exampleFields) {
        criteriaQuery.select(root);
        List<Predicate> criteria = buildCriteriaForFieldsAndMatching(exampleFields);
        if (criteria.size() == 0) {
            log.warn("query by example running with no criteria");
        } else if (criteria.size() == 1) {
            criteriaQuery.where(criteria.get(0));
        } else {
            addJunctionCriteria(criteria);
        }
        addOrderByToCriteria();
    }

    private void addOrderByToCriteria() {
        ArrayList<javax.persistence.criteria.Order> orders = new ArrayList<javax.persistence.criteria.Order>(ordering.size());
        for (QBEOrder qbeOrder : ordering) {
            javax.persistence.criteria.Order order;
            Path<Object> path = root.get(qbeOrder.getOrderBy());
            switch (qbeOrder.getOrder()) {
                case ASCENDING:
                    order = cb.asc(path);
                    break;
                case DESCENDING:
                    order = cb.desc(path);
                    break;
                default:
                    throw new UnsupportedOperationException("no handling implemented for orderType" + qbeOrder.getOrder());
            }
            orders.add(order);
        }
        if (orders.size() > 0) {
            criteriaQuery.orderBy(orders);
        }
    }

    private void addJunctionCriteria(List<Predicate> criteria) {
        switch (junction) {
            case UNION:
                criteriaQuery.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
                break;
            case INTERSECTION:
                criteriaQuery.where(cb.or(criteria.toArray(new Predicate[criteria.size()])));
                break;
            default:
                String message = format("no case for %s %s in switch", Junction.class.getSimpleName(), junction);
                throw new UnsupportedOperationException(message);
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

        if (Matching.MIDDLE == matching || Matching.END == matching) {
            wildcardPrefix = "%";
        }
        if (Matching.MIDDLE == matching || Matching.START == matching) {
            wildcardPostfix = "%";
        }

        for (Field field : exampleFields.keySet()) {
            
            Class<?> fieldType = field.getType();
            Object value;

            log.trace("Setting parameter for field [{}] with type [{}]", field, fieldType);

            if (String.class.equals(fieldType)) {
                log.trace("Field [{}] type is identified as a string", field);
                value = wildcardPrefix + exampleFields.get(field) + wildcardPostfix;
            } else {
                log.trace("Field [{}] type is not identified as a string", field);
                value = exampleFields.get(field);
            }

            query.setParameter(field.getName(), value);
        }
        return query;
    }

    private List<Predicate> buildCriteriaForFieldsAndMatching(HashMap<Field, Object> fields) {
        List<Predicate> criteria = new ArrayList<Predicate>();
        for (Field field : fields.keySet()) {
            if (matching == Matching.EXACT) {
                criteria.add(cb.equal(root.get(field.getName()), cb.parameter(field.getType(), field.getName())));
            } else {
                if (String.class.equals(field.getType())) {
                    criteria.add(cb.like(root.<String>get(field.getName()), cb.parameter(String.class, field.getName())));
                } else {
                    String format = "can not do %s matching on field %s of type %s";
                    throw new UnsupportedOperationException(format(format, matching, field.getName(), field.getType()));
                }
            }
        }
        return criteria;
    }

    private HashMap<Field, Object> createFieldMapForExample() {
        HashMap<Field, Object> exampleFields = new HashMap<Field, Object>();

        HashMap<String, Field> entityFields = fieldMapForEntity(entityClass);
        Collection<Field> exampleFieldCollection = fields(hierarchy(example.getClass()));
        for (Field field : exampleFieldCollection) {
            if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(example);
            } catch (IllegalAccessException e) {
                Object[] args = {field.getName(), example.getClass(), e};
                log.debug("FAILED TO ACCESS FIELD [%s] ON CLASS [%s]. Cause: %s", args);
            }
            if (value != null && entityFields.containsKey(field.getName())) {
                exampleFields.put(field, value);
            }
        }
        return exampleFields;
    }

    private HashMap<String, Field> fieldMapForEntity(Class<E> entityClass) {
        HashMap<String, Field> entityFields = new HashMap<String, Field>();
        for (Field field : fields(hierarchy(entityClass))) {
            entityFields.put(field.getName(), field);
        }
        return entityFields;
    }

    public void orderBy(String orderBy, net.glxn.qbe.types.Order order) {
        String nameOfFieldToOrderBy = findFieldNameToOrderByForColumnAnnotation(orderBy);

        if (nameOfFieldToOrderBy == null) {
            nameOfFieldToOrderBy = findFieldNameToOrderByForFieldNameOnClass(orderBy);
        }

        if (nameOfFieldToOrderBy == null) {
            String message = "" +
                    "Unable to create order parameters for the supplied order by argument [" + orderBy + "] " +
                    "You must use on of the following: " +
                    "name property of the " + Column.class.getCanonicalName() + " annotation " +
                    "or the name of the field on the class you are querying ";
            throw new OrderCreationException(message);
        }

        ordering.add(new QBEOrder(nameOfFieldToOrderBy, order));
    }

    private String findFieldNameToOrderByForFieldNameOnClass(String orderBy) {
        String nameOfFieldToOrderBy = null;
        Field field = fieldMapForEntity(entityClass).get(orderBy);
        if (field != null) {
            nameOfFieldToOrderBy = field.getName();
        }
        return nameOfFieldToOrderBy;
    }

    private String findFieldNameToOrderByForColumnAnnotation(String fieldToOrderBy) {
        String nameOfFieldToOrderBy = null;
        Collection<Field> fields = fieldsWithAnnotation(Column.class, hierarchy(entityClass));
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column.name().equals(fieldToOrderBy)) {
                nameOfFieldToOrderBy = field.getName();
            }
        }
        return nameOfFieldToOrderBy;
    }
}