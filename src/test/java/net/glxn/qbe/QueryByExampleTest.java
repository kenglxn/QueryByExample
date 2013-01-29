package net.glxn.qbe;

import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.glxn.qbe.model.Gender;
import net.glxn.qbe.model.User;
import net.glxn.qbe.model.UserEntity;
import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-query-test.xml")
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = true)
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class QueryByExampleTest {

    @PersistenceContext
    protected EntityManager entityManager;
    private final int NUMBER_OF_RANDOM_USERS = 10;

    @Before
    public void setUp() throws Exception {
        createRandomUsers(NUMBER_OF_RANDOM_USERS);
    }

    @SuppressWarnings({"JpaQlInspection"})
    @After
    public void tearDown() throws Exception {
        entityManager.createQuery("delete from UserEntity").executeUpdate();
    }

    @Test
    public void shouldBeAbleToListOnQueryUserByDisplayName() throws Exception {
        String expectedDisplayName = "displayname";
        entityManager.persist(new UserEntity("uname", expectedDisplayName, "fname", "lname"));
        List<UserEntity> resultList = new QueryByExample(entityManager).query(UserEntity.class).example(new User(expectedDisplayName)).list();
        Assert.assertNotNull(resultList);
        Assert.assertFalse(resultList.isEmpty());
        Assert.assertEquals(1, resultList.size());
        Assert.assertEquals(expectedDisplayName, resultList.get(0).getDisplayName());
    }


    @Test
    public void shouldBeAbleToItemOnQueryUserByDisplayName() throws Exception {
        String expectedDisplayName = "displayname";
        entityManager.persist(new UserEntity("uname", expectedDisplayName, "fname", "lname"));

        UserEntity item = new QueryByExample(entityManager).query(UserEntity.class).example(new User(expectedDisplayName)).item();
        Assert.assertNotNull(item);
        Assert.assertEquals(expectedDisplayName, item.getDisplayName());
    }

    @Test
    public void shouldBeAbleToGetAndWorkWithQueryOnQueryUserByDisplayName() throws Exception {
        String expectedDisplayName = "displayname";
        entityManager.persist(new UserEntity("uname", expectedDisplayName, "fname", "lname"));

        TypedQuery<UserEntity> query = new QueryByExample(entityManager).query(UserEntity.class).example(new User(expectedDisplayName)).getQuery();
        Assert.assertNotNull(query);

        List<UserEntity> resultList = query.setFirstResult(0).setMaxResults(10).getResultList();
        Assert.assertNotNull(resultList);
        Assert.assertFalse(resultList.isEmpty());
        Assert.assertEquals(1, resultList.size());
        Assert.assertEquals(expectedDisplayName, resultList.get(0).getDisplayName());
    }

    @Test
    public void shouldMatchAllForEmptyExample() throws Exception {
        List<UserEntity> resultList = new QueryByExample(entityManager).query(UserEntity.class).example(new User()).list();
        Assert.assertNotNull(resultList);
        Assert.assertFalse(resultList.isEmpty());
        Assert.assertEquals(NUMBER_OF_RANDOM_USERS, resultList.size());
    }

    @Test
    public void shouldBeAbleToPageTheQuery() throws Exception {
        TypedQuery<UserEntity> query = new QueryByExample(entityManager).query(UserEntity.class).example(new User()).getQuery();
        query.setFirstResult(5);
        query.setMaxResults(2);

        List<UserEntity> resultList = query.getResultList();

        Assert.assertNotNull(resultList);
        Assert.assertFalse(resultList.isEmpty());
        Assert.assertEquals(2, resultList.size());
    }

    @Test
    public void shouldMatchExactDisplayName() throws Exception {
        String expectedDisplayName = "displayname";
        entityManager.persist(new UserEntity("uname", "shouldNotMatchOnThisBeginning" + expectedDisplayName, "fname", "lname"));
        entityManager.persist(new UserEntity("uname", expectedDisplayName, "fname", "lname"));
        entityManager.persist(new UserEntity("uname", expectedDisplayName + "shouldNotMatchOnThisEnding", "fname", "lname"));
        entityManager.persist(new UserEntity("uname", "shouldNotMatchOnThisBeginning" + expectedDisplayName + "shouldNotMatchOnThisEnding", "fname", "lname"));

        List<UserEntity> resultList = new QueryByExample(entityManager).query(UserEntity.class).example(new User(expectedDisplayName)).list();
        Assert.assertNotNull(resultList);
        Assert.assertFalse(resultList.isEmpty());
        Assert.assertEquals(1, resultList.size());
        Assert.assertEquals(expectedDisplayName, resultList.get(0).getDisplayName());
    }

    @Test
    public void shouldMatchExactDisplayNameWhenExplicitlyPassingMatchTypeExact() throws Exception {
        String expectedDisplayName = "displayname";
        entityManager.persist(new UserEntity("uname", "PRE_" + expectedDisplayName, "fname", "lname"));
        entityManager.persist(new UserEntity("uname", expectedDisplayName, "fname", "lname"));
        entityManager.persist(new UserEntity("uname", expectedDisplayName + "_POST", "fname", "lname"));
        entityManager.persist(new UserEntity("uname", "PRE_" + expectedDisplayName + "_POST", "fname", "lname"));

        List<UserEntity> resultList = new QueryByExample(entityManager).query(UserEntity.class).example(new User(expectedDisplayName)).usingMatchType(MatchType.EXACT).list();
        Assert.assertNotNull(resultList);
        Assert.assertFalse(resultList.isEmpty());
        Assert.assertEquals(1, resultList.size());
        Assert.assertEquals(expectedDisplayName, resultList.get(0).getDisplayName());
    }

    @Test
    public void shouldMatchBeginnigDisplayName() throws Exception {
        String expectedDisplayName = "displayname";
        entityManager.persist(new UserEntity("uname", "PRE_" + expectedDisplayName, "fname", "lname"));
        entityManager.persist(new UserEntity("uname", expectedDisplayName, "fname", "lname"));
        entityManager.persist(new UserEntity("uname", expectedDisplayName + "_POST", "fname", "lname"));
        entityManager.persist(new UserEntity("uname", "PRE_" + expectedDisplayName + "_POST", "fname", "lname"));

        List<UserEntity> resultList = new QueryByExample(entityManager).query(UserEntity.class).example(new User(expectedDisplayName)).usingMatchType(MatchType.START).list();
        Assert.assertNotNull(resultList);
        Assert.assertFalse(resultList.isEmpty());
        Assert.assertEquals(2, resultList.size());
        for (UserEntity userEntity : resultList) {
            Assert.assertTrue(userEntity.getDisplayName().startsWith(expectedDisplayName));
        }
    }

    @Test
    public void shouldMatchEndDisplayName() throws Exception {
        String expectedDisplayName = "displayname";
        entityManager.persist(new UserEntity("uname", "PRE_" + expectedDisplayName, "fname", "lname"));
        entityManager.persist(new UserEntity("uname", expectedDisplayName, "fname", "lname"));
        entityManager.persist(new UserEntity("uname", expectedDisplayName + "_POST", "fname", "lname"));
        entityManager.persist(new UserEntity("uname", "PRE_" + expectedDisplayName + "_POST", "fname", "lname"));

        List<UserEntity> resultList = new QueryByExample(entityManager).query(UserEntity.class).example(new User(expectedDisplayName)).usingMatchType(MatchType.END).list();
        Assert.assertNotNull(resultList);
        Assert.assertFalse(resultList.isEmpty());
        Assert.assertEquals(2, resultList.size());
        for (UserEntity userEntity : resultList) {
            Assert.assertTrue(userEntity.getDisplayName().endsWith(expectedDisplayName));
        }
    }

    @Test
    public void shouldMatchMiddleDisplayName() throws Exception {
        String expectedDisplayName = "displayname";
        entityManager.persist(new UserEntity("uname", "PRE_" + expectedDisplayName, "fname", "lname"));
        entityManager.persist(new UserEntity("uname", expectedDisplayName, "fname", "lname"));
        entityManager.persist(new UserEntity("uname", expectedDisplayName + "_POST", "fname", "lname"));
        entityManager.persist(new UserEntity("uname", "PRE_" + expectedDisplayName + "_POST", "fname", "lname"));

        List<UserEntity> resultList = new QueryByExample(entityManager).query(UserEntity.class).example(new User(expectedDisplayName)).usingMatchType(MatchType.MIDDLE).list();
        Assert.assertNotNull(resultList);
        Assert.assertFalse(resultList.isEmpty());
        Assert.assertEquals(4, resultList.size());
        for (UserEntity userEntity : resultList) {
            Assert.assertTrue(userEntity.getDisplayName().contains(expectedDisplayName));
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void likeMatchOnFieldWithNonStringTypeShouldThrowException() throws Exception {
        User example = new User();
        example.setGender(Gender.FEMALE);

        new QueryByExample(entityManager).query(UserEntity.class).example(example).usingMatchType(MatchType.MIDDLE).getQuery();
    }

    @Test
    public void shouldMatchExactDisplayNameAndUserName() throws Exception {
        String expectedDisplayName = "SOMEDISPLAYNAME";
        String expectedUserName = "SOMEUSERNAME";

        entityManager.persist(new UserEntity(expectedUserName, expectedDisplayName, "fname", "lname"));

        List<UserEntity> resultList = new QueryByExample(entityManager).query(UserEntity.class).example(new User(expectedUserName, expectedDisplayName)).list();
        Assert.assertNotNull(resultList);
        Assert.assertFalse(resultList.isEmpty());
        Assert.assertEquals(1, resultList.size());
        Assert.assertEquals(expectedUserName, resultList.get(0).getUserName());
        Assert.assertEquals(expectedDisplayName, resultList.get(0).getDisplayName());
    }

    @Test
    public void shouldMatchExactDisplayNameOrUserName() throws Exception {
        String expectedDisplayName = "SOMEDISPLAYNAME";
        String expectedUserName = "SOMEUSERNAME";
        User example = new User(expectedUserName, expectedDisplayName);

        entityManager.persist(new UserEntity("uname", expectedDisplayName, "fname", "lname"));
        entityManager.persist(new UserEntity(expectedUserName, "displayName", "fname", "lname"));

        List<UserEntity> resultList =
            new QueryByExample(entityManager)
                .query(UserEntity.class)
                .example(example)
                .usingJunctionType(JunctionType.OR)
                .list();

        Assert.assertNotNull(resultList);
        Assert.assertFalse(resultList.isEmpty());
        Assert.assertEquals(2, resultList.size());
        for (UserEntity entity : resultList) {
            Assert.assertTrue(expectedUserName.equals(entity.getUserName()) | expectedDisplayName.equals(entity.getDisplayName()));
        }
    }

    @Test
    public void shouldGiveOrderedResultSetUsingAnnotatedPropertyNameOfExampleClass() throws Exception {
        createDataForOrderedTests();

        String fieldToOrderBy = User.DISPLAY_NAME;
        List<UserEntity> resultList =
            new QueryByExample(entityManager)
                .query(UserEntity.class)
                .example(new User("dispname"))
                .usingMatchType(MatchType.START)
                .order(fieldToOrderBy, OrderType.ASC)
                .list();
        assertOrder(resultList, OrderType.ASC);

        resultList =
            new QueryByExample(entityManager)
                .query(UserEntity.class)
                .example(new User("dispname"))
                .usingMatchType(MatchType.START)
                .order(fieldToOrderBy, OrderType.DESC)
                .list();
        assertOrder(resultList, OrderType.DESC);
    }

    @Test
    public void shouldGiveOrderedResultSetUsingAnnotatedPropertyNameOfEntityClass() throws Exception {
        createDataForOrderedTests();

        String fieldToOrderBy = UserEntity.DISPLAY_NAME;
        List<UserEntity> resultList =
            new QueryByExample(entityManager)
                .query(UserEntity.class)
                .example(new User("dispname"))
                .usingMatchType(MatchType.START)
                .order(fieldToOrderBy, OrderType.ASC)
                .list();
        assertOrder(resultList, OrderType.ASC);

        resultList =
            new QueryByExample(entityManager)
                .query(UserEntity.class)
                .example(new User("dispname"))
                .usingMatchType(MatchType.START)
                .order(fieldToOrderBy, OrderType.DESC)
                .list();
        assertOrder(resultList, OrderType.DESC);
    }

    @Test
    public void shouldGiveOrderedResultSetUsingFieldName() throws Exception {
        createDataForOrderedTests();

        String fieldToOrderBy = "displayName";
        List<UserEntity> resultList =
            new QueryByExample(entityManager)
                .query(UserEntity.class)
                .example(new User("dispname"))
                .usingMatchType(MatchType.START)
                .order(fieldToOrderBy, OrderType.ASC)
                .list();
        assertOrder(resultList, OrderType.ASC);

        resultList =
            new QueryByExample(entityManager)
                .query(UserEntity.class)
                .example(new User("dispname"))
                .usingMatchType(MatchType.START)
                .order(fieldToOrderBy, OrderType.DESC)
                .list();
        assertOrder(resultList, OrderType.DESC);
    }

    @Test
    public void shouldGiveOrderedResultSetForMultipleOrders() throws Exception {
        entityManager.persist(new UserEntity("uname1", "dispname1", "fname", "lname1"));
        entityManager.persist(new UserEntity("uname2", "dispname2", "fname", "lname2"));
        entityManager.persist(new UserEntity("uname3", "dispname", "fname3", "lname3"));
        entityManager.persist(new UserEntity("uname4", "dispname", "fname4", "lname4"));
        entityManager.persist(new UserEntity("uname5", "dispname", "fname5", "lname5"));

        List<UserEntity> resultList =
            new QueryByExample(entityManager)
                .query(UserEntity.class)
                .example(new User("dispname"))
                .usingMatchType(MatchType.START)
                .order("firstName", OrderType.DESC)
                .order("displayName", OrderType.ASC)
                .list();
        Assert.assertEquals(5, resultList.size());
        Assert.assertEquals("dispname", resultList.get(0).getDisplayName());
        Assert.assertEquals("fname5", resultList.get(0).getFirstName());
        Assert.assertEquals("dispname", resultList.get(1).getDisplayName());
        Assert.assertEquals("fname4", resultList.get(1).getFirstName());
        Assert.assertEquals("dispname", resultList.get(2).getDisplayName());
        Assert.assertEquals("fname3", resultList.get(2).getFirstName());
        Assert.assertEquals("fname", resultList.get(3).getFirstName());
        Assert.assertEquals("dispname1", resultList.get(3).getDisplayName());
        Assert.assertEquals("fname", resultList.get(4).getFirstName());
        Assert.assertEquals("dispname2", resultList.get(4).getDisplayName());
    }

    @Test(expected = OrderCreationException.class)
    public void shouldThrowExceptionForUnknownOrderByArgument() throws Exception {
        new QueryByExample(entityManager)
            .query(UserEntity.class)
            .example(new User("dispname"))
            .usingMatchType(MatchType.START)
            .order("someBogusFieldName", OrderType.ASC)
            .list();
    }

    private void createDataForOrderedTests() {
        entityManager.persist(new UserEntity("uname1", "dispname1", "fname1", "lname1"));
        entityManager.persist(new UserEntity("uname2", "dispname2", "fname2", "lname2"));
        entityManager.persist(new UserEntity("uname3", "dispname3", "fname3", "lname3"));
    }

    private void assertOrder(List<UserEntity> resultList, OrderType orderType) {
        Assert.assertEquals(3, resultList.size());
        switch (orderType) {
            case ASC:
                Assert.assertEquals("dispname1", resultList.get(0).getDisplayName());
                Assert.assertEquals("dispname2", resultList.get(1).getDisplayName());
                Assert.assertEquals("dispname3", resultList.get(2).getDisplayName());
                break;
            case DESC:
                Assert.assertEquals("dispname3", resultList.get(0).getDisplayName());
                Assert.assertEquals("dispname2", resultList.get(1).getDisplayName());
                Assert.assertEquals("dispname1", resultList.get(2).getDisplayName());
                break;
            default:
                throw new RuntimeException("MUST BE ASC OR DESC FOR ASSERT");
        }
    }

    private void createRandomUsers(int numberOfUsers) {
        for (int i = 0; i < numberOfUsers; i++) {
            String randomVal = UUID.randomUUID().toString();
            entityManager.persist(new UserEntity(randomVal, randomVal, randomVal, randomVal));
        }
    }
}
