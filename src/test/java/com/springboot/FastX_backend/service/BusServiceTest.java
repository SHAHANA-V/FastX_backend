package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.dto.request.BusDto;
import com.springboot.FastX_backend.dto.request.BusUpdateDto;
import com.springboot.FastX_backend.dto.response.BusRespForAdminDto;
import com.springboot.FastX_backend.dto.response.BusRespForOptDto;
import com.springboot.FastX_backend.enums.AcType;
import com.springboot.FastX_backend.enums.BusStatus;
import com.springboot.FastX_backend.enums.BusType;
import com.springboot.FastX_backend.exception.OwnerShipException;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.model.Bus;
import com.springboot.FastX_backend.model.BusOperator;
import com.springboot.FastX_backend.model.User;
import com.springboot.FastX_backend.repository.BusOperatorRepository;
import com.springboot.FastX_backend.repository.BusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BusServiceTest {
    @Mock
    private BusRepository busRepository;

    @Mock
    private BusOperatorRepository busOperatorRepository;

    @InjectMocks
    private BusService busService;


    // 1. Add bus successfully
    @Test
    void addBusTest() {

        BusOperator busOperator = new BusOperator();
        busOperator.setName("Albus Dumbledore");

        BusDto busDto = new BusDto(
                "TN5679",
                "KNP Travels",
                BusType.SEATER,
                30,
                AcType.AC,
                BusStatus.ACTIVE
        );

        when(busOperatorRepository.getBusOperatorByUsername("albus"))
                .thenReturn(busOperator);

        when(busRepository.save(any(Bus.class)))
                .thenAnswer(invocation -> {
                    Bus bus = invocation.getArgument(0);
                    bus.setId(1);
                    return bus;
                });

        long result = busService.addBus("albus", busDto);

        assertEquals(1, result);

        verify(busOperatorRepository, times(1))
                .getBusOperatorByUsername("albus");

        verify(busRepository, times(1)).save(any(Bus.class));
    }


    // 2. Get bus by ID successfully
    @Test
    void getBusOperatorByIdTest() {

        Bus bus = new Bus();
        bus.setId(1);
        bus.setBusName("KNP Travels");

        when(busRepository.findById(1L))
                .thenReturn(Optional.of(bus));

        Bus result = busService.getBusOperatorById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("KNP Travels", result.getBusName());

        verify(busRepository, times(1)).findById(1L);
    }


    // 3. Get bus by invalid ID
    @Test
    void getBusOperatorByIdNotFoundTest() {

        when(busRepository.findById(100L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> busService.getBusOperatorById(100));

        verify(busRepository, times(1)).findById(100L);
    }


    // 4. Get buses using dynamic filters
    @Test
    void getDynamicFilterTest() {

        BusRespForOptDto busDto = new BusRespForOptDto(
                1L,
                "TN5679",
                "KNP Travels",
                30,
                AcType.AC,
                BusType.SEATER,
                BusStatus.ACTIVE
        );

        List<BusRespForOptDto> busList = List.of(busDto);

        when(busRepository.getDynamicFilter(
                "albus",
                BusType.SEATER,
                BusStatus.ACTIVE,
                AcType.AC,
                PageRequest.of(0, 5)
        )).thenReturn(busList);

        List<BusRespForOptDto> result = busService.getDynamicFilter(
                "albus",
                0,
                5,
                BusType.SEATER,
                BusStatus.ACTIVE,
                AcType.AC
        );

        assertEquals(1, result.size());
        assertEquals("KNP Travels", result.get(0).busName());

        verify(busRepository, times(1)).getDynamicFilter(
                "albus",
                BusType.SEATER,
                BusStatus.ACTIVE,
                AcType.AC,
                PageRequest.of(0, 5)
        );
    }


    // 5. Update bus successfully
    @Test
    void updateBusTest() {

        User user = new User();
        user.setUsername("albus");

        BusOperator busOperator = new BusOperator();
        busOperator.setUser(user);

        Bus bus = new Bus();
        bus.setId(1);
        bus.setBusName("Old Travels");
        bus.setBusOperator(busOperator);

        BusUpdateDto updateDto = new BusUpdateDto(
                "New Travels",
                AcType.NON_AC,
                BusStatus.ACTIVE
        );

        when(busRepository.findById(1L))
                .thenReturn(Optional.of(bus));

        when(busRepository.save(any(Bus.class)))
                .thenReturn(bus);

        busService.updateBus("albus", 1, updateDto);

        assertEquals("New Travels", bus.getBusName());
        assertEquals(AcType.NON_AC, bus.getAcType());
        assertEquals(BusStatus.ACTIVE, bus.getBusStatus());

        verify(busRepository, times(1)).findById(1L);
        verify(busRepository, times(1)).save(bus);
    }


    // 6. Update bus by another operator
    @Test
    void updateBusOwnershipExceptionTest() {

        User user = new User();
        user.setUsername("harry");

        BusOperator busOperator = new BusOperator();
        busOperator.setUser(user);

        Bus bus = new Bus();
        bus.setId(1);
        bus.setBusOperator(busOperator);

        BusUpdateDto updateDto = new BusUpdateDto(
                "New Travels",
                AcType.AC,
                BusStatus.ACTIVE
        );

        when(busRepository.findById(1L))
                .thenReturn(Optional.of(bus));

        assertThrows(OwnerShipException.class,
                () -> busService.updateBus("albus", 1, updateDto));

        verify(busRepository, times(1)).findById(1L);
        verify(busRepository, never()).save(any(Bus.class));
    }


    // 7. Update bus without providing valid fields
    @Test
    void updateBusWithoutValidFieldsTest() {

        User user = new User();
        user.setUsername("albus");

        BusOperator busOperator = new BusOperator();
        busOperator.setUser(user);

        Bus bus = new Bus();
        bus.setId(1);
        bus.setBusOperator(busOperator);

        BusUpdateDto updateDto = new BusUpdateDto(
                null,
                null,
                null
        );

        when(busRepository.findById(1L))
                .thenReturn(Optional.of(bus));

        assertThrows(RuntimeException.class,
                () -> busService.updateBus("albus", 1, updateDto));

        verify(busRepository, times(1)).findById(1L);
        verify(busRepository, never()).save(any(Bus.class));
    }


    // 8. Get all buses for admin
    @Test
    void getAllBusForAdminTest() {

        BusRespForAdminDto busDto = new BusRespForAdminDto(
                1,
                "KNP Travels",
                "TN5679",
                BusType.SEATER,
                30,
                AcType.AC,
                BusStatus.ACTIVE,
                "Albus Dumbledore",
                "9876543210",
                "albus@gmail.com"
        );

        List<BusRespForAdminDto> busList = List.of(busDto);

        when(busRepository.getAllBusForAdmin(
                "KNP",
                "Albus",
                BusType.SEATER,
                AcType.AC,
                BusStatus.ACTIVE,
                PageRequest.of(0, 5)
        )).thenReturn(busList);

        List<BusRespForAdminDto> result = busService.getAllBusForAdmin(
                0,
                5,
                "KNP",
                "Albus",
                BusType.SEATER,
                AcType.AC,
                BusStatus.ACTIVE
        );

        assertEquals(1, result.size());
        assertEquals("KNP Travels", result.get(0).busName());

        verify(busRepository, times(1)).getAllBusForAdmin(
                "KNP",
                "Albus",
                BusType.SEATER,
                AcType.AC,
                BusStatus.ACTIVE,
                PageRequest.of(0, 5)
        );
    }


    // 9. Get operator bus count
    @Test
    void getOperatorBusCountTest() {

        when(busRepository.getOperatorBusCount("albus"))
                .thenReturn(5L);

        long result = busService.getOperatorBusCount("albus");

        assertEquals(5L, result);

        verify(busRepository, times(1))
                .getOperatorBusCount("albus");
    }
}
