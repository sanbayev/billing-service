package com.study.billingservice.dto;

import com.study.billingservice.enums.TransactionSortType;
import com.study.billingservice.enums.TransactionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Объект для получения списка транзакций")
public record RequestTransactionDto(

        Long userId,
        @ApiModelProperty("Тип поиска IN/OUT пользоветеля")
        TransactionType transactionType,
        @ApiModelProperty("Тип сортировки")
        TransactionSortType transactionSortType,
        @ApiModelProperty("Страница для получения")
        int page,

        @ApiModelProperty("Количество данных")
        int size) {
}
