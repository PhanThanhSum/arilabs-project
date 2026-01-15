package com.example.airlabproject.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "flight_schedule")
@Data
public class FlightSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔥 MANY TO ONE
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "airline_iata", referencedColumnName = "iata_code")
    private Airline airline;

    private String flightIata;
    private String depIata;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "arr_iata",
        referencedColumnName = "iata_code" 
    )
    private Airport arrivalAirport;


    private String status;

    private LocalDateTime depTime;
    private LocalDateTime depTimeUtc;
    private LocalDateTime arrTime;
    private LocalDateTime arrTimeUtc;

    private LocalDateTime createdAt = LocalDateTime.now();
}
