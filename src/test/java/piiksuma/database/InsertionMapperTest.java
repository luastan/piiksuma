package piiksuma.database;

import javafx.geometry.Pos;
import org.junit.Test;
import piiksuma.Post;
import piiksuma.User;
import piiksuma.UserType;
import piiksuma.api.ApiFacade;
import piiksuma.api.FacadeTest;
import piiksuma.exceptions.PiikDatabaseException;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class InsertionMapperTest extends FacadeTest {

    private static Connection connection = ApiFacade.getEntrypoint().getConnection();

    @Test
    public void customInsertionTest() throws PiikDatabaseException {
        User pepe = new User("id23", "nooombre", "emaaail@gas.com", "somepass", UserType.user);

        Map<String, Object> set = new HashMap<>();
        set.put("id", "id21");
        set.put("email", "emailsd@gas.com");
        set.put("pass", "notNullPass");
        set.put("name", "someCoolName");
        set.put("birthdate", new Timestamp(1996, 9, 27, 0, 0, 0, 0));

        new InsertionMapper<>(connection).customInsertion(set, "piiUser");
        new QueryMapper<User>(connection).defineClass(User.class).createQuery("SELECT * FROM piiuser").list().forEach(System.out::println);

    }

    @Test
    public void normalInsertionTest() throws PiikDatabaseException {
        User pepe = new User("id23", "nooombre", "emaaail@gas.com", "somepass", UserType.user);
        pepe.setBirthday(new Timestamp(1996, 9, 27, 0, 0, 0, 0));

        new InsertionMapper<User>(connection).defineClass(User.class).add(pepe).insert();
        new QueryMapper<User>(connection).defineClass(User.class).createQuery("SELECT * FROM piiuser").list().forEach(System.out::println);

    }

    @Test
    public void heavyFkInsertionTest() throws PiikDatabaseException {
        User pepe = new User("id23", "nooombre", "emaaail@gas.com", "somepass", UserType.user);
        pepe.setBirthday(new Timestamp(1996, 9, 27, 0, 0, 0, 0));
        new InsertionMapper<User>(connection).defineClass(User.class).add(pepe).insert();
        Post post = new Post(pepe, null);
        post.setText("helloooo pipol");
        post.setId("uniqueaf");
        Post postChild = new Post(pepe, null);
        postChild.setFatherPost(post);
        postChild.setId("lmao");
        postChild.setText("I love recursion");
        new InsertionMapper<>(connection).defineClass(Post.class).add(post).insert();
        new InsertionMapper<>(connection).defineClass(Post.class).add(postChild).insert();
        new QueryMapper<Post>(connection).defineClass(Post.class).createQuery("SELECT * FROM post").list().forEach(System.out::println);

    }
}