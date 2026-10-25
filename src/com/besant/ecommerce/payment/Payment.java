package com.besant.ecommerce.payment;

import com.besant.ecommerce.exception.ShopException;

public abstract class Payment {
    protected final double amount;
    protected Payment(double amount) { this.amount = amount; }
    public abstract String getMethod();
    public abstract String process() throws ShopException;
    protected String ref(String prefix) { return prefix + "-" + System.currentTimeMillis() % 1000000; }
}