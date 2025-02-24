package com.study.billingservice.dto;

import io.swagger.annotations.ApiModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ApiModel("История переводов")
public record TransactionHistoryDto(
        LocalDateTime createDate, Long fromUserId, Long toUserId, BigDecimal amount) {
}
