package com.omarkanteh.busbooking.repositories;

import com.omarkanteh.busbooking.entities.Booking;
import com.omarkanteh.busbooking.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByUserIdAndScheduleIdAndStatusNot(Long userId, Long scheduleId, BookingStatus status);
}
