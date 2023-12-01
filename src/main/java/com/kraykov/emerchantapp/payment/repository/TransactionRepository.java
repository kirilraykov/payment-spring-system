package com.kraykov.emerchantapp.payment.repository;

import com.kraykov.emerchantapp.payment.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
