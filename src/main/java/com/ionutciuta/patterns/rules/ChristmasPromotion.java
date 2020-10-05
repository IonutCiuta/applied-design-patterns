package com.ionutciuta.patterns.rules;

import com.ionutciuta.patterns.model.Basket;
import com.ionutciuta.patterns.model.Item;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;

@Component
public class ChristmasPromotion implements IRule {
    @Override
    public boolean matches(Basket basket) {
        return Month.DECEMBER == LocalDateTime.now().getMonth() &&
                Month.DECEMBER == basket.getCreated().getMonth() &&
                20 < basket.getCreated().getDayOfMonth();
    }

    @Override
    public void apply(Basket basket) {
        System.out.println("Applying " + this.getClass().getSimpleName() + " promotion -10.00");
        basket.addItem(new Item("Promo - Santa Hat", 0.00));
        basket.decreaseTotal(10.00);
    }
}
