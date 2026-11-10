package com.springboot.FastX_backend.mapper;

import com.springboot.FastX_backend.dto.request.AmenityDto;
import com.springboot.FastX_backend.model.Amenity;

public class AmenityMapper {
    public static Amenity concertDtoToModel(AmenityDto amenityDto){
        Amenity amenity = new Amenity();
        amenity.setName(amenityDto.name());
        amenity.setDescription(amenityDto.description());
        return amenity;
    }
}
