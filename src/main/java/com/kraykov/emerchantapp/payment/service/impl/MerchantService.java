package com.kraykov.emerchantapp.payment.service.impl;

import com.kraykov.emerchantapp.payment.model.exception.CustomServiceException;
import com.kraykov.emerchantapp.payment.model.user.Merchant;
import com.kraykov.emerchantapp.payment.repository.MerchantRepository;
import com.kraykov.emerchantapp.payment.service.api.IMerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.kraykov.emerchantapp.payment.exception.ErrorCodes.MISSING_MERCHANT_WITH_EMAIL;
import static com.kraykov.emerchantapp.payment.exception.ErrorCodes.MISSING_MERCHANT_WITH_ID;

@Service
public class MerchantService implements IMerchantService {
    private final MerchantRepository merchantRepository;

    @Autowired
    public MerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    @Override
    public List<Merchant> getAllMerchants() {
        return merchantRepository.findAll();
    }

    @Override
    public Merchant getMerchantByEmail(String merchantEmail) {
        return merchantRepository.getMerchantsByEmail(merchantEmail)
                .orElseThrow(() ->
                        new CustomServiceException(MISSING_MERCHANT_WITH_EMAIL.getCode(),
                                "Missing Merchant with email: " + merchantEmail));
    }

    @Override
    public Merchant getMerchantById(Long merchantId) {
        return merchantRepository.findById(merchantId)
                .orElseThrow(() -> new CustomServiceException(MISSING_MERCHANT_WITH_ID.getCode(),
                        "No merchant exists for provided id: " + merchantId));
    }
}
