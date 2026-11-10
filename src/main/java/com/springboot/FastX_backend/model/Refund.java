package com.springboot.FastX_backend.model;

import com.springboot.FastX_backend.enums.RefundStatus;
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
public class Refund {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private double amount;
    @Enumerated(EnumType.STRING)
    private RefundStatus refundStatus;
    @CreationTimestamp
    private Instant createdAt;

    private Instant processedAt;
    @OneToOne
    private Cancellation cancellation;

}
