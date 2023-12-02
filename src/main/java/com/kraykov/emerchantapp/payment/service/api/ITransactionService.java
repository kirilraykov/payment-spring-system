package com.kraykov.emerchantapp.payment.service.api;


import com.kraykov.emerchantapp.payment.model.Transaction;

import java.util.List;

public interface ITransactionService {
    List<Transaction> getAllTransactions();
}
