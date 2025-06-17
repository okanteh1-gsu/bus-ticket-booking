package com.omarkanteh.busbooking.repositories;

import com.omarkanteh.busbooking.entities.Bus;
import com.omarkanteh.busbooking.enums.BusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    List<Bus> findBusByBusType(BusType type);
}
