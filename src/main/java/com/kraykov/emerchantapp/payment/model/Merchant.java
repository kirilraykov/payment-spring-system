package com.kraykov.emerchantapp.payment.model;

import jakarta.persistence.*;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "merchants")
@Data
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Email(message = "Provided email is not valid. Please check input.")
    @NotBlank(message = "Email cannot be empty")
    private String email;
}
