package com.example.airlabproject.service;

import com.example.airlabproject.dto.ContinentDTO;
import com.example.airlabproject.entity.Continent;
import com.example.airlabproject.repository.ContinentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContinentService {
    private final ContinentRepository continentRepository;
    
    public ContinentService(ContinentRepository continentRepository){
        this.continentRepository = continentRepository;
    }
    
    //Khởi tạo continent ban đầu
    public void initContinents() {
        if (continentRepository.count() > 0) return;

        continentRepository.saveAll(List.of(
            new Continent("AF", "Africa"),
            new Continent("AN", "Antarctica"),
            new Continent("AS", "Asia"),
            new Continent("EU", "Europe"),
            new Continent("NA", "North America"),
            new Continent("OC", "Oceania"),
            new Continent("SA", "South America")
        ));
    
        System.out.println("✔ Continents initialized");
    }


    public ContinentDTO create(ContinentDTO dto){
        Continent continent = new Continent(dto.getId(), dto.getName());
        continentRepository.save(continent);
        return dto;
    }

    public List<ContinentDTO> getAll(){
        return continentRepository.findAll()
                .stream()
                .map(c -> new ContinentDTO(c.getId(), c.getName()))
                .collect(Collectors.toList());
    }
    


}
