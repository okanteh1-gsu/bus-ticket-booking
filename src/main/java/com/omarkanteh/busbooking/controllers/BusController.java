package com.omarkanteh.busbooking.controllers;

import com.omarkanteh.busbooking.dto.BusDto;
import com.omarkanteh.busbooking.dto.CreateBusRequest;
import com.omarkanteh.busbooking.entities.Bus;
import com.omarkanteh.busbooking.enums.BusType;
import com.omarkanteh.busbooking.mappers.BusMapper;
import com.omarkanteh.busbooking.repositories.BusRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;


@RestController
@RequestMapping("/buses")
@AllArgsConstructor

public class BusController {
    private final BusRepository busRepository;
    private final BusMapper busMapper;


    @GetMapping
    public Iterable<BusDto> getAllBuses(@RequestParam(required = false, name = "type", defaultValue = "") String type) {
        List<Bus> buses;
        if (!type.isBlank()) {
            try {
                BusType busType = BusType.valueOf(type.toUpperCase());
                buses = busRepository.findBusByBusType(busType);
            } catch (IllegalArgumentException e) {
                buses = busRepository.findAll();
            }
        } else {
           buses = busRepository.findAll();
        }

        return buses
                .stream()
                .map(busMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusDto> getBus( @PathVariable Long id) {
        var bus = busRepository.findById(id).orElse(null);
        if (bus == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(busMapper.toDto(bus));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BusDto> createBus(
            @Valid @RequestBody CreateBusRequest request,
            UriComponentsBuilder uriBuilder
            ) {


        var bus = busMapper.toEntity(request);
        busRepository.save(bus);
        var uri = uriBuilder.path("/buses/{id}").buildAndExpand(bus.getId()).toUri();
        return ResponseEntity.created(uri).body(busMapper.toDto(bus));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BusDto> updateBus(
            @PathVariable Long id,
            @RequestBody CreateBusRequest requestDto
    ) {
        var bus = busRepository.findById(id).orElse(null);
        if (bus == null) {
            return ResponseEntity.notFound().build();
        }
        busMapper.updateBusFromRequest(requestDto, bus);
        busRepository.save(bus);
        return ResponseEntity.ok(busMapper.toDto(bus));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBus(@PathVariable Long id) {
        var bus = busRepository.findById(id).orElse(null);
        if (bus == null) {
            return ResponseEntity.notFound().build();
        }
        busRepository.delete(bus);
        return ResponseEntity.noContent().build();

    }

}
