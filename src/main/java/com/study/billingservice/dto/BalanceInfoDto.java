package com.study.billingservice.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel("Данные баланса")
public record BalanceInfoDto(
        @ApiModelProperty("Сумма")
        BigDecimal amount,

        @ApiModelProperty("Валюта")
        String currency) {
}
