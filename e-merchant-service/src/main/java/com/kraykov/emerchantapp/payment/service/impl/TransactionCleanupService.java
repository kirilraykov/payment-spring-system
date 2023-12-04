package com.kraykov.emerchantapp.payment.service.impl;

import com.kraykov.emerchantapp.payment.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TransactionCleanupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionCleanupService.class);

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionCleanupService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Scheduled(fixedRate = 1800000) // 30 minutes in milliseconds
    public void deleteOldTransactions() {
        LOGGER.info("Deleting transactions older than 1 hour...");
        Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);
        transactionRepository.deleteByTransactionTimeBefore(oneHourAgo);
    }
}
