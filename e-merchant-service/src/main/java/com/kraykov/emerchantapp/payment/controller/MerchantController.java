package com.kraykov.emerchantapp.payment.controller;

import com.kraykov.emerchantapp.payment.model.Transaction;
import com.kraykov.emerchantapp.payment.model.user.Merchant;
import com.kraykov.emerchantapp.payment.service.api.ITransactionService;
import com.kraykov.emerchantapp.payment.service.impl.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.kraykov.emerchantapp.payment.auth.ParseHeaderContents.parseUserEmailFromAuth;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/merchants")
@SecurityRequirement(name = "bearerAuth")
public class MerchantController {

    private final MerchantService merchantService;
    private final ITransactionService transactionService;

    @Autowired
    public MerchantController(MerchantService merchantService, ITransactionService transactionService) {
        this.merchantService = merchantService;
        this.transactionService = transactionService;
    }

    @Operation(summary = "Get all merchants")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<Merchant> getAllMerchants(){
        return merchantService.getAllMerchants();
    }

    @Operation(summary = "Create new transaction.")
    @PostMapping(value = "/transactions")
    @ResponseStatus(HttpStatus.OK)
    @Validated
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        String emailFromAuth = parseUserEmailFromAuth();
        Long merchantId = merchantService.getMerchantByEmail(emailFromAuth).getId();
        return transactionService.createTransaction(merchantId, transaction);
    }

    @Operation(summary = "Get transactions for merchantId")
    @GetMapping("/transactions/{merchantId}")
    @ResponseStatus(HttpStatus.OK)
    public List<Transaction> getAllTransactionsForMerchant(@PathVariable Long merchantId){
        return transactionService.getTransactionsForMerchantId(merchantId);
    }
}
