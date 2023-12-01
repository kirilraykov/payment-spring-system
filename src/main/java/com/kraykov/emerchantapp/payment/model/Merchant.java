package com.kraykov.emerchantapp.payment.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "merchants")
@Data
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
}
