package com.spom.service.frankieone;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "frankieone.onesdk")
@Data
public class FrankieOneSettings {
    private String customerId;
    private String apiKey;
    private String sessionUrl;
}
