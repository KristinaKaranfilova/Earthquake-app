package com.example.earthquake.repository;

import com.example.earthquake.model.Earthquake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EarthquakeRepository extends JpaRepository<Earthquake, Long> {
    List<Earthquake> findByMagnitudeGreaterThan(Double magnitude);
    List<Earthquake> findByTimeAfter(Long time);
}