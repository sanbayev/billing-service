package com.study.billingservice.jobs;

import com.study.billingservice.client.ExternalExchangeClient;
import com.study.billingservice.conf.ExternalExchangeProperties;
import com.study.billingservice.service.ExchangeRateService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class ExchangeRateJob {

    private final ExternalExchangeClient client;
    private final ExchangeRateService exchangeRateService;
    private final ExternalExchangeProperties externalExchangeProperties;

    public ExchangeRateJob(ExternalExchangeClient client, ExchangeRateService exchangeRateService,
                           ExternalExchangeProperties externalExchangeProperties) {
        this.client = client;
        this.exchangeRateService = exchangeRateService;
        this.externalExchangeProperties = externalExchangeProperties;
    }

    @PostConstruct
    public void init() {
        fetchRates();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void fetchRates() {
        List<String> requestCurrencyList = Collections.singletonList(externalExchangeProperties.getBaseCurrency());
        requestCurrencyList.forEach(currency -> client.getRates(externalExchangeProperties.getBaseCurrency())
                .doOnNext(exchangeRateService::save)
                .doOnError(error -> log.info("Error fetching rates: {}", String.valueOf(error)))
                .subscribe());
    }
}
