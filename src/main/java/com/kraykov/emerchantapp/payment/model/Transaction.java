package com.kraykov.emerchantapp.payment.model;

import com.kraykov.emerchantapp.payment.model.user.Merchant;
import com.kraykov.emerchantapp.payment.model.user.TransactionStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    private String customerEmail;
    private String phone;
    private Long referenceId;

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;
}
