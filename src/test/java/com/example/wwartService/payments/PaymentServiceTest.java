package com.example.wwartService.payments;

import com.example.wwartService.model.Invoice;
import com.example.wwartService.model.Order;
import com.example.wwartService.payments.paymentsMetods.BankTransferPayment;
import com.example.wwartService.payments.paymentsMetods.BlikPayment;
import com.example.wwartService.payments.paymentsMetods.CardPayment;
import com.example.wwartService.payments.paymentsMetods.PaypalPayment;
import com.example.wwartService.service.PaymentService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentServiceTest {

    private Order createSampleOrder(final double priceWithVATPerItem,
                                    final int quantity) {
        return new Order.Builder().name("Produkt A")
                                  .description("Opis A")
                                  .quantity(quantity)
                                  .priceWithVAT(priceWithVATPerItem)
                                  .build();
    }

    private Invoice createSampleInvoice(final double priceWithVATPerItem,
                                        final int quantity) {
        final Order order = createSampleOrder(priceWithVATPerItem, quantity);
        return new Invoice.Builder().invoiceNumber(1)
                                    .buyerName("Jan Kowalski")
                                    .buyerAddress("Adres 1")
                                    .buyerAddressEmail("jan@example.com")
                                    .buyerPhone("123456789")
                                    .orderDate(LocalDateTime.now())
                                    .order(List.of(order))
                                    .build();
    }

    @Test
    void paymentServiceShouldUseFactoryAndReturnResultForBankTransfer() {
        // given
        final Invoice invoice = createSampleInvoice(100.0, 2);
        final PaymentService paymentService = new PaymentService();

        // when
        final PaymentResult result = paymentService.processPayment(invoice,
                                                                   PaymentType.BANK_TRANSFER);

        // then
        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        assertEquals(2.0, result.getFee(), 0.001);
        assertEquals(202.0, result.getAmountCharged(), 0.001);
        assertTrue(result.getMessage().contains("Bank transfer"));
    }

    @Test
    void paymentServiceShouldUseFactoryAndReturnResultForCard() {
        // given
        final Invoice invoice = createSampleInvoice(100.0, 2);
        final PaymentService paymentService = new PaymentService();

        // when
        final PaymentResult result = paymentService.processPayment(invoice, PaymentType.CARD);

        // then
        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        assertEquals(5.0, result.getFee(), 0.001);
        assertEquals(205.0, result.getAmountCharged(), 0.001);
        assertTrue(result.getMessage().contains("Card"));
    }

    @Test
    void paymentServiceShouldUseFactoryAndReturnResultForBlik() {
        // given
        final Invoice invoice = createSampleInvoice(100.0, 2);
        final PaymentService paymentService = new PaymentService();

        // when
        final PaymentResult result = paymentService.processPayment(invoice, PaymentType.BLIK);

        // then
        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        assertEquals(3.0, result.getFee(), 0.001);
        assertEquals(203.0, result.getAmountCharged(), 0.001);
        assertTrue(result.getMessage().contains("BLIK"));
    }

    @Test
    void paymentServiceShouldUseFactoryAndReturnResultForPaypal() {
        // given
        final Invoice invoice = createSampleInvoice(100.0, 2);
        final PaymentService paymentService = new PaymentService();

        // when
        final PaymentResult result = paymentService.processPayment(invoice, PaymentType.PAYPAL);

        // then
        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        assertEquals(6.0, result.getFee(), 0.001);
        assertEquals(206.0, result.getAmountCharged(), 0.001);
        assertTrue(result.getMessage().contains("PayPal"));
    }

    @Test
    void paymentServiceShouldReturnDifferentFeesForDifferentPaymentTypes() {
        // given
        final Invoice invoice = createSampleInvoice(100.0, 2);
        final PaymentService paymentService = new PaymentService();

        // when
        final PaymentResult bankResult = paymentService.processPayment(invoice,
                                                                       PaymentType.BANK_TRANSFER);
        final PaymentResult cardResult = paymentService.processPayment(invoice, PaymentType.CARD);
        final PaymentResult blikResult = paymentService.processPayment(invoice, PaymentType.BLIK);
        final PaymentResult paypalResult = paymentService.processPayment(invoice,
                                                                         PaymentType.PAYPAL);

        // then
        assertTrue(bankResult.getFee() < cardResult.getFee());
        assertTrue(bankResult.getFee() < blikResult.getFee());
        assertTrue(paypalResult.getFee() >= cardResult.getFee());
    }


    @Test
    void factoryShouldCreateBankTransferPayment() {
        // given
        final PaymentFactory factory = new PaymentFactory();

        // when
        final Payment payment = factory.create(PaymentType.BANK_TRANSFER);

        // then
        assertNotNull(payment);
        assertInstanceOf(BankTransferPayment.class, payment);
    }

    @Test
    void factoryShouldCreateCardPayment() {
        // given
        final PaymentFactory factory = new PaymentFactory();

        // when
        final Payment payment = factory.create(PaymentType.CARD);

        // then
        assertNotNull(payment);
        assertInstanceOf(CardPayment.class, payment);
    }

    @Test
    void factoryShouldCreateBlikPayment() {
        // given
        final PaymentFactory factory = new PaymentFactory();

        // when
        final Payment payment = factory.create(PaymentType.BLIK);

        // then
        assertNotNull(payment);
        assertInstanceOf(BlikPayment.class, payment);
    }

    @Test
    void factoryShouldCreatePaypalPayment() {
        // given
        final PaymentFactory factory = new PaymentFactory();

        // when
        final Payment payment = factory.create(PaymentType.PAYPAL);

        // then
        assertNotNull(payment);
        assertInstanceOf(PaypalPayment.class, payment);
    }
}
