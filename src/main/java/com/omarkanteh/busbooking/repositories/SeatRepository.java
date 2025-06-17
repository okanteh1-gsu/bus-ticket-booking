package com.omarkanteh.busbooking.repositories;

import com.omarkanteh.busbooking.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Modifying
    @Query("DELETE FROM Seat s WHERE s.schedule.id = :scheduleId")
    void deleteByScheduleId(@Param("scheduleId") Long scheduleId);


    boolean existsBySeatNumberAndScheduleId(String seatNumber, Long scheduleId);

    List<Seat> findByScheduleId(long scheduleId);
}



