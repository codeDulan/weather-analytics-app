package com.codeDulan.weather_analytics_app.client;

import com.codeDulan.weather_analytics_app.config.OpenWeatherProperties;
import com.codeDulan.weather_analytics_app.model.City;
import com.codeDulan.weather_analytics_app.model.Weather;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Instant;


@Slf4j
@Component
public class OpenWeatherClient {

    private final RestClient restClient;
    private final OpenWeatherProperties properties;

    public OpenWeatherClient(RestClient.Builder restClientBuilder, OpenWeatherProperties properties) {
        this.properties = properties;
        this.restClient = restClientBuilder
                .baseUrl(properties.baseUrl())
                .build();
    }

    public Weather fetch(City city) {
        OwmWeatherResponse response;

        try{
            response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/weather")
                            .queryParam("id", city.code())
                            .queryParam("appid", properties.apiKey())
                            .queryParam("units", properties.units())
                            .build())
                    .retrieve()
                    .body(OwmWeatherResponse.class);
        }catch (RestClientException e) {
            throw new WeatherClientException(
                    "Failed to fetch weather for city %s (%s)".formatted(city.code(),city.name()), e);
        }

        if(response == null || response.main() == null || response.primaryWeather() == null) {
            throw new WeatherClientException(
                    "Incomplete weather payload for city %s (%s)".formatted(city.code(), city.name()));
        }

        return toWeather(city, response);

    }

    private Weather toWeather(City city, OwmWeatherResponse r) {
        OwmWeatherResponse.Weather w = r.primaryWeather();
        double windSpeed = r.wind() != null ? Math.max(0, r.wind().speed()) : 0;
        int cloudiness = r.clouds() != null ? clamp(r.clouds().all(), 0, 100) : 0;
        double humidity = clamp(r.main().humidity(), 0, 100);
        String country = r.sys() != null ? r.sys().country() : null;

        return new Weather(
                city.code(),
                city.name(),
                country,
                w.main(),
                w.description(),
                w.icon(),
                r.main().temp(),
                r.main().feelsLike(),
                humidity,
                r.main().pressure(),
                windSpeed,
                cloudiness,
                r.visibility(),
                Instant.ofEpochSecond(r.dt()));
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
