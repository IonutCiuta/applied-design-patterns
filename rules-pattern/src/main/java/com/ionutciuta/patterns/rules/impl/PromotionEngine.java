package com.ionutciuta.patterns.rules.impl;

import com.ionutciuta.patterns.model.Basket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PromotionEngine {

    @Autowired
    private List<BasketRule> promotionRules;

    public void process(Basket basket) {
        System.out.println("=== BEFORE Promotions ===");
        System.out.println(basket);

        System.out.println("\nApplying promotions");
        promotionRules.stream()
                .filter(rule -> rule.matches(basket))
                .forEach(rule -> rule.apply(basket));
        System.out.println("Promotions applies\n");

        System.out.println("=== AFTER Promotions ===");
        System.out.println(basket);
    }
}
