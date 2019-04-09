package piiksuma;

import piiksuma.database.SampleFachada;

import java.util.List;

public class FancyClassTest {
    private Integer whatever;

    public FancyClassTest(Integer whatever) {
        this.whatever = whatever;
    }

    public Integer returnsWhatever() {
        return whatever;
    }

    public List<Integer> returnsFromFachadaBDD() {
        return SampleFachada.getDb().numList();
    }
}
