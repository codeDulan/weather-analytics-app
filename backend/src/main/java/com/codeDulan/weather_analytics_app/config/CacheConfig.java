package com.codeDulan.weather_analytics_app.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String RAW_WEATHER = "rawWeather";
    public static final String RANKED_CITIES = "rankedCities";

    private static final long RAW_WEATHER_MAX_ENTRIES = 500;

    @Bean
    CacheManager cacheManager(OpenWeatherProperties properties) {

        CaffeineCache rawWeather = new CaffeineCache(RAW_WEATHER,
                Caffeine.newBuilder()
                        .expireAfterWrite(properties.rawCacheTtl())
                        .maximumSize(RAW_WEATHER_MAX_ENTRIES)
                        .recordStats()
                        .build());

        CaffeineCache rankedCities = new CaffeineCache(RANKED_CITIES,
                Caffeine.newBuilder()
                        .expireAfterWrite(properties.processedCacheTtl())
                        .maximumSize(1)
                        .recordStats()
                        .build());

        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(List.of(rawWeather, rankedCities));
        manager.initializeCaches();
        return manager;

    }

}
