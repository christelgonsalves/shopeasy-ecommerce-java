package com.besant.ecommerce.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Order {
    public enum Status { CREATED, CONFIRMED, SHIPPED, DELIVERED, CANCELLED }

    public static class Line {
        public final int productId; public final String name; public final String category;
        public final int quantity; public final double unitPrice;
        public Line(int productId, String name, String category, int quantity, double unitPrice) {
            this.productId = productId; this.name = name; this.category = category;
            this.quantity = quantity; this.unitPrice = unitPrice;
        }
    }

    private static int counter = 5000;            // static member
    private final int id = ++counter;
    private final Customer customer;
    private final List<Line> lines = new ArrayList<>();
    private final double subtotal, discountAmount, total;
    private final String paymentMethod, paymentReference;
    private Status status = Status.CREATED;
    private final String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));

    public Order(Customer c, Collection<CartItem> items, double subtotal, double discountAmount,
                 double total, String method, String reference) {
        this.customer = c; this.subtotal = subtotal; this.discountAmount = discountAmount;
        this.total = total; this.paymentMethod = method; this.paymentReference = reference;
        for (CartItem i : items) {
            Product p = i.getProduct();
            lines.add(new Line(p.getId(), p.getName(), p.getCategory(), i.getQuantity(), p.getPrice()));
        }
    }
    public int getId() { return id; }
    public Customer getCustomer() { return customer; }
    public List<Line> getLines() { return lines; }
    public double getTotal() { return total; }
    public Status getStatus() { return status; }
    public void setStatus(Status s) { this.status = s; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id); m.put("customerId", customer.getId()); m.put("customerName", customer.getName());
        List<Object> ls = new ArrayList<>();
        for (Line l : lines) {
            Map<String, Object> x = new LinkedHashMap<>();
            x.put("productId", l.productId); x.put("name", l.name); x.put("category", l.category);
            x.put("quantity", l.quantity); x.put("unitPrice", l.unitPrice); x.put("total", l.unitPrice * l.quantity);
            ls.add(x);
        }
        m.put("items", ls); m.put("subtotal", subtotal); m.put("discount", discountAmount); m.put("total", total);
        m.put("paymentMethod", paymentMethod); m.put("paymentReference", paymentReference);
        m.put("status", status.name()); m.put("createdAt", createdAt);
        return m;
    }
}