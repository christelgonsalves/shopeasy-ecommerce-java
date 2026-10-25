package com.besant.ecommerce.discount;

public class PercentDiscount implements Discount {
    private final String code; private final double percent; private final double minAmount;
    public PercentDiscount(String code, double percent, double minAmount) {
        this.code = code; this.percent = percent; this.minAmount = minAmount;
    }
    public String getCode() { return code; }
    public String describe() {
        return (int) percent + "% off" + (minAmount > 0 ? " on orders above Rs." + (long) minAmount : "");
    }
    public boolean isApplicable(double s) { return s >= minAmount && s > 0; }
    public double calculate(double s) { return s * percent / 100.0; }
}