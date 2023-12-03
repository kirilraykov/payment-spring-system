package com.kraykov.emerchantapp.payment.repository;

import com.kraykov.emerchantapp.payment.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByMerchantId(Long merchantId);
}
