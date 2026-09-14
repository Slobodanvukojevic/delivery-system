package com.delivery.payment_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "invoice")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", nullable = false)
    private Long transactionId;

    @Column(name = "invoice_number", unique = true, nullable = false, length = 50)
    private String invoiceNumber;

    @Column(name = "pdf_url", length = 500)
    private String pdfUrl;

    @Column(name = "issued_date", nullable = false)
    private LocalDate issuedDate;

    @PrePersist
    protected void onCreate() {
        if (this.issuedDate == null) {
            this.issuedDate = LocalDate.now();
        }
    }
}