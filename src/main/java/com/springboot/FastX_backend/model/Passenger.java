package com.springboot.FastX_backend.model;

import jakarta.persistence.*;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false,updatable = true)
    private String name;

    @Column(name = "phone_no", nullable = false)
    private String phoneNo;

    @Column(nullable = false)
    private String email;

    @OneToOne
    private User user;


}
