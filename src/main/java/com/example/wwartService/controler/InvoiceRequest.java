package com.example.wwartService.controler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InvoiceRequest {

    private String buyerName;
    private String buyerAddressEmail;
    private String buyerAddress;
    private String buyerNip;
    private String buyerPhone;
    private Boolean acceptedTerms;
    private Boolean shouldSendPDF;
    private Map<String, Object> browserInfo;
    private List<OrderRequest> orders;
    private OrderSummary orderSummary;

    public InvoiceRequest() {}

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(final String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerAddressEmail() {
        return buyerAddressEmail;
    }

    public void setBuyerAddressEmail(final String buyerAddressEmail) {
        this.buyerAddressEmail = buyerAddressEmail;
    }

    public String getBuyerAddress() {
        return buyerAddress;
    }

    public void setBuyerAddress(final String buyerAddress) {
        this.buyerAddress = buyerAddress;
    }

    public String getBuyerNip() {
        return buyerNip;
    }

    public void setBuyerNip(final String buyerNip) {
        this.buyerNip = buyerNip;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(final String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public Boolean getAcceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(final Boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
    }

    public Boolean getShouldSendPDF() {
        return shouldSendPDF;
    }

    public void setShouldSendPDF(final Boolean shouldSendPDF) {
        this.shouldSendPDF = shouldSendPDF;
    }

    public Map<String, Object> getBrowserInfo() {
        return browserInfo;
    }

    public List<OrderRequest> getOrders() {
        return orders;
    }

    public void setOrders(final List<OrderRequest> orders) {
        this.orders = orders;
    }

    public OrderSummary getOrderSummary() {
        return orderSummary;
    }
}
