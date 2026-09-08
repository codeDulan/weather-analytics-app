package com.codeDulan.weather_analytics_app.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "openweather")
public record OpenWeatherProperties(

        @NotBlank(message = "openweather.api-key must be set (env OPENWEATHER_API_KEY)")
        String apiKey,

        @NotBlank
        @DefaultValue("https://api.openweathermap.org/data/2.5")
        String baseUrl,

        @NotBlank
        @DefaultValue("metric")
        String units,

        @NotBlank
        @DefaultValue("classpath:data/cities.json")
        String citiesResources,

        @NotNull
        @DefaultValue("5m")
        Duration rawCacheTtl,

        @NotNull
        @DefaultValue("5m")
        Duration processedCacheTtl,

        @NotNull
        @DefaultValue("5s")
        Duration requestTimeOut

) {
}