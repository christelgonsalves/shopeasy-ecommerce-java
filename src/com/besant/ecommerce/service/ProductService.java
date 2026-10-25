package com.besant.ecommerce.service;

import com.besant.ecommerce.exception.*;
import com.besant.ecommerce.model.*;
import java.util.*;

public class ProductService {
    private final Map<Integer, Product> products = new LinkedHashMap<>();
    private int nextId = 401;

    public ProductService() {
        try {
            add(101, "Electronics", "Laptop", 55000, 10, "Dell");
            add(102, "Electronics", "Mobile", 18999, 25, "Samsung");
            add(103, "Electronics", "Headphones", 2499, 40, "Sony");
            add(104, "Electronics", "Television", 32000, 8, "LG");
            add(201, "Clothing", "Shirt", 1299, 50, "M");
            add(202, "Clothing", "T-Shirt", 999, 100, "L");
            add(203, "Clothing", "Jeans", 1899, 60, "32");
            add(204, "Clothing", "Shoes", 2999, 35, "9");
            add(301, "Grocery", "Rice", 650, 200, "10 kg");
            add(302, "Grocery", "Sugar", 48, 300, "1 kg");
            add(303, "Grocery", "Oil", 165, 150, "1 L");
            add(304, "Grocery", "Biscuits", 30, 500, "200 g");
        } catch (ShopException e) {
            throw new IllegalStateException(e);
        }
    }

    public synchronized Product add(Integer id, String category, String name, double price,
                                    int stock, String attribute) throws ShopException {
        if (name == null || name.isBlank()) throw new ShopException("Product name is required");
        if (price < 0) throw new ShopException("Price cannot be negative");
        if (stock < 0) throw new ShopException("Quantity cannot be negative");
        if (category == null) throw new ShopException("Category is required");

        int pid;
        if (id == null) {
            while (products.containsKey(nextId)) nextId++;
            pid = nextId++;
        } else {
            if (products.containsKey(id)) throw new ShopException("Product ID " + id + " already exists");
            pid = id;
        }
        Product p;
        switch (category) {
            case "Electronics": p = new Electronics(pid, name.trim(), price, stock, attribute); break;
            case "Clothing":    p = new Clothing(pid, name.trim(), price, stock, attribute); break;
            case "Grocery":     p = new Grocery(pid, name.trim(), price, stock, attribute); break;
            default: throw new ShopException("Unknown category: " + category);
        }
        products.put(pid, p);
        return p;
    }

    public Product get(int id) throws ProductNotFoundException {
        Product p = products.get(id);
        if (p == null) throw new ProductNotFoundException(id);
        return p;
    }

    public Product find(int id) { return products.get(id); }   // returns null if missing

    public synchronized Product update(int id, String name, Double price, Integer stock,
                                       String attribute) throws ShopException {
        Product p = get(id);
        if (price != null && price < 0) throw new ShopException("Price cannot be negative");
        if (stock != null && stock < 0) throw new ShopException("Quantity cannot be negative");
        if (name != null && !name.isBlank()) p.setName(name.trim());
        if (price != null) p.setPrice(price);
        if (stock != null) p.setStock(stock);
        if (attribute != null && !attribute.isBlank()) p.setAttribute(attribute.trim());
        return p;
    }

    public synchronized void delete(int id) throws ProductNotFoundException {
        get(id);
        products.remove(id);
    }

    public List<Product> search(String query, String category) {
        List<Product> result = new ArrayList<>();
        String q = query == null ? "" : query.trim().toLowerCase();
        for (Product p : products.values()) {
            boolean catOk = category == null || category.isBlank() || category.equals("All")
                    || p.getCategory().equalsIgnoreCase(category);
            boolean textOk = q.isEmpty() || p.getName().toLowerCase().contains(q)
                    || String.valueOf(p.getId()).equals(q);
            if (catOk && textOk) result.add(p);
        }
        return result;
    }

    public Collection<Product> all() { return products.values(); }
}