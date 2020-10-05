package com.ionutciuta.patterns.rules;

import com.ionutciuta.patterns.model.Basket;
import org.springframework.stereotype.Component;

public interface IRule {
    boolean matches(Basket basket);
    void apply(Basket basket);
}
