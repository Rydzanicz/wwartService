package com.example.wwartService.policy;

import com.example.wwartService.model.FailedProcessedPolicyEntity;
import com.example.wwartService.model.Invoice;
import com.example.wwartService.service.EmailService;
import com.example.wwartService.service.FailedProcessedPolicyService;
import com.example.wwartService.service.InvoiceService;
import com.example.wwartService.service.PdfGeneratorService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class EmailPolicy {
    private final static String POLICY_NAME = "EmailPolicy";
    final List<Invoice> processedFailed = new ArrayList<>();
    private final InvoiceService invoiceService;
    private final EmailService emailService;
    private final FailedProcessedPolicyService failedProcessedPolicyService;

    public EmailPolicy(final InvoiceService invoiceService, final EmailService emailService, final FailedProcessedPolicyService failedProcessedPolicyService) {
        this.invoiceService = invoiceService;
        this.emailService = emailService;
        this.failedProcessedPolicyService = failedProcessedPolicyService;
    }

    @Scheduled(cron = "${email.policy.cron}")
    public void executeEmailPolicy() {
        final List<Invoice> unsentInvoices = invoiceService.getNoSendInvoicesWithExcluding(processedFailed);
        if (unsentInvoices.isEmpty()) {
            return;
        }

        for (Invoice invoice : unsentInvoices) {
            final Optional<FailedProcessedPolicyEntity> failedProcessed = failedProcessedPolicyService.findInvoicesByInvoiceId(invoice.getInvoiceId());
            if (failedProcessed.isPresent() &&
                failedProcessed.get()
                               .getRetryCount() > 10) {
                processedFailed.add(invoice);
                return;
            }

            try {
                byte[] pdfAttachment = generateInvoicePdf(invoice);
                emailService.sendEmails(invoice.getBuyerAddressEmail(), pdfAttachment, "Faktura-" + invoice.getInvoiceId() + ".pdf");

                if (invoice.isShouldSendPDF()) {
                    emailService.sendPdfEmail(invoice.getBuyerAddressEmail());
                }

                invoiceService.updateEmailSendStatus(invoice.getInvoiceId(), true);
            } catch (Exception e) {
                final String errorMessage = e.getCause() != null ? e.getCause()
                                                                    .getLocalizedMessage() : e.getMessage();
                failedProcessedPolicyService.logError(POLICY_NAME, errorMessage, invoice.getInvoiceId(), failedProcessed);
            }
        }
    }

    private byte[] generateInvoicePdf(Invoice invoice) throws IOException {
        PdfGeneratorService pdfGeneratorService = new PdfGeneratorService();
        return pdfGeneratorService.generateInvoicePdf(invoice)
                                  .toByteArray();
    }
}
