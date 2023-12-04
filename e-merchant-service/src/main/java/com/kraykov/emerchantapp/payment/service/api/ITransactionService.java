package com.kraykov.emerchantapp.payment.service.api;


import com.kraykov.emerchantapp.payment.model.Transaction;

import java.util.List;

public interface ITransactionService {
    List<Transaction> getTransactionsForMerchantId(Long merchantId);
    Transaction createTransaction(Long merchantId, Transaction transaction);
}
