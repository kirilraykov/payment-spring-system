package com.kraykov.emerchantapp.payment.controller;

import com.kraykov.emerchantapp.payment.model.user.Merchant;
import com.kraykov.emerchantapp.payment.service.impl.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/merchants")
@SecurityRequirement(name = "bearerAuth")
public class MerchantController {

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

    @Operation(summary = "Get Merchant by email")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{email}")
    public Merchant getMerchantByEmail(@PathVariable String email){
        return merchantService.getMerchantByEmail(email);
    }
}
