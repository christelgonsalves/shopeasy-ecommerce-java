package com.besant.ecommerce.model;

import com.besant.ecommerce.discount.Discount;
import java.util.*;

public class Cart {
    private final Map<Integer, CartItem> items = new LinkedHashMap<>();
    private Discount discount;

    public Map<Integer, CartItem> getItems() { return items; }
    public Collection<CartItem> list() { return items.values(); }
    public boolean isEmpty() { return items.isEmpty(); }
    public Discount getDiscount() { return discount; }
    public void setDiscount(Discount d) { this.discount = d; }

    public double getSubtotal() {
        double t = 0;
        for (CartItem i : items.values()) t += i.getTotal();
        return t;
    }
    public double getDiscountAmount() {
        return (discount != null && discount.isApplicable(getSubtotal())) ? discount.calculate(getSubtotal()) : 0;
    }
    public double getTotal() { return getSubtotal() - getDiscountAmount(); }
    public void clear() { items.clear(); discount = null; }
}