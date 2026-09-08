package com.codeDulan.weather_analytics_app.model;

import java.time.Instant;

public record Weather(
        String cityCode,
        String cityName,
        String country,
        String condition,
        String description,
        String iconCode,
        double temperatureC,
        double feelsLikeC,
        double humidityPercent,
        double pressureHpa,
        double windSpeedMs,
        int cloudinessPercent,
        Integer visibilityMeters,
        Instant observedAt) {

    public Weather {
        if (cityCode == null || cityCode.isBlank()) {
            throw new IllegalArgumentException("cityCode must not be blank");
        }
        if(humidityPercent < 0 || humidityPercent > 100) {
            throw new IllegalArgumentException("humidity precent out of range: " + humidityPercent);
        }
        if(cloudinessPercent < 0 || cloudinessPercent > 100) {
            throw new IllegalArgumentException("cloudinessPrecent out of range:" + cloudinessPercent);
        }
        if(windSpeedMs < 0) {
            throw new IllegalArgumentException("windSpeedMS must not be negative: " + windSpeedMs);
        }
    }
}
