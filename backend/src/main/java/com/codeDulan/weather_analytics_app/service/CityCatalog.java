package com.codeDulan.weather_analytics_app.service;

import com.codeDulan.weather_analytics_app.config.OpenWeatherProperties;
import com.codeDulan.weather_analytics_app.model.City;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CityCatalog {

    private final OpenWeatherProperties properties;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    private List<City> cities;

    @PostConstruct
    void load() {
        Resource resource = resourceLoader.getResource(properties.citiesResources());
        if(!resource.exists()){
            throw new IllegalStateException(
                    "cities files not found at: " + properties.citiesResources()
            );
        }

        try (InputStream in = resource.getInputStream()) {
            CitiesFile file = objectMapper.readValue(in, CitiesFile.class);
            if(file == null || file.list() == null || file.list.isEmpty()){
                throw new IllegalStateException(
                        "Cities file contains no entries: " + properties.citiesResources()
                );
            }
            this.cities = file.list.stream()
                    .map(entry -> new City(entry.cityCode(), entry.cityName()))
                    .distinct()
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Failed to read cities file: " + properties.citiesResources(), e
            );
        }

        log.info("Loaded {} cities from {}", cities.size(), properties.citiesResources());
    }

    public List<City> cities() {
        return cities;
    }

    private record CitiesFile(@JsonProperty("List") List<CityEntry> list) {

    }

    private record CityEntry(
            @JsonProperty("CityCode") String cityCode,
            @JsonProperty("CityName") String cityName
    ){}

}
