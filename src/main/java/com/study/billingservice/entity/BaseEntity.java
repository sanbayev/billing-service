package com.study.billingservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    @Column(name = "create_date", nullable = false, updatable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createDate;

    @Column(name = "update_date", nullable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime updateDate;
}
