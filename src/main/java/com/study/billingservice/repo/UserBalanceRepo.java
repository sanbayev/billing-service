package com.study.billingservice.repo;

import com.study.billingservice.entity.UserBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserBalanceRepo extends JpaRepository<UserBalance, Long> {

    Optional<UserBalance> findByUserId(Long userId);
}
