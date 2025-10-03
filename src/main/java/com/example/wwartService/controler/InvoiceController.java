package com.example.wwartService.controler;

import com.example.wwartService.model.Invoice;
import com.example.wwartService.model.Order;
import com.example.wwartService.service.EmailService;
import com.example.wwartService.service.InvoiceService;
import com.example.wwartService.service.PdfGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class InvoiceController {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);
    private final InvoiceService invoiceService;
    private final PdfGeneratorService pdfGeneratorService;
    private final EmailService emailService;

    public InvoiceController(final InvoiceService invoiceService, final PdfGeneratorService pdfGeneratorService, final EmailService emailService) {
        this.invoiceService = invoiceService;
        this.pdfGeneratorService = pdfGeneratorService;
        this.emailService = emailService;
    }

    @PostMapping(value = "/save-invoice", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveInvoice(@RequestBody InvoiceRequest invoiceRequest) {
        if (invoiceRequest == null || invoiceRequest.getBuyerName() == null || invoiceRequest.getOrders() == null) {
            logger.error("Invalid request data - required fields missing");
            throw new IllegalArgumentException("Invalid request payload");
        }

        if (invoiceRequest.getAcceptedTerms() == null || !invoiceRequest.getAcceptedTerms()) {
            logger.error("Terms and conditions were not accepted by client: {}", invoiceRequest.getBuyerName());
            return ResponseEntity.badRequest()
                                 .body("Terms and conditions have to be accepted");
        }

        if (invoiceRequest.getShouldSendPDF() == null || !invoiceRequest.getShouldSendPDF()) {
            logger.error("ShouldSendPDF were not accepted by client: {}", invoiceRequest.getBuyerName());
            return ResponseEntity.badRequest()
                                 .body("ShouldSendPDF have to be");
        }

        try {
            final Invoice lastInvoice = invoiceService.getLastInvoices();
            final List<Order> orders = invoiceRequest.getOrders()
                                                     .stream()
                                                     .map(orderReq -> {
                                                         StringBuilder description = new StringBuilder();
                                                         description.append("Kategoria: ")
                                                                    .append(orderReq.getCategory())
                                                                    .append("; ");

                                                         if (orderReq.getSize() != null) {
                                                             description.append("Rozmiar: ")
                                                                        .append(orderReq.getSize())
                                                                        .append("; ");
                                                         }
                                                         if (orderReq.getColor() != null) {
                                                             description.append("Kolor: ")
                                                                        .append(orderReq.getColor())
                                                                        .append("; ");
                                                         }

                                                         return new Order(orderReq.getName(), description.toString(), orderReq.getQuantity(), orderReq.getPrice());
                                                     })
                                                     .collect(Collectors.toList());

            final Invoice newInvoice = new Invoice(lastInvoice.extractAndIncreaseInvoiceNumber(),
                                                   invoiceRequest.getBuyerName(),
                                                   invoiceRequest.getBuyerAddress(),
                                                   invoiceRequest.getBuyerAddressEmail(),
                                                   invoiceRequest.getBuyerNip(),
                                                   invoiceRequest.getBuyerPhone(),
                                                   LocalDateTime.now(),
                                                   false,
                                                   invoiceRequest.getShouldSendPDF(),
                                                   orders);

            logger.info("Saving invoice {} for client: {}", newInvoice.getInvoiceId(), invoiceRequest.getBuyerName());

            if (invoiceRequest.getOrderSummary() != null) {
                logger.info("Order statistics - Products: {}, Personalized: {}, Shipping method: {}",
                            invoiceRequest.getOrderSummary()
                                          .getUniqueProducts(),
                            invoiceRequest.getOrderSummary()
                                          .getHasPersonalizedItems(),
                            invoiceRequest.getOrderSummary()
                                          .getShippingMethod());
            }

            if (invoiceRequest.getBrowserInfo() != null) {
                logger.debug("Browser information: {}", invoiceRequest.getBrowserInfo());
            }

            invoiceService.saveInvoiceWithOrders(newInvoice, orders);

            return ResponseEntity.ok()
                                 .contentType(MediaType.TEXT_PLAIN)
                                 .body("Invoice saved successfully with ID: " + newInvoice.getInvoiceId());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error saving invoice: " + e.getMessage());
        }
    }

    @PostMapping(value = "/mail", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> generateMail(@RequestBody MailRequest mailRequest) {
        if (mailRequest == null || mailRequest.getAddressEmail() == null) {
            logger.error("Mail request - required fields missing");
            throw new IllegalArgumentException("Invalid request payload");
        }

        emailService.sendEmail(mailRequest.getAddressEmail(), mailRequest.getName(), mailRequest.getMessage());
        return ResponseEntity.ok()
                             .build();
    }

    @PostMapping(value = "/generate-invoice", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> generateInvoice(@RequestParam() String invoiceId) {
        if (invoiceId == null) {
            throw new IllegalArgumentException("Invalid request payload");
        }

        try {
            final Invoice invoice = invoiceService.getInvoicesByInvoiceId(invoiceId);

            final byte[] out = pdfGeneratorService.generateInvoicePdf(invoice)
                                                  .toByteArray();

            final String fileName = "Faktura-" + invoiceId + ".pdf";

            final HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + fileName);
            return new ResponseEntity<>(out, headers, HttpStatus.OK);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(("Error generating invoice: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping(value = "/get-invoices-data", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Invoice>> getInvoices(@RequestParam(required = false) String invoiceId, @RequestParam(required = false) String addressEmail) {

        try {
            List<Invoice> invoices = new ArrayList<>();

            if (invoiceId != null && !invoiceId.isEmpty()) {
                invoices.add(invoiceService.getInvoicesByInvoiceId(invoiceId));
            } else {
                if (addressEmail != null && !addressEmail.isEmpty()) {
                    invoices = invoiceService.getInvoicesByAddressEmail(addressEmail);
                } else {
                    invoices = invoiceService.getAllInvoices();
                }
            }

            if (invoices == null || invoices.isEmpty()) {
                return ResponseEntity.notFound()
                                     .build();
            }

            return ResponseEntity.ok(invoices);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .build();
        }
    }
}
