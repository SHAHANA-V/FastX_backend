package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.dto.request.RouteStopDto;
import com.springboot.FastX_backend.dto.request.RouteStopUpdateDto;
import com.springboot.FastX_backend.dto.response.RouteStopRespDto;
import com.springboot.FastX_backend.dto.response.RouteStopRespV2Dto;
import com.springboot.FastX_backend.dto.response.RouteStopSelectionRespDto;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.mapper.RouteStopMapper;
import com.springboot.FastX_backend.model.Route;
import com.springboot.FastX_backend.model.RouteStop;
import com.springboot.FastX_backend.repository.RouteRepository;
import com.springboot.FastX_backend.repository.RouteStopRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class RouteStopService {
    private final RouteStopRepository routeStopRepository;
    private final RouteRepository routeRepository;

    @Transactional(rollbackFor = Exception.class)
    public void addRouteStop(@Valid RouteStopDto routeStopDto, long routeId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route id invalid..."));

        List<RouteStop> routeStops =
                routeStopRepository.findAllByRoute_IdOrderByStopOrderAscIdAsc(routeId);

        if (routeStopDto.stopOrder() < 1 ||
                routeStopDto.stopOrder() > routeStops.size() + 1) {
            throw new RuntimeException("Invalid stop order");
        }

        RouteStop routeStop = RouteStopMapper.covertDtoToModel(routeStopDto);
        routeStop.setRoute(route);

        // Insert the new stop at the requested position
        routeStops.add(routeStopDto.stopOrder() - 1, routeStop);

        // Temporarily assign negative orders
        for (int i = 0; i < routeStops.size(); i++) {
            routeStops.get(i).setStopOrder(-(i + 1));
        }

        routeStopRepository.saveAllAndFlush(routeStops);

        // Assign sequential positive orders
        for (int i = 0; i < routeStops.size(); i++) {
            routeStops.get(i).setStopOrder(i + 1);
        }

        routeStopRepository.saveAll(routeStops);
    }

    public List<RouteStopRespDto> getRouteStopForRoute(long routeId) {
        Route route = routeRepository.findById(routeId).orElseThrow(()->new ResourceNotFoundException("Route id invalid..."));
        return routeStopRepository.getRouteStopForRoute(routeId);
    }

    public RouteStopRespV2Dto getRouteStopByStopOrder(long routeId, int stopOrder) {
        Route route = routeRepository.findById(routeId).orElseThrow(()->new ResourceNotFoundException("Route id invalid..."));
        Optional<RouteStopRespV2Dto> optional = routeStopRepository.getRouteStopByStopOrder(routeId,stopOrder);
        if(optional.isEmpty()){
            throw new ResourceNotFoundException("No Stop on this Stop Order Id is found");
        }
        return optional.get();
    }

    public void updateRouteStop(@Valid RouteStopUpdateDto routeStopUpdateDto,long routeId,long routeStopId) {
        //check route exists
        Route route = routeRepository.findById(routeId).orElseThrow(()->new ResourceNotFoundException("Route id invalid..."));

        //check route stop exists
        RouteStop routeStop = routeStopRepository.findByIdAndRoute_Id(routeStopId, routeId).orElseThrow(() -> new ResourceNotFoundException("Route stop not found"));

        boolean flag = false;

        //update stopname
        if(!(routeStopUpdateDto.stopName()==null) && !(routeStopUpdateDto.stopName().trim().isEmpty())){
            routeStop.setStopName(routeStopUpdateDto.stopName());
            flag = true;
        }

        // update cityname
        if (routeStopUpdateDto.cityName() != null && !routeStopUpdateDto.cityName().trim().isEmpty()) {
            routeStop.setCityName(routeStopUpdateDto.cityName());
            flag = true;
        }

        //update stoptype
        if(!(routeStopUpdateDto.stopType()==null)) {
            routeStop.setStopType(routeStopUpdateDto.stopType());
            flag = true;
        }

        if(!flag){
            throw new RuntimeException("No Valid fields Provided for update");
        }

        routeStopRepository.save(routeStop);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addRouteStopBulk(List<RouteStopDto> list, long routeId) {
        Route route = routeRepository.findById(routeId).orElseThrow(()->new ResourceNotFoundException("Route id invalid..."));
        List<RouteStop> routeStopList = list.stream()
                .map(dto -> {
                    RouteStop routeStop = RouteStopMapper.covertDtoToModel(dto);
                    routeStop.setRoute(route);
                    return routeStop;
                })
                .toList();
        routeStopRepository.saveAll(routeStopList);
    }

    public RouteStopSelectionRespDto getRouteStopForCities(long routeId, String sourceCity, String destinationCity) {
        // Check route exists
        routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route id invalid..."));

        // Get boarding stops
        List<RouteStopRespDto> boardingStops =
                routeStopRepository.getBoardingStops(routeId, sourceCity);

        // Get dropping stops
        List<RouteStopRespDto> droppingStops =
                routeStopRepository.getDroppingStops(routeId, destinationCity);

        // Remove dropping stops that occur before or at the boarding stop
        if (!boardingStops.isEmpty() && !droppingStops.isEmpty()) {

            int firstBoardingOrder = boardingStops.get(0).stopOrder();

            droppingStops = droppingStops.stream()
                    .filter(stop -> stop.stopOrder() > firstBoardingOrder)
                    .toList();
        }

        return new RouteStopSelectionRespDto(boardingStops, droppingStops);
    }
    @Transactional(rollbackFor = Exception.class)
    public void deleteRouteStop(long routeId, long routeStopId) {

        routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route id invalid..."));

        RouteStop routeStop = routeStopRepository
                .findByIdAndRoute_Id(routeStopId, routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route stop not found"));

        List<RouteStop> routeStops =
                routeStopRepository.findAllByRoute_IdOrderByStopOrderAscIdAsc(routeId);

        // Remove the selected stop
        routeStops.remove(routeStop);

        routeStopRepository.delete(routeStop);
        routeStopRepository.flush();

        // Temporarily assign negative orders
        for (int i = 0; i < routeStops.size(); i++) {
            routeStops.get(i).setStopOrder(-(i + 1));
        }

        routeStopRepository.saveAllAndFlush(routeStops);

        // Reassign sequential orders
        for (int i = 0; i < routeStops.size(); i++) {
            routeStops.get(i).setStopOrder(i + 1);
        }

        routeStopRepository.saveAll(routeStops);
    }
}
