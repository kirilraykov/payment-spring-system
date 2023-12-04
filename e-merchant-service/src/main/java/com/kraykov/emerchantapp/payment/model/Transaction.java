package com.kraykov.emerchantapp.payment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kraykov.emerchantapp.payment.model.user.Merchant;
import com.kraykov.emerchantapp.payment.model.user.TransactionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Amount must not be null")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @NotBlank(message = "Each transaction should have transaction type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Email(message = "Provided email is not valid")
    private String customerEmail;
    private String phone;

    @NotBlank(message = "Each transaction should have a reference ID")
    private Long referenceId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Instant transactionTime;

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Merchant merchant;
}
