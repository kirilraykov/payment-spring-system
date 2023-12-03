package com.kraykov.emerchantapp.payment.repository;

import com.kraykov.emerchantapp.payment.model.MerchantStatus;
import com.kraykov.emerchantapp.payment.model.user.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    Optional<Merchant> getMerchantsByEmail(String email);
    Optional<Merchant> findByIdAndStatus(Long id, MerchantStatus status);
}
