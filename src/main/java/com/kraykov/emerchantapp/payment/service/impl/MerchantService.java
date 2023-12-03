package com.kraykov.emerchantapp.payment.service.impl;

import com.kraykov.emerchantapp.payment.model.user.Merchant;
import com.kraykov.emerchantapp.payment.model.user.User;
import com.kraykov.emerchantapp.payment.repository.UserRepository;
import com.kraykov.emerchantapp.payment.service.api.IMerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantService implements IMerchantService {
    private final UserRepository merchantRepository;

    @Autowired
    public MerchantService(UserRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    @Override
    public List<User> getAllMerchants() {
        return merchantRepository.findAll();
    }
}
