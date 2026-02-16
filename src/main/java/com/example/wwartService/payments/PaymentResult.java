package com.example.wwartService.payments;

public class PaymentResult {
    private final PaymentStatus status;
    private final double amountCharged;
    private final double fee;
    private final String message;

    public PaymentResult(final PaymentStatus status,
                         final double amountCharged,
                         final double fee,
                         String message) {
        this.status = status;
        this.amountCharged = amountCharged;
        this.fee = fee;
        this.message = message;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public double getAmountCharged() {
        return amountCharged;
    }

    public double getFee() {
        return fee;
    }

    public String getMessage() {
        return message;
    }
}
