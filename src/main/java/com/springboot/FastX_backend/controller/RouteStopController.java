package com.springboot.FastX_backend.controller;

import com.springboot.FastX_backend.dto.request.RouteStopDto;
import com.springboot.FastX_backend.dto.request.RouteStopUpdateDto;
import com.springboot.FastX_backend.dto.response.RouteStopRespDto;
import com.springboot.FastX_backend.dto.response.RouteStopRespV2Dto;
import com.springboot.FastX_backend.dto.response.RouteStopSelectionRespDto;
import com.springboot.FastX_backend.service.RouteStopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class RouteStopController{

    private final RouteStopService routeStopService;

   @PostMapping("/api/add/routestop/{routeId}")
    public void addRouteStop(@Valid @RequestBody RouteStopDto routeStopDto, @PathVariable long routeId){
       routeStopService.addRouteStop(routeStopDto,routeId);
   }

   @GetMapping("/api/get/routestop/{routeId}")
    public List<RouteStopRespDto> getRouteStopForRoute(@PathVariable long routeId){
       return routeStopService.getRouteStopForRoute(routeId);
   }

   @GetMapping("/api/get/routestop/stoporder/{routeId}/{stopOrder}")
    public RouteStopRespV2Dto getRouteStopByStopOrder(@PathVariable long routeId, @PathVariable int stopOrder){
       return routeStopService.getRouteStopByStopOrder(routeId,stopOrder);
   }

   @PutMapping("/api/update/routeStop/{routeId}/{routeStopId}")
    public void updateRouteStop(@Valid @RequestBody RouteStopUpdateDto routeStopUpdateDto,@PathVariable long routeId,@PathVariable long routeStopId){
       routeStopService.updateRouteStop(routeStopUpdateDto,routeId,routeStopId);
   }

   @PostMapping("/api/add/routestop/bulk/{routeId}")
    public void addRouteStopBulk(@Valid @RequestBody List<RouteStopDto> list,@PathVariable long routeId){
       routeStopService.addRouteStopBulk(list,routeId);
   }

    @GetMapping("/api/get/routestop/{routeId}/cities")
    public RouteStopSelectionRespDto getRouteStopForCities(
            @PathVariable long routeId,
            @RequestParam(name = "source") String sourceCity,
            @RequestParam(name = "destination") String destinationCity) {

        return routeStopService.getRouteStopForCities(routeId, sourceCity, destinationCity);
    }

    @DeleteMapping("/api/delete/routestop/{routeId}/{routeStopId}")
    public void deleteRouteStop(
            @PathVariable long routeId,
            @PathVariable long routeStopId) {

        routeStopService.deleteRouteStop(routeId, routeStopId);
    }
}
