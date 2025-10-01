package com.example.wwartService.model;

public class Order {
    private final String name;
    private final String description;
    private final int quantity;
    private final double price;
    private final double priceWithVAT;
    private final double vatRate = 0.55;


    public Order(final String name, final String description, final int quantity, final double priceWithVAT) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty.");
        }
        if (quantity == 0) {
            throw new IllegalArgumentException("Quantity cannot be zero.");
        }
        if (priceWithVAT == 0) {
            throw new IllegalArgumentException("Price date cannot be zero.");
        }
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.priceWithVAT = priceWithVAT;
        this.price = priceWithVAT / (1 + this.vatRate);
    }

    public Order(final OrderEntity order) {
        this.name = order.getProductName();
        this.description = order.getProductDescription();
        this.quantity = order.getQuantity();
        this.priceWithVAT = order.getPrice();
        this.price = priceWithVAT / (1 + this.vatRate);
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getPriceWithVAT() {
        return priceWithVAT;
    }

    public double getVAT() {
        return priceWithVAT - price;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
