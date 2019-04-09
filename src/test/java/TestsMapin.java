import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import piiksuma.User;
import piiksuma.api.dao.UserDao;
import piiksuma.database.DeleteMapper;
import piiksuma.database.InsertionMapper;
import piiksuma.database.QueryMapper;
import piiksuma.database.SampleFachada;

import java.sql.Connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestsMapin {

    private static User user = new User("Marcos López Lamas", "MarpinTesting", "mll2725@gmail.com");
    private static Connection conection = SampleFachada.getDb().getConexion();
    private static UserDao userDao = new UserDao(SampleFachada.getDb().getConexion());

    @Before
    public void setUp() throws Exception {
        new InsertionMapper<User>(SampleFachada.getDb().getConexion()).defineClass(User.class).add(user).insert();
    }

    @After
    public void tearDown() throws Exception {
        try {
            new DeleteMapper<User>(SampleFachada.getDb().getConexion()).defineClass(User.class).add(user).delete();
        } catch (RuntimeException ignore) {

        }
    }


    @Ignore
    public void removeUser_null() {

        userDao.removeUser(null, null);
    }

    @Test
    public void removeUser_propperPermission() {
        userDao.removeUser(user, user);

        assertEquals(0, new QueryMapper<User>(conection).createQuery("select * where email=?").defineParameters(user.getEmail()).list().size());
    }

    @Test
    public void removeUser_noPermission() {
        userDao.removeUser(user, new User("Marcos López Lamas", "Marpesting", "m5@gmail.com"));

        assertNotNull(new QueryMapper<User>(conection).createQuery("select * where email=?").defineParameters(user.getEmail()).findFirst());
    }

    @Test
    public void removeUser_adminPermission() {
        userDao.removeUser(user, new User());

        assertEquals(0, new QueryMapper<User>(conection).createQuery("select * where email=?").defineParameters(user.getEmail()).list().size());
    }
}