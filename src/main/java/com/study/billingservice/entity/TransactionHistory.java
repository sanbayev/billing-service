package com.study.billingservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transaction_history", indexes = {@Index(name = "idx_from_user_id", columnList = "from_user_id"),
        @Index(name = "idx_to_user_id", columnList = "to_user_id")})
public class TransactionHistory extends BaseEntity {


    @Column(name = "from_user_id")
    private Long fromUserId;

    @Column(name = "to_user_id")
    private Long toUserId;

    @Column(precision = 19, scale = 4)
    private BigDecimal amount;

    private String comment;
}
