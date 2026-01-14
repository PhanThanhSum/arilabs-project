package com.example.airlabproject.service;

import com.example.airlabproject.dto.CountryDTO;
import com.example.airlabproject.entity.Continent;
import com.example.airlabproject.entity.Country;
import com.example.airlabproject.repository.ContinentRepository;
import com.example.airlabproject.repository.CountryRepository;
import com.google.gson.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;

@Service
public class CountryService {

    private final CountryRepository countryRepository;
    private final ContinentRepository continentRepository;

    public CountryService(CountryRepository countryRepository, ContinentRepository continentRepository) {
        this.countryRepository = countryRepository;
        this.continentRepository = continentRepository;
    }

    private static final Logger log = LoggerFactory.getLogger(CountryService.class);

    // HttpClient nên được tái sử dụng thay vì tạo mới mỗi lần (tốt cho hiệu năng)
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Value("${api-key-airlabs}")
    private String airlabsApiKey;


    public List<CountryDTO> getAll() {
        return countryRepository.findAll()
                .stream()
                .map(c -> new CountryDTO(c.getCode(), c.getCode3(), c.getName(), c.getContinent() != null ? c.getContinent().getId() : null))
                .collect(Collectors.toList());
    }

    public List<CountryDTO> getByContinentId(String continentId) {
        if (continentId == null || continentId.isBlank()) return null;

        Continent continent = continentRepository.findById(continentId).orElse(null);
        if (continent == null) {
            continentRepository.save(new Continent(continentId));
        }

        List<Country> countries = countryRepository.findAllByContinent_Id(continentId);
        if (countries.isEmpty()) {
            countries = fetchAndSaveCountriesByContinent(continentId);
        }
        return countries
                .stream()
                .map(c -> new CountryDTO(c.getCode(), c.getCode3(), c.getName(), c.getContinent() != null ? c.getContinent().getId() : null))
                .collect(Collectors.toList());
    }

    public int saveAllFromAirlabs() {
        List<Continent> continents = continentRepository.findAll();
        if (continents.isEmpty()) return 0;

        int saved = 0;
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new GsonBuilder().create();

        for (Continent continent : continents) {
            String url = "https://airlabs.co/api/v9/countries?api_key=" + airlabsApiKey + "&continent=" + continent.getId();
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonArray countries = root.getAsJsonArray("response");
                if (countries == null) continue;

                List<Country> batch = new ArrayList<>();
                for (JsonElement element : countries) {
                    JsonObject obj = element.getAsJsonObject();
                    String code = obj.has("code") && !obj.get("code").isJsonNull() ? obj.get("code").getAsString() : null;
                    String code3 = obj.has("code3") && !obj.get("code3").isJsonNull() ? obj.get("code3").getAsString() : null;
                    String name = obj.has("name") && !obj.get("name").isJsonNull() ? obj.get("name").getAsString() : null;
                    if (code == null || name == null) continue;
                    batch.add(new Country(code, code3, name, continent));
                }
                if (!batch.isEmpty()) {
                    countryRepository.saveAll(batch);
                    saved += batch.size();
                }
            } catch (Exception e) {
                // Skip this continent on error
            }

            JsonArray dataArray = root.getAsJsonArray("response");
            List<Country> countries = new ArrayList<>();

            // 3. Loop và Map dữ liệu
            for (JsonElement element : dataArray) {
                JsonObject obj = element.getAsJsonObject();

                String code = getSafeString(obj, "code");
                String code3 = getSafeString(obj, "code3");
                String name = getSafeString(obj, "name");

                countries.add(new Country(code, code3, name, new Continent("AS")));
            }

            // 4. Lưu vào DB
            if (!countries.isEmpty()) {
                List<Country> savedCountries = countryRepository.saveAll(countries);
                log.info("Saved {} countries for continent {}", savedCountries.size(), continentId);
            }
            return countries;

        } catch (Exception e) {
            log.error("Error processing continentId: " + continentId, e);
        }
        return null;
    }

    private String getSafeString(JsonObject obj, String memberName) {
        if (obj.has(memberName) && !obj.get(memberName).isJsonNull()) {
            return obj.get(memberName).getAsString();
        }
        return null;
    }
}
