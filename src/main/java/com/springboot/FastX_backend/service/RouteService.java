package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.dto.request.RouteDto;
import com.springboot.FastX_backend.dto.response.AdminRouteRespDto;
import com.springboot.FastX_backend.enums.RouteStatus;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.mapper.RouteMapper;
import com.springboot.FastX_backend.model.Route;
import com.springboot.FastX_backend.repository.RouteRepository;
import com.springboot.FastX_backend.repository.RouteStopRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;

    private final RouteStopRepository routeStopRepository;

    public void addRoute(@Valid RouteDto routeDto) {
        //step 1:prepare route object
        Route route = RouteMapper.convertDtoToModel(routeDto);

        //step 2:set status
        route.setRouteStatus(RouteStatus.ACTIVE);

        //step 3:save in db
        routeRepository.save(route);
    }

    public List<Route> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        return routeRepository.findAll(pageable).getContent();
    }

    public List<Route> getAllActiveRoutes(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        return routeRepository.getAllActiveRoutes(pageable);
    }

    public void updateRouteStatus(RouteStatus routeStatus, long id) {
        Route route = routeRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Invalid Route id"));
        route.setRouteStatus(routeStatus);
        routeRepository.save(route);
    }
    public List<AdminRouteRespDto> getAllRoutesForAdmin(
            int page, int size, String search, String source,
            String destination, RouteStatus routeStatus) {

        Pageable pageable = PageRequest.of(page, size);

        return routeRepository.getAllRoutesForAdmin(
                search, source, destination, routeStatus, pageable
        );
    }
}
