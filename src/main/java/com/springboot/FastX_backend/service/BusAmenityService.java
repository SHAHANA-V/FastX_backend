package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.dto.response.BusAmenityRespDto;
import com.springboot.FastX_backend.exception.AccessDeniedException;
import com.springboot.FastX_backend.exception.AmenityAlreadyExistException;
import com.springboot.FastX_backend.exception.OwnerShipException;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.model.Amenity;
import com.springboot.FastX_backend.model.Bus;
import com.springboot.FastX_backend.model.BusAmenity;
import com.springboot.FastX_backend.repository.AmenityRepository;
import com.springboot.FastX_backend.repository.BusAmenityRepository;
import com.springboot.FastX_backend.repository.BusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BusAmenityService {
    private final BusAmenityRepository busAmenityRepository;
    private final BusRepository busRepository;
    private final AmenityRepository amenityRepository;

    public void addBusAmenity(String busOperatorName, long busId, long amenityId) {
        //step 1: fetch the bus from db
        Bus bus = busRepository.findById(busId).orElseThrow(()->new ResourceNotFoundException("Bus Id is invalid"));

        //step 2:check whether the logged in user has access to the bus
        if(!(bus.getBusOperator().getUser().getUsername().equals(busOperatorName))){
            throw new OwnerShipException("Bus not found for this operator");
        }

        //step 3:fetch the amenity from the db
        Amenity amenity = amenityRepository.findById(amenityId).orElseThrow(()->new ResourceNotFoundException("Amenity Id is invalid..."));

        //step 4:prepare the busAmenity object
        BusAmenity busAmenity = new BusAmenity();

        //step 5:check whether the amenity is already attached to the bus
        Optional<BusAmenity> existing = busAmenityRepository.findByBusAndAmenity(busId, amenityId);
        if (existing.isPresent()) {
            throw new AmenityAlreadyExistException("This amenity is already attached to the bus.");
        }

        //step 6:set the bus and amenity
        busAmenity.setBus(bus);
        busAmenity.setAmenity(amenity);

        //step 7:save the obj in repo
        busAmenityRepository.save(busAmenity);

    }

    public List<String> getAmenityForSpecificBus(long busId) {
        Bus bus = busRepository.findById(busId).orElseThrow(()->new ResourceNotFoundException("Bus Id is invalid"));
        return busAmenityRepository.getAmenityForSpecificBus(busId);
    }


    public void deleteAmenityFromBus(String busOperatorName, long busId, long amenityId) {
        //Step 1:check whether the bus exists
        Bus bus = busRepository.findById(busId).orElseThrow(()->new ResourceNotFoundException("Bus Id is invalid.."));

        //step 2:check whether the amenity exist
        Amenity amenity = amenityRepository.findById(amenityId).orElseThrow(()->new ResourceNotFoundException("Amenity Id is invalid.."));

        //step 3:check the logged in user is allowed to access the bus
        if(!(bus.getBusOperator().getUser().getUsername().equals(busOperatorName))){
            throw new AccessDeniedException("You are not authorized to modify this bus");
        }
        //step 4:check whether the bus has a amenity attached if no throw error
        BusAmenity busAmenity = busAmenityRepository
                .findByBus_IdAndAmenity_Id(busId, amenityId)
                .orElseThrow(() -> new ResourceNotFoundException("This amenity is not assigned to the bus"));

        //step 5:delete it
        busAmenityRepository.delete(busAmenity);
    }
}
