package com.omarkanteh.busbooking.repositories;

import com.omarkanteh.busbooking.entities.BusSchedule;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BusScheduleRepository extends JpaRepository<BusSchedule, Long> {
    List<BusSchedule> findByRouteId(Long routeId, Pageable pageable);
    List<BusSchedule> findByBusId(Long busId, Pageable pageable);
    List<BusSchedule> findByRouteIdAndBusId(Long routeId, Long busId, Pageable pageable);
    List<BusSchedule> findByDepartureTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    List<BusSchedule> findByRouteIdAndDepartureTimeBetween(Long routeId, LocalDateTime start, LocalDateTime end);


    boolean existsByBusIdAndDepartureTimeBetween( Long busId, LocalDateTime localDateTime, LocalDateTime localDateTime1);
}
