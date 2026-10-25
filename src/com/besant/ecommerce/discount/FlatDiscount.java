package com.besant.ecommerce.discount;

public class FlatDiscount implements Discount {
    private final String code; private final double amount; private final double minAmount;
    public FlatDiscount(String code, double amount, double minAmount) {
        this.code = code; this.amount = amount; this.minAmount = minAmount;
    }
    public String getCode() { return code; }
    public String describe() {
        return "Flat Rs." + (long) amount + " off" + (minAmount > 0 ? " on orders above Rs." + (long) minAmount : "");
    }
    public boolean isApplicable(double s) { return s >= minAmount && s > 0; }
    public double calculate(double s) { return Math.min(amount, s); }
}