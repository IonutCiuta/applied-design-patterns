package com.ionutciuta.patterns.rules;

import com.ionutciuta.patterns.model.Basket;
import com.ionutciuta.patterns.model.Item;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;

@Component
public class HalloweenPromotion implements IRule {

    @Override
    public boolean matches(Basket basket) {
        return Month.OCTOBER == LocalDateTime.now().getMonth() &&
                Month.OCTOBER == basket.getCreated().getMonth();
    }

    @Override
    public void apply(Basket basket) {
        System.out.println("Applying " + this.getClass().getSimpleName() + " promotion -13.00");
        basket.addItem(new Item("Promo - Creepy Skeleton Suit", 0.00));
        basket.decreaseTotal(13.00);
    }
}
