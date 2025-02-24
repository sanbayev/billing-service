package com.study.billingservice.service;

import com.study.billingservice.dto.BalanceInfoDto;
import com.study.billingservice.dto.RequestTransactionDto;
import com.study.billingservice.dto.TransactionHistoryDto;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface UserBalanceService {

    BalanceInfoDto getBalanceInfo(Long userId, String currency);

    BalanceInfoDto addBalance(Long userId, BigDecimal amount, String comment);

    BalanceInfoDto subtractBalance(Long userId, BigDecimal amount, String comment);

    BalanceInfoDto transferBalance(Long fromUserId, Long toUserId, BigDecimal amount, String comment);

    Page<TransactionHistoryDto> getTransactionHistoryList(RequestTransactionDto requestTransactionDto);
}


