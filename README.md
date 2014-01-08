### QBE: Flexible JPA entity querying using example objects.

#### How does it work

QBE inspects the supplied example object and builds JPA Criteria based on the example objects field contents. 


#### Dependencies:

hibernate-entitymanager

#### Get it:

To get started using QBE, just clone and build the project:

    git clone git://github.com/kenglxn/QueryByExample.git
    cd QueryByExample/
    mvn clean install

and then add QBE as a dependency in your project

    <dependency>
        <groupId>net.glxn</groupId>
        <artifactId>qbe</artifactId>
        <version>1.2</version>
    </dependency>

If you don't want to clone and build yourself, simply grab the jars from here: https://github.com/kenglxn/QueryByExample/tree/master/dist

Then just run maven to install them into your local repo:

    mvn install:install-file -Dfile=qbe-1.2.jar -DgroupId=net.glxn -DartifactId=qbe -Dversion=1.2 -Dpackaging=jar -DgeneratePom=true

#### Usage:

```java
// get a list of entities using an arbitrary pojo as example input
List<Entity> resultList = 
    QBE.using(entityManager)
        .query(Entity.class)
        .by(new Pojo("foo"))
        .list();

// get a single result using an arbitrary pojo as example input
Entity item =
    QBE.using(entityManager)
        .query(Entity.class)
        .by(new Pojo("foo"))
        .item();

// get and work with the underlying TypedQuery object (useful e.g. for paging)
TypedQuery<Entity> query =
    QBE.using(entityManager)
        .query(Entity.class)
        .by(new Pojo("foo"))
        .getQuery();
List<Entity> resultList =
    query
        .setFirstResult(0)
        .setMaxResults(10)
        .getResultList();

// override the Matching logic (defaults to EXACT)
QBE.using(entityManager)
    .query(Entity.class)
    .by(new Pojo("foo"))
    .use(Matching.EXACT);
QBE.using(entityManager)
    .query(Entity.class)
    .by(new Pojo("foo"))
    .use(Matching.START);
QBE.using(entityManager)
    .query(Entity.class)
    .by(new Pojo("foo"))
    .use(Matching.MIDDLE);
QBE.using(entityManager)
    .query(Entity.class)]
    .by(new Pojo("foo"))
    .use(Matching.END);

// override the Junction for multiple fields (defaults to UNION)
List<Entity> resultList =
    QBE.using(entityManager)
        .query(Entity.class)
        .by(example)
        .use(Junction.INTERSECTION)
        .list();

// define the ordering of the result
List<Entity> resultList =
    QBE.using(entityManager)
        .query(Entity.class)
        .by(new Pojo("foo"))
        .use(Matching.START)
        .orderBy(fieldToOrderBy, Order.ASCENDING)
        .list();

// define multiple fields for ordering semantics
List<Entity> resultList =
    QBE.using(entityManager)
        .query(Entity.class)
        .by(new Pojo("foo"))
        .use(Matching.START)
        .orderBy("firstName", Order.DESCENDING)
        .orderBy("lastName", Order.ASCENDING)
        .list();
```

#### API Documentation

Javadocs are located here: http://kenglxn.github.io/QueryByExample/apidocs/index.html

#### License:

http://www.apache.org/licenses/LICENSE-2.0.html
