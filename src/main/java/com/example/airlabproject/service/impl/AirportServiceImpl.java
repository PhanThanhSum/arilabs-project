package com.example.airlabproject.service.impl;

import com.example.airlabproject.dto.AirportDTO;
import com.example.airlabproject.dto.CountryDTO;
import com.example.airlabproject.repository.AirportRepository;
import com.example.airlabproject.service.AirportService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AirportServiceImpl implements AirportService {
    private final AirportRepository airportRepository;

    public AirportServiceImpl(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    @Override
    public List<AirportDTO> getAll() {
        return airportRepository.findAll()
                .stream()
                .map(c -> new AirportDTO(c.getIataCode(), c.getName(), c.getIcaoCode(), c.getLat(), c.getLng(), c.getParentCountry() != null ? c.getParentCountry().getCode() : null))
                .collect(Collectors.toList());
    }

}
