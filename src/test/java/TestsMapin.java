import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import piiksuma.User;
import piiksuma.UserType;
import piiksuma.api.dao.UserDao;
import piiksuma.database.DeleteMapper;
import piiksuma.database.InsertionMapper;
import piiksuma.database.QueryMapper;
import piiksuma.database.SampleFachada;

import java.sql.Connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestsMapin {

    private static User owner = new User("Marcos López Lamas", "marpin", "mll2725@gmail.com", "sesamo", UserType.user);
    private static User other = new User("Marcos López Lamas", "otherMarpin", "mll27@gmail.com", "sesamo", UserType.user);
    private static User admin = new User("Marcos López Lamas","adminMarpin", "mll@gmail.com", "sesamo", UserType.administrator);

    private static Connection conection = SampleFachada.getDb().getConexion();
    private static UserDao userDao = new UserDao(conection);

    @Before
    public void setUp() throws Exception {
        new InsertionMapper<User>(conection).defineClass(User.class).add(owner).insert();
    }

    @After
    public void tearDown() throws Exception {
        try {
            new DeleteMapper<User>(conection).defineClass(User.class).add(owner).delete();
        } catch (RuntimeException ignore) {

        }
    }


    @Ignore
    public void removeUser_null() {
        userDao.removeUser(null, null);
    }

    @Test
    public void removeUser_propperPermission() {
        userDao.removeUser(owner, owner);

        assertEquals(0, new QueryMapper<User>(conection).createQuery("SELECT * FROM piiuser WHERE email LIKE ?").defineParameters(owner.getEmail()).list().size());
    }

    @Ignore
    public void removeUser_noPermission() {
        userDao.removeUser(owner, other);

        assertNotNull(new QueryMapper<User>(conection).createQuery("SELECT * FROM piiuser WHERE email LIKE ?").defineParameters(owner.getEmail()).findFirst());
    }

    @Test
    public void removeUser_adminPermission() {
        userDao.removeUser(owner, admin);

        assertEquals(0, new QueryMapper<User>(conection).createQuery("SELECT * FROM piiuser WHERE email LIKE ?").defineParameters(owner.getEmail()).list().size());
    }
}