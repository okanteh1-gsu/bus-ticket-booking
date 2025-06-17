package com.omarkanteh.busbooking.controllers;


import com.omarkanteh.busbooking.dto.*;
import com.omarkanteh.busbooking.entities.BusSchedule;
import com.omarkanteh.busbooking.entities.Seat;
import com.omarkanteh.busbooking.enums.BookingStatus;
import com.omarkanteh.busbooking.mappers.BusScheduleMapper;
import com.omarkanteh.busbooking.repositories.BusRepository;
import com.omarkanteh.busbooking.repositories.BusScheduleRepository;
import com.omarkanteh.busbooking.repositories.RouteRepository;
import com.omarkanteh.busbooking.repositories.SeatRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/schedules")
@AllArgsConstructor
public class BusScheduleController {

    private final BusScheduleRepository scheduleRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final BusScheduleMapper scheduleMapper;
    private final SeatRepository seatRepository;


    @GetMapping
    public ResponseEntity<List<ScheduleDto>> getAllSchedules(
            @RequestParam(required = false) Long routeId,
            @RequestParam(required = false) Long busId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureAfter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureBefore,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size);
        List<BusSchedule> schedules;

        if (routeId != null && busId != null) {
            schedules = scheduleRepository.findByRouteIdAndBusId(routeId, busId, pageable);
        } else if (routeId != null) {
            schedules = scheduleRepository.findByRouteId(routeId, pageable);
        } else if (busId != null) {
            schedules = scheduleRepository.findByBusId(busId, pageable);
        } else if (departureAfter != null && departureBefore != null) {
            schedules = scheduleRepository.findByDepartureTimeBetween(departureAfter, departureBefore, pageable);
        } else {
            schedules = scheduleRepository.findAll(pageable).getContent();
        }

        return ResponseEntity.ok(schedules.stream().map(scheduleMapper::toDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDto> getSchedule(@PathVariable Long id) {
        return scheduleRepository.findById(id)
                .map(scheduleMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Transactional
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSchedule(
            @Valid @RequestBody CreateScheduleRequest request,
            UriComponentsBuilder uriBuilder) {

        if (scheduleRepository.existsByBusIdAndDepartureTimeBetween(
                request.getBusId(),
                request.getDepartureTime().minusHours(1),
                request.getArrivalTime().plusHours(1))) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Bus has conflicting schedule within 1 hour window"));
        }

        if (!busRepository.existsById(request.getBusId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Bus not found with id: " + request.getBusId()));
        }

        if (!routeRepository.existsById(request.getRouteId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Route not found with id: " + request.getRouteId()));
        }

        if (request.getArrivalTime().isBefore(request.getDepartureTime())) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Arrival time must be after departure time"));
        }
        var route = routeRepository.findById(request.getRouteId()).orElseThrow( () ->
                new IllegalArgumentException("Route not found with ID: " + request.getRouteId()));
        var bus = busRepository.findById(request.getBusId()).orElseThrow(()->
                new IllegalArgumentException("Bus not found with ID: " + request.getBusId()));

        BusSchedule schedule = scheduleMapper.toEntity(request);
        schedule.setBus(bus);
        schedule.setRoute(route);

        scheduleRepository.save(schedule);


        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= bus.getTotalSeats(); i++) {
            Seat seat = new Seat();
            seat.setSeatNumber("SEAT " + i);
            seat.setBooked(false);
            seat.setSchedule(schedule);
            seats.add(seat);
        }
        seatRepository.saveAll(seats);
        ScheduleDto scheduleDto = scheduleMapper.toDto(schedule);
        System.out.println("DTO Fare: " +scheduleDto.getFare());

        URI location = uriBuilder.path("/schedules/{id}")
                .buildAndExpand(schedule.getId())
                .toUri();

        return ResponseEntity.created(location).body(scheduleDto);
    }

    @Transactional
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody UpdateScheduleRequest request) {

        return scheduleRepository.findById(id)
                .map(schedule -> {
                    if (request.getArrivalTime() != null &&
                            request.getArrivalTime().isBefore(schedule.getDepartureTime())) {
                        return ResponseEntity.badRequest()
                                .body(new ErrorResponse("Arrival time must be after departure time"));
                    }

                    scheduleMapper.updateFromRequest(request, schedule);
                    scheduleRepository.save(schedule);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Transactional
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deleteSchedule(@PathVariable Long id) {
        return scheduleRepository.findById(id)
                .map(schedule -> {

                    seatRepository.deleteByScheduleId(id);
                    scheduleRepository.delete(schedule);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/available")
    public ResponseEntity<List<ScheduleWithAvailabilityDto>> getAvailableSchedules(
            @RequestParam Long routeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {

        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);

        List<BusSchedule> schedules = scheduleRepository
                .findByRouteIdAndDepartureTimeBetween(routeId, startOfDay, endOfDay);

        return ResponseEntity.ok(schedules.stream()
                .map(schedule -> {
                    int bookedCount = schedule.getBookings().stream()
                            .filter(b->b.getStatus() != BookingStatus.CANCELLED)
                            .mapToInt(b -> b.getSeats().size())
                            .sum();
                    int availableSeats = schedule.getBus().getTotalSeats() - bookedCount;
                    return new ScheduleWithAvailabilityDto(
                            scheduleMapper.toDto(schedule),
                            availableSeats
                    );
                })
                .toList());
    }
}
