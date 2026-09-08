package com.codeDulan.weather_analytics_app.config;

import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RestClientConfig {

    @Bean
    RestClientCustomizer openWeatherTimeoutCustomizer(OpenWeatherProperties properties) {
        HttpClientSettings settings = HttpClientSettings.defaults()
                .withConnectTimeout(properties.requestTimeOut())
                .withReadTimeout(properties.requestTimeOut());

        return builder -> builder.requestFactory(
                ClientHttpRequestFactoryBuilder.detect().build(settings)
        );


    }

}
