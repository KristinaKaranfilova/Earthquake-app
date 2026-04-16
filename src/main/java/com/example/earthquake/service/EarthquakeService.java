package com.example.earthquake.service;

import com.example.earthquake.model.Earthquake;
import com.example.earthquake.repository.EarthquakeRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class EarthquakeService {

    private final EarthquakeRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();

    public EarthquakeService(EarthquakeRepository repository) {
        this.repository = repository;
    }

    public void fetchEarthquakes() {
        String url = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";

        try {
            Map response = restTemplate.getForObject(url, Map.class);
            List<Map> features = (List<Map>) response.get("features");

            repository.deleteAll(); // брише старо

            for (Map feature : features) {
                Map props = (Map) feature.get("properties");

                Double mag = (Double) props.get("mag");

                if (mag != null && mag > 1.0) { // FILTER

                    Earthquake eq = new Earthquake();
                    eq.setMagnitude(mag);
                    eq.setMagType((String) props.get("magType"));
                    eq.setPlace((String) props.get("place"));
                    eq.setTitle((String) props.get("title"));
                    eq.setTime((Long) props.get("time"));

                    repository.save(eq);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error fetching earthquakes");
        }
    }

    public List<Earthquake> getAll() {
        return repository.findAll();
    }
}