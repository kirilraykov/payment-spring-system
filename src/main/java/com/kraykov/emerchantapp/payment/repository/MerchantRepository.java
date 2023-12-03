package com.kraykov.emerchantapp.payment.repository;

import com.kraykov.emerchantapp.payment.model.user.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    Merchant getMerchantsByEmail(String email);
}
