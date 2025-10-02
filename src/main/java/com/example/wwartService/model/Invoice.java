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

    private final String buyerName;
    private final String buyerAddress;
    private final String buyerAddressEmail;
    private final String buyerNIP;
    private final String buyerPhone;
    private final LocalDateTime orderDate;
    private final boolean isEmailSend;
    private final List<Order> order;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private String invoiceId;

    public Invoice(final int invoiceNR,
                   final String buyerName,
                   final String buyerAddress,
                   final String buyerAddressEmail,
                   final String buyerNIP,
                   final String buyerPhone,
                   final LocalDateTime orderDate,
                   final boolean isEmailSend,
                   final List<Order> order) {
        if (buyerName == null || buyerName.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        if (buyerAddress == null || buyerAddress.isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty.");
        }
        if (buyerAddressEmail == null || buyerAddressEmail.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }
        if (buyerPhone == null || buyerPhone.isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be null or empty.");
        }
        if (orderDate == null) {
            throw new IllegalArgumentException("Order date cannot be null or empty.");
        }
        if (order.isEmpty()) {
            throw new IllegalArgumentException("List of Order cannot be null or empty.");
        }

        this.invoiceId = generateInvoiceId(invoiceNR, orderDate);
        this.buyerName = buyerName;
        this.buyerAddress = buyerAddress;
        this.buyerAddressEmail = buyerAddressEmail;
        this.buyerNIP = buyerNIP;
        this.buyerPhone = buyerPhone;
        this.orderDate = orderDate;
        this.isEmailSend = isEmailSend;
        this.order = order;
    }

    public Invoice(final int invoiceNR,
                   final String buyerName,
                   final String buyerAddress,
                   final String buyerAddressEmail,
                   final String buyerNIP,
                   final String buyerPhone,
                   final String orderDate,
                   final boolean isEmailSend,
                   final List<Order> order) {
        if (buyerName == null || buyerName.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        if (buyerAddress == null || buyerAddress.isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty.");
        }
        if (buyerAddressEmail == null || buyerAddressEmail.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }
        if (buyerPhone == null || buyerPhone.isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be null or empty.");
        }
        if (orderDate == null || orderDate.equals("")) {
            throw new IllegalArgumentException("Order date cannot be null or empty.");
        }
        if (order.isEmpty()) {
            throw new IllegalArgumentException("List of Order cannot be null or empty.");
        }

        this.invoiceId = generateInvoiceId(invoiceNR, LocalDateTime.parse(orderDate, formatter));
        this.buyerName = buyerName;
        this.buyerAddress = buyerAddress;
        this.buyerAddressEmail = buyerAddressEmail;
        this.buyerNIP = buyerNIP;
        this.buyerPhone = buyerPhone;
        this.orderDate = LocalDateTime.parse(orderDate, formatter);
        this.isEmailSend = isEmailSend;
        this.order = order;
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
        this.order = new ArrayList<>();
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
        this.order = invoice.getOrders()
                            .stream()
                            .map(Order::new)
                            .toList();
    }

    public static String generateInvoiceId(final int invoiceNumber, final LocalDateTime orderDate) {
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

    public DateTimeFormatter getFormatter() {
        return formatter;
    }
}
