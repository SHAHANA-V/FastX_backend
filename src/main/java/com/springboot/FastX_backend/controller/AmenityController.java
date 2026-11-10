package com.springboot.FastX_backend.controller;

import com.springboot.FastX_backend.dto.request.AmenityDto;
import com.springboot.FastX_backend.dto.request.AmenityUpdateDto;
import com.springboot.FastX_backend.model.Amenity;
import com.springboot.FastX_backend.service.AmenityService;
import com.springboot.FastX_backend.service.BusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class AmenityController {
    private final AmenityService amenityService;

    @PostMapping("/api/add/amenity")
    public void addAmenity(@Valid @RequestBody AmenityDto amenityDto){
        amenityService.addAmenity(amenityDto);
    }

    @GetMapping("api/amenity/all")
    public List<Amenity> getAllAmenity(@RequestParam(name = "page",required = false,defaultValue = "0")int page,
                                       @RequestParam(name = "size",required = false,defaultValue = "5")int size){
        return amenityService.getAllAmenity(page,size);
    }

    @GetMapping("/api/amenity/{id}")
    public Amenity getAmenityById(@PathVariable long id){
        return amenityService.getAmenityById(id);
    }

    @PutMapping("/api/amenity/update/description/{id}")
    public void updateAmenity(@RequestBody AmenityUpdateDto amenityUpdateDto, @PathVariable long id){
        amenityService.updateAmenity(amenityUpdateDto,id);
    }

    @DeleteMapping("/api/amenity/delete/{id}")
    public void deleteAmenity(@PathVariable long id){
        amenityService.deleteAmenity(id);
    }
}
