package com.springboot.FastX_backend.model;

import com.springboot.FastX_backend.enums.BookingStatus;
import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;
    @Column(name = "total_amount",nullable = false)
    private double totalAmount;
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;
    @CreationTimestamp
    private Instant bookedAt;
    private Instant paymentDeadline;
    @ManyToOne
    @JoinColumn(name = "passenger_id",nullable = false)
    private Passenger passenger;
    @ManyToOne
    @JoinColumn(name = "schedule_id",nullable = false)
    private Schedule schedule;
    @ManyToOne
    @JoinColumn(name = "boarding_stop_id", nullable = false)
    private RouteStop boardingStop;
    @ManyToOne
    @JoinColumn(name = "dropping_stop_id", nullable = false)
    private RouteStop droppingStop;
    @OneToMany(mappedBy = "booking")
    private List<BookingSeat> bookingSeats;
}
