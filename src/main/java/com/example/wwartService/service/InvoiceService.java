package com.example.wwartService.service;

import com.example.wwartService.model.Invoice;
import com.example.wwartService.model.InvoiceEntity;
import com.example.wwartService.model.Order;
import com.example.wwartService.model.OrderEntity;
import com.example.wwartService.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;

    public InvoiceService(final InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public List<Invoice> getAllInvoices() {
        final List<InvoiceEntity> entities = invoiceRepository.findAll();
        return entities.stream()
                       .map(this::mapToInvoice)
                       .toList();
    }

    public List<String> getUniqueEmail() {
        return invoiceRepository.findUniqueEmails();
    }

    public Invoice getLastInvoices() {
        final Optional<InvoiceEntity> invoiceEntity = invoiceRepository.getLastInvoices();

        return invoiceEntity.map(Invoice::new)
                            .orElseGet(Invoice::new);
    }

    public void updateEmailSendStatus(final String invoiceId,
                                      final boolean status) {
        invoiceRepository.updateEmailSendStatus(invoiceId, status);
    }

    public Invoice getInvoicesByInvoiceId(String invoiceId) {
        final InvoiceEntity entities = invoiceRepository.findInvoicesByInvoiceId(invoiceId);
        return new Invoice(entities);
    }

    public List<Invoice> getInvoicesByAddressEmail(String addressEmail) {
        final List<InvoiceEntity> entities = invoiceRepository.findInvoicesByEmail(addressEmail);
        return entities.stream()
                       .map(this::mapToInvoice)
                       .toList();
    }

    public List<Invoice> getNoSendInvoicesWithExcluding(final List<Invoice> processedFailed) {
        final List<InvoiceEntity> entities;
        if (processedFailed.isEmpty()) {
            entities = invoiceRepository.findNoSendInvoices();
        } else {
            entities = invoiceRepository.findUnsentInvoicesExcluding(processedFailed.stream()
                                                                                    .map(Invoice::getInvoiceId)
                                                                                    .toList());
        }
        return entities.stream()
                       .map(this::mapToInvoice)
                       .toList();
    }

    private Invoice mapToInvoice(final InvoiceEntity entity) {
        return new Invoice.Builder().invoiceNumber(Integer.parseInt(entity.getInvoiceId()
                                                                          .split("/")[1]))
                                    .buyerName(entity.getName())
                                    .buyerAddress(entity.getAddress())
                                    .buyerAddressEmail(entity.getEmail())
                                    .buyerNIP(entity.getNip())
                                    .buyerPhone(entity.getPhone())
                                    .orderDateString(entity.getOrderDate())
                                    .emailSend(entity.isEmailSend())
                                    .shouldSendPDF(entity.isShouldSendPDF())
                                    .order(entity.getOrders()
                                                 .stream()
                                                 .map(Order::new)
                                                 .toList())
                                    .build();
    }

    public void saveInvoiceWithOrders(final Invoice invoice,
                                      final List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            throw new IllegalArgumentException("List of Product cannot be null or empty.");
        }
        final InvoiceEntity invoiceEntity = new InvoiceEntity(invoice);

        final List<OrderEntity> ordersEntity = orders.stream()
                                                     .map(order -> {
                                                         OrderEntity orderEntity = new OrderEntity(
                                                                 order);
                                                         orderEntity.setInvoice(invoiceEntity);
                                                         return orderEntity;
                                                     })
                                                     .toList();

        invoiceEntity.setOrders(ordersEntity);

        invoiceRepository.save(invoiceEntity);
    }
}