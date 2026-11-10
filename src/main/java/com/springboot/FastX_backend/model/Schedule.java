package com.springboot.FastX_backend.model;
import com.springboot.FastX_backend.enums.ScheduleStatus;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Schedule {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;

    @Column(name = "journey_date",nullable = false)
    private LocalDate journeyDate;

    @Column(name = "departure_time",nullable = false)
    private LocalTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    private LocalTime arrivalTime;

    @Column(nullable = false)
    private double fare;

    @Enumerated(EnumType.STRING)
    private ScheduleStatus scheduleStatus;

    @ManyToOne
    @JoinColumn(name = "bus_id",nullable = false)
    private Bus bus;

    @ManyToOne
    @JoinColumn(name = "route_id",nullable = false)
    private Route route;
}
