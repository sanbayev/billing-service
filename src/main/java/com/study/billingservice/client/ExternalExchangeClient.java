package com.study.billingservice.client;

import com.study.billingservice.conf.ExternalExchangeProperties;
import com.study.billingservice.dto.ExternalExchangeRateResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ExternalExchangeClient {

    private final WebClient webClient;
    private final ExternalExchangeProperties externalExchangeProperties;

    public ExternalExchangeClient(WebClient.Builder builder,
                                  ExternalExchangeProperties externalExchangeProperties) {
        this.externalExchangeProperties = externalExchangeProperties;
        this.webClient = builder.baseUrl(externalExchangeProperties.getBaseUrl()).build();
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
                .bodyToMono(ExternalExchangeRateResponse.class);
    }
}
