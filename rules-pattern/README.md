### Rules Pattern ###

A few weeks back, someone tagged in a company wide "Good Practices" challenge and I ended up writing a long post on Teams.
I enjoyed doing that quite a lot and I decided to share it here as well, in a more detailed format. Here's what I came up with.

On multiple occasions in my career I was tasked with checking some input data - e.g. an incoming HTTP request - against multiple business rules.
Most of the time, my instinct would say "Just drag that thing through some if statements".

However, after doing this a couple of times, I realised that here has to be a better way of implementing this.
Managing a big if-else block is not fun especially when the number of conditions you have to check is dynamic.
Also, I'm not very keen on using switch statements for many of the same reasons.


In my quest to become a more productive programmer, I ended up doing a bit of studying and came across the Rule Pattern.
All of the code that you'll see bellow is hosted [here](https://github.com/IonutCiuta/patterns).
It's mostly Java with a few Spring annotations on top of it.

#### The problem ####

Say you have on online shop. You want your shop to be smart so that you can give your customers contextual offers
based on the content of their basket and other variables such as the date of the order.

e.g. It's October, Halloween season is in full effect 🎃 and after that Christmas is just around the corner 🎅🏼.
We want to add promotions for both holidays in our shop, however we'd like to apply them accordingly. How would you go about this?

Let's say we have this basket.

```java
// Basket has list of items, total and the date it was created
// An item has a name and a price

Basket basket = new Basket();
basket.addItem(new Item("The Shinning Book", 5.0));
basket.addItem(new Item("Hereditary Bluray", 15.00));
basket.addItem(new Item("The extremely expensive new Iphone", 1400.00));
```

Clearly, the easiest way to check what promotions to apply is to just drag the basket through a nasty if-else block or even - God forbid - a switch 👻

```java
class PromoApplier {
    public void applyPromos(Basket basket) {
        // Check if we can apply promo for big spenders
        if(basket.getTotal() > 1000.00) {
            basket.decreaseTotal(100.00);
        }
        // Check if we can apply Halloween promo
        if(Month.OCTOBER == LocalDateTime.now().getMonth() &&
                Month.OCTOBER == basket.getCreated().getMonth()) {
            basket.addItem(new Item("Promo - Creepy Skeleton Suit", 0.00));
            basket.decreaseTotal(13.00);
        }
        // Check if we can apply Xmas promo
        if(Month.DECEMBER == LocalDateTime.now().getMonth() &&
                Month.DECEMBER == basket.getCreated().getMonth() &&
                20 < basket.getCreated().getDayOfMonth()) {
            basket.addItem(new Item("Promo - Santa Hat", 0.00));
            basket.decreaseTotal(10.00);
        }
    }
}
```

Yes, the code above is _awful_. It breaks many common sense rules.
It's dense, does too many things and knows about too many business rules.

It's also resilient to change. If you need to add a new promotion you'll have to fit it in. Also, good luck testing this.

The way I like to design this is by using **rules**.
After I read about it, I used this approach on several projects it proved to be a _flexible_ way of _decomposing large decision logic_.

#### The solution ####
We first need to define what a rule is. A rule should contain the logic that dictates whether or not it can be applied to a target object and the logic that it should apply.
```java
public interface IRule<E> {
    boolean matches(E input);
    void apply(E input);
}
```

The rule above is a bit too generic, E is not an actually type. Let's make it a bit more specific.
```java
public interface BasketRule extends IRule<Basket> {
}
```

Based on this blueprint, we can set up actual rules that apply concrete logic to our basket. Here is an example:
```java
@Component
public class HalloweenPromotion implements BasketRule {

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
```
Our `HalloweenPromotion` gives all of our customers a free skeleton suit 💀 and a 13 (dollars?) discount based on the logic in the `apply` function.
That's a pretty neat deal if you ask me.
However, as per the `matches` function, this is only applicable if the order was created in October and it's still in October when rule is evaluated.

Let's add a simpler rule:
```java
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
```
This rule is straightforward. If your order is bigger than 1000, you get a discount of 100.
Let's add a 3rd rule that hopefully won't be applied during Halloween season.

```java
@Component
public class ChristmasPromotion implements BasketRule {
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
```
This last rule would give you a free Santa hat 🎅🏻 and a small discount if your order was placed near Christmas.

Notice that all the rules described above are annotated with Spring's `@Component`. This example works neatly with Spring, but you can apply it just as well for plain Java.
```java
// This neatly puts all our BasketRules together
@Autowired
private List<BasketRule> promotionRules;
```

We now have some rules and we can very easily check them against our initial basket through some Spring magic and plain old polymorphism.
We just need the logic to apply them which again is super smooth.
```java
promotionRules.stream()
    .filter(rule -> rule.matches(basket))
    .forEach(rule -> rule.apply(basket));
```

For the sake of coherence, I've put these two pieces of code in the `PromotionEngine` class.

If you run my demo code, you'll see the following output:
```
=== BEFORE Promotions ===
Basket:
Created: 2020-10-18T21:58:50.760277
Name: The Shinning Book
Price: 5.0
---
Name: Hereditary Bluray
Price: 15.0
---
Name: The extremely expensive new Iphone
Price: 1400.0
---
========
Total: 1420.0

Applying promotions
Applying DiscountForBigOrdersPromotion promotion -100.00
Applying HalloweenPromotion promotion -13.00
Promotions applies

=== AFTER Promotions ===
Basket:
Created: 2020-10-18T21:58:50.760277
Name: The Shinning Book
Price: 5.0
---
Name: Hereditary Bluray
Price: 15.0
---
Name: The extremely expensive new Iphone
Price: 1400.0
---
Name: Promo - Creepy Skeleton Suit
Price: 0.0
---
========
Total: 1307.0
```

Let's take a look at what just happened:
* your basket has total value of **1420.0** and was created in **October**.
* two promotions were applied: DiscountForBigOrdersPromotion that gave us -100 discount and HalloweenPromotion that have us a -13 discount
* most importantly, you get that free Creepy Skeleton Suit for your socially distant Halloween party 😉💀


"Ok, but isn't this more complicated?" You might ask. Well, not really. Here's why:
* you can easily add new rules or remove old ones
* the rule checking logic and the rule specific logic are decoupled
* easy to test
* code is cleaner and more flexibile

#### Related reads ####

After I posted this, a colleague of mine mentioned that there might be a connection between the Rules Pattern and the Chain of Responsibility (CoR) pattern.
While I've read about CoR on other occasions, I felt the need to look up some implementation examples and I stumbled across [this thorough article](https://refactoring.guru/design-patterns/chain-of-responsibility).
I highly recommend you give it a try. I would say that the Rules Pattern is a specialisation of the CoR pattern with some caveats.
In the version I described, rules don't care about ordering and are applied exhaustively meaning the input is processed by all the rules.

Don't forget to take a look at the code [here](https://github.com/IonutCiuta/patterns). Thanks for reading!