package piiksuma.database;

import org.junit.Test;
import piiksuma.Post;
import piiksuma.User;
import piiksuma.UserType;
import piiksuma.api.ApiFacade;
import piiksuma.api.FacadeTest;
import piiksuma.exceptions.PiikDatabaseException;

import java.sql.Connection;
import java.sql.Timestamp;

import static org.junit.Assert.*;

public class DeleteMapperTest extends FacadeTest {
    Connection connection = ApiFacade.getEntrypoint().getConnection();

    @Test
    public void testDeleteFk1Depth() throws PiikDatabaseException {
        User pepe = new User("id23", "nooombre", "emaaail@gas.com", "somepass", UserType.user);
        pepe.setBirthday(new Timestamp(1996, 9, 27, 0, 0, 0, 0));

        new InsertionMapper<User>(connection).defineClass(User.class).add(pepe).insert();

        Post post = new Post(pepe, null);
        post.setText("helloooo pipol");
        post.setId("uniqueaf");
        new InsertionMapper<>(connection).defineClass(Post.class).add(post).insert();
        new QueryMapper<Post>(connection).defineClass(Post.class).createQuery("SELECT * FROM post").list().forEach(System.out::println);
        System.out.println("----");
        new DeleteMapper<>(connection).defineClass(Post.class).add(post).delete();
        new QueryMapper<Post>(connection).defineClass(Post.class).createQuery("SELECT * FROM post").list().forEach(System.out::println);
    }

    @Test
    public void testDeleteNormal() throws PiikDatabaseException {
        User pepe = new User("id23", "nooombre", "emaaail@gas.com", "somepass", UserType.user);
        pepe.setBirthday(new Timestamp(1996, 9, 27, 0, 0, 0, 0));

        new InsertionMapper<User>(connection).defineClass(User.class).add(pepe).insert();
        new QueryMapper<User>(connection).defineClass(User.class).createQuery("SELECT * FROM piiuser").list().forEach(System.out::println);

        System.out.println("-----");
        new DeleteMapper<>(connection).defineClass(User.class).add(pepe).delete();
        new QueryMapper<User>(connection).defineClass(User.class).createQuery("SELECT * FROM piiuser").list().forEach(System.out::println);
    }
}