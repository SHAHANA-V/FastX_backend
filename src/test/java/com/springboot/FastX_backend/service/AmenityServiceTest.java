package com.springboot.FastX_backend.service;
import com.springboot.FastX_backend.dto.request.AmenityDto;
import com.springboot.FastX_backend.dto.request.AmenityUpdateDto;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.model.Amenity;
import com.springboot.FastX_backend.repository.AmenityRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AmenityServiceTest {
    @InjectMocks
    private AmenityService amenityService;

    @Mock
    private AmenityRepository amenityRepository;


    @Test
    public void addAmenityTest() {

        AmenityDto amenityDto = new AmenityDto(
                "WiFi",
                "Free WiFi facility"
        );

        amenityService.addAmenity(amenityDto);

        verify(amenityRepository, times(1))
                .save(any(Amenity.class));

        verifyNoMoreInteractions(amenityRepository);
    }


    @Test
    public void getAllAmenityTest() {

        Amenity amenity1 = new Amenity(
                1L, "WiFi", "Free WiFi facility"
        );

        Amenity amenity2 = new Amenity(
                2L, "Charging Point", "Mobile charging facility"
        );

        when(amenityRepository.findAll(PageRequest.of(0, 5)))
                .thenReturn(new PageImpl<>(List.of(amenity1, amenity2)));

        List<Amenity> result = amenityService.getAllAmenity(0, 5);

        assertEquals(2, result.size());
        assertEquals("WiFi", result.get(0).getName());
        assertEquals("Charging Point", result.get(1).getName());

        verify(amenityRepository, times(1))
                .findAll(PageRequest.of(0, 5));

        verifyNoMoreInteractions(amenityRepository);
    }


    @Test
    public void getAmenityByIdTest() {

        long id = 1L;

        Amenity amenity = new Amenity(
                id, "WiFi", "Free WiFi facility"
        );

        when(amenityRepository.findById(id))
                .thenReturn(Optional.of(amenity));

        Amenity result = amenityService.getAmenityById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("WiFi", result.getName());

        verify(amenityRepository, times(1))
                .findById(id);

        verifyNoMoreInteractions(amenityRepository);
    }


    @Test
    public void getAmenityByIdTest_Invalid() {

        long id = 10L;

        when(amenityRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> amenityService.getAmenityById(id)
        );

        verify(amenityRepository, times(1))
                .findById(id);

        verifyNoMoreInteractions(amenityRepository);
    }


    @Test
    public void updateAmenityTest() {

        long id = 1L;

        Amenity amenity = new Amenity(
                id, "WiFi", "Old description"
        );

        AmenityUpdateDto updateDto = new AmenityUpdateDto(
                "High speed free WiFi"
        );

        when(amenityRepository.findById(id))
                .thenReturn(Optional.of(amenity));

        amenityService.updateAmenity(updateDto, id);

        assertEquals("High speed free WiFi", amenity.getDescription());

        verify(amenityRepository, times(1))
                .findById(id);

        verify(amenityRepository, times(1))
                .save(amenity);

        verifyNoMoreInteractions(amenityRepository);
    }


    @Test
    public void deleteAmenityTest() {

        long id = 1L;

        Amenity amenity = new Amenity(
                id, "WiFi", "Free WiFi facility"
        );

        when(amenityRepository.findById(id))
                .thenReturn(Optional.of(amenity));

        amenityService.deleteAmenity(id);

        verify(amenityRepository, times(1))
                .findById(id);

        verify(amenityRepository, times(1))
                .deleteById(id);

        verifyNoMoreInteractions(amenityRepository);
    }
}
