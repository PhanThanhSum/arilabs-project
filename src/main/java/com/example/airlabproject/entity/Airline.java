package com.example.airlabproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "airline")
public class Airline {

    @Id
    @Column(name = "iata_code", length = 3) // 🔥 BẮT BUỘC
    private String iataCode;

    @Column(name = "icao_code", length = 4)
    private String icaoCode;

    @Column(name = "name")
    private String name;
}
