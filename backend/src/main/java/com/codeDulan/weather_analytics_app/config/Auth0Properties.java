package com.codeDulan.weather_analytics_app.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "auth0")
public record Auth0Properties (

    @NotBlank(message = "auth0.domain must be set")
    String domain,

    @NotBlank(message = "auth0.audience must be set")
    String audience

    ){

    public String issuerUri() {
        return "https://" + domain + "/";
    }
}
