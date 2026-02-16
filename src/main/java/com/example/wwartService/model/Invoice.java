package com.example.wwartService.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Invoice {
    private static final String INVOICE_ID_PATTERN = "^FV/(\\d{4})/(\\d{2})/(\\d{4})$";
    private static final String FORMAT = "FV/%04d/%02d/%d";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String buyerName;
    private final String buyerAddress;
    private final String buyerAddressEmail;
    private final String buyerNIP;
    private final String buyerPhone;
    private final LocalDateTime orderDate;
    private final boolean isEmailSend;
    private final boolean shouldSendPDF;
    private final List<Order> order;
    private String invoiceId;

    private Invoice(Builder builder) {

        validate(builder);
        this.invoiceId = generateInvoiceId(builder.invoiceNumber, builder.orderDate);
        this.buyerName = builder.buyerName;
        this.buyerAddress = builder.buyerAddress;
        this.buyerAddressEmail = builder.buyerAddressEmail;
        this.buyerNIP = builder.buyerNIP;
        this.buyerPhone = builder.buyerPhone;
        this.orderDate = builder.orderDate;
        this.isEmailSend = builder.isEmailSend;
        this.shouldSendPDF = builder.shouldSendPDF;
        this.order = builder.order;
    }

    public Invoice(final InvoiceEntity invoice) {
        validateInvoiceId(invoice.getInvoiceId());
        this.invoiceId = invoice.getInvoiceId();
        this.buyerName = invoice.getName();
        this.buyerAddress = invoice.getAddress();
        this.buyerAddressEmail = invoice.getEmail();
        this.buyerNIP = invoice.getNip();
        this.buyerPhone = invoice.getPhone();
        this.orderDate = LocalDateTime.parse(invoice.getOrderDate(), formatter);
        this.isEmailSend = invoice.isEmailSend();
        this.shouldSendPDF = invoice.isShouldSendPDF();
        this.order = invoice.getOrders()
                            .stream()
                            .map(Order::new)
                            .toList();
    }

    public Invoice() {
        this.invoiceId = generateInvoiceId(1, LocalDateTime.now());
        this.buyerName = null;
        this.buyerAddress = null;
        this.buyerAddressEmail = null;
        this.buyerNIP = null;
        this.buyerPhone = null;
        this.orderDate = LocalDateTime.now();
        this.isEmailSend = true;
        this.shouldSendPDF = false;
        this.order = new ArrayList<>();
    }

    public static String generateInvoiceId(final int invoiceNumber,
                                           final LocalDateTime orderDate) {
        if (invoiceNumber <= 0) {
            throw new IllegalArgumentException("Invoice ID cannot be 0 or less than 0.");
        }
        final int month = orderDate.getMonthValue();
        final int year = orderDate.getYear();

        return String.format(FORMAT, invoiceNumber, month, year);
    }

    public static boolean validateInvoiceId(final String invoiceId) {
        if (invoiceId == null || invoiceId.isEmpty()) {
            throw new IllegalArgumentException("InvoiceId cannot be null.");
        }

        if (!Pattern.matches(INVOICE_ID_PATTERN, invoiceId)) {
            throw new IllegalArgumentException(
                    "Invalid Invoice ID format. Correct format: FV/{number}/{month}/{year}, e.g., FV/0001/01/2024");
        }
        return true;
    }

    private void validate(final Builder builder) {
        if (builder.buyerName == null || builder.buyerName.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        if (builder.buyerAddress == null || builder.buyerAddress.isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty.");
        }
        if (builder.buyerAddressEmail == null || builder.buyerAddressEmail.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }
        if (builder.buyerPhone == null || builder.buyerPhone.isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be null or empty.");
        }
        if (builder.orderDate == null) {
            throw new IllegalArgumentException("Order date cannot be null or empty.");
        }
        if (builder.order == null || builder.order.isEmpty()) {
            throw new IllegalArgumentException("List of Order cannot be null or empty.");
        }
        if (builder.invoiceNumber <= 0) {
            throw new IllegalArgumentException("Invoice ID cannot be 0 or less than 0.");
        }
    }

    private int extractInvoiceNumber() {
        final Pattern pattern = Pattern.compile(INVOICE_ID_PATTERN);
        final Matcher matcher = pattern.matcher(invoiceId);

        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            throw new IllegalArgumentException(
                    "Invalid Invoice ID format. Cannot extract invoice number.");
        }
    }

    public int extractAndIncreaseInvoiceNumber() {
        return extractInvoiceNumber() + 1;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(final String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getBuyerAddress() {
        return buyerAddress;
    }

    public String getBuyerAddressEmail() {
        return buyerAddressEmail;
    }

    public List<Order> getOrder() {
        return order;
    }

    public String getBuyerNIP() {
        return buyerNIP;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public boolean isEmailSend() {
        return isEmailSend;
    }

    public boolean isShouldSendPDF() {
        return shouldSendPDF;
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }

    public static class Builder {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "yyyy-MM-dd HH:mm:ss");
        private int invoiceNumber;
        private String buyerName;
        private String buyerAddress;
        private String buyerAddressEmail;
        private String buyerNIP;
        private String buyerPhone;
        private LocalDateTime orderDate;
        private boolean isEmailSend;
        private boolean shouldSendPDF;
        private List<Order> order;

        public Builder invoiceNumber(int invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
            return this;
        }

        public Builder buyerName(String buyerName) {
            this.buyerName = buyerName;
            return this;
        }

        public Builder buyerAddress(String buyerAddress) {
            this.buyerAddress = buyerAddress;
            return this;
        }

        public Builder buyerAddressEmail(String buyerAddressEmail) {
            this.buyerAddressEmail = buyerAddressEmail;
            return this;
        }

        public Builder buyerNIP(String buyerNIP) {
            this.buyerNIP = buyerNIP;
            return this;
        }

        public Builder buyerPhone(String buyerPhone) {
            this.buyerPhone = buyerPhone;
            return this;
        }

        public Builder orderDate(LocalDateTime orderDate) {
            this.orderDate = orderDate;
            return this;
        }

        public Builder orderDateString(final String orderDate) {
            this.orderDate = LocalDateTime.parse(orderDate, formatter);
            return this;
        }

        public Builder emailSend(boolean emailSend) {
            this.isEmailSend = emailSend;
            return this;
        }

        public Builder shouldSendPDF(boolean shouldSendPDF) {
            this.shouldSendPDF = shouldSendPDF;
            return this;
        }

        public Builder order(List<Order> order) {
            this.order = order;
            return this;
        }

        public Invoice build() {
            return new Invoice(this);
        }
    }
}
