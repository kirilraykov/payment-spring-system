package com.kraykov.emerchantapp.payment.controller;

import com.kraykov.emerchantapp.payment.model.Merchant;
import com.kraykov.emerchantapp.payment.service.impl.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/merchants")
@SecurityRequirement(name = "bearerAuth")
public class MerchantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantController.class);

    private final MerchantService merchantService;

    @Autowired
    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @Operation(summary = "Get all merchants")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<Merchant> getAllMerchants(){
        return merchantService.getAllMerchants();
    }
}
