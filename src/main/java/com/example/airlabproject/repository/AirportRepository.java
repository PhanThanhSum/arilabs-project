package com.example.airlabproject.repository;

import com.example.airlabproject.dto.AirportDTO;
import com.example.airlabproject.entity.Airport;
import com.example.airlabproject.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AirportRepository extends JpaRepository<Airport, String> {
    List<AirportDTO> getAirportByParentCountry(Country parentCountry);

    List<AirportDTO> findAllByParentCountry_Code(String parentCountryCode);
}
