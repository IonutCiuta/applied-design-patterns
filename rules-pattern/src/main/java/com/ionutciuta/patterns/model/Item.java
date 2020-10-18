package com.ionutciuta.patterns.model;

public class Item {
    public String name;
    public Double price;

    public Item(String name, Double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Name: " + name + "\nPrice: " + price + "\n---";
    }
}
