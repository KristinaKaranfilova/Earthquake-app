package com.example.earthquake.controller;


import com.example.earthquake.model.Earthquake;
import com.example.earthquake.service.EarthquakeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/earthquakes")
@CrossOrigin(origins = "http://localhost:3000")
public class EarthquakeController {

    private final EarthquakeService service;

    public EarthquakeController(EarthquakeService service) {
        this.service = service;
    }

    @GetMapping("/fetch")
    public String fetch() {
        service.fetchEarthquakes();
        return "Data fetched!";
    }

    @GetMapping
    public List<Earthquake> getAll() {
        return service.getAll();
    }
}