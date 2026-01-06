package com.example.airlabproject.service;

import com.example.airlabproject.dto.CountryDTO;

import java.util.List;

public interface CountryService {
    CountryDTO create(CountryDTO dto);
    List<CountryDTO> getAll();
    int setAll();
}
