package com.omarkanteh.busbooking.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omarkanteh.busbooking.dto.DistanceTimeDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class DistanceAndTimeCalculator {
    @Value("${google.api.url}")
    private String googleApiUrl;

    @Value("${google.api.key}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public DistanceTimeDto calculateDistanceAndTime(String origin, String destination) {
        try {
            // URL encode origin and destination parameters
            String encodedOrigin = URLEncoder.encode(origin, StandardCharsets.UTF_8);
            String encodedDestination = URLEncoder.encode(destination, StandardCharsets.UTF_8);

            String requestUrl = String.format("%s?origins=%s&destinations=%s&key=%s",
                    googleApiUrl, encodedOrigin, encodedDestination, apiKey);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


            JsonNode root= objectMapper.readTree(response.body());
            JsonNode element = root.path("rows").get(0).path("elements").get(0);
            int distance = element.path("distance").path("value").asInt() / 1000;
            int time = element.path("duration").path("value").asInt() / 60;
            return new
                    DistanceTimeDto(distance, time);

        } catch (Exception e) {
            throw new RuntimeException("error calculating distance and time: " + e.getMessage(), e);
        }

    }
}

