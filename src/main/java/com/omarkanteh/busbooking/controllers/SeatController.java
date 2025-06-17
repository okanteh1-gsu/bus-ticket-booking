package com.omarkanteh.busbooking.controllers;

import com.omarkanteh.busbooking.dto.CreateSeatDto;
import com.omarkanteh.busbooking.dto.SeatDto;
import com.omarkanteh.busbooking.entities.BusSchedule;
import com.omarkanteh.busbooking.entities.Seat;
import com.omarkanteh.busbooking.mappers.SeatMapper;
import com.omarkanteh.busbooking.repositories.BusScheduleRepository;
import com.omarkanteh.busbooking.repositories.SeatRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/seats")
@AllArgsConstructor
public class SeatController {
    private final SeatRepository seatRepository;
    private final BusScheduleRepository busScheduleRepository;
    private final SeatMapper seatMapper;

    @PostMapping
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSeat(
            @RequestBody CreateSeatDto seatDto,
            UriComponentsBuilder uriBuilder
            ) {

        if (seatRepository.existsBySeatNumberAndScheduleId(seatDto.getSeatNumber(), seatDto.getScheduleId()))
        {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Seat already exists");
        }

        BusSchedule schedule =  busScheduleRepository.findById(seatDto.getScheduleId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Schedule not found with id: "
                                + seatDto.getScheduleId()));

        Seat seat = seatMapper.toEntity(seatDto);
        seat.setBooked(false);
        seat.setSchedule(schedule);
        seatRepository.save(seat);
        SeatDto seatDtoRes = seatMapper.toDto(seat);

        URI uri = uriBuilder.path("/seats/{id}").buildAndExpand(seat.getId()).toUri();
        return ResponseEntity.created(uri).body(seatDtoRes);
    }

    @GetMapping
    public List<SeatDto> getAllSeats(
            @RequestParam(required = false) Long scheduleId,
            @RequestParam(required = false) Boolean isBooked
    ) {
        List<Seat> seats;
        if(scheduleId != null && isBooked != null) {
            seats = seatRepository.findByScheduleId(scheduleId)
                    .stream()
                    .filter(seat -> seat.isBooked() == isBooked)
                    .toList();
        } else if (scheduleId != null) {
            seats = seatRepository.findByScheduleId(scheduleId);
        } else if (isBooked != null) {
            seats = seatRepository.findAll().stream()
                    .filter(seat -> seat.isBooked() == isBooked)
                    .toList();
        }  else {
            seats = seatRepository.findAll();
        }

        return seats.stream()
                .map(seatMapper::toDto)
                .toList();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SeatDto> updateSeat(
            @PathVariable(name = "id") Long id,
            @RequestBody SeatDto request
    ) {
        Seat seat = seatRepository.findById(id).orElse(null);
        if (seat == null) {
            return ResponseEntity.notFound().build();
        }
        seatMapper.Update(request, seat);
        seatRepository.save(seat);
        return ResponseEntity.ok(seatMapper.toDto(seat));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeat(@PathVariable long id) {
        Seat seat = seatRepository.findById(id).orElse(null);
        if (seat == null) {
            return ResponseEntity.notFound().build();
        }
        seatRepository.delete(seat);
        return ResponseEntity.noContent().build();
    }

}
