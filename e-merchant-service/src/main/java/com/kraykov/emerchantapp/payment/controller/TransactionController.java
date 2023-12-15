package com.kraykov.emerchantapp.payment.controller;

import com.kraykov.emerchantapp.payment.model.TransactionType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/transactions")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    @GetMapping("/types")
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionType> getAllTransactionTypes(){
        return List.of(TransactionType.values());
    }
}
