package com.example.airlabproject.util;

import java.util.List;

import com.example.airlabproject.dto.CountryDTO;

public class GlobalVariable {
    private static List<CountryDTO> countryList;
    
    public static List<CountryDTO> getCountryList() {
        return countryList;
    }
    
    
}
