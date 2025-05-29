package com.study.billingservice.conf;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "external-exchange")
@Getter
@Setter
public class ExternalExchangeProperties {

    private String baseUrl;
    private String accessKey;
    private String baseCurrency;
    private List<String> supportedCurrencies;
}
