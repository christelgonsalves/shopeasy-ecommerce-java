package com.besant.ecommerce.model;

public class Grocery extends Product {
    public Grocery(int id, String name, double price, int stock, String attribute) {
        super(id, name, price, stock, "Grocery", attribute);
    }
    @Override
    public String getAttributeLabel() { return "Pack Size"; }
}
