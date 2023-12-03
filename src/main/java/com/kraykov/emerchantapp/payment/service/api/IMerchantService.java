package com.kraykov.emerchantapp.payment.service.api;

import com.kraykov.emerchantapp.payment.model.user.Merchant;

import java.util.List;

public interface IMerchantService {
    List<Merchant> getAllMerchants();
    Merchant getMerchantByEmail(String merchantEmail);
}
