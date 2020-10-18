package com.ionutciuta.patterns.rules.impl;

import com.ionutciuta.patterns.model.Basket;
import org.springframework.stereotype.Component;

@Component
public class DiscountForBigOrdersPromotion implements BasketRule {
    @Override
    public boolean matches(Basket basket) {
        return basket.getTotal() > 1000.00;
    }

    @Override
    public void apply(Basket basket) {
        System.out.println("Applying " + this.getClass().getSimpleName() + " promotion -100.00");
        basket.decreaseTotal(100.00);
    }
}
