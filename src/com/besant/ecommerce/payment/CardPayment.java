package com.besant.ecommerce.payment;

import com.besant.ecommerce.exception.ShopException;

public class CardPayment extends Payment {
    private final String number, holder, cvv;
    public CardPayment(double amount, String number, String holder, String cvv) {
        super(amount);
        this.number = number == null ? "" : number.replaceAll("\\s", "");
        this.holder = holder == null ? "" : holder.trim();
        this.cvv = cvv == null ? "" : cvv.trim();
    }
    public String getMethod() { return "CARD"; }
    public String process() throws ShopException {
        if (!number.matches("\\d{16}")) throw new ShopException("Card number must be 16 digits");
        if (holder.isEmpty()) throw new ShopException("Card holder name is required");
        if (!cvv.matches("\\d{3}")) throw new ShopException("CVV must be 3 digits");
        return ref("CARD") + " (**** " + number.substring(12) + ")";
    }
}