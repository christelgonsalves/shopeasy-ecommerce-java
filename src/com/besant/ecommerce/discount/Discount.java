package com.besant.ecommerce.discount;

public interface Discount {
    String getCode();
    String describe();
    boolean isApplicable(double subtotal);
    double calculate(double subtotal);
}
