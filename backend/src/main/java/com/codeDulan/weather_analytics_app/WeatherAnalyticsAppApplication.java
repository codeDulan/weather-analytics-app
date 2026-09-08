package com.codeDulan.weather_analytics_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class WeatherAnalyticsAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherAnalyticsAppApplication.class, args);
	}

}
