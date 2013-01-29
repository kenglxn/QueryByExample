package net.glxn.qbe;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;


/**
 * This class is where the QBE magic happens.
 *
 * Once you have an instance of this class you can call the following:<br/>
 * * {@link QBEExample#list()}<br/>
 * * {@link QBEExample#item()}<br/>
 * * {@link QBEExample#getQuery()}<br/>
 *
 * @param <T> the example class type
 * @param <E> the entity class type
 */
public class QBEExample<T, E> {

    private final QueryBuilder<T, E> queryBuilder;

    QBEExample(T example, Class<E> entityClass, EntityManager entityManager) {
        this.queryBuilder = new QueryBuilder<T, E>(example, entityClass, entityManager, MatchType.EXACT, JunctionType.AND);
    }

    /**
     * overrides the {@link JunctionType} on this object instance then returns itself
     * @param junctionType the {@link JunctionType} semantics you wish to use
     * @return the QBEExample object instance
     */
    public QBEExample<T, E> usingJunctionType(JunctionType junctionType) {
        this.queryBuilder.setJunctionType(junctionType);
        return this;
    }

    /**
     * overrides the {@link MatchType} on this object instance then returns itself
     * @param matchType the {@link MatchType} semantics you wish to use
     * @return the QBEExample object instance
     */
    public QBEExample<T, E> usingMatchType(MatchType matchType) {
        this.queryBuilder.setMatchType(matchType);
        return this;
    }

    /**
     * Defines the order of the result set
     * @param orderBy the property to order by, can be one of the following:
     *                       {@link javax.persistence.Column} annotated property on entity,
     *                       {@link javax.xml.bind.annotation.XmlElement} annotated property on example,
     *                       or simply the field name on entity or example class
     * @param orderType signals the order to be ascending or descending
     * @return the QBEExample object instance
     * @throws OrderCreationException if the orderBy parameter can not be matched against any of the following paths as described in the parameter doc
     */
    public QBEExample<T, E> order(String orderBy, OrderType orderType) throws OrderCreationException {
        this.queryBuilder.order(orderBy, orderType);
        return this;
    }

    /**
     * get the {@link TypedQuery} that has been generating based on the example and entity.
     * @return the typed query
     */
    public TypedQuery<E> getQuery() {
        return queryBuilder.build();
    }

    /**
     * gets results by calling {@link javax.persistence.TypedQuery#getResultList()}
     * <em>NB: any exception thrown from the underlying TypedQuery call will bubble through</em>
     * @return list of results
     */
    public List<E> list() {
        return queryBuilder.build().getResultList();
    }

    /**
     * gets item using {@link javax.persistence.TypedQuery#getSingleResult()}
     * <br/><br/>
     * <em>NB: any exception thrown from the underlying TypedQuery call will bubble through</em>
     * @return the single item matching the query
     */
    public E item() {
        return queryBuilder.build().getSingleResult();
    }
}
