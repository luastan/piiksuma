import org.junit.Ignore;
import org.junit.Test;
import piiksuma.database.SampleFachada;

import static org.junit.Assert.assertNotNull;

public class Test1 {
    @Ignore
    public void whatever_test1() {
        System.out.println(SampleFachada.getDb().test().get(0).get("nombre"));
    }

    @Test
    public void test2_prueba() {
        assertNotNull("lol");
    }
}
