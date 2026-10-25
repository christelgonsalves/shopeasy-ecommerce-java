package com.besant.ecommerce.payment;

import com.besant.ecommerce.exception.ShopException;

public class UpiPayment extends Payment {
    private final String upiId;
    public UpiPayment(double amount, String upiId) { super(amount); this.upiId = upiId == null ? "" : upiId.trim(); }
    public String getMethod() { return "UPI"; }
    public String process() throws ShopException {
        if (!upiId.matches("[\\w.\\-]{2,}@[A-Za-z]{2,}")) throw new ShopException("Invalid UPI ID (example: name@upi)");
        return ref("UPI");
    }
}