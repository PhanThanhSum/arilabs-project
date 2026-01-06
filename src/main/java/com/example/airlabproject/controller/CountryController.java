package com.example.airlabproject.controller;

import com.example.airlabproject.dto.CountryDTO;
import com.example.airlabproject.service.CountryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
public class CountryController {
    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping
    public List<CountryDTO> getAll() {
        return countryService.getAll();
    }

    @PostMapping("/set-all")
    public int setAll() {
        return countryService.setAll();
    }
}
