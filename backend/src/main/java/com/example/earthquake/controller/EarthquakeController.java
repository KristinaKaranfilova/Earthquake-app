package com.example.earthquake.controller;

import com.example.earthquake.dto.EarthquakeResponseDTO;
import com.example.earthquake.service.EarthquakeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/earthquakes")
public class EarthquakeController {

    private final EarthquakeService service;

    public EarthquakeController(EarthquakeService service) {
        this.service = service;
    }

    @GetMapping("/fetch")
    public ResponseEntity<Map<String, Object>> fetch() {
        int count = service.fetchEarthquakes();
        return ResponseEntity.ok(Map.of("message", "Data fetched", "count", count));
    }

    @GetMapping
    public ResponseEntity<List<EarthquakeResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/filter/magnitude")
    public ResponseEntity<List<EarthquakeResponseDTO>> filterByMagnitude(
            @RequestParam(defaultValue = "2.0") Double min) {
        return ResponseEntity.ok(service.filterByMagnitude(min));
    }

    @GetMapping("/filter/time")
    public ResponseEntity<List<EarthquakeResponseDTO>> filterByTime(
            @RequestParam Long after) {
        return ResponseEntity.ok(service.filterByTime(after));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
