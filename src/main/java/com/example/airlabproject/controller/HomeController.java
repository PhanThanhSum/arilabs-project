package com.example.airlabproject.controller;

import com.example.airlabproject.entity.Continent;
import com.example.airlabproject.entity.Country;
import com.example.airlabproject.service.CountryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class HomeController {

    private CountryService countryService;

    @GetMapping("/")
    public String home(Model model) {
        return "index";
    }
}
