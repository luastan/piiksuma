import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import piiksuma.FancyClassTest;
import piiksuma.database.FachadaBDD;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MockitoTestSample {

    static List<Integer> listaNumeros;

    @Mock
    FancyClassTest fancyClassTest;  // Instancia falsa como el billete de 39

    @Mock
    FachadaBDD mockedDB = FachadaBDD.getDb();

    @BeforeClass
    public static void setUp() throws Exception {
        listaNumeros = new ArrayList<>();
        listaNumeros.add(4);
        listaNumeros.add(5);
        listaNumeros.add(6);
    }


    @Test
    public void testNumber() {
        Integer mockedValue = 86;
        // Define el return del método returnsWhatever
        when(fancyClassTest.returnsWhatever()).thenReturn(mockedValue);

        // Comprueba que esa clase devuelve X
        assertEquals(fancyClassTest.returnsWhatever(), mockedValue);
    }

    @Test
    public void testMasComplicado() {
        // Se guarda la antigua fachada
        FachadaBDD antigua = FachadaBDD.getDb();

        // Se le mete a la fachada una instancia falsa
        FachadaBDD.setDb(mockedDB);

        // Se define lo que debe devolver el metodo .numList() de la fachada
        when(mockedDB.numList()).thenReturn(listaNumeros);

        // Se hace una instancia de la clase que va a usar la fachada
        FancyClassTest instanciaNormal = new FancyClassTest(93);

        // Se comprueba que se inyectó bien el método
        assertEquals(listaNumeros, instanciaNormal.returnsFromFachadaBDD());

        // Se vuelve a poner la antigua
        FachadaBDD.setDb(antigua);
    }
}
