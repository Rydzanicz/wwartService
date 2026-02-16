package com.example.wwartService.controller;

import com.example.wwartService.controler.InvoiceController;
import com.example.wwartService.controler.InvoiceRequest;
import com.example.wwartService.controler.MailRequest;
import com.example.wwartService.controler.OrderRequest;
import com.example.wwartService.model.Invoice;
import com.example.wwartService.model.Order;
import com.example.wwartService.service.EmailService;
import com.example.wwartService.service.InvoiceService;
import com.example.wwartService.service.PdfGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InvoiceControllerTest {

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private PdfGeneratorService pdfGeneratorService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private InvoiceController invoiceController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveInvoiceSuccess() {
        // given
        final List<Order> orders = List.of(new Order.Builder().name("Product")
                                                              .description("Description")
                                                              .quantity(2)
                                                              .priceWithVAT(200.0)
                                                              .build());

        final InvoiceRequest validRequest = new InvoiceRequest();
        validRequest.setBuyerName("Test Buyer");
        validRequest.setBuyerAddress("Test Address");
        validRequest.setBuyerAddressEmail("buyer@example.com");
        validRequest.setBuyerNip("1234567890");
        validRequest.setBuyerPhone("123456789");
        validRequest.setOrders(List.of(new OrderRequest(1L,
                                                        "Product",
                                                        "Description",
                                                        "Category",
                                                        2,
                                                        200.0,
                                                        200.0,
                                                        400.0,
                                                        null,
                                                        null)));
        validRequest.setAcceptedTerms(true);
        validRequest.setShouldSendPDF(true);

        final Invoice lastInvoice = new Invoice.Builder().invoiceNumber(1)
                                                         .buyerName("Last Buyer")
                                                         .buyerAddress("Last Address")
                                                         .buyerAddressEmail("last@example.com")
                                                         .buyerNIP("0987654321")
                                                         .buyerPhone("123456789")
                                                         .orderDate(LocalDateTime.now())
                                                         .order(orders)
                                                         .build();

        when(invoiceService.getLastInvoices()).thenReturn(lastInvoice);

        // when
        final ResponseEntity<String> response = invoiceController.saveInvoice(validRequest);

        // then
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody()
                           .startsWith("Invoice saved successfully"));
        verify(invoiceService, times(1)).getLastInvoices();
        verify(invoiceService, times(1)).saveInvoiceWithOrders(any(Invoice.class), anyList());
    }

    @Test
    void testSaveInvoiceNullRequest() {
        // given
        // when

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                                                () -> invoiceController.saveInvoice(
                                                                        null));

        // then
        assertEquals("Invalid request payload", exception.getMessage());
    }

    @Test
    void testSaveInvoiceMissingBuyerName() {
        // given
        final InvoiceRequest invalidRequest = new InvoiceRequest();
        invalidRequest.setBuyerAddress("Some Address");
        invalidRequest.setOrders(Collections.emptyList());

        // when
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                                                () -> invoiceController.saveInvoice(
                                                                        invalidRequest));

        // then
        assertEquals("Invalid request payload", exception.getMessage());
    }

    @Test
    void testSaveInvoiceMissingOrders() {
        // given
        final InvoiceRequest invalidRequest = new InvoiceRequest();
        invalidRequest.setBuyerName("Test Buyer");
        invalidRequest.setBuyerAddress("Some Address");
        invalidRequest.setBuyerAddressEmail("buyer@example.com");

        // when
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                                                () -> invoiceController.saveInvoice(
                                                                        invalidRequest));

        // then
        assertEquals("Invalid request payload", exception.getMessage());
    }

    @Test
    void testSaveInvoiceInternalServerError() {
        // given
        final InvoiceRequest validRequest = new InvoiceRequest();
        validRequest.setBuyerName("Test Buyer");
        validRequest.setBuyerAddress("Some Address");
        validRequest.setBuyerAddressEmail("buyer@example.com");
        validRequest.setOrders(Collections.singletonList(new OrderRequest(1L,
                                                                          "Product",
                                                                          "Description",
                                                                          "Category",
                                                                          1,
                                                                          100.0,
                                                                          120.0,
                                                                          100.0,
                                                                          "M",
                                                                          "Czerwony")));
        validRequest.setAcceptedTerms(true);
        validRequest.setShouldSendPDF(true);

        when(invoiceService.getLastInvoices()).thenThrow(new RuntimeException("Database error"));

        // when
        final ResponseEntity<String> response = invoiceController.saveInvoice(validRequest);

        // then
        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody()
                           .startsWith("Error saving invoice"));
    }

    @Test
    public void testGenerateInvoiceSuccess() throws IOException {
        // given
        final String invoiceId = "INV/001/2024";

        final LocalDateTime ordersDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);
        final List<Order> orders = List.of(new Order.Builder().name("Produkt A")
                                                              .description("Opis A")
                                                              .quantity(1)
                                                              .priceWithVAT(100.0)
                                                              .build());
        final Invoice mockInvoice = new Invoice.Builder().invoiceNumber(1)
                                                         .buyerName("Jan Kowalski")
                                                         .buyerAddress("Popowicka 68")
                                                         .buyerAddressEmail(
                                                                 "jan.kowalski@example.com")
                                                         .buyerNIP("")
                                                         .buyerPhone("123456789")
                                                         .orderDate(ordersDate)
                                                         .order(orders)
                                                         .build();

        when(invoiceService.getInvoicesByInvoiceId(any())).thenReturn(mockInvoice);

        ByteArrayOutputStream mockOutput = new ByteArrayOutputStream();
        mockOutput.write("Fake PDF content".getBytes(StandardCharsets.UTF_8));
        when(pdfGeneratorService.generateInvoicePdf(mockInvoice)).thenReturn(mockOutput);

        // when
        final ResponseEntity<?> response = invoiceController.generateInvoice(invoiceId);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(invoiceService, times(1)).getInvoicesByInvoiceId(any());
        verify(pdfGeneratorService, times(1)).generateInvoicePdf(mockInvoice);
    }

    @Test
    public void testGetInvoicesByInvoiceId() {
        // given
        final LocalDateTime ordersDate = LocalDateTime.parse("2025-01-01 14:30:00", formatter);
        final List<Order> orders = List.of(new Order.Builder().name("Produkt A")
                                                              .description("Opis A")
                                                              .quantity(1)
                                                              .priceWithVAT(100.0)
                                                              .build());
        final Invoice invoice = new Invoice.Builder().invoiceNumber(1)
                                                     .buyerName("Jan Kowalski")
                                                     .buyerAddress("Popowicka 68")
                                                     .buyerAddressEmail("jan.kowalski@example.com")
                                                     .buyerPhone("123456789")
                                                     .orderDate(ordersDate)
                                                     .order(orders)
                                                     .build();

        when(invoiceService.getInvoicesByInvoiceId("FV/0001/01/2025")).thenReturn(invoice);

        // when
        final ResponseEntity<List<Invoice>> response = invoiceController.getInvoices(
                "FV/0001/01/2025",
                null);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1,
                     response.getBody()
                             .size());
        verify(invoiceService, times(1)).getInvoicesByInvoiceId("FV/0001/01/2025");
    }

    @Test
    void testGenerateInvoiceNullId() {
        // given
        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                                          () -> invoiceController.generateInvoice(
                                                                  null));

        // then
        assertEquals("Invalid request payload", exception.getMessage());
    }

    @Test
    void testGenerateInvoiceInvoiceNotFound() {
        // given
        final String invoiceId = "NON_EXISTENT";

        when(invoiceService.getInvoicesByInvoiceId(anyString())).thenThrow(new IllegalArgumentException(
                "Invoice not found"));

        // when
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                                                () -> invoiceController.generateInvoice(
                                                                        invoiceId));

        // then
        assertEquals("Invoice not found", exception.getMessage());
    }

    @Test
    public void testGetInvoicesByEmail() {
        // given
        final LocalDateTime ordersDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);
        final List<Order> orders = List.of(new Order.Builder().name("Produkt A")
                                                              .description("Opis A")
                                                              .quantity(1)
                                                              .priceWithVAT(100.0)
                                                              .build());
        final List<Invoice> invoices = new ArrayList<>();
        invoices.add(new Invoice.Builder().invoiceNumber(1)
                                          .buyerName("Jan Kowalski")
                                          .buyerAddress("Popowicka 68")
                                          .buyerAddressEmail("jan.kowalski@example.com")
                                          .buyerPhone("123456789")
                                          .orderDate(ordersDate)
                                          .order(orders)
                                          .build());

        when(invoiceService.getInvoicesByAddressEmail("jan.kowalski@example.com")).thenReturn(
                invoices);

        // when
        final ResponseEntity<List<Invoice>> response = invoiceController.getInvoices(null,
                                                                                     "jan.kowalski@example.com");

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1,
                     response.getBody()
                             .size());
        verify(invoiceService, times(1)).getInvoicesByAddressEmail("jan.kowalski@example.com");
    }

    @Test
    public void testGetAllInvoices() {
        // given
        final LocalDateTime ordersDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);

        final List<Order> orders = List.of(new Order.Builder().name("Produkt A")
                                                              .description("Opis A")
                                                              .quantity(1)
                                                              .priceWithVAT(100.0)
                                                              .build());
        final List<Invoice> invoices = new ArrayList<>();
        invoices.add(new Invoice.Builder().invoiceNumber(1)
                                          .buyerName("Jan Kowalski")
                                          .buyerAddress("Popowicka 68")
                                          .buyerAddressEmail("jan.kowalski@example.com")
                                          .buyerPhone("123456789")
                                          .orderDate(ordersDate)
                                          .order(orders)
                                          .build());
        invoices.add(new Invoice.Builder().invoiceNumber(2)
                                          .buyerName("Anna Nowak")
                                          .buyerAddress("Kwiatowa 12")
                                          .buyerAddressEmail("anna.nowak@example.com")
                                          .buyerPhone("123456789")
                                          .orderDate(ordersDate)
                                          .order(orders)
                                          .build());

        when(invoiceService.getAllInvoices()).thenReturn(invoices);

        // when
        final ResponseEntity<List<Invoice>> response = invoiceController.getInvoices(null, null);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2,
                     response.getBody()
                             .size());
        verify(invoiceService, times(1)).getAllInvoices();
    }

    @Test
    public void testGetInvoicesNoContent() {
        // given
        when(invoiceService.getAllInvoices()).thenReturn(new ArrayList<>());

        // when
        final ResponseEntity<List<Invoice>> response = invoiceController.getInvoices(null, null);

        // then
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        verify(invoiceService, times(1)).getAllInvoices();
    }

    @Test
    public void testGetAllInvoicesError() {
        // given
        doThrow(new RuntimeException("Database error")).when(invoiceService)
                                                       .getAllInvoices();

        // when
        final ResponseEntity<List<Invoice>> response = invoiceController.getInvoices(null, null);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testSaveInvoiceTermsNotAccepted() {

        // given
        final InvoiceRequest request = new InvoiceRequest();
        request.setBuyerName("Test Buyer");
        request.setOrders(List.of(new OrderRequest()));
        request.setAcceptedTerms(false);
        request.setShouldSendPDF(true);

        // when
        final ResponseEntity<String> response = invoiceController.saveInvoice(request);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Terms and conditions have to be accepted", response.getBody());
    }

    @Test
    void testGenerateInvoiceIOException() throws IOException {

        // given
        final String invoiceId = "FV/0001/01/2025";
        final Invoice invoice = new Invoice.Builder().invoiceNumber(1)
                                                     .buyerName("Test")
                                                     .buyerAddress("Address")
                                                     .buyerAddressEmail("test@example.com")
                                                     .buyerNIP("1234567890")
                                                     .buyerPhone("123456789")
                                                     .orderDate(LocalDateTime.now())
                                                     .emailSend(false)
                                                     .shouldSendPDF(false)
                                                     .order(List.of(new Order.Builder().name(
                                                                                               "Produkt A")
                                                                                       .description(
                                                                                               "Opis A")
                                                                                       .quantity(1)
                                                                                       .priceWithVAT(
                                                                                               100.0)
                                                                                       .build()))
                                                     .build();

        when(invoiceService.getInvoicesByInvoiceId(invoiceId)).thenReturn(invoice);
        when(pdfGeneratorService.generateInvoicePdf(invoice)).thenThrow(new IOException("File error"));

        // when
        final ResponseEntity<byte[]> response = invoiceController.generateInvoice(invoiceId);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(new String(response.getBody()).contains("Error generating invoice"));
    }

    @Test
    void testGenerateMailSuccess() {
        // given
        final MailRequest mailRequest = new MailRequest();
        mailRequest.setAddressEmail("email@example.com");
        mailRequest.setName("Jan Kowalski");
        mailRequest.setMessage("Test message");

        // when
        final ResponseEntity<Void> response = invoiceController.generateMail(mailRequest);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testGenerateMailInvalidRequest() {
        final MailRequest invalidRequest = new MailRequest();
        invalidRequest.setAddressEmail(null);

        // when
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                                                () -> invoiceController.generateMail(
                                                                        invalidRequest));

        // then
        assertEquals("Invalid request payload", exception.getMessage());
    }

}
