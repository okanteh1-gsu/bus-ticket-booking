package com.omarkanteh.busbooking.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "routes")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String origin;

    private String destination;

    private int distance; // in km


    @Column(name = "estimated_duration")
    private int estimatedDuration; // in minutes

    @OneToMany(mappedBy = "route")
    private List<BusSchedule> schedules;
}