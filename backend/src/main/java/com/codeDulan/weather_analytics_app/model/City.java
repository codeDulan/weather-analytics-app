package com.codeDulan.weather_analytics_app.model;


import java.util.Objects;

public record City(String code, String name) {

    public City{
        Objects.requireNonNull(code, "city code must not be null");
        Objects.requireNonNull(name, "city name must not be null");

        code = code.trim();
        name = name.trim();

        if(code.isEmpty()) {
            throw new IllegalArgumentException("city code must not be blank");
        }
    }

}
