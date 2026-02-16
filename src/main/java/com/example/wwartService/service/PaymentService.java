package com.example.wwartService.service;

import com.example.wwartService.model.Invoice;
import com.example.wwartService.payments.Payment;
import com.example.wwartService.payments.PaymentFactory;
import com.example.wwartService.payments.PaymentResult;
import com.example.wwartService.payments.PaymentType;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentFactory paymentFactory = new PaymentFactory();

    public PaymentResult processPayment(final Invoice invoice,
                                        final PaymentType paymentType) {
        final Payment payment = paymentFactory.create(paymentType);
        return payment.pay(invoice);
    }
}
