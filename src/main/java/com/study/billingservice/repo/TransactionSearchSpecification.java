package com.study.billingservice.repo;

import com.study.billingservice.dto.RequestTransactionDto;
import com.study.billingservice.entity.TransactionHistory;
import com.study.billingservice.enums.TransactionType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TransactionSearchSpecification implements Specification<TransactionHistory> {

    private static final String FROM_USER_ID_FIELD = "fromUserId";
    private static final String TO_USER_ID_FIELD = "toUserId";
    private RequestTransactionDto requestTransactionDto;

    public TransactionSearchSpecification(RequestTransactionDto requestTransactionDto) {
        this.requestTransactionDto = requestTransactionDto;
    }

    @Override
    public Predicate toPredicate(Root<TransactionHistory> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        final List<Predicate> predicates = new ArrayList<>();
        TransactionType transactionType = requestTransactionDto.transactionType();
        Long searchUserId = requestTransactionDto.userId();

        if (transactionType == TransactionType.IN) {
            predicates.add(builder.equal(root.get(TO_USER_ID_FIELD), searchUserId));
        }
        if (transactionType == TransactionType.OUT) {
            predicates.add(builder.equal(root.get(FROM_USER_ID_FIELD), searchUserId));
        }

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
