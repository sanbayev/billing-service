package com.study.billingservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionSortType {
    DEFAULT("id"), DATE("updateDate"), AMOUNT("amount");

    private String value;
}
