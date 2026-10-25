package com.besant.ecommerce.service;

import com.besant.ecommerce.discount.*;
import com.besant.ecommerce.exception.ShopException;
import java.util.*;

public class DiscountService {
    private final Map<String, Discount> offers = new LinkedHashMap<>();

    public DiscountService() {
        offers.put("SAVE10", new PercentDiscount("SAVE10", 10, 5000));
        offers.put("BIG20", new PercentDiscount("BIG20", 20, 50000));
        offers.put("FLAT500", new FlatDiscount("FLAT500", 500, 2000));
    }

    public Discount get(String code) throws ShopException {
        Discount d = code == null ? null : offers.get(code.trim().toUpperCase());
        if (d == null) throw new ShopException("Invalid discount code");
        return d;
    }

    public Collection<Discount> all() { return offers.values(); }
}