package com.example.wwartService.controller;

import com.example.wwartService.controler.InvoiceController;
import com.example.wwartService.controler.InvoiceRequest;
import com.example.wwartService.controler.OrderRequest;
import com.example.wwartService.model.Invoice;
import com.example.wwartService.model.Order;
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

    @InjectMocks
    private InvoiceController invoiceController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveInvoiceSuccess() {
        // given
        final List<Order> orders = List.of(new Order("Product", "Description", 2, 200.0));
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
                                                        null,
                                                        null,
                                                        null,
                                                        null,
                                                        false,
                                                        null)));
        validRequest.setAcceptedTerms(true);

        final Invoice lastInvoice = new Invoice(1,
                                                "Last Buyer",
                                                "Last Address",
                                                "last@example.com",
                                                "0987654321",
                                                "123456789",
                                                LocalDateTime.now(),
                                                false,
                                                orders);

        when(invoiceService.getLastInvoices()).thenReturn(lastInvoice);

        // when
        final ResponseEntity<String> response = invoiceController.saveInvoice(validRequest);

        // then
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().startsWith("Invoice saved successfully"));
        verify(invoiceService, times(1)).getLastInvoices();
        verify(invoiceService, times(1)).saveInvoiceWithOrders(any(Invoice.class), anyList());
    }


    @Test
    void testSaveInvoiceNullRequest() {
        // given
        // when

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            invoiceController.saveInvoice(null);
        });

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
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            invoiceController.saveInvoice(invalidRequest);
        });

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
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            invoiceController.saveInvoice(invalidRequest);
        });

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
                                                                          "Czerwony",
                                                                          "OKRAGLA",
                                                                          "LUX",
                                                                          "BACK",
                                                                          true,
                                                                          "0.15")));
        validRequest.setAcceptedTerms(true);

        when(invoiceService.getLastInvoices()).thenThrow(new RuntimeException("Database error"));

        // when
        final ResponseEntity<String> response = invoiceController.saveInvoice(validRequest);

        // then
        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().startsWith("Error saving invoice"));
    }


    @Test
    public void testGenerateInvoiceSuccess() throws IOException {
        // given
        final String invoiceId = "INV/001/2024";

        final LocalDateTime ordersDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 1, 100.0));

        final Invoice mockInvoice = new Invoice(1,
                                                "Jan Kowalski",
                                                "Popowicka 68",
                                                "jan.kowalski@example.com",
                                                "",
                                                "123456789",
                                                ordersDate,
                                                false,
                                                orders);

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
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 1, 100.0));
        final Invoice invoice = new Invoice(1,
                                            "Jan Kowalski",
                                            "Popowicka 68",
                                            "jan.kowalski@example.com",
                                            null,
                                            "123456789",
                                            ordersDate,
                                            false,
                                            orders);
        when(invoiceService.getInvoicesByInvoiceId("FV/001/2025")).thenReturn(invoice);

        // when
        final ResponseEntity<List<Invoice>> response = invoiceController.getInvoices("FV/001/2025", null);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(invoiceService, times(1)).getInvoicesByInvoiceId("FV/001/2025");
    }

    @Test
    void testGenerateInvoiceNullId() {
        // given
        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            invoiceController.generateInvoice(null);
        });

        // then
        assertEquals("Invalid request payload", exception.getMessage());
    }

    @Test
    void testGenerateInvoiceInvoiceNotFound() {
        // given
        final String invoiceId = "NON_EXISTENT";

        when(invoiceService.getInvoicesByInvoiceId(anyString())).thenThrow(new IllegalArgumentException("Invoice not found"));

        // when
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            invoiceController.generateInvoice(invoiceId);
        });

        // then
        assertEquals("Invoice not found", exception.getMessage());
    }

    @Test
    public void testGetInvoicesByEmail() {
        // given
        final LocalDateTime ordersDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 1, 100.0));
        final List<Invoice> invoices = new ArrayList<>();
        invoices.add(new Invoice(1,
                                 "Jan Kowalski",
                                 "Popowicka 68",
                                 "jan.kowalski@example.com",
                                 null,
                                 "123456789",
                                 ordersDate,
                                 false,
                                 orders));
        when(invoiceService.getInvoicesByAddressEmail("jan.kowalski@example.com")).thenReturn(invoices);

        // when
        final ResponseEntity<List<Invoice>> response = invoiceController.getInvoices(null, "jan.kowalski@example.com");

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(invoiceService, times(1)).getInvoicesByAddressEmail("jan.kowalski@example.com");
    }

    @Test
    public void testGetAllInvoices() {
        // given
        final LocalDateTime ordersDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);

        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 1, 100.0));
        final List<Invoice> invoices = new ArrayList<>();
        invoices.add(new Invoice(1,
                                 "Jan Kowalski",
                                 "Popowicka 68",
                                 "jan.kowalski@example.com",
                                 null,
                                 "123456789",
                                 ordersDate,
                                 false,
                                 orders));
        invoices.add(new Invoice(2, "Anna Nowak", "Kwiatowa 12", "anna.nowak@example.com", null, "123456789", ordersDate, false, orders));
        when(invoiceService.getAllInvoices()).thenReturn(invoices);

        // when
        final ResponseEntity<List<Invoice>> response = invoiceController.getInvoices(null, null);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(invoiceService, times(1)).getAllInvoices();
    }

    @Test
    public void testGetAllUniqueEmails() {
        // given
        final List<String> emails = List.of("jan.kowalski@example.com", "anna.nowak@example.com");
        when(invoiceService.getUniqueEmail()).thenReturn(emails);

        // when
        final ResponseEntity<List<String>> response = invoiceController.getUniqueEmail();

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(invoiceService, times(1)).getUniqueEmail();
    }

    @Test
    public void testGetUniqueEmailError() {
        // given
        doThrow(new RuntimeException("Database error")).when(invoiceService).getUniqueEmail();

        // when
        ResponseEntity<List<String>> response = invoiceController.getUniqueEmail();

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
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
        doThrow(new RuntimeException("Database error")).when(invoiceService).getAllInvoices();

        // when
        ResponseEntity<List<Invoice>> response = invoiceController.getInvoices(null, null);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

}
