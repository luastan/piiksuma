package piiksuma;

import piiksuma.database.FachadaBDD;

public class Arranque {
    public static void main(String[] args) {
        System.out.println("lol");
        System.out.println(FachadaBDD.getDb().test().get(0).get("nombre"));
    }
}
