package com.ionutciuta.patterns.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Basket {
    public List<Item> items;

    private Double total;

    private final LocalDateTime created;

    public Basket() {
        this.items = new ArrayList<>();
        this.total = 0.0;
        this.created = LocalDateTime.now();
    }

    public void addItem(Item item) {
        this.items.add(item);
        this.total += item.price;
    }

    public void decreaseTotal(Double amount) {
        this.total -= amount;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public Double getTotal() {
        return total;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Basket:\n");
        items.forEach(item -> sb.append(item.toString()).append("\n"));
        sb.append("========").append("\n");
        sb.append("Total: ").append(total);
        return sb.toString();
    }
}
