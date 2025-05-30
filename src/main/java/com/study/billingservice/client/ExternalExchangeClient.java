package com.study.billingservice.client;

import com.study.billingservice.conf.ExternalExchangeProperties;
import com.study.billingservice.dto.ExternalExchangeRateResponse;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ExternalExchangeClient {

    private final WebClient webClient;
    private final ExternalExchangeProperties externalExchangeProperties;
    private final Retry retry;

    public ExternalExchangeClient(WebClient.Builder builder,
                                  ExternalExchangeProperties externalExchangeProperties,
                                  RetryRegistry retryRegistry) {
        this.externalExchangeProperties = externalExchangeProperties;
        this.webClient = builder.baseUrl(externalExchangeProperties.getBaseUrl()).build();
        this.retry = retryRegistry.retry("exchange-rate");
    }

    public Mono<ExternalExchangeRateResponse> getRates(String baseCurrency) {
        String symbols = String.join(",", externalExchangeProperties.getSupportedCurrencies());

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/latest")
                        .queryParam("base", baseCurrency)
                        .queryParam("access_key", externalExchangeProperties.getAccessKey())
                        .queryParam("symbols", symbols)
                        .queryParam("format", "1")
                        .build())
                .retrieve()
                .bodyToMono(ExternalExchangeRateResponse.class)
                .transformDeferred(RetryOperator.of(retry))
                .onErrorResume(error -> {
                    log.warn("Error fetching rates: {}", error.getMessage());
                    return Mono.empty();
                });
    }
}
