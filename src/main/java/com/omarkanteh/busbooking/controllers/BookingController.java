package com.omarkanteh.busbooking.controllers;

import com.omarkanteh.busbooking.dto.CreateBookingRequest;
import com.omarkanteh.busbooking.dto.ErrorResponse;
import com.omarkanteh.busbooking.entities.Seat;
import com.omarkanteh.busbooking.enums.BookingStatus;
import com.omarkanteh.busbooking.mappers.BookingMapper;
import com.omarkanteh.busbooking.repositories.BookingRepository;
import com.omarkanteh.busbooking.repositories.BusScheduleRepository;
import com.omarkanteh.busbooking.repositories.SeatRepository;
import com.omarkanteh.busbooking.repositories.UserRepository;
import com.omarkanteh.busbooking.services.EmailService;
import com.omarkanteh.busbooking.services.SeatService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/bookings")
@AllArgsConstructor
public class BookingController {
    private final BookingRepository bookingRepository;
    private final BusScheduleRepository busScheduleRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final SeatRepository seatRepository;
    private final SeatService seatService;
    private final EmailService emailService;

    @PostMapping
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createBooking(
            @RequestBody CreateBookingRequest request,
            UriComponentsBuilder uriBuilder
            ) {

        var schedule =  busScheduleRepository.findById(request.getScheduleId()).orElseThrow(() ->
                new IllegalArgumentException("schedule not found"));
        long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var user = userRepository.findById(userId).orElseThrow(()->
                new UsernameNotFoundException("user not found"));

        boolean exists = bookingRepository.existsByUserIdAndScheduleIdAndStatusNot(userId,
                schedule.getId(),
                BookingStatus.CANCELLED);

        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("You have already made a booking for this schedule.");
        }

        List<Seat> selectedSeats = seatService.validateAndFetchSeats(request.getSeatIds(), schedule);

        var booking = bookingMapper.toEntity(request);
        booking.setUser(user);
        booking.setSchedule(schedule);
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.PENDING);

        var totalFare = schedule.getFare().multiply(BigDecimal.valueOf(selectedSeats.size()));
        booking.setTotalFare(totalFare.doubleValue());
        bookingRepository.save(booking);

        for (Seat seat : selectedSeats) {
            seat.setBooked(true);
            seat.setBooking(booking);
        }
        seatRepository.saveAll(selectedSeats);
        booking.setSeats(selectedSeats);
        bookingRepository.save(booking);
        emailService.sendBookingConfirmation(user.getEmail(), booking);


        URI uri = uriBuilder.path("/bookings/{id}").buildAndExpand(booking.getId()).toUri();
        return ResponseEntity.created(uri).body(bookingMapper.toDto(booking));
    }

//    Cancel booking
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var booking = bookingRepository.findById(id).orElseThrow(()-> new
                ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (!booking.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("You can only cancel your own bookings"));
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Booking is already cancelled"));
        }
        if (LocalDateTime.now().isAfter(booking.getSchedule().getDepartureTime().minusHours(3))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Cannot cancel within 3 hours of departure"));
        }
        for (Seat seat : booking.getSeats()) {
            seat.setBooking(null);
            seat.setBooked(false);
        }
        seatRepository.saveAll(booking.getSeats());

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        return ResponseEntity.noContent().build();
    }

}
