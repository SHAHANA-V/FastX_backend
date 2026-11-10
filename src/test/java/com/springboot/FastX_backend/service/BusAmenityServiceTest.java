package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.exception.AccessDeniedException;
import com.springboot.FastX_backend.exception.AmenityAlreadyExistException;
import com.springboot.FastX_backend.exception.OwnerShipException;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.model.*;
import com.springboot.FastX_backend.repository.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class BusAmenityServiceTest {
    @InjectMocks
    private BusAmenityService busAmenityService;

    @Mock
    private BusAmenityRepository busAmenityRepository;

    @Mock
    private BusRepository busRepository;

    @Mock
    private AmenityRepository amenityRepository;


    private Bus getBus(String username) {

        User user = new User();
        user.setUsername(username);

        BusOperator operator = new BusOperator();
        operator.setUser(user);

        Bus bus = new Bus();
        bus.setId(1L);
        bus.setBusOperator(operator);

        return bus;
    }


    @Test
    public void addBusAmenityTest() {

        String username = "albus";
        long busId = 1L;
        long amenityId = 2L;

        Bus bus = getBus(username);

        Amenity amenity = new Amenity();
        amenity.setId(amenityId);
        amenity.setName("WiFi");

        when(busRepository.findById(busId))
                .thenReturn(Optional.of(bus));

        when(amenityRepository.findById(amenityId))
                .thenReturn(Optional.of(amenity));

        when(busAmenityRepository.findByBusAndAmenity(busId, amenityId))
                .thenReturn(Optional.empty());

        busAmenityService.addBusAmenity(username, busId, amenityId);

        verify(busRepository, times(1)).findById(busId);
        verify(amenityRepository, times(1)).findById(amenityId);
        verify(busAmenityRepository, times(1))
                .findByBusAndAmenity(busId, amenityId);
        verify(busAmenityRepository, times(1))
                .save(any(BusAmenity.class));

        verifyNoMoreInteractions(
                busRepository,
                amenityRepository,
                busAmenityRepository
        );
    }


    @Test
    public void addBusAmenityTest_DuplicateAmenity() {

        String username = "albus";
        long busId = 1L;
        long amenityId = 2L;

        Bus bus = getBus(username);

        Amenity amenity = new Amenity();
        amenity.setId(amenityId);

        BusAmenity existing = new BusAmenity();
        existing.setBus(bus);
        existing.setAmenity(amenity);

        when(busRepository.findById(busId))
                .thenReturn(Optional.of(bus));

        when(amenityRepository.findById(amenityId))
                .thenReturn(Optional.of(amenity));

        when(busAmenityRepository.findByBusAndAmenity(busId, amenityId))
                .thenReturn(Optional.of(existing));

        assertThrows(
                AmenityAlreadyExistException.class,
                () -> busAmenityService.addBusAmenity(
                        username, busId, amenityId
                )
        );

        verify(busRepository, times(1)).findById(busId);
        verify(amenityRepository, times(1)).findById(amenityId);
        verify(busAmenityRepository, times(1))
                .findByBusAndAmenity(busId, amenityId);

        verifyNoMoreInteractions(
                busRepository,
                amenityRepository,
                busAmenityRepository
        );
    }


    @Test
    public void getAmenityForSpecificBusTest() {

        long busId = 1L;

        Bus bus = getBus("albus");

        when(busRepository.findById(busId))
                .thenReturn(Optional.of(bus));

        when(busAmenityRepository.getAmenityForSpecificBus(busId))
                .thenReturn(List.of("WiFi", "Charging Point"));

        List<String> result =
                busAmenityService.getAmenityForSpecificBus(busId);

        assertEquals(2, result.size());
        assertEquals("WiFi", result.get(0));
        assertEquals("Charging Point", result.get(1));

        verify(busRepository, times(1)).findById(busId);
        verify(busAmenityRepository, times(1))
                .getAmenityForSpecificBus(busId);

        verifyNoMoreInteractions(busRepository, busAmenityRepository);
    }


    @Test
    public void getAmenityForSpecificBusTest_InvalidBus() {

        long busId = 10L;

        when(busRepository.findById(busId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> busAmenityService.getAmenityForSpecificBus(busId)
        );

        verify(busRepository, times(1)).findById(busId);

        verifyNoMoreInteractions(busRepository, busAmenityRepository);
    }


    @Test
    public void deleteAmenityFromBusTest() {

        String username = "albus";
        long busId = 1L;
        long amenityId = 2L;

        Bus bus = getBus(username);

        Amenity amenity = new Amenity();
        amenity.setId(amenityId);

        BusAmenity busAmenity = new BusAmenity();
        busAmenity.setBus(bus);
        busAmenity.setAmenity(amenity);

        when(busRepository.findById(busId))
                .thenReturn(Optional.of(bus));

        when(amenityRepository.findById(amenityId))
                .thenReturn(Optional.of(amenity));

        when(busAmenityRepository.findByBus_IdAndAmenity_Id(busId, amenityId))
                .thenReturn(Optional.of(busAmenity));

        busAmenityService.deleteAmenityFromBus(username, busId, amenityId);

        verify(busRepository, times(1)).findById(busId);
        verify(amenityRepository, times(1)).findById(amenityId);
        verify(busAmenityRepository, times(1))
                .findByBus_IdAndAmenity_Id(busId, amenityId);
        verify(busAmenityRepository, times(1)).delete(busAmenity);

        verifyNoMoreInteractions(
                busRepository,
                amenityRepository,
                busAmenityRepository
        );
    }


    @Test
    public void deleteAmenityFromBusTest_InvalidOwner() {

        String username = "harry";
        long busId = 1L;
        long amenityId = 2L;

        Bus bus = getBus("albus");

        Amenity amenity = new Amenity();
        amenity.setId(amenityId);

        when(busRepository.findById(busId))
                .thenReturn(Optional.of(bus));

        when(amenityRepository.findById(amenityId))
                .thenReturn(Optional.of(amenity));

        assertThrows(
                AccessDeniedException.class,
                () -> busAmenityService.deleteAmenityFromBus(
                        username, busId, amenityId
                )
        );

        verify(busRepository, times(1)).findById(busId);
        verify(amenityRepository, times(1)).findById(amenityId);

        verifyNoMoreInteractions(
                busRepository,
                amenityRepository,
                busAmenityRepository
        );
    }
}
