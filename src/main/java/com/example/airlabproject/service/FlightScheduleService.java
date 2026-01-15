package com.example.airlabproject.service;

import com.example.airlabproject.dto.FlightScheduleDTO;
import com.example.airlabproject.entity.Airline;
import com.example.airlabproject.entity.Airport;
import com.example.airlabproject.entity.FlightSchedule;
import com.example.airlabproject.repository.AirlineRepository;
import com.example.airlabproject.repository.AirportRepository;
import com.example.airlabproject.repository.FlightScheduleRepository;
import com.google.gson.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class FlightScheduleService {

    @Autowired
    private FlightScheduleRepository flightRepository;

    @Autowired
    private AirlineRepository airlineRepository;

    @Autowired
    private AirportRepository airportRepository;


    @Value("${api-key-airlabs}")
    private String apiKey;

    private static final String API_URL = "https://airlabs.co/api/v9/schedules";

    // =========================
    // PUBLIC API
    // =========================
    @Transactional
    public List<FlightScheduleDTO> getFlights(String airportCode) {

        LocalDateTime timeThreshold = LocalDateTime.now().minusMinutes(30);

        List<FlightSchedule> cachedFlights =
                flightRepository.findByDepIataAndCreatedAtAfter(
                        airportCode, timeThreshold
                );

        if (!cachedFlights.isEmpty()) {
            System.out.println("--> Lấy dữ liệu từ DATABASE (ManyToOne Airline)");
            return cachedFlights.stream()
                    .map(this::toDTO)
                    .toList();
        }

        System.out.println("--> Gọi AIRLABS API mới");
        return fetchFromApiAndSave(airportCode);
    }

    // =========================
    // FETCH API + SAVE
    // =========================
    private List<FlightScheduleDTO> fetchFromApiAndSave(String airportCode) {

        String url = API_URL + "?dep_iata=" + airportCode + "&api_key=" + apiKey;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject root =
                    JsonParser.parseString(response.body()).getAsJsonObject();

            JsonArray responseArray =
                    root.has("response") && root.get("response").isJsonArray()
                            ? root.getAsJsonArray("response")
                            : null;

            List<FlightSchedule> flightList = new ArrayList<>();
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            if (responseArray != null) {
                for (JsonElement el : responseArray) {
                    JsonObject node = el.getAsJsonObject();

                    String airlineIata = getString(node, "airline_iata");

                    Airline airline = airlineRepository
                            .findById(airlineIata)
                            .orElse(null);

                    FlightSchedule f = new FlightSchedule();
                    f.setAirline(airline);
                    f.setFlightIata(getString(node, "flight_iata"));
                    f.setDepIata(getString(node, "dep_iata"));
                    String arrIata = getString(node, "arr_iata");

                    Airport arrivalAirport = airportRepository
                        .findById(arrIata)
                        .orElse(null);

                    f.setArrivalAirport(arrivalAirport);

                    f.setStatus(getString(node, "status"));

                    f.setDepTime(parseTime(getString(node, "dep_time"), formatter));
                    f.setDepTimeUtc(parseTime(getString(node, "dep_time_utc"), formatter));
                    f.setArrTime(parseTime(getString(node, "arr_time"), formatter));
                    f.setArrTimeUtc(parseTime(getString(node, "arr_time_utc"), formatter));

                    flightList.add(f);
                }
            }

            // Refresh cache theo sân bay
            flightRepository.deleteByDepIata(airportCode);

            List<FlightSchedule> savedFlights =
                    flightRepository.saveAll(flightList);

            return savedFlights.stream()
                    .map(this::toDTO)
                    .toList();

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // =========================
    // ENTITY -> DTO
    // =========================
    private FlightScheduleDTO toDTO(FlightSchedule f) {

        FlightScheduleDTO dto = new FlightScheduleDTO();

        dto.setFlightIata(f.getFlightIata());
        if (f.getArrivalAirport() != null) {
            dto.setArrAirportName(
                f.getArrivalAirport().getName()
            );
        } else {
            dto.setArrAirportName(null);
        }

        dto.setStatus(f.getStatus());
        dto.setDepTime(f.getDepTime());
        dto.setDepTimeUtc(f.getDepTimeUtc());
        dto.setArrTime(f.getArrTime());
        dto.setArrTimeUtc(f.getArrTimeUtc());

        if (f.getAirline() != null) {
            dto.setAirlineIata(f.getAirline().getIataCode());
            dto.setAirlineName(f.getAirline().getName());
        } else {
            dto.setAirlineIata(null);
            dto.setAirlineName(null);
        }

        return dto;
    }

    // =========================
    // UTIL
    // =========================
    private String getString(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull()
                ? obj.get(key).getAsString()
                : null;
    }

    private LocalDateTime parseTime(String value, DateTimeFormatter fmt) {
        try {
            if (value == null || value.isEmpty()) return null;
            return LocalDateTime.parse(value, fmt);
        } catch (Exception ignore) {
            return null;
        }
    }
}
