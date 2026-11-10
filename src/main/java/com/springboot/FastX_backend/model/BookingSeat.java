package com.springboot.FastX_backend.model;

import com.springboot.FastX_backend.enums.Gender;
import com.springboot.FastX_backend.enums.IdType;
import jakarta.persistence.*;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "booking_seat")
public class BookingSeat {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String passengerName;
    @Column(nullable = false)
    private int age;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Enumerated(EnumType.STRING)
    private IdType idType;
    private String idDocument;
    @ManyToOne
    @JoinColumn(name = "booking_id",nullable = false)
    private  Booking booking;
    @ManyToOne
    @JoinColumn(name = "seat_id",nullable = false)
    private Seat seat;


}
