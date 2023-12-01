package com.kraykov.emerchantapp.payment.repository;

import com.kraykov.emerchantapp.payment.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    List<Merchant> findByName(String name);
}
