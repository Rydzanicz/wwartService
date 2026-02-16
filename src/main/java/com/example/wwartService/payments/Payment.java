package com.example.wwartService.payments;

import com.example.wwartService.model.Invoice;

public interface Payment {
    PaymentResult pay(Invoice invoice);
}
