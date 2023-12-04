package com.kraykov.emerchantapp.payment.model.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kraykov.emerchantapp.payment.model.MerchantStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("MERCHANT")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Merchant extends User{
    private String description;
    @Enumerated(EnumType.STRING)
    private MerchantStatus status;
    private BigDecimal totalTransactionSum;
}



