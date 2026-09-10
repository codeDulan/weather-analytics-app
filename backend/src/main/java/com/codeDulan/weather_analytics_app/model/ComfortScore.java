package com.codeDulan.weather_analytics_app.model;

import java.util.Map;

public record ComfortScore(double value, Map<String, Double> breakdown) {

    public ComfortScore {
        if(value < 0 || value > 100) {
            throw new IllegalArgumentException("comfort scoew out of range: " + value);
        }

        breakdown = Map.copyOf(breakdown);
    }

}
