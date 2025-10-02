package com.example.wwartService.service;

import com.example.wwartService.model.Invoice;
import com.example.wwartService.model.InvoiceEntity;
import com.example.wwartService.model.Order;
import com.example.wwartService.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InvoiceServiceTest {

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Mock
    private InvoiceRepository invoiceRepository;
    private InvoiceService invoiceService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        invoiceService = new InvoiceService(invoiceRepository);
    }

    @Test
    public void testGetAllInvoices() {
        // given
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 1, 100.0));
        final LocalDateTime ordersDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);
        final String buyerPhone = "987654321";
        final List<InvoiceEntity> entities = new ArrayList<>();
        entities.add(new InvoiceEntity(new Invoice(1,
                                                   "Jan Kowalski",
                                                   "Popowicka 68",
                                                   "jan.kowalski@example.com",
                                                   null,
                                                   buyerPhone,
                                                   ordersDate,
                                                   false,
                                                   false,
                                                   orders)));

        entities.add(new InvoiceEntity(new Invoice(2,
                                                   "Anna Nowak",
                                                   "Kwiatowa 12",
                                                   "anna.nowak@example.com",
                                                   null,
                                                   buyerPhone,
                                                   ordersDate,
                                                   false,
                                                   false,
                                                   orders)));
        when(invoiceRepository.findAll()).thenReturn(entities);

        // when
        final List<Invoice> invoices = invoiceService.getAllInvoices();

        // then
        assertNotNull(invoices);
        assertEquals(2, invoices.size());
        assertEquals("Jan Kowalski",
                     invoices.get(0)
                             .getBuyerName());
        assertEquals("Anna Nowak",
                     invoices.get(1)
                             .getBuyerName());
        verify(invoiceRepository, times(1)).findAll();
    }

    @Test
    public void testGetUniqueEmail() {
        // given
        final List<String> emails = List.of("jan.kowalski@example.com", "anna.nowak@example.com");
        when(invoiceRepository.findUniqueEmails()).thenReturn(emails);

        // when
        final List<String> uniqueEmails = invoiceService.getUniqueEmail();

        // then
        assertNotNull(uniqueEmails);
        assertEquals(2, uniqueEmails.size());
        assertTrue(uniqueEmails.contains("jan.kowalski@example.com"));
        assertTrue(uniqueEmails.contains("anna.nowak@example.com"));
        verify(invoiceRepository, times(1)).findUniqueEmails();
    }

    @Test
    public void testGetInvoicesByInvoiceId() {
        // given
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 1, 100.0));
        final String invoiceId = "FV/0001/01/2025";
        final LocalDateTime ordersDate = LocalDateTime.parse("2025-01-01 14:30:00", formatter);
        final String buyerPhone = "987654321";
        final InvoiceEntity entities = new InvoiceEntity(new Invoice(1,
                                                                     "Jan Kowalski",
                                                                     "Popowicka 68",
                                                                     "jan.kowalski@example.com",
                                                                     null,
                                                                     buyerPhone,
                                                                     ordersDate,
                                                                     false,
                                                                     false,
                                                                     orders));
        when(invoiceRepository.findInvoicesByInvoiceId(invoiceId)).thenReturn(entities);

        // when
        final Invoice invoices = invoiceService.getInvoicesByInvoiceId(invoiceId);

        // then
        assertNotNull(invoices);
        assertEquals("FV/0001/01/2025", invoices.getInvoiceId());
        verify(invoiceRepository, times(1)).findInvoicesByInvoiceId(invoiceId);
    }

    @Test
    public void testGetLastInvoices() {
        // given
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 1, 100.0));
        final LocalDateTime ordersDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);
        final String buyerPhone = "987654321";
        final Optional<InvoiceEntity> lastInvoiceEntity = Optional.of(new InvoiceEntity(new Invoice(
                5,
                "Anna Nowak",
                "Kwiatowa 12",
                "anna.nowak@example.com",
                null,
                buyerPhone,
                ordersDate,
                false,
                false,
                orders)));
        when(invoiceRepository.getLastInvoices()).thenReturn(lastInvoiceEntity);

        // when
        final Invoice lastInvoice = invoiceService.getLastInvoices();

        // then
        assertNotNull(lastInvoice);
        assertEquals("FV/0005/01/2024", lastInvoice.getInvoiceId());
        assertEquals("Anna Nowak", lastInvoice.getBuyerName());
        assertEquals("Kwiatowa 12", lastInvoice.getBuyerAddress());
        assertEquals("anna.nowak@example.com", lastInvoice.getBuyerAddressEmail());
        verify(invoiceRepository, times(1)).getLastInvoices();
    }

    @Test
    public void testGetInvoicesByAddressEmail() {
        // given
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 1, 100.0));
        final String email = "jan.kowalski@example.com";
        final LocalDateTime ordersDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);
        final String buyerPhone = "987654321";
        final List<InvoiceEntity> entities = List.of(new InvoiceEntity(new Invoice(1,
                                                                                   "Jan Kowalski",
                                                                                   "Popowicka 68",
                                                                                   email,
                                                                                   null,
                                                                                   buyerPhone,
                                                                                   ordersDate,
                                                                                   false,
                                                                                   false,
                                                                                   orders)));
        when(invoiceRepository.findInvoicesByEmail(email)).thenReturn(entities);

        // when
        final List<Invoice> invoices = invoiceService.getInvoicesByAddressEmail(email);

        // then
        assertNotNull(invoices);
        assertEquals(1, invoices.size());
        assertEquals("jan.kowalski@example.com",
                     invoices.get(0)
                             .getBuyerAddressEmail());
        verify(invoiceRepository, times(1)).findInvoicesByEmail(email);
    }

    @Test
    public void testSaveInvoice() {
        // given
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 1, 100.0));
        final LocalDateTime ordersDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);
        final String buyerPhone = "987654321";
        final Invoice invoice = new Invoice(1,
                                            "Jan Kowalski",
                                            "Popowicka 68",
                                            "jan.kowalski@example.com",
                                            null,
                                            buyerPhone,
                                            ordersDate,
                                            false,
                                            false,
                                            orders);
        final InvoiceEntity savedEntity = new InvoiceEntity(invoice);
        when(invoiceRepository.save(any(InvoiceEntity.class))).thenReturn(savedEntity);

        // when
        invoiceService.saveInvoiceWithOrders(invoice, orders);

        // then
        verify(invoiceRepository, times(1)).save(any(InvoiceEntity.class));
    }

    @Test
    public void testSaveInvoiceThrowsExceptionForNullInvoice() {
        // given
        // when
        // then
        assertThrows(IllegalArgumentException.class,
                     () -> invoiceService.saveInvoiceWithOrders(null, null));
    }

    @Test
    void testUpdateEmailSendStatusToTrue() {
        // Given
        final String invoiceId = "FV/001/2024";
        final boolean status = true;

        // When
        invoiceService.updateEmailSendStatus(invoiceId, status);

        // Then
        verify(invoiceRepository, times(1)).updateEmailSendStatus(invoiceId, status);
    }

    @Test
    void testUpdateEmailSendStatusToFalse() {
        // Given
        final String invoiceId = "FV/002/2024";
        final boolean status = false;

        // When
        invoiceService.updateEmailSendStatus(invoiceId, status);

        // Then
        verify(invoiceRepository, times(1)).updateEmailSendStatus(invoiceId, status);
    }

    @Test
    void testUpdateEmailSendStatusForNonExistingInvoiceId() {
        // Given
        final String invoiceId = "FV/999/2024"; // Non-existing invoice ID
        final boolean status = true;

        doThrow(new IllegalArgumentException("Invoice not found")).when(invoiceRepository)
                                                                  .updateEmailSendStatus(invoiceId,
                                                                                         status);

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> {
            invoiceService.updateEmailSendStatus(invoiceId, status);
        });

        verify(invoiceRepository, times(1)).updateEmailSendStatus(invoiceId, status);
    }
}