package com.codeDulan.weather_analytics_app.controller;

import com.codeDulan.weather_analytics_app.dto.CityComfortDto;
import com.codeDulan.weather_analytics_app.service.WeatherAnalyticsService;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherAnalyticsService weatherAnalyticsService;
    private final CacheManager cacheManager;

    @GetMapping("/cities")
    public List<CityComfortDto> cities() {
        return weatherAnalyticsService.rankedCities().stream()
                .map(CityComfortDto::from)
                .toList();
    }

    @GetMapping("/debug/cache")
    public Map<String, CacheStatsView> cacheStatus() {
        Map<String, CacheStatsView> result = new LinkedHashMap<>();
        for (String name : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(name);

            if (cache instanceof CaffeineCache caffeine) {
                com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = caffeine.getNativeCache();
                CacheStats stats = nativeCache.stats();
                result.put(name, new CacheStatsView(
                        nativeCache.estimatedSize(),
                        stats.hitCount(),
                        stats.missCount(),
                        round4(stats.hitRate()),
                        stats.evictionCount(),
                        stats.hitCount() + stats.missCount() == 0
                                ? "EMPTY"
                                : stats.hitCount() > 0 ? "HIT" : "MISS"));
            }
        }
        return result;

    }

    private static double round4(double value) {
        return Math.round(value * 10_000.0) / 10_000.0;
    }

    public record CacheStatsView(
            long size,
            long hitCount,
            long missCount,
            double hitRate,
            long evictionCount,
            String status
    ){}

}
