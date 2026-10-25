package com.besant.ecommerce.service;

import com.besant.ecommerce.discount.Discount;
import com.besant.ecommerce.exception.*;
import com.besant.ecommerce.model.*;
import java.util.*;

public class CartService {
    private final ProductService products;
    private final DiscountService discounts;

    public CartService(ProductService products, DiscountService discounts) {
        this.products = products;
        this.discounts = discounts;
    }

    public void add(Customer c, int productId, int qty) throws ShopException {
        Product p = products.get(productId);
        if (qty <= 0) throw new ShopException("Quantity must be at least 1");
        if (p.getStock() == 0) throw new ShopException(p.getName() + " is out of stock");   // rule 6
        CartItem existing = c.getCart().getItems().get(productId);
        int newQty = qty + (existing == null ? 0 : existing.getQuantity());
        if (newQty > p.getStock()) throw new InsufficientStockException(p.getName(), newQty, p.getStock()); // rule 7
        if (existing == null) c.getCart().getItems().put(productId, new CartItem(p, qty));
        else existing.setQuantity(newQty);
    }

    public void remove(Customer c, int productId) throws ShopException {
        if (c.getCart().getItems().remove(productId) == null)
            throw new ShopException("Product is not in your cart");
    }

    public void updateQuantity(Customer c, int productId, int qty) throws ShopException {
        CartItem item = c.getCart().getItems().get(productId);
        if (item == null) throw new ShopException("Product is not in your cart");
        if (qty <= 0) { remove(c, productId); return; }
        Product p = item.getProduct();
        if (qty > p.getStock()) throw new InsufficientStockException(p.getName(), qty, p.getStock());
        item.setQuantity(qty);
    }

    public void applyDiscount(Customer c, String code) throws ShopException {
        Discount d = discounts.get(code);
        if (!d.isApplicable(c.getCart().getSubtotal()))
            throw new ShopException("Offer not applicable: " + d.describe());
        c.getCart().setDiscount(d);
    }

    public void clearDiscount(Customer c) { c.getCart().setDiscount(null); }

    /** Converts the cart to a Map so it can be turned into JSON later. */
    public Map<String, Object> view(Cart cart) {
        Map<String, Object> m = new LinkedHashMap<>();
        List<Object> list = new ArrayList<>();
        for (CartItem i : cart.list()) {
            Product p = i.getProduct();
            Map<String, Object> x = new LinkedHashMap<>();
            x.put("productId", p.getId()); x.put("name", p.getName()); x.put("category", p.getCategory());
            x.put("price", p.getPrice()); x.put("quantity", i.getQuantity());
            x.put("stock", p.getStock()); x.put("total", i.getTotal());
            list.add(x);
        }
        m.put("items", list);
        m.put("subtotal", cart.getSubtotal());
        m.put("discountCode", cart.getDiscount() == null ? null : cart.getDiscount().getCode());
        m.put("discountAmount", cart.getDiscountAmount());
        m.put("total", cart.getTotal());
        return m;
    }
}