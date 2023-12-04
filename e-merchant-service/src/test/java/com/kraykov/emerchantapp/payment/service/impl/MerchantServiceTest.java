package com.kraykov.emerchantapp.payment.service.impl;

import com.kraykov.emerchantapp.payment.model.exception.CustomServiceException;
import com.kraykov.emerchantapp.payment.model.user.Merchant;
import com.kraykov.emerchantapp.payment.model.user.UserSignupResponse;
import com.kraykov.emerchantapp.payment.repository.MerchantRepository;
import com.kraykov.emerchantapp.payment.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MerchantServiceTest {

    @InjectMocks
    private MerchantService merchantService;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private MerchantRepository merchantRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindMerchantById() {
        Long merchantId = 1L;
        Merchant mockMerchant = new Merchant(); // Populate mockMerchant as needed
        when(merchantRepository.findById(merchantId)).thenReturn(Optional.of(mockMerchant));

        Merchant foundMerchant = merchantService.getMerchantById(merchantId);

        assertNotNull(foundMerchant);
        assertEquals(mockMerchant, foundMerchant);
        verify(merchantRepository).findById(merchantId);
    }

    @Test
    public void testCreateMerchant() {
        Merchant newMerchant = new Merchant();
        when(userRepository.save(any(Merchant.class))).thenReturn(newMerchant);

        UserSignupResponse signupResponse = userService.registerUser(newMerchant);

        assertNotNull(signupResponse);
        verify(userRepository).save(newMerchant);
    }

    @Test
    public void testFindMerchantByIdNotFound() {
        Long merchantId = 1L;
        when(merchantRepository.findById(merchantId)).thenReturn(Optional.empty());

        assertThrows(CustomServiceException.class, () -> merchantService.getMerchantById(merchantId));
    }

    @Test
    public void testFindMerchantByEmailNotFound() {
        String nonExistingEmail = "NonExistingEmail@gmail.com";
        when(merchantRepository.getMerchantsByEmail(nonExistingEmail)).thenReturn(Optional.empty());

        assertThrows(CustomServiceException.class, () -> merchantService.getMerchantByEmail(nonExistingEmail));
    }
}
