package net.glxn.qbe;

import javax.persistence.EntityManager;

/**
 * This class has one method: {@link QBEQuery#example(Object)}
 * @param <E>
 */
public class QBEQuery<E> {
    private Class<E> entityClass;
    private EntityManager entityManager;

    protected QBEQuery(Class<E> entityClass, EntityManager entityManager) {
        this.entityClass = entityClass;
        this.entityManager = entityManager;
    }

    /**
     * set the example object that will be used when creating the query.
     * The example object must have fields matching the same names as the fields you want to match on the entity class.
     * The query criteria will use exact matching({@link MatchType#EXACT}) as well as conjunction({@link JunctionType#AND}) semantics,
     * if you wish to use other junction or matching semantics see: {@link QBEExample#usingJunctionType(JunctionType)} and {@link QBEExample#usingMatchType(MatchType)}
     * @param example the object containing the fields to query on
     * @param <T> the type of the exampe object
     * @return a new {@link QBEExample} with the given example object
     */
    public <T> QBEExample<T, E> example(T example) {
        return new QBEExample<T, E>(example, entityClass, entityManager);
    }
}
