package com.study.billingservice.dto;

import com.study.billingservice.enums.BalanceOperationType;
import io.swagger.annotations.ApiModel;

import java.math.BigDecimal;

@ApiModel("Данные для обновления баланса")
public record TransferRequestDto(

        Long toUserId,

        BigDecimal amount,
        String comment,

        BalanceOperationType balanceOperationType
) {
}
