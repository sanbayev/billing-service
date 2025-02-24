package com.study.billingservice.repo;

import com.study.billingservice.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TransactionHistoryRepo extends JpaRepository<TransactionHistory, Long>, JpaSpecificationExecutor<TransactionHistory> {

    List<TransactionHistory> findAllByFromUserId(Long fromUserId);

    List<TransactionHistory> findAllByToUserId(Long toUserId);
}
