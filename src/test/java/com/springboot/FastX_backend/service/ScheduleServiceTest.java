package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.dto.request.ScheduleDto;
import com.springboot.FastX_backend.dto.response.ScheduleRespDto;
import com.springboot.FastX_backend.dto.response.ScheduleUpdateDto;
import com.springboot.FastX_backend.dto.response.SeatRespV2Dto;
import com.springboot.FastX_backend.enums.*;
import com.springboot.FastX_backend.exception.OwnerShipException;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.model.*;
import com.springboot.FastX_backend.repository.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceTest {
    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private BusRepository busRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RefundRepository refundRepository;

    @Mock
    private CancellationRepository cancellationRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private ScheduleService scheduleService;


    // Helper method to create a bus
    private Bus getBus() {

        User user = new User();
        user.setUsername("albus");

        BusOperator operator = new BusOperator();
        operator.setName("Albus Dumbledore");
        operator.setUser(user);

        Bus bus = new Bus();
        bus.setId(1);
        bus.setBusName("KNP Travels");
        bus.setBusNumber("TN5679");
        bus.setBusStatus(BusStatus.ACTIVE);
        bus.setBusOperator(operator);

        return bus;
    }


    // Helper method to create a route
    private Route getRoute() {

        Route route = new Route();
        route.setId(1);
        route.setSource("Chennai");
        route.setDestination("Madurai");

        return route;
    }


    // Helper method to create a schedule
    private Schedule getSchedule() {

        Schedule schedule = new Schedule();
        schedule.setId(1);
        schedule.setJourneyDate(LocalDate.now().plusDays(2));
        schedule.setDepartureTime(LocalTime.of(6, 0));
        schedule.setArrivalTime(LocalTime.of(12, 0));
        schedule.setFare(500);
        schedule.setScheduleStatus(ScheduleStatus.SCHEDULED);
        schedule.setBus(getBus());
        schedule.setRoute(getRoute());

        return schedule;
    }


    // 1. Add schedule successfully
    @Test
    void addScheduleTest() {

        Bus bus = getBus();
        Route route = getRoute();

        ScheduleDto dto = new ScheduleDto(
                LocalDate.now().plusDays(2),
                LocalTime.of(6, 0),
                LocalTime.of(12, 0),
                500
        );

        when(busRepository.findById(1L))
                .thenReturn(Optional.of(bus));

        when(routeRepository.findById(1L))
                .thenReturn(Optional.of(route));

        when(scheduleRepository.findByBus_IdAndJourneyDateBetween(
                eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());

        scheduleService.addSchedule("albus", dto, 1L, 1L);

        ArgumentCaptor<Schedule> captor =
                ArgumentCaptor.forClass(Schedule.class);

        verify(scheduleRepository, times(1)).save(captor.capture());

        Schedule savedSchedule = captor.getValue();

        assertEquals(LocalDate.now().plusDays(2),
                savedSchedule.getJourneyDate());

        assertEquals(LocalTime.of(6, 0),
                savedSchedule.getDepartureTime());

        assertEquals(LocalTime.of(12, 0),
                savedSchedule.getArrivalTime());

        assertEquals(500, savedSchedule.getFare());
        assertEquals(ScheduleStatus.SCHEDULED,
                savedSchedule.getScheduleStatus());

        assertEquals(bus, savedSchedule.getBus());
        assertEquals(route, savedSchedule.getRoute());
    }


    // 2. Add schedule when bus does not exist
    @Test
    void addScheduleBusNotFoundTest() {

        ScheduleDto dto = new ScheduleDto(
                LocalDate.now().plusDays(2),
                LocalTime.of(6, 0),
                LocalTime.of(12, 0),
                500
        );

        when(busRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                scheduleService.addSchedule("albus", dto, 1L, 1L));

        verify(scheduleRepository, never()).save(any(Schedule.class));
    }


    // 3. Add schedule for an inactive bus
    @Test
    void addScheduleInactiveBusTest() {

        Bus bus = getBus();
        bus.setBusStatus(BusStatus.INACTIVE);

        ScheduleDto dto = new ScheduleDto(
                LocalDate.now().plusDays(2),
                LocalTime.of(6, 0),
                LocalTime.of(12, 0),
                500
        );

        when(busRepository.findById(1L))
                .thenReturn(Optional.of(bus));

        assertThrows(IllegalArgumentException.class, () ->
                scheduleService.addSchedule("albus", dto, 1L, 1L));

        verify(routeRepository, never()).findById(anyLong());
        verify(scheduleRepository, never()).save(any(Schedule.class));
    }


    // 4. Add schedule by another bus operator
    @Test
    void addScheduleOwnershipTest() {

        Bus bus = getBus();

        ScheduleDto dto = new ScheduleDto(
                LocalDate.now().plusDays(2),
                LocalTime.of(6, 0),
                LocalTime.of(12, 0),
                500
        );

        when(busRepository.findById(1L))
                .thenReturn(Optional.of(bus));

        assertThrows(OwnerShipException.class, () ->
                scheduleService.addSchedule("harry", dto, 1L, 1L));

        verify(routeRepository, never()).findById(anyLong());
        verify(scheduleRepository, never()).save(any(Schedule.class));
    }


    // 5. Add schedule when another schedule overlaps
    @Test
    void addScheduleOverlapTest() {

        Bus bus = getBus();
        Route route = getRoute();

        ScheduleDto dto = new ScheduleDto(
                LocalDate.now().plusDays(2),
                LocalTime.of(7, 0),
                LocalTime.of(13, 0),
                500
        );

        Schedule existing = getSchedule();

        when(busRepository.findById(1L))
                .thenReturn(Optional.of(bus));

        when(routeRepository.findById(1L))
                .thenReturn(Optional.of(route));

        when(scheduleRepository.findByBus_IdAndJourneyDateBetween(
                eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(existing));

        assertThrows(IllegalArgumentException.class, () ->
                scheduleService.addSchedule("albus", dto, 1L, 1L));

        verify(scheduleRepository, never()).save(any(Schedule.class));
    }


    // 6. Get all schedules
    @Test
    void getAllScheduleTest() {

        ScheduleRespDto dto = new ScheduleRespDto(
                1L, 1L,
                LocalDate.now().plusDays(2),
                LocalTime.of(6, 0),
                LocalTime.of(12, 0),
                500.0,
                ScheduleStatus.SCHEDULED,
                1L,
                "TN5679",
                "KNP Travels",
                "Albus Dumbledore",
                "Chennai",
                "Madurai",
                BusType.SLEEPER,
                AcType.AC
        );

        when(scheduleRepository.getAllSchedule(PageRequest.of(0, 5)))
                .thenReturn(List.of(dto));

        List<ScheduleRespDto> result =
                scheduleService.getAllSchedule(0, 5);

        assertEquals(1, result.size());
        assertEquals("KNP Travels", result.get(0).busName());

        verify(scheduleRepository, times(1))
                .getAllSchedule(PageRequest.of(0, 5));
    }


    // 7. Get schedules for a particular operator
    @Test
    void getAllScheduleOperatorTest() {

        ScheduleRespDto dto = new ScheduleRespDto(
                1L, 1L,
                LocalDate.now().plusDays(2),
                LocalTime.of(6, 0),
                LocalTime.of(12, 0),
                500.0,
                ScheduleStatus.SCHEDULED,
                1L,
                "TN5679",
                "KNP Travels",
                "Albus Dumbledore",
                "Chennai",
                "Madurai",
                BusType.SLEEPER,
                AcType.AC
        );

        when(scheduleRepository.getAllScheduleOperator(
                "albus",
                ScheduleStatus.SCHEDULED,
                PageRequest.of(0, 5)))
                .thenReturn(List.of(dto));

        List<ScheduleRespDto> result =
                scheduleService.getAllScheduleOperator(
                        "albus", ScheduleStatus.SCHEDULED, 0, 5);

        assertEquals(1, result.size());
        assertEquals(ScheduleStatus.SCHEDULED,
                result.get(0).scheduleStatus());

        verify(scheduleRepository, times(1))
                .getAllScheduleOperator(
                        "albus",
                        ScheduleStatus.SCHEDULED,
                        PageRequest.of(0, 5));
    }


    // 8. Update schedule successfully
    @Test
    void updateScheduleTest() {

        Schedule schedule = getSchedule();

        ScheduleUpdateDto dto = new ScheduleUpdateDto(
                null,
                LocalTime.of(7, 0),
                null,
                600.0,
                null
        );

        when(scheduleRepository.findById(1L))
                .thenReturn(Optional.of(schedule));

        when(scheduleRepository.findByBus_IdAndJourneyDateBetween(
                eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(schedule));

        scheduleService.updateSchedule("albus", 1L, dto);

        assertEquals(LocalTime.of(7, 0),
                schedule.getDepartureTime());

        assertEquals(600, schedule.getFare());

        verify(scheduleRepository, times(1)).save(schedule);
    }


    // 9. Update completed schedule
    @Test
    void updateCompletedScheduleTest() {

        Schedule schedule = getSchedule();
        schedule.setScheduleStatus(ScheduleStatus.COMPLETED);

        ScheduleUpdateDto dto = new ScheduleUpdateDto(
                null,
                LocalTime.of(7, 0),
                null,
                null,
                null
        );

        when(scheduleRepository.findById(1L))
                .thenReturn(Optional.of(schedule));

        assertThrows(IllegalArgumentException.class, () ->
                scheduleService.updateSchedule("albus", 1L, dto));

        verify(scheduleRepository, never()).save(any(Schedule.class));
    }


    // 10. Update schedule without providing fields
    @Test
    void updateScheduleNoFieldsTest() {

        Schedule schedule = getSchedule();

        ScheduleUpdateDto dto = new ScheduleUpdateDto(
                null, null, null, null, null
        );

        when(scheduleRepository.findById(1L))
                .thenReturn(Optional.of(schedule));

        assertThrows(IllegalArgumentException.class, () ->
                scheduleService.updateSchedule("albus", 1L, dto));

        verify(scheduleRepository, never()).save(any(Schedule.class));
    }


    // 11. Filter schedules with blank source and destination
    @Test
    void getScheduleFilterTest() {

        when(scheduleRepository.getScheduleFilter(
                null, null, null, null, null, null, null))
                .thenReturn(List.of());

        List<ScheduleRespDto> result =
                scheduleService.getScheduleFilter(
                        " ", " ", null, null, null, null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(scheduleRepository, times(1)).getScheduleFilter(
                null, null, null, null, null, null, null);
    }


    // 12. Get seats by schedule ID
    @Test
    void getSeatsByScheduleIdTest() {

        Schedule schedule = getSchedule();

        SeatRespV2Dto seat = new SeatRespV2Dto(
                1L, "1A", SeatType.WINDOW, false
        );

        when(scheduleRepository.findById(1L))
                .thenReturn(Optional.of(schedule));

        when(seatRepository.getAllSeatsForBus(
                1L, PageRequest.of(0, 5)))
                .thenReturn(List.of(seat));

        List<SeatRespV2Dto> result =
                scheduleService.getSeatsByScheduleId(1L, 0, 5);

        assertEquals(1, result.size());
        assertEquals("1A", result.get(0).seatNumber());

        verify(seatRepository, times(1))
                .getAllSeatsForBus(1L, PageRequest.of(0, 5));
    }


    // 13. Get schedule details by schedule ID
    @Test
    void getScheduleRouteTest() {

        Schedule schedule = getSchedule();

        when(scheduleRepository.findById(1L))
                .thenReturn(Optional.of(schedule));

        ScheduleRespDto result =
                scheduleService.getScheduleRoute(1L);

        assertEquals(1L, result.scheduleId());
        assertEquals("KNP Travels", result.busName());
        assertEquals("Chennai", result.source());
        assertEquals("Madurai", result.destination());
    }


    // 14. Get operator schedule count
    @Test
    void getOperatorScheduleCountTest() {

        when(scheduleRepository.getOperatorScheduleCount("albus"))
                .thenReturn(5L);

        long result =
                scheduleService.getOperatorScheduleCount("albus");

        assertEquals(5L, result);

        verify(scheduleRepository, times(1))
                .getOperatorScheduleCount("albus");
    }


    // 15. Cancel schedule successfully when there are no bookings
    @Test
    void cancelScheduleTest() {

        Schedule schedule = getSchedule();

        when(scheduleRepository.findById(1L))
                .thenReturn(Optional.of(schedule));

        when(bookingRepository.findBySchedule_Id(1L))
                .thenReturn(List.of());

        scheduleService.cancelSchedule("albus", 1L, "Bus maintenance");

        assertEquals(ScheduleStatus.CANCELLED,
                schedule.getScheduleStatus());

        verify(scheduleRepository, times(1)).save(schedule);
        verify(bookingRepository, times(1)).findBySchedule_Id(1L);
        verify(notificationRepository, never()).save(any(Notification.class));
    }
}
