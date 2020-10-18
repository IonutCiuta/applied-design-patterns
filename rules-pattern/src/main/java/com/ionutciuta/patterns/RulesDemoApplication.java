package com.ionutciuta.patterns;

import com.ionutciuta.patterns.model.Basket;
import com.ionutciuta.patterns.model.Item;
import com.ionutciuta.patterns.rules.IRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class RulesDemoApplication implements CommandLineRunner {
	@Autowired
	private List<IRule> promotionRules;

	public static void main(String[] args) {
		SpringApplication.run(RulesDemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Basket basket = new Basket();
		basket.addItem(new Item("The Shinning Book", 5.0));
		basket.addItem(new Item("Hereditary Bluray", 15.00));
		basket.addItem(new Item("The extremely expensive new Iphone", 1400.00));

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
