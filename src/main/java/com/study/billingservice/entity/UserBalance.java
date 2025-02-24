package com.study.billingservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_balance")
public class UserBalance extends BaseEntity {

    @Column(name = "user_id", unique = true)
    private Long userId;

    @Column(nullable = false, precision = 18, scale = 6)
    private BigDecimal amount;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    public void addBalance(BigDecimal additionalAmount) {
        this.amount = this.amount.add(additionalAmount).setScale(6, RoundingMode.HALF_UP);
    }

    public void subtractBalance(BigDecimal additionalAmount) {
        this.amount = this.amount.subtract(additionalAmount).setScale(6, RoundingMode.HALF_UP);
    }
}
