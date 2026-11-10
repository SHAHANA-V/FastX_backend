package com.springboot.FastX_backend.controller;

import com.springboot.FastX_backend.dto.response.BusAmenityRespDto;
import com.springboot.FastX_backend.model.Bus;
import com.springboot.FastX_backend.model.BusAmenity;
import com.springboot.FastX_backend.service.BusAmenityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class BusAmenityController {
    private final BusAmenityService busAmenityService;

    @PostMapping("/api/add/bus/amenity/{busId}/{amenityId}")
    public void addBusAmenity(Principal principal,@PathVariable long busId,@PathVariable long amenityId){
        busAmenityService.addBusAmenity(principal.getName(),busId,amenityId);
    }

    @GetMapping("/api/bus/amenity/{busId}")
    public List<String> getAmenityForSpecificBus(@PathVariable long busId){
        return busAmenityService.getAmenityForSpecificBus(busId);
    }

    @DeleteMapping("/api/delete/amenity/bus/{busId}/{amenityId}")
    public void deleteAmenityFromBus(Principal principal,@PathVariable long busId,@PathVariable long amenityId){
        busAmenityService.deleteAmenityFromBus(principal.getName(),busId,amenityId);
    }

}
