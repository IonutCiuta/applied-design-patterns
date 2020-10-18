### Rules Pattern ###

On multiple occasions I was tasked with checking input data - e.g. an incoming HTTP request - against multiple rules.
Most of the time, my instinct would say "Just drag that thing through some if statements".


However, after doing this a couple of times, I realised that here has to be a better way of doing this.
Managing a big if-else block is not fun especially when the number of conditions you check is dynamic.
And I don't even want to talk about using a switch statement.


So I ended up doing a bit of studying and came across the Rule Pattern and I thought I'd share this with you.
All of the code that you'll see is hosted [here](https://github.com/IonutCiuta/patterns).

#### The problem ####

You have a basket of items and you need to apply various pricing rules or promotions.

e.g. Halloween 🎃 is just around the corner but... in a way, so is Christmas 🎅🏼.
However, you'd like to only apply the Halloween promotion.

```java
// Basket has list of items, total and the date it was created
// An item has a name and a price

Basket basket = new Basket();
basket.addItem(new Item("The Shinning Book", 5.0));
basket.addItem(new Item("Hereditary Bluray", 15.00));
basket.addItem(new Item("The extremely expensive new Iphone", 1400.00));
```

How would you go about this?

Clearly, the easiest way is to just drag the basket through a nasty if-else block or even - God forbid - a swich 👻

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

It's also resilient to change. If you need a new promotion you'll have to fit it in. Also, good luck testing this.

One way I like to decompose this is by using **rules**. I've used this approach on many projects I've worked in and it proved to be a very _flexible_ way of _decomposing large decision logic_.

#### The solution ####
We first need to define what a rule is. A rule should contain the logic that dictates whether or not it can be applied to a target object and the logic that it should apply.
```java
public interface IRule {
    boolean matches(Basket basket);
    void apply(Basket basket);
}
```

Now, we can set up multiple rules using this template. Here are some examples:
```java
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
```

Or, a more simple one:
```java
@Component
public class DiscountForBigOrdersPromotion implements IRule {
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

Note that both of them are annotated with `@Component`. This example works super nicely with Spring, but you can apply it just as well for plain Java.

We now have some rules and we can very easily apply them to the basket above through some Spring magic and plain old polymorphism.
```java
// This neatly puts all our IRule's together
@Autowired
private List<IRule> promotionRules;
```

Now we just need the logic to apply them which again is super smooth.
```java
promotionRules.stream()
    .filter(rule -> rule.matches(basket))
    .forEach(rule -> rule.apply(basket));
```

If you run my example, you'll see the following output:
```
=== BEFORE Promotions ===
Basket:
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

=== AFTER Promotions ===
Basket:
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

Notice that because you used the rule mini-engine, you got a free skeleton suit for your socially distant Halloween party 😉💀

"Ok, but isn't this more complicated?" You might ask. Well, not really. Here's why:
* you can easily add new rules or remove old ones
* the rule checking logic and the rule specific logic are decoupled
* easy to test
* code is cleaner and more flexibile

#### Related reads ####

After I posted this, a colleague of mine mentioned that there might be a connection between the Rules Pattern and the Chain of Responsibility (CoR) pattern.
While I've read about CoR on other occasions, I felt the need looked up some implementation examples and I stumbled across [this really great article](https://refactoring.guru/design-patterns/chain-of-responsibility).
I highly recommend you give it a try. I would say that the Rule Pattern is a specialisation of the CoR pattern with some caveats.
In the version I described, rules don't care about ordering and are applied exhaustively meaning the input is processed by all the rules.

Don't forget to take a look at the code [here](https://github.com/IonutCiuta/patterns). Thanks for reading!