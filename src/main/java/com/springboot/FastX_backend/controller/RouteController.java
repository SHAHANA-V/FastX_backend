package com.springboot.FastX_backend.controller;

import com.springboot.FastX_backend.dto.request.RouteDto;
import com.springboot.FastX_backend.dto.response.AdminRouteRespDto;
import com.springboot.FastX_backend.enums.RouteStatus;
import com.springboot.FastX_backend.model.Route;
import com.springboot.FastX_backend.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class RouteController {

    private final RouteService routeService;

    @PostMapping("/api/add/route")
    public void addRoute(@Valid @RequestBody RouteDto routeDto){
        routeService.addRoute(routeDto);
    }

    @GetMapping("/api/get/routes")
    public List<Route> getAllRoutes(@RequestParam(name = "page",required = false,defaultValue = "0")int page,
                                       @RequestParam(name = "size",required = false,defaultValue = "5")int size){
        return routeService.getAll(page,size);
    }

    @GetMapping("/api/get/active/route")
    public List<Route> getAllActiveRoutes(@RequestParam(name = "page",required = false,defaultValue = "0")int page,
                                          @RequestParam(name = "size",required = false,defaultValue = "5")int size){
        return routeService.getAllActiveRoutes(page,size);
    }

    @PutMapping("/api/update/status/{id}")
    public void updateRouteStatus(@RequestParam(name = "status")RouteStatus routeStatus, @PathVariable long id){
        routeService.updateRouteStatus(routeStatus,id);
    }

    @GetMapping("/api/admin/routes")
    public List<AdminRouteRespDto> getAllRoutesForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) RouteStatus routeStatus) {

        return routeService.getAllRoutesForAdmin(
                page, size, search, source, destination, routeStatus
        );
    }
}
