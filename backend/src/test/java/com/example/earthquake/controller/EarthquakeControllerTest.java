package com.example.earthquake.controller;

import com.example.earthquake.dto.EarthquakeResponseDTO;
import com.example.earthquake.exception.EarthquakeNotFoundException;
import com.example.earthquake.exception.GlobalExceptionHandler;
import com.example.earthquake.service.EarthquakeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EarthquakeController.class)
@Import(GlobalExceptionHandler.class)
class EarthquakeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EarthquakeService service;

    @Test
    void GET_fetch_returns200WithCount() throws Exception {
        when(service.fetchEarthquakes()).thenReturn(5);

        mockMvc.perform(get("/api/earthquakes/fetch"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(5))
                .andExpect(jsonPath("$.message").value("Data fetched"));
    }

    @Test
    void GET_all_returns200WithList() throws Exception {
        EarthquakeResponseDTO dto = EarthquakeResponseDTO.builder()
                .id(1L)
                .magnitude(3.5)
                .place("Test Location")
                .build();
        when(service.getAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/earthquakes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].magnitude").value(3.5))
                .andExpect(jsonPath("$[0].place").value("Test Location"));
    }

    @Test
    void GET_filterByMagnitude_passesMinParam() throws Exception {
        when(service.filterByMagnitude(3.0)).thenReturn(List.of());

        mockMvc.perform(get("/api/earthquakes/filter/magnitude").param("min", "3.0"))
                .andExpect(status().isOk());

        verify(service).filterByMagnitude(3.0);
    }

    @Test
    void GET_filterByTime_passesAfterParam() throws Exception {
        when(service.filterByTime(1713260000000L)).thenReturn(List.of());

        mockMvc.perform(get("/api/earthquakes/filter/time").param("after", "1713260000000"))
                .andExpect(status().isOk());

        verify(service).filterByTime(1713260000000L);
    }

    @Test
    void DELETE_byId_returns204() throws Exception {
        doNothing().when(service).deleteById(1L);

        mockMvc.perform(delete("/api/earthquakes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void DELETE_nonExistentId_returns404() throws Exception {
        doThrow(new EarthquakeNotFoundException(99L)).when(service).deleteById(99L);

        mockMvc.perform(delete("/api/earthquakes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
