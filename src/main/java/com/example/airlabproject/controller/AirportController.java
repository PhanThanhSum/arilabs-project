package com.example.airlabproject.controller;

import com.example.airlabproject.dto.AirportDTO;
import com.example.airlabproject.service.AirportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/airports")
public class AirportController {
    private final AirportService airportService;

    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }


    @GetMapping
    public List<AirportDTO> getAll(){
        return airportService.getAll();
    }
}
