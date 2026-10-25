package com.besant.ecommerce.model;

public class Electronics extends Product {
    public Electronics(int id, String name, double price, int stock, String attribute) {
        super(id, name, price, stock, "Electronics", attribute);
    }
    @Override
    public String getAttributeLabel() { return "Brand"; }
}