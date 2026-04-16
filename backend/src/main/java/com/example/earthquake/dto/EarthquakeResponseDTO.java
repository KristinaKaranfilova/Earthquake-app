package com.example.earthquake.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EarthquakeResponseDTO {
    private Long id;
    private Double magnitude;
    private String magType;
    private String place;
    private String title;
    private Long time;
}
