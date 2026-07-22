package com.smartclinic.billing.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class PayOSConfig {

    @Value("${payos.client-id:demo-client-id}")
    private String clientId;

    @Value("${payos.api-key:demo-api-key}")
    private String apiKey;

    @Value("${payos.checksum-key:demo-checksum-key}")
    private String checksumKey;
}
