package com.besant.ecommerce.model;

import java.util.Map;

public class Customer extends User {
    private final String address;
    private final Cart cart = new Cart();

    public Customer(int id, String name, String email, String password, String address) {
        super(id, name, email, password, "CUSTOMER");
        this.address = address;
    }
    public String getAddress() { return address; }
    public Cart getCart() { return cart; }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = super.toMap();
        m.put("address", address);
        return m;
    }
}