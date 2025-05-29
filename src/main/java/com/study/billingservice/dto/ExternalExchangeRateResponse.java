package com.study.billingservice.dto;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class ExternalExchangeRateResponse {

    private boolean success;
    private String base;
    private Date date;
    private Map<String, Double> rates;

}
