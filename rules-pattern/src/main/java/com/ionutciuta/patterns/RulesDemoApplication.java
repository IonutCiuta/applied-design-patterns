package com.ionutciuta.patterns;

import com.ionutciuta.patterns.model.Basket;
import com.ionutciuta.patterns.model.Item;
import com.ionutciuta.patterns.rules.impl.PromotionEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RulesDemoApplication implements CommandLineRunner {

	@Autowired
	private PromotionEngine promotionEngine;

	public static void main(String[] args) {
		SpringApplication.run(RulesDemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Basket basket = new Basket();
		basket.addItem(new Item("The Shinning Book", 5.0));
		basket.addItem(new Item("Hereditary Bluray", 15.00));
		basket.addItem(new Item("The extremely expensive new Iphone", 1400.00));

		promotionEngine.process(basket);
	}
}
