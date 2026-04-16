package com.example.earthquake.service;

import com.example.earthquake.model.Earthquake;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GeoJsonParserService {

    private static final Logger log = LoggerFactory.getLogger(GeoJsonParserService.class);
    private static final double MIN_MAGNITUDE = 2.0;

    /**
     * Parses a USGS GeoJSON string and returns Earthquake entities
     * with magnitude > 2.0. Entries with missing or null magnitude are skipped.
     */
    public List<Earthquake> parse(String rawJson) {
        List<Earthquake> results = new ArrayList<>();

        JSONArray features = new JSONObject(rawJson).optJSONArray("features");
        if (features == null) {
            log.warn("GeoJSON contained no 'features' array");
            return results;
        }

        for (int i = 0; i < features.length(); i++) {
            JSONObject feature = features.optJSONObject(i);
            if (feature == null) continue;

            JSONObject props = feature.optJSONObject("properties");
            if (props == null) continue;

            if (props.isNull("mag")) {
                log.debug("Skipping feature {}: null magnitude", i);
                continue;
            }

            double mag = props.getDouble("mag");
            if (mag <= MIN_MAGNITUDE) continue;

            Earthquake eq = new Earthquake();
            eq.setMagnitude(mag);
            eq.setMagType(props.isNull("magType") ? null : props.getString("magType"));
            eq.setPlace(props.isNull("place") ? null : props.getString("place"));
            eq.setTitle(props.isNull("title") ? null : props.getString("title"));
            eq.setTime(props.isNull("time") ? null : props.getLong("time"));
            results.add(eq);
        }

        log.debug("Parsed {} earthquakes above M{}", results.size(), MIN_MAGNITUDE);
        return results;
    }
}
