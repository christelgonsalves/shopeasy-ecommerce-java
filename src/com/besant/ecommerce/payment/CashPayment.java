package com.besant.ecommerce.payment;

public class CashPayment extends Payment {
    public CashPayment(double amount) { super(amount); }
    public String getMethod() { return "COD"; }
    public String process() { return ref("COD") + " (pay on delivery)"; }
}