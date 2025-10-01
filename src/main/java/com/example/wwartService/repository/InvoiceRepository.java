package com.example.wwartService.repository;


import com.example.wwartService.model.InvoiceEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {

    InvoiceEntity findInvoicesByInvoiceId(String invoiceId);

    List<InvoiceEntity> findInvoicesByEmail(String email);

    @Query(value = "SELECT * FROM invoices ORDER BY invoice_id DESC LIMIT 1", nativeQuery = true)
    Optional<InvoiceEntity> getLastInvoices();

    @Query(value = "SELECT DISTINCT email FROM invoices", nativeQuery = true)
    List<String> findUniqueEmails();

    @Modifying
    @Transactional
    @Query("UPDATE InvoiceEntity i SET i.isEmailSend = :status WHERE i.invoiceId = :invoiceId")
    void updateEmailSendStatus(String invoiceId, boolean status);

    @Query("SELECT i FROM InvoiceEntity i WHERE i.isEmailSend = false AND (i.invoiceId NOT IN :excludedIds OR :excludedIds IS NULL)")
    List<InvoiceEntity> findUnsentInvoicesExcluding(@Param("excludedIds") List<String> excludedIds);

    @Query("SELECT i FROM InvoiceEntity i WHERE i.isEmailSend = false")
    List<InvoiceEntity> findNoSendInvoices();
}