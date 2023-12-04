package com.kraykov.emerchantapp.payment.service.impl;

import com.kraykov.emerchantapp.payment.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Background Scheduled Job which is calling the scheduled method every 30mins,
 * which will delete any transaction older than 1 hour.
 * Note: No validations are set on which transaction can be deleted, as it only demonstrates the scheduler.
 * In a more complex system, validations for deletion will be implemented.
 */
@Service
public class TransactionCleanupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionCleanupService.class);

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionCleanupService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // 30 minutes in milliseconds
    @Scheduled(fixedRate = 1800000)
    public void deleteOldTransactions() {
        LOGGER.info("Deleting transactions older than 1 hour...");
        Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);
        transactionRepository.deleteByTransactionTimeBefore(oneHourAgo);
    }
}
