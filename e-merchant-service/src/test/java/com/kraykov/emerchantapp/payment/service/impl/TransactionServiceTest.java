package com.kraykov.emerchantapp.payment.service.impl;

import com.kraykov.emerchantapp.payment.model.Transaction;
import com.kraykov.emerchantapp.payment.model.TransactionType;
import com.kraykov.emerchantapp.payment.model.exception.CustomServiceException;
import com.kraykov.emerchantapp.payment.model.user.Merchant;
import com.kraykov.emerchantapp.payment.model.user.TransactionStatus;
import com.kraykov.emerchantapp.payment.repository.TransactionRepository;
import com.kraykov.emerchantapp.payment.service.api.IMerchantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static com.kraykov.emerchantapp.payment.model.TransactionType.AUTHORIZE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

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
        transactionToCreate.setTransactionType(AUTHORIZE);

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
    public void testRefundTransactionAlreadyRefunded() {
        Long merchantId = 1L;
        Transaction transactionToRefund = Transaction.builder()
                .transactionType(TransactionType.REFUND)
                .referenceId(25L)
                .build();

        Merchant mockMerchant = new Merchant();
        mockMerchant.setId(merchantId);

        Transaction mockChargedTransaction = new Transaction();
        mockChargedTransaction.setTransactionType(TransactionType.CHARGE);
        mockChargedTransaction.setStatus(TransactionStatus.REFUNDED);
        mockChargedTransaction.setReferenceId(25L);

        when(merchantService.getMerchantById(merchantId)).thenReturn(mockMerchant);
        when(transactionRepository.findByMerchantId(merchantId)).thenReturn(List.of(mockChargedTransaction));

        CustomServiceException thrownException = assertThrows(
                CustomServiceException.class,
                () -> transactionService.createTransaction(merchantId, transactionToRefund),
                "Expected createTransaction to throw, but it didn't"
        );

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

        CustomServiceException thrownException = assertThrows(
                CustomServiceException.class,
                () -> transactionService.createTransaction(merchantId, transactionToCharge),
                "Expected createTransaction to throw, but it didn't"
        );

        assertTrue(thrownException.getMessage().contains("Transaction is not authorized."));
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

        CustomServiceException thrownException = assertThrows(
                CustomServiceException.class,
                () -> transactionService.createTransaction(merchantId, transactionWithInvalidAmount),
                "Expected createTransaction to throw, but it didn't"
        );

        assertTrue(thrownException.getMessage().contains("Charge transaction should have valid positive amount."));
    }
}
