package com.kraykov.emerchantapp.payment.controller;

import com.kraykov.emerchantapp.payment.model.Transaction;
import com.kraykov.emerchantapp.payment.model.TransactionType;
import com.kraykov.emerchantapp.payment.service.api.ITransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/transactions")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final ITransactionService transactionService;

    @Autowired
    public TransactionController(ITransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping(value = "/create/{merchantId}")
    @ResponseStatus(HttpStatus.OK)
    @Validated
    public Transaction createTransaction(@PathVariable @NotBlank Long merchantId,
                                         @RequestBody Transaction transaction) {

        return transactionService.createTransaction(merchantId, transaction);
    }

    @GetMapping("{merchantId}")
    @ResponseStatus(HttpStatus.OK)
    public List<Transaction> getAllTransactionsForMerchant(@PathVariable Long merchantId){
        return transactionService.getTransactionsForMerchantId(merchantId);
    }

    @GetMapping("/types")
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionType> getAllTransactionTypes(){
        return List.of(TransactionType.values());
    }
}
