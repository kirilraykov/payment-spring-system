package com.kraykov.emerchantapp;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class EMerchantAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(EMerchantAppApplication.class, args);
	}

}
