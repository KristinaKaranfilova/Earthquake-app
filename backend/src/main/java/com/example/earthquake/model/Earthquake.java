package com.example.earthquake.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "earthquakes")
public class Earthquake {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double magnitude;

    @Column(name = "mag_type", length = 10)
    private String magType;

    @Column(length = 255)
    private String place;

    @Column(length = 512)
    private String title;

    @Column(name = "time_ms")
    private Long time;
}
