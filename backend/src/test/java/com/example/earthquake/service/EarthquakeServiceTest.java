package com.example.earthquake.service;

import com.example.earthquake.dto.EarthquakeResponseDTO;
import com.example.earthquake.exception.EarthquakeApiException;
import com.example.earthquake.exception.EarthquakeNotFoundException;
import com.example.earthquake.model.Earthquake;
import com.example.earthquake.repository.EarthquakeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EarthquakeServiceTest {

    @Mock
    private EarthquakeRepository repository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private GeoJsonParserService parser;

    @InjectMocks
    private EarthquakeService service;

    private static final String SAMPLE_GEOJSON = "{\"features\":[]}";
    private Earthquake sampleEq;

    @BeforeEach
    void setUp() {
        sampleEq = new Earthquake(1L, 3.5, "ml", "10km NE of City", "M 3.5 - City", 1713260000000L);
    }

    @Test
    void fetchEarthquakes_success_returnsCount() {
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(SAMPLE_GEOJSON);
        when(parser.parse(SAMPLE_GEOJSON)).thenReturn(List.of(sampleEq));

        int count = service.fetchEarthquakes();

        assertThat(count).isEqualTo(1);
        verify(repository).deleteAll();
        verify(repository).saveAll(List.of(sampleEq));
    }

    @Test
    void fetchEarthquakes_apiFailure_throwsEarthquakeApiException() {
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new RestClientException("timeout"));

        assertThatThrownBy(() -> service.fetchEarthquakes())
                .isInstanceOf(EarthquakeApiException.class)
                .hasMessageContaining("Failed to reach USGS API");
    }

    @Test
    void getAll_returnsMappedDTOs() {
        when(repository.findAll()).thenReturn(List.of(sampleEq));

        List<EarthquakeResponseDTO> result = service.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMagnitude()).isEqualTo(3.5);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void filterByMagnitude_delegatesToRepository() {
        when(repository.findByMagnitudeGreaterThan(2.0)).thenReturn(List.of(sampleEq));

        List<EarthquakeResponseDTO> result = service.filterByMagnitude(2.0);

        assertThat(result).hasSize(1);
        verify(repository).findByMagnitudeGreaterThan(2.0);
    }

    @Test
    void filterByTime_delegatesToRepository() {
        long afterMs = 1713260000000L;
        when(repository.findByTimeAfter(afterMs)).thenReturn(List.of(sampleEq));

        List<EarthquakeResponseDTO> result = service.filterByTime(afterMs);

        assertThat(result).hasSize(1);
        verify(repository).findByTimeAfter(afterMs);
    }

    @Test
    void deleteById_existingId_deletesSuccessfully() {
        when(repository.existsById(1L)).thenReturn(true);

        service.deleteById(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void deleteById_nonExistentId_throwsNotFoundException() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteById(99L))
                .isInstanceOf(EarthquakeNotFoundException.class)
                .hasMessageContaining("99");
    }
}
