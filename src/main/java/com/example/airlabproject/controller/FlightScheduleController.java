package com.example.airlabproject.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.airlabproject.dto.FlightScheduleDTO;
import com.example.airlabproject.service.FlightScheduleService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("api/flights")
public class FlightScheduleController {
    private FlightScheduleService flightService;

    @GetMapping()
    public List<FlightScheduleDTO> getAllFlights(@RequestParam(value = "airport_code") String airportCode) {
        return flightService.getFlights(airportCode);
    }

}
