package com.besant.ecommerce.service;

import com.besant.ecommerce.exception.*;
import com.besant.ecommerce.model.*;
import com.besant.ecommerce.model.Order.Status;
import com.besant.ecommerce.payment.Payment;
import java.util.*;

public class OrderService {
    private final ProductService products;
    private final PaymentService payments;
    private final List<Order> orders = new ArrayList<>();

    public OrderService(ProductService products, PaymentService payments) {
        this.products = products;
        this.payments = payments;
    }

    public synchronized Order checkout(Customer c, String method, Map<String, String> details)
            throws ShopException {
        Cart cart = c.getCart();
        if (cart.isEmpty()) throw new ShopException("Your cart is empty");                    // rule 8
        for (CartItem i : cart.list()) {
            Product p = products.get(i.getProduct().getId());
            if (i.getQuantity() > p.getStock())
                throw new InsufficientStockException(p.getName(), i.getQuantity(), p.getStock());
        }
        double total = cart.getTotal();
        Payment payment = payments.create(method, total, details);
        String reference = payment.process();                                                 // rule 9: pay first
        Order order = new Order(c, cart.list(), cart.getSubtotal(), cart.getDiscountAmount(),
                total, payment.getMethod(), reference);
        orders.add(order);
        order.setStatus(Status.CONFIRMED);
        for (CartItem i : cart.list()) {                                                      // rule 10
            Product p = i.getProduct();
            p.setStock(p.getStock() - i.getQuantity());
        }
        cart.clear();
        return order;
    }

    public Order find(int orderId) throws ShopException {
        for (Order o : orders) if (o.getId() == orderId) return o;
        throw new ShopException("Order " + orderId + " not found");
    }

    public synchronized Order changeStatus(int orderId, Status target, boolean byAdmin, int requesterId)
            throws ShopException {
        Order o = find(orderId);
        if (!byAdmin && o.getCustomer().getId() != requesterId)
            throw new ShopException("This is not your order");
        if (!byAdmin && target != Status.CANCELLED)
            throw new ShopException("Only admin can update this status");
        Status cur = o.getStatus();

        switch (target) {
            case CANCELLED:
                if (cur == Status.DELIVERED) throw new ShopException("A delivered order cannot be cancelled"); // rule 11
                if (cur == Status.CANCELLED) throw new ShopException("Order is already cancelled");
                if (!byAdmin && cur == Status.SHIPPED)
                    throw new ShopException("A shipped order can only be cancelled by admin");
                for (Order.Line l : o.getLines()) {          // put stock back
                    Product p = products.find(l.productId);
                    if (p != null) p.setStock(p.getStock() + l.quantity);
                }
                break;
            case SHIPPED:
                if (cur == Status.CANCELLED) throw new ShopException("A cancelled order cannot be shipped"); // rule 12
                if (cur != Status.CONFIRMED) throw new ShopException("Only confirmed orders can be shipped");
                break;
            case DELIVERED:
                if (cur != Status.SHIPPED) throw new ShopException("Only shipped orders can be delivered");
                break;
            default:
                throw new ShopException("Invalid status change");
        }
        o.setStatus(target);
        return o;
    }

    public List<Order> forCustomer(int customerId) {
        List<Order> result = new ArrayList<>();
        for (Order o : orders) if (o.getCustomer().getId() == customerId) result.add(o);
        Collections.reverse(result);      // newest first
        return result;
    }

    public List<Order> all() {
        List<Order> result = new ArrayList<>(orders);
        Collections.reverse(result);
        return result;
    }
}