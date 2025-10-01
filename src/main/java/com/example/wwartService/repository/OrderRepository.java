package com.example.wwartService.repository;

import com.example.wwartService.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByInvoice_InvoiceId(String invoiceId);
}