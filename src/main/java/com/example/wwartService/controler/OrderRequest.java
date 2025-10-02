package com.example.wwartService.controler;

public class OrderRequest {
    private Long id;
    private String name;
    private String description;
    private String category;
    private Integer quantity;
    private Double price;
    private Double originalPrice;
    private Double itemTotal;
    private String size;
    private String color;

    public OrderRequest() {}

    public OrderRequest(final Long id,
                        final String name,
                        final String description,
                        final String category,
                        final Integer quantity,
                        final Double price,
                        final Double originalPrice,
                        final Double itemTotal,
                        final String size,
                        final String color) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
        this.originalPrice = originalPrice;
        this.itemTotal = itemTotal;
        this.size = size;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getPrice() {
        return price;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public Double getItemTotal() {
        return itemTotal;
    }

    public String getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }
}
