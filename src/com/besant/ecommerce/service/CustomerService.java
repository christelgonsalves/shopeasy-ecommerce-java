package com.besant.ecommerce.service;

import com.besant.ecommerce.exception.ShopException;
import com.besant.ecommerce.model.*;
import java.util.*;

public class CustomerService {
    private final Map<String, User> usersByEmail = new HashMap<>();
    private final Map<Integer, Customer> customers = new LinkedHashMap<>();
    private final Admin admin = new Admin(1, "Store Admin", "admin@shop.com", "admin123");
    private int nextId = 1001;

    public CustomerService() { usersByEmail.put(admin.getEmail(), admin); }

    public synchronized Customer register(String name, String email, String password, String address)
            throws ShopException {
        if (name == null || name.isBlank()) throw new ShopException("Name is required");
        if (email == null || !email.matches("[\\w.+\\-]+@[\\w\\-]+\\.[A-Za-z.]{2,}"))
            throw new ShopException("Enter a valid email address");
        if (password == null || password.length() < 4)
            throw new ShopException("Password must be at least 4 characters");
        String key = email.trim().toLowerCase();
        if (usersByEmail.containsKey(key)) throw new ShopException("Email is already registered");   // rule 4
        Customer c = new Customer(nextId++, name.trim(), key, password, address == null ? "" : address.trim());
        usersByEmail.put(key, c);
        customers.put(c.getId(), c);
        return c;
    }

    public User login(String email, String password) throws ShopException {
        User u = email == null ? null : usersByEmail.get(email.trim().toLowerCase());
        if (u == null || !u.checkPassword(password)) throw new ShopException("Invalid email or password");
        return u;
    }

    public Customer getCustomer(int id) throws ShopException {
        Customer c = customers.get(id);
        if (c == null) throw new ShopException("Please login first");          // rule 5
        return c;
    }

    public void requireAdmin(int id) throws ShopException {
        if (admin.getId() != id) throw new ShopException("Admin access required");
    }

    public int customerCount() { return customers.size(); }
}