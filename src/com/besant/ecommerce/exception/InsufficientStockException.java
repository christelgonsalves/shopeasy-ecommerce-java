package com.besant.ecommerce.exception;

public class InsufficientStockException extends ShopException {
    public InsufficientStockException(String name, int requested, int available) {
        super("Only " + available + " unit(s) of " + name + " available, but " + requested + " requested");
    }
}