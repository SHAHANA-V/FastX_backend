package com.springboot.FastX_backend.model;

import com.springboot.FastX_backend.enums.StopType;
import jakarta.persistence.*;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class RouteStop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String stopName;
    @Enumerated(EnumType.STRING)
    private StopType stopType;
    @Column(nullable = false)
    private int stopOrder;
    @Column(nullable = false)
    private String cityName;
    @ManyToOne
    @JoinColumn(name = "route_id",nullable = false)
    private Route route;

}
