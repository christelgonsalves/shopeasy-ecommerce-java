package com.besant.ecommerce.model;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Product {
    private final int id;
    private String name;
    private double price;
    private int stock;
    private final String category;
    private String attribute;

    protected Product(int id, String name, double price, int stock, String category, String attribute) {
        this.id = id; this.name = name; this.price = price; this.stock = stock;
        this.category = category; this.attribute = attribute;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String n) { this.name = n; }
    public double getPrice() { return price; }
    public void setPrice(double p) { this.price = p; }
    public int getStock() { return stock; }
    public void setStock(int s) { this.stock = s; }
    public String getCategory() { return category; }
    public String getAttribute() { return attribute; }
    public void setAttribute(String a) { this.attribute = a; }

    public abstract String getAttributeLabel();   // polymorphism

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id); m.put("name", name); m.put("price", price); m.put("stock", stock);
        m.put("category", category); m.put("attributeLabel", getAttributeLabel()); m.put("attribute", attribute);
        return m;
    }
}