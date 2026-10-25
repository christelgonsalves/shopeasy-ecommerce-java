package com.besant.ecommerce.service;

import com.besant.ecommerce.exception.ShopException;
import com.besant.ecommerce.payment.*;
import java.util.Map;

public class PaymentService {
    public Payment create(String method, double amount, Map<String, String> d) throws ShopException {
        if (method == null) throw new ShopException("Select a payment method");
        switch (method.toUpperCase()) {
            case "UPI":  return new UpiPayment(amount, d.get("upiId"));
            case "CARD": return new CardPayment(amount, d.get("cardNumber"), d.get("cardHolder"), d.get("cvv"));
            case "COD":  return new CashPayment(amount);
            default: throw new ShopException("Unknown payment method: " + method);
        }
    }
}