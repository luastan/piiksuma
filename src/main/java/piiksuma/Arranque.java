package piiksuma;

import piiksuma.database.SampleFachada;

public class Arranque {
    public static void main(String[] args) {
        //System.out.println("lol");
        //System.out.println(SampleFachada.getDb().test().get(0).get("nombre"));

        System.out.println("\n\n");

        System.out.println(SampleFachada.getDb().usuarios());
        
        System.out.println("\n\n");
    }
}
