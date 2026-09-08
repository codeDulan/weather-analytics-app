package com.codeDulan.weather_analytics_app.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OwmWeatherResponse(
        long id,
        String name,
        List<Weather> weather,
        Main main,
        Wind wind,
        Clouds clouds,
        Integer visibility,
        long dt,
        Sys sys) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Weather(
            String main,
            String description,
            String icon){
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Main(
            double temp,
            @JsonProperty("feels_like") double feelsLike,
            double pressure,
            double humidity){
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Wind(
            double speed,
            Integer deg){
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Clouds(
            @JsonProperty("all") int all){
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Sys(
            String country){
    }

    public Weather primaryWeather(){
        return (weather == null || weather.isEmpty()) ? null : weather.getFirst();
    }
}
