package com.omarkanteh.busbooking.services;


import com.omarkanteh.busbooking.entities.BusSchedule;
import com.omarkanteh.busbooking.entities.Seat;
import com.omarkanteh.busbooking.repositories.SeatRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;

    public List<Seat> validateAndFetchSeats(List<Long> seatIds, BusSchedule schedule) {
        List<Seat> seats = seatRepository.findAllById(seatIds);
        if (seats.size() != seatIds.size()) {
            throw new IllegalArgumentException("One or more seats do not exist.");
        }

        for (Seat seat : seats) {
            if (!seat.getSchedule().getId().equals(schedule.getId())) {
                throw new IllegalArgumentException("Seat " + seat.getSeatNumber() + " does not belong to this schedule.");
            }
            if (seat.isBooked()) {
                throw new IllegalStateException("Seat " + seat.getSeatNumber() + " is already booked.");
            }
        }

        return seats;
    }
}

