package com.example.wwartService.payments.paymentsMetods;

import com.example.wwartService.model.Invoice;
import com.example.wwartService.model.Order;
import com.example.wwartService.payments.Payment;
import com.example.wwartService.payments.PaymentResult;
import com.example.wwartService.payments.PaymentStatus;

public class BlikPayment implements Payment {

    private static final double FEE_RATE = 0.015;

    @Override
    public PaymentResult pay(final Invoice invoice) {
        final double baseAmount = invoice.getOrder()
                                         .stream()
                                         .mapToDouble(Order::getPriceWithVATToPay)
                                         .sum();

        final double fee = round(baseAmount * FEE_RATE);
        final double total = round(baseAmount + fee);

        final String message = "BLIK payment processed. Fee: " + fee;
        return new PaymentResult(PaymentStatus.SUCCESS, total, fee, message);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
