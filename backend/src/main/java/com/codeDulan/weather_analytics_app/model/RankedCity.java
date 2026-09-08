package com.codeDulan.weather_analytics_app.model;

public record RankedCity(int rank, Weather weather, ComfortScore comfort) {

    public RankedCity {
        if(rank < 1) {
            throw new IllegalArgumentException("rank must be greater than 1, was " + rank);
        }
    }

    public static RankedCity unranked(Weather weather, ComfortScore comfort) {
        return new RankedCity(1, weather, comfort);
    }

    public RankedCity withRank(int newRank) {
        return new RankedCity(newRank, weather, comfort);
    }
}
