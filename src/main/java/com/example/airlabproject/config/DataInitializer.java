package com.example.airlabproject.config;

import com.example.airlabproject.service.AirlineService;
import com.example.airlabproject.service.AirportService;
import com.example.airlabproject.service.ContinentService;
import com.example.airlabproject.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ContinentService continentService;
    private final CountryService countryService;
    private final AirportService airportService;
    private final AirlineService airlineService;

    @Override
    public void run(String... args) {
        System.out.println("=== INIT DATA START ===");

        // Init Continent (manual)
        continentService.initContinents();

        // Init ALL Countries
        countryService.initAllCountries();

        // Init ALL Airports
        airportService.initAllAirports();
        
        // Init All Airlines
        airlineService.initAllAirlines();

        System.out.println("=== INIT DATA DONE ===");
    }
}
