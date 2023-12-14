package com.kraykov.emerchantapp.payment.repository;

import com.kraykov.emerchantapp.payment.model.Transaction;
import com.kraykov.emerchantapp.payment.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByMerchantId(Long merchantId);
    @Transactional
    void deleteByTransactionTimeBefore(Instant time);

//    @Query("SELECT COUNT(t) > 0 FROM Transaction t " +
//            "WHERE t.merchant.id = :merchantId " +
//            "AND t.transactionType = :transactionType")
    Optional<Transaction> findFirstByMerchantIdAndTransactionType(Long merchantId, TransactionType transactionType);

    List<Transaction> findByMerchantIdAndReferenceId(Long merchantId, Long referenceId);
}
