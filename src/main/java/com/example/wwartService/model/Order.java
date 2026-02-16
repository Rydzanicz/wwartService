package com.example.wwartService.model;

public class Order {
    private final String name;
    private final String description;
    private final int quantity;
    private final double price;
    private final double priceWithVAT;
    private final double vatRate = 0.55;

    private Order(final Builder builder) {
        validate(builder);
        this.name = builder.name;
        this.description = builder.description;
        this.quantity = builder.quantity;
        this.priceWithVAT = builder.priceWithVAT;
        this.price = this.priceWithVAT / (1 + this.vatRate);
    }

    public Order(final OrderEntity order) {
        this(new Builder().name(order.getProductName())
                          .description(order.getProductDescription())
                          .quantity(order.getQuantity())
                          .priceWithVAT(order.getPrice()));
    }

    private void validate(final Builder builder) {
        if (builder.name == null || builder.name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        if (builder.description == null || builder.description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty.");
        }
        if (builder.quantity == 0) {
            throw new IllegalArgumentException("Quantity cannot be zero.");
        }
        if (builder.priceWithVAT == 0) {
            throw new IllegalArgumentException("Price date cannot be zero.");
        }
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

    public static class Builder {
        private String name;
        private String description;
        private int quantity;
        private double priceWithVAT;

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder description(final String description) {
            this.description = description;
            return this;
        }

        public Builder quantity(final int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder priceWithVAT(final double priceWithVAT) {
            this.priceWithVAT = priceWithVAT;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
