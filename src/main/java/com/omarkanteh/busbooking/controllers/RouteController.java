package com.omarkanteh.busbooking.controllers;

import com.omarkanteh.busbooking.dto.DistanceTimeDto;
import com.omarkanteh.busbooking.dto.RouteDto;
import com.omarkanteh.busbooking.mappers.RouteMapper;
import com.omarkanteh.busbooking.repositories.RouteRepository;
import com.omarkanteh.busbooking.services.DistanceAndTimeCalculator;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/routes")
public class RouteController {
    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;
    private final DistanceAndTimeCalculator distanceAndTimeCalculator;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createRoute(
            @Valid @RequestBody RouteDto routeDto,
            UriComponentsBuilder uriBuilder
    ) {
        if(routeRepository.existsByOriginAndDestination(routeDto.getOrigin(),routeDto.getDestination())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("error", "Route Already exists"));
        }

        var route = routeMapper.toEntity(routeDto);

        try {
            if (route.getDistance() <= 0 || route.getEstimatedDuration() <= 0) {
                var distanceAndTime = distanceAndTimeCalculator
                        .calculateDistanceAndTime(route.getOrigin(), route.getDestination());
                route.setEstimatedDuration(distanceAndTime.getTime());
                route.setDistance(distanceAndTime.getDistance());
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "failed to calculate distance/time: " + e.getMessage()));
        }

        routeRepository.save(route);

        var uri = uriBuilder.path("/routes/{id}").buildAndExpand(route.getId()).toUri();
        return ResponseEntity.created(uri).body(routeMapper.toDto(route));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteDto> getRoute(@PathVariable Long id) {
        var route = routeRepository.findById(id).orElse(null);
        if (route == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(routeMapper.toDto(route));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RouteDto> updateRoute(
            @PathVariable Long id,
            @Valid @RequestBody RouteDto updatedRoute) {
        var route = routeRepository.findById(id).orElse(null);
        if (route == null) {
            return ResponseEntity.notFound().build();
        }
        routeMapper.updateEntityFromDto(updatedRoute, route);
        routeRepository.save(route);
        return ResponseEntity.ok(routeMapper.toDto(route));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        var route = routeRepository.findById(id).orElse(null);
        if (route == null) {
            return ResponseEntity.notFound().build();
        }
        routeRepository.delete(route);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    public ResponseEntity<?> getAllRoutes() {
        var routes = routeRepository.findAll();
        var routeDto = routes.stream().map(routeMapper::toDto).toList();
        return ResponseEntity.ok(routeDto);
    }
}
