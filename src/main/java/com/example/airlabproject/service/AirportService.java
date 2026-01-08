package com.example.airlabproject.service;

import com.example.airlabproject.dto.AirportDTO;

import java.util.List;

public interface AirportService {
    List<AirportDTO> getAll();
    int setAll();
}
