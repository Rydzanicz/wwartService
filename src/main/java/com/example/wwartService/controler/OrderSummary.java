package com.example.wwartService.controler;

public class OrderSummary {

    private Integer totalItems;
    private Integer uniqueProducts;
    private Boolean hasPersonalizedItems;
    private Double averageItemPrice;
    private String shippingMethod;
    private String paymentMethod;

    public OrderSummary() {}

    public Integer getTotalItems() {
        return totalItems;
    }

    public Integer getUniqueProducts() {
        return uniqueProducts;
    }

    public Boolean getHasPersonalizedItems() {
        return hasPersonalizedItems;
    }

    public Double getAverageItemPrice() {
        return averageItemPrice;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}
