package com.study.billingservice.repo;

import com.study.billingservice.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRateRepo extends JpaRepository<ExchangeRate, Long> {

    
}
