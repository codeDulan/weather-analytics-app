package com.codeDulan.weather_analytics_app.service;

import com.codeDulan.weather_analytics_app.model.ComfortScore;
import com.codeDulan.weather_analytics_app.model.Weather;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ComfortIndexService {

    // weights
    static final double WEIGHT_TEMPERATURE = 0.55;
    static final double WEIGHT_HUMIDITY = 0.25;
    static final double WEIGHT_WIND = 0.15;
    static final double WEIGHT_SKY = 0.05;

    // temperature
    static final double IDEAL_FEELS_LIKE_C = 22.0;
    static final double TEMP_PENALTY_PER_DEGREE = 5.5;

    // humidity
    static final double HUMIDITY_COMFORT_LOW = 30.0;
    static final double HUMIDITY_COMFORT_HIGH = 55.0;
    static final double HUMIDITY_PENALTY_PER_PERCENT_DRY = 1.5;
    static final double HUMIDITY_PENALTY_PER_PERCENT_HUMID = 2.0;

    // wind
    static final double WIND_COMFORT_MAX_MS = 3.0;
    static final double WIND_PENALTY_PER_MS = 8.0;

    // sky
    static final double SKY_PENALTY_PER_PERCENT = 0.5;

    public ComfortScore score(Weather weather) {
        double temperature = temperatureScore(weather.feelsLikeC());
        double humidity = humidityScore(weather.humidityPercent());
        double wind = windScore(weather.windSpeedMs());
        double sky = skyScore(weather.cloudinessPercent());

        double overall = WEIGHT_TEMPERATURE * temperature + WEIGHT_HUMIDITY * humidity + WEIGHT_WIND * wind + WEIGHT_SKY * sky;

        Map<String, Double> breakdown = new LinkedHashMap<>();
        breakdown.put("temperature", round1(temperature));
        breakdown.put("humidity", round1(humidity));
        breakdown.put("wind", round1(wind));
        breakdown.put("sky", round1(sky));

        return new ComfortScore(round1(clamp0to100(overall)), breakdown);
    }

    double temperatureScore(double feelsLikeC){
        double distance = Math.abs(feelsLikeC - IDEAL_FEELS_LIKE_C);
        return clamp0to100(100.0 - TEMP_PENALTY_PER_DEGREE * distance);
    }

    double humidityScore(double humidityPercent){
        if(humidityPercent < HUMIDITY_COMFORT_LOW) {
            double belowBand = HUMIDITY_COMFORT_LOW - humidityPercent;
            return clamp0to100(100.0 - HUMIDITY_PENALTY_PER_PERCENT_DRY * belowBand);
        }

        if(humidityPercent > HUMIDITY_COMFORT_HIGH){
            double aboveBand = humidityPercent - HUMIDITY_COMFORT_HIGH;
            return clamp0to100(100.0 - HUMIDITY_PENALTY_PER_PERCENT_HUMID * aboveBand);
        }
        return 100.0;
    }

    double windScore(double windSpeedMs){
        if(windSpeedMs <= WIND_COMFORT_MAX_MS){
            return 100.0;
        }
        double excess = windSpeedMs - WIND_COMFORT_MAX_MS;
        return clamp0to100(100.0 - WIND_PENALTY_PER_MS * excess);
    }

    double skyScore(double cloudinessPercent){
        return clamp0to100(100.0 - SKY_PENALTY_PER_PERCENT * cloudinessPercent);
    }

    private static double clamp0to100(double value){
        return Math.max(0.0, Math.min(100.0, value));
    }

    private static double round1(double value){
        return Math.round(value * 10.0) / 10.0;
    }

}
