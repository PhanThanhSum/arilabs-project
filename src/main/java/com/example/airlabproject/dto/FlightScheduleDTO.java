package com.example.airlabproject.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FlightScheduleDTO {

    // =====================
    // AIRLINE
    // =====================
    private String airlineIata;
    private String airlineName;   // ✅ tên hãng bay

    // =====================
    // FLIGHT
    // =====================
    private String flightIata;

    // =====================
    // AIRPORT (ARRIVAL)
    // =====================
    private String arrAirportName; // ✅ tên sân bay đến

    // =====================
    // STATUS
    // =====================
    private String status;

    // =====================
    // TIME
    // =====================
    private LocalDateTime depTime;
    private LocalDateTime depTimeUtc;
    private LocalDateTime arrTime;
    private LocalDateTime arrTimeUtc;
}
