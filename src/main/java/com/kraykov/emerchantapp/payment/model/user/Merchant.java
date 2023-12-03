package com.kraykov.emerchantapp.payment.model.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("MERCHANT")
@Data
@EqualsAndHashCode(callSuper = true)
//@JsonIgnoreProperties(value = "password")
public class Merchant extends User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private String status;
    private BigDecimal totalTransactionSum;
}



