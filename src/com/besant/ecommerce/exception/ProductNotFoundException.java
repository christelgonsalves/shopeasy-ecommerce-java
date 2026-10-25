package com.besant.ecommerce.exception;

public class ProductNotFoundException extends ShopException {
    public ProductNotFoundException(int id) { super("Product with ID " + id + " not found"); }
}