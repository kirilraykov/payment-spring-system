package com.kraykov.emerchantapp.payment.service.api;

import com.kraykov.emerchantapp.payment.model.user.Merchant;
import com.kraykov.emerchantapp.payment.model.user.User;

import java.util.List;

public interface IMerchantService {
    List<User> getAllMerchants();
}
