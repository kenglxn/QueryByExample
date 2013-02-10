package net.glxn.qbe;

import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import net.glxn.qbe.exception.*;
import net.glxn.qbe.model.*;
import net.glxn.qbe.types.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.glxn.qbe.model.PojoUser;
import junit.framework.Assert;

import static junit.framework.Assert.assertEquals;
import static net.glxn.qbe.model.Gender.FEMALE;
import static net.glxn.qbe.model.Gender.MALE;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-query-test.xml")
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = true)
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class QBETest {

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
        entityManager.createQuery("delete from User").executeUpdate();
    }

    @Test
    public void shouldGetListByExample() throws Exception {
        User user = new User("nick", "foo@bar.com", MALE);
        entityManager.persist(user);

        List<User> resultList =
                QBE.using(entityManager)
                   .query(User.class)
                   .by(new PojoUser(user.getNick()))
                   .list();

        assertEquals(1, resultList.size());
        assertEquals(user.getNick(), resultList.get(0).getNick());
    }


    @Test
    public void shouldGetItemByExample() throws Exception {
        User user = new User("nick", "foo@bar.com", MALE);
        entityManager.persist(user);

        User item =
                QBE.using(entityManager)
                   .query(User.class)
                   .by(new PojoUser(user.getNick()))
                   .item();

        assertEquals(user.getNick(), item.getNick());
    }

    @Test
    public void shouldGetQueryByExample() throws Exception {
        User user = new User("nick", "foo@bar.com", MALE);
        entityManager.persist(user);

        TypedQuery<User> query =
                QBE.using(entityManager)
                   .query(User.class)
                   .by(new PojoUser(user.getNick()))
                   .getQuery();

        List<User> resultList = query.setFirstResult(0).setMaxResults(10).getResultList();
        assertEquals(1, resultList.size());
        assertEquals(user.getNick(), resultList.get(0).getNick());
    }

    @Test
    public void shouldMatchAllForEmptyExample() throws Exception {
        List<User> resultList =
                QBE.using(entityManager)
                   .query(User.class)
                   .by(new PojoUser())
                   .list();

        assertEquals(NUMBER_OF_RANDOM_USERS, resultList.size());
    }

    @Test
    public void shouldSupportPaging() throws Exception {
        TypedQuery<User> query =
                QBE.using(entityManager)
                   .query(User.class)
                   .by(new PojoUser())
                   .getQuery();

        query.setFirstResult(5);
        query.setMaxResults(2);

        List<User> resultList = query.getResultList();

        assertEquals(2, resultList.size());
    }

    @Test
    public void shouldSupportExactMatching() throws Exception {
        String nick = "nick";
        String randomValue = randomValue();

        entityManager.persist(new User(randomValue + nick, "foo@bar.com", MALE));
        entityManager.persist(new User(nick, "foo@bar.com", MALE));
        entityManager.persist(new User(nick + randomValue, "foo@bar.com", MALE));
        entityManager.persist(new User(randomValue + nick + randomValue, "foo@bar.com", MALE));

        List<User> resultList =
                QBE.using(entityManager)
                   .query(User.class)
                   .by(new PojoUser(nick))
                   .list();

        assertEquals(1, resultList.size());
        assertEquals(nick, resultList.get(0).getNick());
    }

    @Test
    public void shouldSupportMatching() throws Exception {
        String nick = "nick";
        String randomValue = randomValue();

        entityManager.persist(new User(randomValue + nick, "foo@bar.com", MALE));
        entityManager.persist(new User(nick, "foo@bar.com", MALE));
        entityManager.persist(new User(nick + randomValue, "foo@bar.com", MALE));
        entityManager.persist(new User(randomValue + nick + randomValue, "foo@bar.com", MALE));

        QBEExample<PojoUser, User> qbeExample = QBE.using(entityManager)
                                                   .query(User.class)
                                                   .by(new PojoUser(nick));
        List<User> resultList =
                qbeExample.use(Matching.EXACT)
                          .list();

        assertEquals(1, resultList.size());
        assertEquals(nick, resultList.get(0).getNick());

        resultList =
                qbeExample.use(Matching.START)
                          .list();

        assertEquals(2, resultList.size());
        for (User user : resultList) {
            Assert.assertTrue(user.getNick().startsWith(nick));
        }

        resultList =
                qbeExample.use(Matching.END)
                          .list();

        assertEquals(2, resultList.size());
        for (User user : resultList) {
            Assert.assertTrue(user.getNick().endsWith(nick));
        }

        resultList =
                qbeExample.use(Matching.MIDDLE)
                          .list();

        assertEquals(4, resultList.size());
        for (User user : resultList) {
            Assert.assertTrue(user.getNick().contains(nick));
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void noneExactMatchingOnFieldWithNonStringTypeShouldThrowException() throws Exception {
        PojoUser example = new PojoUser();
        example.setGender(FEMALE);

        QBE.using(entityManager)
           .query(User.class)
           .by(example)
           .use(Matching.MIDDLE)
           .getQuery();
    }

    @Test
    public void shouldSupportMultipleFields() throws Exception {
        User user = new User("nick", "email", FEMALE);
        entityManager.persist(user);

        List<User> resultList =
                QBE.using(entityManager)
                   .query(User.class)
                   .by(new PojoUser(user.getNick(), user.getEmail())).list();

        assertEquals(1, resultList.size());
        assertEquals(user.getNick(), resultList.get(0).getNick());
        assertEquals(user.getEmail(), resultList.get(0).getEmail());
    }

    @Test
    public void shouldSupportIntersection() throws Exception {
        User user1 = new User(randomValue(), randomValue(), MALE);
        User user2 = new User(randomValue(), randomValue(), MALE);
        entityManager.persist(user1);
        entityManager.persist(user2);

        QBEExample<PojoUser, User> qbeExample = QBE.using(entityManager)
                                                   .query(User.class)
                                                   .by(new PojoUser(user1.getNick(), user2.getEmail()));
        List<User> resultList = qbeExample.list();

        assertEquals(0, resultList.size());

        resultList =
                qbeExample.use(Junction.INTERSECTION)
                          .list();

        assertEquals(2, resultList.size());
        for (User entity : resultList) {
            Assert.assertTrue(user1.getNick().equals(entity.getNick()) | user2.getEmail().equals(entity.getEmail()));
        }
    }

    @Test
    public void shouldOrderUsingColumnName() throws Exception {
        createDataForOrderedTests();

        String fieldToOrderBy = User.NICK_COLUMN_NAME;
        List<User> resultList =
                QBE.using(entityManager)
                   .query(User.class)
                   .by(new PojoUser("nick"))
                   .use(Matching.START)
                   .orderBy(fieldToOrderBy, Order.ASCENDING)
                   .list();
        assertOrder(resultList, Order.ASCENDING);

        resultList =
                QBE.using(entityManager)
                   .query(User.class)
                   .by(new PojoUser("nick"))
                   .use(Matching.START)
                   .orderBy(fieldToOrderBy, Order.DESCENDING)
                   .list();
        assertOrder(resultList, Order.DESCENDING);
    }

    @Test
    public void shouldOrderUsingFieldName() throws Exception {
        createDataForOrderedTests();

        String fieldToOrderBy = "nick";
        List<User> resultList =
                QBE.using(entityManager)
                   .query(User.class)
                   .by(new PojoUser("nick"))
                   .use(Matching.START)
                   .orderBy(fieldToOrderBy, Order.ASCENDING)
                   .list();
        assertOrder(resultList, Order.ASCENDING);

        resultList =
                QBE.using(entityManager)
                   .query(User.class)
                   .by(new PojoUser("nick"))
                   .use(Matching.START)
                   .orderBy(fieldToOrderBy, Order.DESCENDING)
                   .list();
        assertOrder(resultList, Order.DESCENDING);
    }

    @Test
    public void shouldGiveOrderedResultSetForMultipleOrders() throws Exception {
        entityManager.persist(new User("nick", "email1", MALE));
        entityManager.persist(new User("nick", "email2", MALE));
        entityManager.persist(new User("nick3", "email", MALE));
        entityManager.persist(new User("nick4", "email", MALE));
        entityManager.persist(new User("nick5", "email", MALE));

        List<User> resultList =
                QBE.using(entityManager)
                   .query(User.class)
                   .by(new PojoUser("nick"))
                   .use(Matching.START)
                   .orderBy("nick", Order.DESCENDING)
                   .orderBy("email", Order.ASCENDING)
                   .list();

        assertEquals(5, resultList.size());
        assertEquals("email", resultList.get(0).getEmail());
        assertEquals("nick5", resultList.get(0).getNick());
        assertEquals("email", resultList.get(1).getEmail());
        assertEquals("nick4", resultList.get(1).getNick());
        assertEquals("email", resultList.get(2).getEmail());
        assertEquals("nick3", resultList.get(2).getNick());
        assertEquals("email1", resultList.get(3).getEmail());
        assertEquals("nick", resultList.get(3).getNick());
        assertEquals("email2", resultList.get(4).getEmail());
        assertEquals("nick", resultList.get(4).getNick());
    }

    @Test(expected = OrderCreationException.class)
    public void shouldThrowExceptionForUnknownOrderByArgument() throws Exception {
        QBE.using(entityManager)
           .query(User.class)
           .by(new PojoUser("nick"))
           .use(Matching.START)
           .orderBy("someBogusFieldName", Order.ASCENDING)
           .list();
    }

    private void createDataForOrderedTests() {
        entityManager.persist(new User("nick1", "email1", FEMALE));
        entityManager.persist(new User("nick2", "email2", FEMALE));
        entityManager.persist(new User("nick3", "email3", FEMALE));
    }

    private void assertOrder(List<User> resultList, Order order) {
        assertEquals(3, resultList.size());
        switch (order) {
            case ASCENDING:
                assertEquals("nick1", resultList.get(0).getNick());
                assertEquals("nick2", resultList.get(1).getNick());
                assertEquals("nick3", resultList.get(2).getNick());
                break;
            case DESCENDING:
                assertEquals("nick3", resultList.get(0).getNick());
                assertEquals("nick2", resultList.get(1).getNick());
                assertEquals("nick1", resultList.get(2).getNick());
                break;
            default:
                throw new IllegalArgumentException("MUST BE ASC OR DESC FOR ASSERT");
        }
    }

    private void createRandomUsers(int numberOfUsers) {
        for (int i = 0; i < numberOfUsers; i++) {
            entityManager.persist(new User(randomValue(), randomValue(), i % 2 == 0 ? MALE : FEMALE));
        }
    }

    private String randomValue() {
        return UUID.randomUUID().toString();
    }
}
