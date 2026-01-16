package com.example.airlabproject.service;

import com.example.airlabproject.dto.AirportDTO;
import com.example.airlabproject.entity.Airport;
import com.example.airlabproject.repository.AirportRepository;
import com.example.airlabproject.repository.CountryRepository;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AirportService {

    private final CountryRepository countryRepository;
    private final AirportRepository airportRepository;

    @Value("${api-key-airlabs}")
    private String airlabsApiKey;

    public AirportService(CountryRepository countryRepository, AirportRepository airportRepository) {
        this.countryRepository = countryRepository;
        this.airportRepository = airportRepository;
    }

    /**
     * Khởi tạo toàn bộ airport chỉ với 1 request
     */
    public void initAllAirports() {
        if (airportRepository.count() > 0) {
            System.out.println(">>> Airports already initialized");
            return;
        }

        System.out.println(">>> Fetching ALL airports from Airlabs (1 request)");

        HttpClient client = HttpClient.newHttpClient();
        String url = "https://airlabs.co/api/v9/airports?api_key=" + airlabsApiKey;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray airports = root.getAsJsonArray("response");
            if (airports == null || airports.isEmpty()) {
                System.out.println(">>> No airports returned from API");
                return;
            }

            List<Airport> batch = new ArrayList<>();

            for (JsonElement element : airports) {
                JsonObject obj = element.getAsJsonObject();

                String iata = getString(obj, "iata_code");
                String name = getString(obj, "name");
                String icao = getString(obj, "icao_code");
                String countryCode = getString(obj, "country_code");

                if (iata == null || name == null) continue;

                Airport ap = new Airport();
                ap.setIataCode(iata);
                ap.setName(name);
                ap.setIcaoCode(icao);
                ap.setLat(getBigDecimal(obj, "lat"));
                ap.setLng(getBigDecimal(obj, "lng"));

                if (countryCode != null) {
                    countryRepository.findById(countryCode)
                            .ifPresent(ap::setCountry);
                }

                batch.add(ap);
            }

            airportRepository.saveAll(batch);
            System.out.println("✔ Saved airports: " + batch.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * API cho frontend: chỉ đọc DB, KHÔNG gọi Airlabs nữa
     */
    public List<AirportDTO> getByCountryCode(String countryCode) {
        return airportRepository.findAllByCountry_Code(countryCode)
                .stream()
                .map(a -> new AirportDTO(
                        a.getIataCode(),
                        a.getName(),
                        a.getIcaoCode(),
                        a.getLat(),
                        a.getLng(),
                        a.getCountry() != null ? a.getCountry().getCode() : null
                ))
                .collect(Collectors.toList());
    }

    // ======================
    // Helpers
    // ======================

    private String getString(JsonObject obj, String field) {
        return obj.has(field) && !obj.get(field).isJsonNull()
                ? obj.get(field).getAsString()
                : null;
    }

    private BigDecimal getBigDecimal(JsonObject obj, String field) {
        try {
            return obj.has(field) && !obj.get(field).isJsonNull()
                    ? BigDecimal.valueOf(obj.get(field).getAsDouble())
                    : null;
        } catch (Exception e) {
            return null;
        }
    }
}
