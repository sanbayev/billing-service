package com.study.billingservice.service;

import com.study.billingservice.dto.BalanceInfoDto;
import com.study.billingservice.dto.RequestTransactionDto;
import com.study.billingservice.dto.TransactionHistoryDto;
import com.study.billingservice.dto.TransferRequestDto;
import org.springframework.data.domain.Page;

public interface UserBalanceService {

    BalanceInfoDto getBalanceInfo(Long userId, String currency);

    BalanceInfoDto addBalance(Long userId, TransferRequestDto transferRequestDto);

    BalanceInfoDto subtractBalance(Long userId, TransferRequestDto transferRequestDto);

    BalanceInfoDto transferBalance(Long fromUserId, TransferRequestDto transferRequestDto);

    Page<TransactionHistoryDto> getTransactionHistoryList(RequestTransactionDto requestTransactionDto);
}


