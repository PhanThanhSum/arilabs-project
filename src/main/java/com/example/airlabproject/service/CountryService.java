package com.example.airlabproject.service;

import com.example.airlabproject.dto.CountryDTO;

import java.util.List;

public interface CountryService {
    List<CountryDTO> getAll();
    int setAll();
}
