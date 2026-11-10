package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.dto.request.RouteDto;
import com.springboot.FastX_backend.dto.response.AdminRouteRespDto;
import com.springboot.FastX_backend.enums.RouteStatus;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.model.Route;
import com.springboot.FastX_backend.repository.RouteRepository;
import com.springboot.FastX_backend.repository.RouteStopRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RouteServiceTest {
    @Mock
    private RouteRepository routeRepository;

    @Mock
    private RouteStopRepository routeStopRepository;

    @InjectMocks
    private RouteService routeService;


    // 1. Add route successfully
    @Test
    void addRouteTest() {

        RouteDto routeDto = new RouteDto("Chennai", "Madurai");

        routeService.addRoute(routeDto);

        ArgumentCaptor<Route> captor =
                ArgumentCaptor.forClass(Route.class);

        verify(routeRepository, times(1)).save(captor.capture());

        Route savedRoute = captor.getValue();

        assertEquals("Chennai", savedRoute.getSource());
        assertEquals("Madurai", savedRoute.getDestination());
        assertEquals(RouteStatus.ACTIVE, savedRoute.getRouteStatus());
    }


    // 2. Get all routes with pagination
    @Test
    void getAllTest() {

        Route route1 = new Route();
        route1.setId(1);
        route1.setSource("Chennai");
        route1.setDestination("Madurai");
        route1.setRouteStatus(RouteStatus.ACTIVE);

        Route route2 = new Route();
        route2.setId(2);
        route2.setSource("Chennai");
        route2.setDestination("Coimbatore");
        route2.setRouteStatus(RouteStatus.INACTIVE);

        Page<Route> page = new PageImpl<>(List.of(route1, route2));

        when(routeRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        List<Route> result = routeService.getAll(0, 5);

        assertEquals(2, result.size());
        assertEquals("Chennai", result.get(0).getSource());
        assertEquals("Madurai", result.get(0).getDestination());

        verify(routeRepository, times(1))
                .findAll(PageRequest.of(0, 5));
    }


    // 3. Get all active routes
    @Test
    void getAllActiveRoutesTest() {

        Route route = new Route();
        route.setId(1);
        route.setSource("Chennai");
        route.setDestination("Madurai");
        route.setRouteStatus(RouteStatus.ACTIVE);

        when(routeRepository.getAllActiveRoutes(any(Pageable.class)))
                .thenReturn(List.of(route));

        List<Route> result = routeService.getAllActiveRoutes(0, 5);

        assertEquals(1, result.size());
        assertEquals(RouteStatus.ACTIVE, result.get(0).getRouteStatus());

        verify(routeRepository, times(1))
                .getAllActiveRoutes(PageRequest.of(0, 5));
    }


    // 4. Update route status successfully
    @Test
    void updateRouteStatusTest() {

        Route route = new Route();
        route.setId(1);
        route.setSource("Chennai");
        route.setDestination("Madurai");
        route.setRouteStatus(RouteStatus.ACTIVE);

        when(routeRepository.findById(1L))
                .thenReturn(Optional.of(route));

        routeService.updateRouteStatus(RouteStatus.INACTIVE, 1L);

        assertEquals(RouteStatus.INACTIVE, route.getRouteStatus());

        verify(routeRepository, times(1)).findById(1L);
        verify(routeRepository, times(1)).save(route);
    }


    // 5. Update route status when route does not exist
    @Test
    void updateRouteStatusNotFoundTest() {

        when(routeRepository.findById(100L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                routeService.updateRouteStatus(RouteStatus.INACTIVE, 100L));

        verify(routeRepository, times(1)).findById(100L);
        verify(routeRepository, never()).save(any(Route.class));
    }


    // 6. Get all routes for admin with filters
    @Test
    void getAllRoutesForAdminTest() {

        AdminRouteRespDto route = new AdminRouteRespDto(
                1L,
                "Chennai",
                "Madurai",
                RouteStatus.ACTIVE,
                5L
        );

        when(routeRepository.getAllRoutesForAdmin(
                "Chennai",
                "Chennai",
                "Madurai",
                RouteStatus.ACTIVE,
                PageRequest.of(0, 5)
        )).thenReturn(List.of(route));

        List<AdminRouteRespDto> result =
                routeService.getAllRoutesForAdmin(
                        0,
                        5,
                        "Chennai",
                        "Chennai",
                        "Madurai",
                        RouteStatus.ACTIVE
                );

        assertEquals(1, result.size());
        assertEquals("Chennai", result.get(0).source());
        assertEquals("Madurai", result.get(0).destination());
        assertEquals(RouteStatus.ACTIVE, result.get(0).routeStatus());
        assertEquals(5L, result.get(0).totalStops());

        verify(routeRepository, times(1)).getAllRoutesForAdmin(
                "Chennai",
                "Chennai",
                "Madurai",
                RouteStatus.ACTIVE,
                PageRequest.of(0, 5)
        );
    }
}
