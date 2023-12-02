package com.kraykov.emerchantapp.payment.service.impl;

import com.kraykov.emerchantapp.payment.model.Transaction;
import com.kraykov.emerchantapp.payment.repository.TransactionRepository;
import com.kraykov.emerchantapp.payment.service.api.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService implements ITransactionService {
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
