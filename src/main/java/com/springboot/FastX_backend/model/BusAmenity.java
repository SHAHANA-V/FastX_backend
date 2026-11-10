package com.springboot.FastX_backend.model;

import jakarta.persistence.*;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "bus_amenity")
public class BusAmenity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Bus bus;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Amenity amenity;

}
