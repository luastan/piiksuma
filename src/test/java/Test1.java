import org.junit.Test;
import piiksuma.database.FachadaBDD;

import static org.junit.Assert.assertNotNull;

public class Test1 {
    @Test
    public void whatever_test1() {
        System.out.println(FachadaBDD.getDb().test().get(0).get("nombre"));
    }

    @Test
    public void test2_prueba() {
        assertNotNull("lol");
    }
}
