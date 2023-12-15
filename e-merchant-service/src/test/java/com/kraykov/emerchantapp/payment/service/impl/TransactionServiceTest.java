package com.kraykov.emerchantapp.payment.service.impl;

import com.kraykov.emerchantapp.payment.model.Transaction;
import com.kraykov.emerchantapp.payment.model.TransactionStatus;
import com.kraykov.emerchantapp.payment.model.TransactionType;
import com.kraykov.emerchantapp.payment.model.exception.CustomServiceException;
import com.kraykov.emerchantapp.payment.model.user.Merchant;
import com.kraykov.emerchantapp.payment.repository.MerchantRepository;
import com.kraykov.emerchantapp.payment.repository.TransactionRepository;
import com.kraykov.emerchantapp.payment.service.api.IMerchantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.kraykov.emerchantapp.payment.model.TransactionType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private MerchantRepository merchantRepository;

    @Mock
    private IMerchantService merchantService;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetTransactionsForMerchantId() {
        Long merchantId = 1L;
        List<Transaction> expectedTransactions = List.of(new Transaction());
        when(transactionRepository.findByMerchantId(merchantId)).thenReturn(expectedTransactions);

        List<Transaction> actualTransactions = transactionService.getTransactionsForMerchantId(merchantId);

        assertEquals(expectedTransactions, actualTransactions);
        verify(transactionRepository).findByMerchantId(merchantId);
    }

    @Test
    public void testCreateAuthorizeTransaction() {
        Long merchantId = 1L;
        Transaction transactionToCreate = Transaction.builder()
                .transactionType(AUTHORIZE)
                .phone("0884442424")
                .customerEmail("test@gmail.com")
                .status(TransactionStatus.APPROVED)
                .build();

        Merchant mockMerchant = new Merchant();
        mockMerchant.setId(merchantId);

        when(merchantService.getMerchantById(merchantId)).thenReturn(mockMerchant);
        when(transactionRepository.findByMerchantId(merchantId)).thenReturn(List.of());
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);

        Transaction createdTransaction = transactionService.createTransaction(merchantId, transactionToCreate);

        assertNotNull(createdTransaction);
        assertEquals(AUTHORIZE, createdTransaction.getTransactionType());
        assertEquals("0884442424", createdTransaction.getPhone());
        assertEquals("test@gmail.com", createdTransaction.getCustomerEmail());
    }

    @Test
    public void testCreateChargeTransaction() {
        Long merchantId = 1L;
        Transaction transactionToCreate = Transaction.builder()
                .transactionType(CHARGE)
                .phone("0884442424")
                .customerEmail("test@gmail.com")
                .amount(new BigDecimal("10.00"))
                .referenceId(3434L)
                .status(TransactionStatus.APPROVED)
                .build();

        Merchant mockMerchant = new Merchant();
        mockMerchant.setId(merchantId);
        mockMerchant.setTotalTransactionSum(new BigDecimal("253.00"));

        when(merchantService.getMerchantById(merchantId)).thenReturn(mockMerchant);
        when(transactionRepository.findFirstByMerchantIdAndTransactionType(merchantId, AUTHORIZE)).thenReturn(Optional.ofNullable(Transaction.builder()
                .transactionType(AUTHORIZE)
                .status(TransactionStatus.APPROVED)
                .build()));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);

        Transaction createdTransaction = transactionService.createTransaction(merchantId, transactionToCreate);

        assertNotNull(createdTransaction);
        assertEquals(CHARGE, createdTransaction.getTransactionType());
    }

    @Test
    public void testRefundTransactionAlreadyRefunded() {
        Long merchantId = 1L;
        Transaction transactionToRefund = Transaction.builder()
                .transactionType(TransactionType.REFUND)
                .referenceId(25L)
                .build();

        Merchant mockMerchant = new Merchant();
        mockMerchant.setId(merchantId);

        Transaction authorizedTransaction = Transaction.builder()
                .transactionType(AUTHORIZE)
                .status(TransactionStatus.APPROVED)
                .build();
        Transaction refundedTransaction = Transaction.builder()
                .transactionType(REFUND)
                .status(TransactionStatus.APPROVED)
                .referenceId(2434L)
                .build();

        when(merchantService.getMerchantById(merchantId)).thenReturn(mockMerchant);
        when(transactionRepository.findByMerchantId(merchantId)).thenReturn(List.of(authorizedTransaction));
        when(transactionRepository.findByMerchantIdAndReferenceId(merchantId, 2434L)).thenReturn(List.of(refundedTransaction));

        CustomServiceException thrownException = getExceptionThrown(merchantId, transactionToRefund);

        assertTrue(thrownException.getMessage().contains("Payment has already been refunded."));
    }

    @Test
    public void testChargeTransactionMissingAuthorization() {
        Long merchantId = 1L;
        Transaction transactionToCharge = Transaction.builder()
                .transactionType(TransactionType.CHARGE)
                .referenceId(26L)
                .amount(BigDecimal.valueOf(100))
                .build();

        Merchant mockMerchant = new Merchant();
        mockMerchant.setId(merchantId);

        // Assuming no existing transactions with the given reference ID
        when(merchantService.getMerchantById(merchantId)).thenReturn(mockMerchant);
        when(transactionRepository.findByMerchantId(merchantId)).thenReturn(List.of());

        CustomServiceException thrownException = getExceptionThrown(merchantId, transactionToCharge);

        assertTrue(thrownException.getMessage().contains("Transaction is not authorized."));
    }

    @Test
    public void testRefundTransactionMissingCharge() {
        Long merchantId = 1L;
        Transaction transactionToRefund = Transaction.builder()
                .transactionType(TransactionType.REFUND)
                .referenceId(26L)
                .amount(BigDecimal.valueOf(100))
                .build();

        Merchant mockMerchant = new Merchant();
        mockMerchant.setId(merchantId);

        when(merchantService.getMerchantById(merchantId)).thenReturn(mockMerchant);
        when(transactionRepository.findFirstByMerchantIdAndTransactionType(merchantId, AUTHORIZE)).thenReturn(Optional.ofNullable(Transaction.builder()
                .transactionType(AUTHORIZE)
                .status(TransactionStatus.APPROVED)
                .build()));

        CustomServiceException thrownException = getExceptionThrown(merchantId, transactionToRefund);

        assertTrue(thrownException.getMessage()
                .contains("Cannot refund payment which doesn't have a valid Charge transaction"));
    }

    @Test
    public void testRefundTransactionWithReversedState() {
        Long merchantId = 1L;
        Transaction transactionToRefund = Transaction.builder()
                .transactionType(TransactionType.REFUND)
                .referenceId(26L)
                .amount(BigDecimal.valueOf(100))
                .build();

        Merchant mockMerchant = new Merchant();
        mockMerchant.setId(merchantId);

        when(merchantService.getMerchantById(merchantId)).thenReturn(mockMerchant);
        when(transactionRepository.findFirstByMerchantIdAndTransactionType(merchantId, AUTHORIZE)).thenReturn(Optional.ofNullable(Transaction.builder()
                .transactionType(AUTHORIZE)
                .status(TransactionStatus.REVERSED)
                .build()));

        CustomServiceException thrownException = getExceptionThrown(merchantId, transactionToRefund);

        assertTrue(thrownException.getMessage()
                .contains("Authorization transaction has been reversed. Payment is denied."));
    }

    @Test
    public void testReversalTransactionWithMissingAuthorization() {
        Long merchantId = 1L;
        Transaction reversalTransaction = Transaction.builder()
                .transactionType(TransactionType.REVERSAL)
                .status(TransactionStatus.APPROVED)
                .build();

        Merchant mockMerchant = new Merchant();
        mockMerchant.setId(merchantId);

        when(merchantService.getMerchantById(merchantId)).thenReturn(mockMerchant);
        when(transactionRepository.findFirstByMerchantIdAndTransactionType(merchantId, AUTHORIZE)).thenReturn(Optional.empty());

        CustomServiceException thrownException = getExceptionThrown(merchantId, reversalTransaction);

        assertTrue(thrownException.getMessage()
                .contains("Cannot create reversal transaction when authorization transaction is missing."));
    }

    @Test
    public void testChargeTransactionWithMissingReferenceId() {
        Long merchantId = 1L;
        Transaction transactionToCharge = Transaction.builder()
                .transactionType(TransactionType.CHARGE)
                .amount(BigDecimal.valueOf(100))
                .build();

        Merchant mockMerchant = new Merchant();
        mockMerchant.setId(merchantId);

        when(merchantService.getMerchantById(merchantId)).thenReturn(mockMerchant);
        when(transactionRepository.findFirstByMerchantIdAndTransactionType(merchantId, AUTHORIZE)).thenReturn(Optional.ofNullable(Transaction.builder()
                .transactionType(AUTHORIZE)
                .status(TransactionStatus.APPROVED)
                .build()));

        CustomServiceException thrownException = getExceptionThrown(merchantId, transactionToCharge);

        assertTrue(thrownException.getMessage()
                .contains("Reference ID should be provided for the current transaction"));
    }

    @Test
    public void testChargeTransactionInvalidAmount() {
        Long merchantId = 1L;
        Transaction transactionWithInvalidAmount = Transaction.builder()
                .transactionType(TransactionType.CHARGE)
                .referenceId(27L)
                .amount(BigDecimal.valueOf(-100)) // Invalid amount
                .build();

        Merchant mockMerchant = new Merchant();
        mockMerchant.setId(merchantId);

        when(merchantService.getMerchantById(merchantId)).thenReturn(mockMerchant);

        CustomServiceException thrownException = getExceptionThrown(merchantId, transactionWithInvalidAmount);

        assertTrue(thrownException.getMessage().contains("Charge transaction should have valid positive amount."));
    }

    private CustomServiceException getExceptionThrown(Long merchantId, Transaction transaction) {
        return assertThrows(
                CustomServiceException.class,
                () -> transactionService.createTransaction(merchantId, transaction),
                "Expected createTransaction to throw, but it didn't"
        );
    }
}
