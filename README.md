QueryByExample

A flexible query by example library for using JPA2
QueryByExample enables querying an entity by passing in an example object, of any type.
The only requirement is that the field names match. It will pull any non-null field from the example and match a given entity class with the same fields.

Examples:

```java
// get a list of entities using an arbitrary pojo as example input
List<Entity> resultList = 
    new QueryByExample(entityManager)
        .query(Entity.class)
        .example(new Pojo("foo"))
        .list();

// get a single result using an arbitrary pojo as example input
Entity item =
    new QueryByExample(entityManager)
        .query(Entity.class)
        .example(new Pojo("foo))
        .item();

// get and work with the underlying TypedQuery object (useful e.g. for paging)
TypedQuery<Entity> query =
    new QueryByExample(entityManager)
        .query(Entity.class)
        .example(new Pojo("foo"))
        .getQuery();
List<Entity> resultList =
    query
        .setFirstResult(0)
        .setMaxResults(10)
        .getResultList();

// override the MatchType logic (defaults to exact)
new QueryByExample(entityManager)
    .query(Entity.class)
    .example(new Pojo("foo"))
    .usingMatchType(MatchType.EXACT);
new QueryByExample(entityManager)
    .query(Entity.class)
    .example(new Pojo("foo"))
    .usingMatchType(MatchType.START);
new QueryByExample(entityManager)
    .query(Entity.class)
    .example(new Pojo("foo"))
    .usingMatchType(MatchType.MIDDLE);
new QueryByExample(entityManager)
    .query(Entity.class)]
    .example(new Pojo("foo"))
    .usingMatchType(MatchType.END);

// override the JunctionType for multiple fields (defaults to AND)
List<Entity> resultList =
    new QueryByExample(entityManager)
        .query(Entity.class)
        .example(example)
        .usingJunctionType(JunctionType.OR)
        .list();

// define the ordering of the result
List<Entity> resultList =
    new QueryByExample(entityManager)
        .query(Entity.class)
        .example(new Pojo("foo"))
        .usingMatchType(MatchType.START)
        .order(fieldToOrderBy, OrderType.ASC)
        .list();

// define multiple fields for ordering semantics
List<Entity> resultList =
    new QueryByExample(entityManager)
        .query(Entity.class)
        .example(new Pojo("foo"))
        .usingMatchType(MatchType.START)
        .order("firstName", OrderType.DESC)
        .order("lastName", OrderType.ASC)
        .list();
```

