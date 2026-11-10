package com.springboot.FastX_backend.model;

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
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    @Column(length = 500)
    private String message;
    @CreationTimestamp
    private Instant createdAt;
    @Column(nullable = false)
    private boolean isRead = false;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
