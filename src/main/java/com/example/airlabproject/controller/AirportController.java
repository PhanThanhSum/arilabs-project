package com.example.airlabproject.controller;

import com.example.airlabproject.dto.AirportDTO;
import com.example.airlabproject.service.AirportService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/airports")
public class AirportController {
    private AirportService airportService;

    @GetMapping
    public List<AirportDTO> getAll(@RequestParam(value = "country_code") String countryCode){
        return airportService.getByCountryCode(countryCode);
    }

}
