package com.kraykov.emerchantapp.payment.service.impl;

import com.kraykov.emerchantapp.payment.model.Transaction;
import com.kraykov.emerchantapp.payment.model.TransactionStatus;
import com.kraykov.emerchantapp.payment.model.TransactionType;
import com.kraykov.emerchantapp.payment.model.exception.CustomServiceException;
import com.kraykov.emerchantapp.payment.model.user.Merchant;
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
import static com.kraykov.emerchantapp.payment.model.TransactionStatus.*;
import static com.kraykov.emerchantapp.payment.model.TransactionType.AUTHORIZE;
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
     * Check if this merchant is authorized.
     * If yes, throw error that the merchant has already been authorized
     * If no, create the new transaction with type Authorize and status approved
     * @param merchantId - existing merchant ID
     * @param transactionToCreate - the transaction to be created
     * @return the created transaction (if creation has succeeded)
     */
    private Transaction processAuthorizeTransaction(Long merchantId, Transaction transactionToCreate) {
        Merchant merchantForCurrentTransaction = merchantService.getMerchantById(merchantId);
        boolean isMerchantAuthorized = transactionRepository
                .findFirstByMerchantIdAndTransactionType(merchantId, AUTHORIZE).isPresent();

        transactionToCreate.setMerchant(merchantForCurrentTransaction);
        transactionToCreate.setTransactionTime(Instant.now());

        if(!isMerchantAuthorized) {
            transactionToCreate.setStatus(APPROVED);
            return transactionRepository.save(transactionToCreate);
        } else {
            transactionToCreate.setStatus(ERROR);
            transactionRepository.save(transactionToCreate);
            throw new CustomServiceException(MERCHANT_ALREADY_AUTHORIZED_TRANSACTION.getCode(),
                    "Merchant has already been authorized.");
        }
    }

    /**
     * Get the merchant for the provided merchantId.
     * Get all transactions for the current reference ID and merchant.
     * Check if there are any transactions for that reference that are not ERRORS - either charge or refund with status approved.
     * If yes, that means that the transaction has been completed.
     * If not, create the Charge transaction with status APPROVED
     * @param merchantId - existing merchant ID
     * @param transactionToCreate - the transaction to be created
     * @return the created transaction (if creation has succeeded)
     */
    private Transaction processChargeTransaction(Long merchantId, Transaction transactionToCreate) {
        verifyTransactionAmountPositive(transactionToCreate.getAmount());
        verifyReferenceId(transactionToCreate.getReferenceId());
        verifyMerchantAuthorizedForPayment(merchantId);

        Merchant merchantForCurrentTransaction = merchantService.getMerchantById(merchantId);
        List<Transaction> existingTransactionsForCurrentPayment = transactionRepository.findByMerchantIdAndReferenceId(
                merchantId, transactionToCreate.getReferenceId());

        transactionToCreate.setMerchant(merchantForCurrentTransaction);
        transactionToCreate.setTransactionTime(Instant.now());

        boolean isExistingPaymentCompleted = existingTransactionsForCurrentPayment.stream().anyMatch(
                transaction -> !ERROR.equals(transaction.getStatus()));

        if(!isExistingPaymentCompleted) {
            updateMerchantTotalTransactionSum(merchantForCurrentTransaction, transactionToCreate.getAmount());
            transactionToCreate.setStatus(APPROVED);
            return transactionRepository.save(transactionToCreate);
        }
        else {
            transactionToCreate.setStatus(ERROR);
            transactionRepository.save(transactionToCreate);
            throw new CustomServiceException(TRANSACTION_ALREADY_CHARGED.getCode(),
                    "Transaction has been completed. It has already been charged or refunded.");
        }
    }

    /**
     * Get all merchant's transactions for the current referenceId.
     * Check if those contain valid Charge transaction with status APPROVED.
     * IF yes, continue and refund the transaction with check whether refund has already been done.
     * @param merchantId - existing merchant ID
     * @param transactionToCreate - the transaction to be created
     * @return the created transaction (if creation has succeeded)
     */
    private Transaction processRefundTransaction(Long merchantId, Transaction transactionToCreate) {
        verifyReferenceId(transactionToCreate.getReferenceId());
        verifyMerchantAuthorizedForPayment(merchantId);

        Merchant merchantForCurrentTransaction = merchantService.getMerchantById(merchantId);
        List<Transaction> existingTransactionsForCurrentPayment = transactionRepository.findByMerchantIdAndReferenceId(
                merchantId, transactionToCreate.getReferenceId());

        Transaction chargeTransaction = existingTransactionsForCurrentPayment.stream()
                .filter(transaction -> TransactionType.CHARGE.equals(transaction.getTransactionType())
                        && APPROVED.equals(transaction.getStatus()))
                .findAny().orElseThrow(() -> new CustomServiceException(TRANSACTION_MISSING_PAYMENT.getCode(),
                        "Cannot refund payment which doesn't have a valid Charge transaction"));

        TransactionStatus chargeTransactionStatus = chargeTransaction.getStatus();
        transactionToCreate.setMerchant(merchantForCurrentTransaction);
        transactionToCreate.setTransactionTime(Instant.now());

        if(!REFUNDED.equals(chargeTransactionStatus)) {
            updateMerchantTotalTransactionSum(merchantForCurrentTransaction, transactionToCreate.getAmount().negate());
            chargeTransaction.setStatus(REFUNDED);
            transactionRepository.save(chargeTransaction);
            transactionToCreate.setStatus(APPROVED);
            return transactionRepository.save(transactionToCreate);
        } else {
            transactionToCreate.setStatus(ERROR);
            transactionRepository.save(transactionToCreate);

            throw new CustomServiceException(TRANSACTION_ALREADY_REFUNDED.getCode(),
                    "Payment has already been refunded.");
        }
    }

    /**
     * Check if there is an authorize transaction for the current mechant ID
     * If yes, set it to reversed.
     * If not, throw error that the transaction needs to be authorized first.
     * @param merchantId - existing merchant ID
     * @param transactionToCreate - the transaction to be created
     * @return the created transaction (if creation has succeeded)
     */
    private Transaction processReversalTransaction(Long merchantId, Transaction transactionToCreate) {
        Merchant merchantForCurrentTransaction = merchantService.getMerchantById(merchantId);

        Transaction authorizedTransaction = transactionRepository.findFirstByMerchantIdAndTransactionType(merchantId, AUTHORIZE)
                .orElseThrow(() -> new CustomServiceException(TRANSACTION_MISSING_AUTHORIZATION.getCode(),
                        "Transaction is not authorized."));

        authorizedTransaction.setStatus(REVERSED);
        transactionRepository.save(authorizedTransaction);

        transactionToCreate.setMerchant(merchantForCurrentTransaction);
        transactionToCreate.setTransactionTime(Instant.now());
        transactionToCreate.setStatus(APPROVED);
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

    private void verifyMerchantAuthorizedForPayment(Long merchantId) {
        Transaction authorizedTransaction = transactionRepository.findFirstByMerchantIdAndTransactionType(merchantId, AUTHORIZE)
                .orElseThrow(() -> new CustomServiceException(TRANSACTION_MISSING_AUTHORIZATION.getCode(),
                        "Transaction is not authorized."));

        if(REVERSED.equals(authorizedTransaction.getStatus())){
            throw new CustomServiceException(AUTHORIZATION_REVERSED.getCode(),
                    "Authorization transaction has been reversed. Payment is denied.");
        }
    }

    private void verifyReferenceId(Long referenceId) {
        if(isNull(referenceId)) {
            throw new CustomServiceException(REFERENCE_ID_MISSING.getCode(),
                    "Reference ID should be provided for the current transaction");
        }
    }
}
