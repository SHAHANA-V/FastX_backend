package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.dto.request.AmenityDto;
import com.springboot.FastX_backend.dto.request.AmenityUpdateDto;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.mapper.AmenityMapper;
import com.springboot.FastX_backend.model.Amenity;
import com.springboot.FastX_backend.repository.AmenityRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AmenityService {
    private final AmenityRepository amenityRepository;

    public void addAmenity(@Valid AmenityDto amenityDto) {

        Amenity amenity = AmenityMapper.concertDtoToModel(amenityDto);
        amenityRepository.save(amenity);
    }

    public List<Amenity> getAllAmenity(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return amenityRepository.findAll(pageable).get().toList();
    }

    public Amenity getAmenityById(long id) {
        return amenityRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Amenity Id is invalid..."));
    }

    public void updateAmenity(AmenityUpdateDto amenityUpdateDto, long id) {
        // Step 1: Fetch amenity from DB
        Amenity amenity = amenityRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Amenity Id is invalid..."));

        // Step 2: Validate description
        if (!(amenityUpdateDto.description()==null) && !(amenityUpdateDto.description().trim().isEmpty())) {
            amenity.setDescription(amenityUpdateDto.description());
        }

        // Step 3: Save changes
        amenityRepository.save(amenity);
    }

    public void deleteAmenity(long id) {
        getAmenityById(id);
        amenityRepository.deleteById(id);
    }
}
