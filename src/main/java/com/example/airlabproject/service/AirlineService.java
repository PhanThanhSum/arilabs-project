package com.example.airlabproject.service;

import com.example.airlabproject.entity.Airline;
import com.example.airlabproject.repository.AirlineRepository;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class AirlineService {

    private final AirlineRepository airlineRepository;

    @Value("${api-key-airlabs}")
    private String apiKey;

    public AirlineService(AirlineRepository airlineRepository) {
        this.airlineRepository = airlineRepository;
    }

    public void initAllAirlines() {
        if (airlineRepository.count() > 0) return;

        System.out.println("→ Loading airlines from AirLabs API");

        String url = "https://airlabs.co/api/v9/airlines?api_key=" + apiKey;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray data = root.getAsJsonArray("response");

            List<Airline> airlines = new ArrayList<>();

            for (JsonElement el : data) {
                JsonObject obj = el.getAsJsonObject();

                String iata = getString(obj, "iata_code");
                String icao = getString(obj, "icao_code");
                String name = getString(obj, "name");

                if (iata == null || name == null) continue;

                Airline airline = new Airline();
                airline.setIataCode(iata);
                airline.setIcaoCode(icao);
                airline.setName(name);

                airlines.add(airline);
            }

            airlineRepository.saveAll(airlines);
            System.out.println("✔ Airlines initialized: " + airlines.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getString(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull()
                ? obj.get(key).getAsString()
                : null;
    }
}
