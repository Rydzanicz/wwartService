package com.example.wwartService.service;


import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;


@Service
public class EmailService {

    private static final String EMAIL_FROM = "noReplyViggoProgramer@wp.pl";
    private static final String MY_EMAIL = "rydzanicz.mm@gmail.com";
    private static final String SUBJECT = "Dziękujemy za udział  w etapie testowania – Twoja faktura testowa w załączniku";
    private final JavaMailSender mailSender;

    private final String emailBody = "Szanowni Państwo,\n\n" +
                                     "Dziękujemy za dokonanie testowego zakupu w naszej firmie. Poniżej znajdą Państwo " +
                                     "szczegóły" +
                                     " transakcji oraz fakturę VAT w załączniku.\n\n" +
                                     "W razie jakichkolwiek pytań lub wątpliwości związanych z fakturą lub zakupem, prosimy o kontakt z naszym biurem obsługi klienta.\n" +
                                     "Pamiętajcie, że na tę wiadomość nie należy odpowiadać, ponieważ jest generowana automatycznie.\n\n" +
                                     "Z wyrazami szacunku,\n" +
                                     "Michał Rydzanicz\n" +
                                     "---\n\n" +
                                     "**Dane kontaktowe:**\n" +
                                     "Email: rydzanicz.mm@gmail.com\n" +
                                     "**Uwaga:** W przypadku problemów z otwarciem załącznika prosimy o kontakt pod adresem e-mail.\n\n" +
                                     "Dziękujemy za zaufanie i zapraszamy ponownie!\n\n" +
                                     "Z poważaniem";

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmails(final String recipientEmail, final byte[] pdfAttachment, final String fileName) {
        sendEmail(recipientEmail, pdfAttachment, fileName);
        sendEmail(MY_EMAIL, pdfAttachment, fileName);

    }

    public void sendPdfEmail(final String recipientEmail) {
        final MimeMessage message = mailSender.createMimeMessage();

        try {
            final MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(EMAIL_FROM);
            helper.setTo(recipientEmail);
            helper.setSubject("Dziękujemy za zakup w moim sklepie. Ebook w załączniku");
            helper.setText("Dziękujemy za zakup w moim sklepie. Ebook w załączniku");

            final FileSystemResource file = new FileSystemResource(new File("src/main/resources/PDF.pdf"));
            helper.addAttachment(file.getFilename(), file);

            mailSender.send(message);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to send email.", e);
        }
    }

    public void sendEmail(final String addressEmail, final String name, final String emailBody) {
        final MimeMessage message = mailSender.createMimeMessage();

        try {
            final MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(EMAIL_FROM);
            helper.setTo(MY_EMAIL);
            helper.setSubject(name);
            helper.setText(addressEmail + ": " + emailBody);

            mailSender.send(message);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to send email.", e);
        }
    }

    private void sendEmail(final String recipientEmail, final byte[] pdfAttachment, final String fileName) {
        final MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(EMAIL_FROM);
            helper.setTo(recipientEmail);
            helper.setSubject(SUBJECT);
            helper.setText(emailBody);

            if (pdfAttachment == null) {
                throw new IllegalStateException();
            }
            helper.addAttachment(fileName, new ByteArrayResource(pdfAttachment));

            mailSender.send(message);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to send email.", e);
        }
    }
}