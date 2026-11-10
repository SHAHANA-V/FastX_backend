package com.springboot.FastX_backend.model;

import com.springboot.FastX_backend.enums.RouteStatus;
import jakarta.persistence.*;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private String destination;

    @Enumerated(EnumType.STRING)
    private RouteStatus routeStatus;

}
