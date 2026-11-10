package com.springboot.FastX_backend.model;

import com.springboot.FastX_backend.enums.CancellationStatus;
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
public class Cancellation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false,length = 500)
    private String reason;
    @CreationTimestamp
    private Instant requestedAt;
    private Instant cancelledAt;
    @Column(nullable = false)
    private double refundAmount;
    @Enumerated(EnumType.STRING)
    private CancellationStatus cancellationStatus;
    @OneToOne
    private Booking booking;

}
