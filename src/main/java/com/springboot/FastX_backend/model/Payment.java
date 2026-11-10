package com.springboot.FastX_backend.model;

import com.springboot.FastX_backend.enums.PaymentMethod;
import com.springboot.FastX_backend.enums.PaymentStatus;
import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private double amount;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    private Instant paidAt;
    @OneToOne
    @JoinColumn(name = "booking_id",nullable = false,unique = true)
    private Booking booking;
}
