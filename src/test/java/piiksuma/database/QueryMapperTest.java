package piiksuma.database;

import org.junit.Test;
import piiksuma.Post;
import piiksuma.api.ApiFacade;
import piiksuma.api.FacadeTest;

import java.sql.Connection;

import static org.junit.Assert.*;

public class QueryMapperTest extends FacadeTest {
    private static Connection connection = ApiFacade.getEntrypoint().getConnection();


    @Test
    public void testFkQueries() {

        new QueryMapper<Post>(connection).defineClass(Post.class).createQuery("SELECT * FROM post;")
                .list().forEach(System.out::println);
    }
}