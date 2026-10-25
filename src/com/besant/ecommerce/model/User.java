package com.besant.ecommerce.model;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class User {
    private final int id;
    private String name;
    private final String email;
    private final String password;
    private final String role;

    protected User(int id, String name, String email, String password, String role) {
        this.id = id; this.name = name; this.email = email; this.password = password; this.role = role;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean checkPassword(String p) { return password.equals(p); }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id); m.put("name", name); m.put("email", email); m.put("role", role);
        return m;
    }
}