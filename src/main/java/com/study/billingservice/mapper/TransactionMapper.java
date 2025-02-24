package com.study.billingservice.mapper;

import com.study.billingservice.dto.TransactionHistoryDto;
import com.study.billingservice.entity.TransactionHistory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionHistoryDto entityToDto(TransactionHistory transactionHistory);
}
