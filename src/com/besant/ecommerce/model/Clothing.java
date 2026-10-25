package com.besant.ecommerce.model;

public class Clothing extends Product {
    public Clothing(int id, String name, double price, int stock, String attribute) {
        super(id, name, price, stock, "Clothing", attribute);
    }
    @Override
    public String getAttributeLabel() { return "Size"; }
}