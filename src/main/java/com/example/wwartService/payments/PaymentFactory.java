package com.example.wwartService.payments;

import com.example.wwartService.payments.paymentsMetods.BankTransferPayment;
import com.example.wwartService.payments.paymentsMetods.BlikPayment;
import com.example.wwartService.payments.paymentsMetods.CardPayment;
import com.example.wwartService.payments.paymentsMetods.PaypalPayment;

public class PaymentFactory {

    public Payment create(final PaymentType type) {
        return switch (type) {
            case BANK_TRANSFER -> new BankTransferPayment();
            case CARD          -> new CardPayment();
            case BLIK          -> new BlikPayment();
            case PAYPAL        -> new PaypalPayment();
        };
    }
}
