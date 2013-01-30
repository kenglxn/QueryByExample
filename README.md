QueryByExample: Flexible JPA2 entity querying using example objects.


There is a comprehensive javadoc here: http://kenglxn.github.com/QueryByExample

Also have a look at the test class for examples: https://github.com/kenglxn/QueryByExample/blob/master/src/test/java/net/glxn/qbe/QueryByExampleTest.java

Dependencies:
* org.jboss.query.query-impl-reflection aka. [Query API](https://github.com/aslakknutsen/Query) by [@aslakknutsen](https://github.com/aslakknutsen)
* slf4j-api
* hibernate-entitymanager

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
        .example(new Pojo("foo"))
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

