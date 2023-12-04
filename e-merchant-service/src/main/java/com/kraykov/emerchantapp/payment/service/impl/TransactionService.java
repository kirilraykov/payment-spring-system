package com.kraykov.emerchantapp.payment.service.impl;

import com.kraykov.emerchantapp.payment.model.Transaction;
import com.kraykov.emerchantapp.payment.model.TransactionType;
import com.kraykov.emerchantapp.payment.model.exception.CustomServiceException;
import com.kraykov.emerchantapp.payment.model.user.Merchant;
import com.kraykov.emerchantapp.payment.model.user.TransactionStatus;
import com.kraykov.emerchantapp.payment.repository.MerchantRepository;
import com.kraykov.emerchantapp.payment.repository.TransactionRepository;
import com.kraykov.emerchantapp.payment.service.api.IMerchantService;
import com.kraykov.emerchantapp.payment.service.api.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static com.kraykov.emerchantapp.payment.exception.ErrorCodes.*;
import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;

@Service
public class TransactionService implements ITransactionService {
    private final TransactionRepository transactionRepository;
    private final MerchantRepository merchantRepository;
    private final IMerchantService merchantService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              MerchantRepository merchantRepository, IMerchantService merchantService) {
        this.transactionRepository = transactionRepository;
        this.merchantRepository = merchantRepository;
        this.merchantService = merchantService;
    }

    @Override
    public List<Transaction> getTransactionsForMerchantId(Long merchantId) {
        return transactionRepository.findByMerchantId(merchantId);
    }

    @Override
    public Transaction createTransaction(Long merchantId, Transaction transaction) {
        TransactionType transactionType = transaction.getTransactionType();
        switch (transactionType) {
            case AUTHORIZE -> {
                return processAuthorizeTransaction(merchantId, transaction);
            }
            case CHARGE -> {
                return processChargeTransaction(merchantId, transaction);
            }
            case REFUND -> {
                return processRefundTransaction(merchantId, transaction);
            }
            case REVERSAL -> {
                return processReversalTransaction(merchantId, transaction);
            }
        }
        throw new CustomServiceException(FAILED_TO_CREATE_TRANSACTION.getCode(), "Failed to created transaction");
    }

    /**
     * Get the merchant for the provided merchantId.
     * Get all merchant's transactions and check if there is existing authorizations with the same referenceId.
     * If yes, throw error as it has already been authorized(first step).
     * If not, save the transaction as expected and DON'T charge the merchant.
     * @param merchantId - existing merchant ID
     * @param transactionToCreate - the transaction to be created
     * @return the created transaction (if creation has succeeded)
     */
    private Transaction processAuthorizeTransaction(Long merchantId, Transaction transactionToCreate) {
        Merchant merchantForCurrentTransaction = merchantService.getMerchantById(merchantId);
        List<Transaction> existingTransactionsForPayment = getTransactionsForMerchantId(merchantId).stream()
                .filter(transaction -> transactionToCreate.getReferenceId().equals(transaction.getReferenceId())
                        && transaction.getTransactionType().equals(TransactionType.AUTHORIZE))
                .toList();

        if(existingTransactionsForPayment.isEmpty()) {
            transactionToCreate.setMerchant(merchantForCurrentTransaction);
            transactionToCreate.setTransactionTime(Instant.now());
            return transactionRepository.save(transactionToCreate);
        } else {
            throw new CustomServiceException(MERCHANT_ALREADY_AUTHORIZED_TRANSACTION.getCode(),
                    "Merchant has already authorized the current transaction.");
        }
    }

    /**
     * Get the merchant for the provided merchantId.
     * Get all merchant's transactions for the current referenceId.
     * Check if filtered transactions contain authorization transaction.
     * If yes, check the status is null OR reversed(it should only be updated when payment has completed(approved, reversed, refunded,error).
     *    if null -> do the transaction, calculate merchant total, update authorization transaction to approved and save new one
     *    If not null -> throw error since the payment has already been completed
     * @param merchantId - existing merchant ID
     * @param transactionToCreate - the transaction to be created
     * @return the created transaction (if creation has succeeded)
     */
    private Transaction processChargeTransaction(Long merchantId, Transaction transactionToCreate) {
        verifyTransactionAmountPositive(transactionToCreate.getAmount());

        Merchant merchantForCurrentTransaction = merchantService.getMerchantById(merchantId);
        List<Transaction> existingTransactionsForCurrentPayment = getTransactionsForMerchantId(merchantId).stream()
                .filter(transaction -> transactionToCreate.getReferenceId().equals(transaction.getReferenceId()))
                .toList();

        Transaction authorizedTransactionForCurrentPayment = existingTransactionsForCurrentPayment.stream()
                .filter(transaction -> transaction.getTransactionType().equals(TransactionType.AUTHORIZE)).findFirst()
                .orElseThrow(() -> new CustomServiceException(TRANSACTION_MISSING_AUTHORIZATION.getCode(),
                        "Transaction is not authorized."));

        TransactionStatus authorizedTransactionStatus = authorizedTransactionForCurrentPayment.getStatus();
        if(isNull(authorizedTransactionStatus) || TransactionStatus.REVERSED.equals(authorizedTransactionStatus)) {
            updateMerchantTotalTransactionSum(merchantForCurrentTransaction, transactionToCreate.getAmount());
            authorizedTransactionForCurrentPayment.setStatus(TransactionStatus.APPROVED);
            transactionRepository.save(authorizedTransactionForCurrentPayment);
            transactionToCreate.setMerchant(merchantForCurrentTransaction);
            transactionToCreate.setTransactionTime(Instant.now());
            transactionToCreate.setStatus(TransactionStatus.APPROVED);
            return transactionRepository.save(transactionToCreate);
        }
        else {
            throw new CustomServiceException(TRANSACTION_ALREADY_CHARGED.getCode(),
                    "Transaction has already been charged and completed.");
        }
    }

    /**
     * Get all merchant's transactions for the current referenceId.
     * Check if filtered transactions contain charge transaction.
     * If not, throw exception as refund depends on charge first
     * If yes, check if transaction has NOT been already refunded.
     * If no, set the refunded status and save the transaction to be created.
     * @param merchantId - existing merchant ID
     * @param transactionToCreate - the transaction to be created
     * @return the created transaction (if creation has succeeded)
     */
    private Transaction processRefundTransaction(Long merchantId, Transaction transactionToCreate) {
        Merchant merchantForCurrentTransaction = merchantService.getMerchantById(merchantId);
        List<Transaction> existingTransactionsForCurrentPayment = getTransactionsForMerchantId(merchantId).stream()
                .filter(transaction -> transactionToCreate.getReferenceId().equals(transaction.getReferenceId()))
                .toList();

        Transaction chargeTransaction = existingTransactionsForCurrentPayment.stream()
                .filter(transaction -> TransactionType.CHARGE.equals(transaction.getTransactionType()))
                .findAny().orElseThrow(() -> new CustomServiceException(TRANSACTION_MISSING_PAYMENT.getCode(),
                        "Cannot refund payment which doesn't have Charge transaction"));

        TransactionStatus chargeTransactionStatus = chargeTransaction.getStatus();
        if(!TransactionStatus.REFUNDED.equals(chargeTransactionStatus)) {
            updateMerchantTotalTransactionSum(merchantForCurrentTransaction, transactionToCreate.getAmount().negate());
            chargeTransaction.setStatus(TransactionStatus.REFUNDED);
            transactionRepository.save(chargeTransaction);
            transactionToCreate.setMerchant(merchantForCurrentTransaction);
            transactionToCreate.setTransactionTime(Instant.now());
            transactionToCreate.setStatus(TransactionStatus.APPROVED);
            return transactionRepository.save(transactionToCreate);
        } else {
            throw new CustomServiceException(TRANSACTION_ALREADY_REFUNDED.getCode(),
                    "Payment has already been refunded.");
        }
    }

    /**
     * Get all merchant's transactions for the current referenceId.
     * Check if filtered transactions contain authorization transaction
     * If yes, set the status to REVERSED, and save the new transaction
     * If no, throw error that no authorized transaction exists for the given payment (referenceId)
     * @param merchantId - existing merchant ID
     * @param transactionToCreate - the transaction to be created
     * @return the created transaction (if creation has succeeded)
     */
    private Transaction processReversalTransaction(Long merchantId, Transaction transactionToCreate) {
        Merchant merchantForCurrentTransaction = merchantService.getMerchantById(merchantId);

        List<Transaction> existingTransactionsForCurrentPayment = getTransactionsForMerchantId(merchantId).stream()
                .filter(transaction -> transactionToCreate.getReferenceId().equals(transaction.getReferenceId()))
                .toList();

        Transaction authorizedTransactionForCurrentPayment = existingTransactionsForCurrentPayment.stream()
                .filter(transaction -> transaction.getTransactionType().equals(TransactionType.AUTHORIZE)).findFirst()
                .orElseThrow(() -> new CustomServiceException(TRANSACTION_MISSING_AUTHORIZATION.getCode(),
                        "Transaction is not authorized."));

        authorizedTransactionForCurrentPayment.setStatus(TransactionStatus.REVERSED);
        transactionRepository.save(authorizedTransactionForCurrentPayment);
        transactionToCreate.setMerchant(merchantForCurrentTransaction);
        transactionToCreate.setTransactionTime(Instant.now());
        transactionToCreate.setStatus(TransactionStatus.APPROVED);
        return transactionRepository.save(transactionToCreate);
    }

    private void updateMerchantTotalTransactionSum(Merchant merchant, BigDecimal transactionAmount) {
        merchant.setTotalTransactionSum(merchant.getTotalTransactionSum().add(transactionAmount));
        merchantRepository.save(merchant);
    }

    private void verifyTransactionAmountPositive(BigDecimal transactionAmount){
        if(transactionAmount.compareTo(ZERO) <= 0) {
            throw new CustomServiceException(TRANSACTION_INVALID_AMOUNT.getCode(),
                    "Charge transaction should have valid positive amount.");
        }
    }
}
