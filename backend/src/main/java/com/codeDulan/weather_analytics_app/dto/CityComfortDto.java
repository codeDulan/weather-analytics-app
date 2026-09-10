package com.codeDulan.weather_analytics_app.dto;

import com.codeDulan.weather_analytics_app.model.RankedCity;

import java.time.Instant;
import java.util.Map;

public record CityComfortDto(
        int rank,
        String cityCode,
        String cityName,
        String country,
        String description,
        String condition,
        String iconCode,
        double temperatureC,
        double feelsLikeC,
        double humidityPercent,
        double windSpeedMs,
        Integer visibilityMeters,
        int cloudinessPercent,
        double comfortScore,
        Map<String, Double> comfortBreakdown,
        Instant observeAt) {

    public static CityComfortDto from(RankedCity ranked) {
        var w = ranked.weather();
        return new CityComfortDto(
                ranked.rank(),
                w.cityCode(),
                w.cityName(),
                w.country(),
                w.description(),
                w.condition(),
                w.iconCode(),
                w.temperatureC(),
                w.feelsLikeC(),
                w.humidityPercent(),
                w.windSpeedMs(),
                w.visibilityMeters(),
                w.cloudinessPercent(),
                ranked.comfort().value(),
                ranked.comfort().breakdown(),
                w.observedAt());
    }

}
