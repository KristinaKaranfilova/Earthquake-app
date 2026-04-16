package com.example.earthquake.service;

import com.example.earthquake.dto.EarthquakeResponseDTO;
import com.example.earthquake.model.Earthquake;

public class EarthquakeMapper {

    private EarthquakeMapper() {}

    public static EarthquakeResponseDTO toDTO(Earthquake eq) {
        return EarthquakeResponseDTO.builder()
                .id(eq.getId())
                .magnitude(eq.getMagnitude())
                .magType(eq.getMagType())
                .place(eq.getPlace())
                .title(eq.getTitle())
                .time(eq.getTime())
                .build();
    }
}
