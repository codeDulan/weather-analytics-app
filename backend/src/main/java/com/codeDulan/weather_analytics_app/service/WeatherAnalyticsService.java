package com.codeDulan.weather_analytics_app.service;

import com.codeDulan.weather_analytics_app.client.OpenWeatherClient;
import com.codeDulan.weather_analytics_app.client.WeatherClientException;
import com.codeDulan.weather_analytics_app.model.City;
import com.codeDulan.weather_analytics_app.model.ComfortScore;
import com.codeDulan.weather_analytics_app.model.RankedCity;
import com.codeDulan.weather_analytics_app.model.Weather;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherAnalyticsService {

    private final CityCatalog cityCatalog;
    private final OpenWeatherClient openWeatherClient;
    private final ComfortIndexService comfortIndexService;

    public List<RankedCity> rankedCities() {
        List<City> cities = cityCatalog.cities();
        List<RankedCity> scored = new ArrayList<>(cities.size());

        for(City city : cities) {
            try {
                Weather weather = openWeatherClient.fetch(city);
                ComfortScore comfort = comfortIndexService.score(weather);
                scored.add(RankedCity.unranked(weather, comfort));
            } catch (WeatherClientException e) {
                log.warn("Skipping city {} ({}): {}", city.code(), city.name(), e.getMessage());
            }
        }

        scored.sort(Comparator
                .comparingDouble((RankedCity rc) -> rc.comfort().value()).reversed()
                .thenComparing(rc -> rc.weather().cityName()));

        List<RankedCity> ranked = new ArrayList<>(scored.size());
        for (int i = 0; i < scored.size(); i++) {
            ranked.add(scored.get(i).withRank(i+1));
        }

        if(ranked.isEmpty()) {
            log.error("Weather could not be fetched for any of the {} catelog cities", cities.size());
        }else  if (ranked.size() < cities.size()) {
            log.warn("Ranked {} of {} catalog cities, the rest failed", ranked.size(), cities.size());
        }

        return ranked;
    }

}
