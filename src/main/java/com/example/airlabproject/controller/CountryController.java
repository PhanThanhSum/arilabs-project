package com.example.airlabproject.controller;

import lombok.AllArgsConstructor;
import com.example.airlabproject.dto.CountryDTO;
import com.example.airlabproject.service.CountryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
@AllArgsConstructor
public class CountryController {

    private CountryService countryService;

    @GetMapping
    public List<CountryDTO> getAll() {
        return countryService.getAll();
    }

}