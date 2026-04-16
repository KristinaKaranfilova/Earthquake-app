package com.example.earthquake.service;

import com.example.earthquake.dto.EarthquakeResponseDTO;
import com.example.earthquake.exception.EarthquakeApiException;
import com.example.earthquake.exception.EarthquakeDatabaseException;
import com.example.earthquake.exception.EarthquakeNotFoundException;
import com.example.earthquake.model.Earthquake;
import com.example.earthquake.repository.EarthquakeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class EarthquakeService {

    private static final Logger log = LoggerFactory.getLogger(EarthquakeService.class);
    private static final String USGS_URL =
            "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";

    private final EarthquakeRepository repository;
    private final RestTemplate restTemplate;
    private final GeoJsonParserService parser;

    public EarthquakeService(EarthquakeRepository repository,
                             RestTemplate restTemplate,
                             GeoJsonParserService parser) {
        this.repository = repository;
        this.restTemplate = restTemplate;
        this.parser = parser;
    }

    @Transactional
    public int fetchEarthquakes() {
        log.info("Fetching earthquakes from USGS");

        String rawJson;
        try {
            rawJson = restTemplate.getForObject(USGS_URL, String.class);
        } catch (RestClientException e) {
            throw new EarthquakeApiException("Failed to reach USGS API", e);
        }

        List<Earthquake> earthquakes;
        try {
            earthquakes = parser.parse(rawJson);
        } catch (Exception e) {
            throw new EarthquakeApiException("Failed to parse USGS GeoJSON", e);
        }

        try {
            repository.deleteAll();
            repository.saveAll(earthquakes);
            log.info("Stored {} earthquakes", earthquakes.size());
            return earthquakes.size();
        } catch (Exception e) {
            throw new EarthquakeDatabaseException("Failed to save earthquakes", e);
        }
    }

    public List<EarthquakeResponseDTO> getAll() {
        log.debug("Retrieving all earthquakes");
        return repository.findAll()
                .stream()
                .map(EarthquakeMapper::toDTO)
                .toList();
    }

    public List<EarthquakeResponseDTO> filterByMagnitude(Double minMagnitude) {
        log.debug("Filtering earthquakes with magnitude > {}", minMagnitude);
        return repository.findByMagnitudeGreaterThan(minMagnitude)
                .stream()
                .map(EarthquakeMapper::toDTO)
                .toList();
    }

    public List<EarthquakeResponseDTO> filterByTime(Long afterEpochMs) {
        log.debug("Filtering earthquakes after epoch {}", afterEpochMs);
        return repository.findByTimeAfter(afterEpochMs)
                .stream()
                .map(EarthquakeMapper::toDTO)
                .toList();
    }

    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting earthquake id={}", id);
        if (!repository.existsById(id)) {
            throw new EarthquakeNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
