package com.study.billingservice.service.impl;

import com.study.billingservice.dto.ExternalExchangeRateResponse;
import com.study.billingservice.entity.ExchangeRate;
import com.study.billingservice.repo.ExchangeRateRepo;
import com.study.billingservice.service.ExchangeRateService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateRepo exchangeRateRepo;

    public ExchangeRateServiceImpl(ExchangeRateRepo exchangeRateRepo) {
        this.exchangeRateRepo = exchangeRateRepo;
    }

    @Override
    public void save(ExternalExchangeRateResponse externalExchangeRateResponse) {
        String baseCurrency = externalExchangeRateResponse.getBase();
        Map<String, Double> ratesMap = externalExchangeRateResponse.getRates();
        ratesMap.forEach((targetCurrency, currencyRate) -> {
            BigDecimal convertedRate = BigDecimal.valueOf(currencyRate);
            Optional<ExchangeRate> exchangeRateOptional = exchangeRateRepo.findByBaseCurrencyAndTargetCurrency(baseCurrency, targetCurrency);
            if (exchangeRateOptional.isEmpty()) {
                ExchangeRate exchangeRate = createEntity(baseCurrency, targetCurrency, convertedRate);
                exchangeRateRepo.save(exchangeRate);
            } else {
                ExchangeRate existingRate = exchangeRateOptional.get();
                if (!existingRate.getRate().equals(convertedRate)) {
                    existingRate.setUpdateDate(LocalDateTime.now());
                    existingRate.setRate(convertedRate);
                    exchangeRateRepo.save(existingRate);
                }
            }
        });
    }

    private ExchangeRate createEntity(String baseCurrency, String targetCurrency, BigDecimal rate) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setCreateDate(LocalDateTime.now());
        exchangeRate.setUpdateDate(LocalDateTime.now());
        exchangeRate.setBaseCurrency(baseCurrency);
        exchangeRate.setTargetCurrency(targetCurrency);
        exchangeRate.setRate(rate);
        return exchangeRate;
    }
}
