package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.dto.request.BookingDto;
import com.springboot.FastX_backend.dto.request.BookingSeatDto;
import com.springboot.FastX_backend.dto.response.*;
import com.springboot.FastX_backend.enums.*;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.model.*;
import com.springboot.FastX_backend.repository.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @InjectMocks
    private BookingService bookingService;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private RouteStopRepository routeStopRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private BookingSeatRepository bookingSeatRepository;

    @Mock
    private PaymentRepository paymentRepository;


    @Test
    public void addBookingTest() {

        String username = "john";

        Passenger passenger = new Passenger();
        passenger.setName("John");

        Route route = new Route();
        route.setId(1L);

        Bus bus = new Bus();
        bus.setId(1L);
        bus.setBusName("KNP Travels");
        bus.setBusStatus(BusStatus.ACTIVE);

        Schedule schedule = new Schedule();
        schedule.setId(1L);
        schedule.setFare(500);
        schedule.setScheduleStatus(ScheduleStatus.SCHEDULED);
        schedule.setBus(bus);
        schedule.setRoute(route);
        schedule.setJourneyDate(LocalDate.now());
        schedule.setDepartureTime(LocalTime.of(5, 0));

        RouteStop boardingStop = new RouteStop();
        boardingStop.setId(1L);
        boardingStop.setStopName("Chennai");
        boardingStop.setStopOrder(1);
        boardingStop.setRoute(route);

        RouteStop droppingStop = new RouteStop();
        droppingStop.setId(2L);
        droppingStop.setStopName("Madurai");
        droppingStop.setStopOrder(2);
        droppingStop.setRoute(route);

        Seat seat = new Seat();
        seat.setId(10L);
        seat.setSeatNumber("1A");
        seat.setBus(bus);

        BookingSeatDto seatDto = new BookingSeatDto(
                "Harry Potter",
                21,
                Gender.MALE,
                IdType.AADHAR,
                "document.pdf",
                10L
        );

        BookingDto bookingDto = new BookingDto(
                1L,
                1L,
                2L,
                List.of(seatDto)
        );

        when(passengerRepository.getPassengerByUsername(username))
                .thenReturn(passenger);

        when(scheduleRepository.findById(1L))
                .thenReturn(Optional.of(schedule));

        when(routeStopRepository.findById(1L))
                .thenReturn(Optional.of(boardingStop));

        when(routeStopRepository.findById(2L))
                .thenReturn(Optional.of(droppingStop));

        when(seatRepository.findById(10L))
                .thenReturn(Optional.of(seat));

        when(bookingSeatRepository.countSeatConflicts(
                eq(10L),
                eq(1L),
                eq(BookingStatus.CONFIRMED),
                eq(BookingStatus.PENDING),
                any(Instant.class),
                eq(2),
                eq(1)
        )).thenReturn(0L);

        BookingRespDto result =
                bookingService.addBooking(username, bookingDto);

        assertNotNull(result);
        assertEquals(500.0, result.totalAmount());
        assertEquals(BookingStatus.PENDING, result.bookingStatus());
        assertEquals("KNP Travels", result.busName());
        assertEquals("Chennai", result.boardingStop());
        assertEquals("Madurai", result.droppingStop());

        verify(passengerRepository, times(1))
                .getPassengerByUsername(username);

        verify(scheduleRepository, times(1))
                .findById(1L);

        verify(routeStopRepository, times(1))
                .findById(1L);

        verify(routeStopRepository, times(1))
                .findById(2L);

        verify(seatRepository, times(1))
                .findById(10L);

        verify(bookingRepository, times(1))
                .save(any(Booking.class));

        verify(bookingSeatRepository, times(1))
                .countSeatConflicts(
                        eq(10L),
                        eq(1L),
                        eq(BookingStatus.CONFIRMED),
                        eq(BookingStatus.PENDING),
                        any(Instant.class),
                        eq(2),
                        eq(1)
                );

        verify(bookingSeatRepository, times(1))
                .save(any(BookingSeat.class));

        verifyNoMoreInteractions(
                passengerRepository,
                scheduleRepository,
                routeStopRepository,
                seatRepository,
                bookingRepository,
                bookingSeatRepository
        );
    }


    @Test
    public void getBookingForPassengerTest() {

        String name = "john";

        MyBookingRespDto dto1 = new MyBookingRespDto(
                1L, Instant.now(), BookingStatus.CONFIRMED,
                1000.0, ScheduleStatus.SCHEDULED,
                "KNP Travels", "TN5679",
                LocalDate.now(), LocalTime.of(5, 0),
                "Chennai", "Madurai", null
        );

        MyBookingRespDto dto2 = new MyBookingRespDto(
                2L, Instant.now(), BookingStatus.CONFIRMED,
                1500.0, ScheduleStatus.SCHEDULED,
                "SPK Travels", "TN5678",
                LocalDate.now(), LocalTime.of(6, 0),
                "Chennai", "Vellore", null
        );

        when(bookingRepository.getBookingForPassenger(
                name, PageRequest.of(0, 5)
        )).thenReturn(List.of(dto1, dto2));

        when(bookingSeatRepository.getBookingSeats(1L))
                .thenReturn(new ArrayList<>());

        when(bookingSeatRepository.getBookingSeats(2L))
                .thenReturn(new ArrayList<>());

        List<MyBookingRespDto> result =
                bookingService.getBookingForPassenger(name, 0, 5);

        assertEquals(2, result.size());
        assertEquals("KNP Travels", result.get(0).busName());
        assertEquals("SPK Travels", result.get(1).busName());

        verify(bookingRepository, times(1))
                .getBookingForPassenger(name, PageRequest.of(0, 5));

        verify(bookingSeatRepository, times(1))
                .getBookingSeats(1L);

        verify(bookingSeatRepository, times(1))
                .getBookingSeats(2L);

        verifyNoMoreInteractions(
                bookingRepository,
                bookingSeatRepository
        );
    }



    @Test
    public void getTicketDetailsTest() {

        long bookingId = 1L;
        String name = "john";

        User user = new User();

        BusOperator operator = new BusOperator();
        operator.setName("Albus Dumbledore");

        Bus bus = new Bus();
        bus.setBusName("KNP Travels");
        bus.setBusNumber("TN5679");
        bus.setBusType(BusType.SLEEPER);
        bus.setAcType(AcType.AC);
        bus.setBusOperator(operator);

        Schedule schedule = new Schedule();
        schedule.setBus(bus);
        schedule.setJourneyDate(LocalDate.now());
        schedule.setDepartureTime(LocalTime.of(5, 0));
        schedule.setArrivalTime(LocalTime.of(20, 0));

        RouteStop boardingStop = new RouteStop();
        boardingStop.setStopName("Chennai");

        RouteStop droppingStop = new RouteStop();
        droppingStop.setStopName("Madurai");

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setTotalAmount(1000);
        booking.setSchedule(schedule);
        booking.setBoardingStop(boardingStop);
        booking.setDroppingStop(droppingStop);

        PaymentRespDto payment = new PaymentRespDto(
                1L,
                1000,
                PaymentMethod.UPI,
                PaymentStatus.SUCCESS,
                Instant.now(),
                bookingId
        );

        List<BookingSeatRespDto> passengers = List.of(
                new BookingSeatRespDto(
                        "Harry Potter", 21, Gender.MALE, "1A"
                )
        );

        when(bookingRepository.getBookingDetailsV2(bookingId, name))
                .thenReturn(Optional.of(booking));

        when(paymentRepository.getPaymentByBookingId(bookingId))
                .thenReturn(payment);

        when(bookingSeatRepository.getBookingSeats(bookingId))
                .thenReturn(passengers);

        TicketDetailsRespDto result =
                bookingService.getTicketDetails(bookingId, name);

        assertNotNull(result);
        assertEquals("KNP Travels", result.busName());
        assertEquals("TN5679", result.busNumber());
        assertEquals("Albus Dumbledore", result.busOperatorName());
        assertEquals(PaymentStatus.SUCCESS, result.paymentStatus());
        assertEquals(1, result.passengers().size());

        verify(bookingRepository, times(1))
                .getBookingDetailsV2(bookingId, name);

        verify(paymentRepository, times(1))
                .getPaymentByBookingId(bookingId);

        verify(bookingSeatRepository, times(1))
                .getBookingSeats(bookingId);

        verifyNoMoreInteractions(
                bookingRepository,
                paymentRepository,
                bookingSeatRepository
        );
    }


    @Test
    public void getBookingStatusStatsTest() {

        String name = "albus";

        List<Object[]> data = new ArrayList<>();

        data.add(new Object[]{BookingStatus.CONFIRMED, 5L});
        data.add(new Object[]{BookingStatus.CANCELLED, 2L});

        when(bookingRepository.getBookingStatusStats(name))
                .thenReturn(data);

        ChartDto result =
                bookingService.getBookingStatusStats(name);

        assertEquals(2, result.labels().size());
        assertEquals("CONFIRMED", result.labels().get(0));
        assertEquals("CANCELLED", result.labels().get(1));

        assertEquals(5L, result.values().get(0));
        assertEquals(2L, result.values().get(1));

        verify(bookingRepository, times(1))
                .getBookingStatusStats(name);

        verifyNoMoreInteractions(bookingRepository);
    }


    @Test
    public void getMonthlyBookingStatsTest() {

        String name = "albus";

        List<Object[]> data = new ArrayList<>();

        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = currentDate.minusMonths(5)
                .withDayOfMonth(1);

        data.add(new Object[]{
                startDate.getYear(),
                startDate.getMonthValue(),
                5L
        });

        data.add(new Object[]{
                currentDate.getYear(),
                currentDate.getMonthValue(),
                10L
        });

        Instant startInstant = startDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        when(bookingRepository.getMonthlyBookingStats(name, startInstant))
                .thenReturn(data);

        ChartDto result =
                bookingService.getMonthlyBookingStats(name);

        assertEquals(6, result.labels().size());
        assertEquals(6, result.values().size());

        assertEquals(5L, result.values().get(0));
        assertEquals(10L, result.values().get(5));

        verify(bookingRepository, times(1))
                .getMonthlyBookingStats(name, startInstant);

        verifyNoMoreInteractions(bookingRepository);
    }


    @Test
    public void getOperatorBookingCountTest() {

        String name = "albus";

        when(bookingRepository.getOperatorBookingCount(name))
                .thenReturn(10L);

        long result =
                bookingService.getOperatorBookingCount(name);

        assertEquals(10L, result);

        verify(bookingRepository, times(1))
                .getOperatorBookingCount(name);

        verifyNoMoreInteractions(bookingRepository);
    }


    @Test
    public void getBookingsForOperatorTest() {

        String name = "albus";
        BookingStatus status = BookingStatus.CONFIRMED;
        Long busId = 1L;

        OperatorBookingRespDto dto1 = new OperatorBookingRespDto(
                1L, Instant.now(), BookingStatus.CONFIRMED,
                1000.0, ScheduleStatus.SCHEDULED,
                "KNP Travels", "TN5679",
                LocalDate.now(), LocalTime.of(5, 0),
                "Chennai", "Madurai",
                "Harry Potter", "9876543210", "harry@gmail.com"
        );

        OperatorBookingRespDto dto2 = new OperatorBookingRespDto(
                2L, Instant.now(), BookingStatus.CONFIRMED,
                1500.0, ScheduleStatus.SCHEDULED,
                "SPK Travels", "TN5678",
                LocalDate.now(), LocalTime.of(6, 0),
                "Chennai", "Vellore",
                "Ron Weasley", "9876543211", "ron@gmail.com"
        );

        when(bookingRepository.getBookingsForOperator(
                name, status, busId, PageRequest.of(0, 5)
        )).thenReturn(List.of(dto1, dto2));

        List<OperatorBookingRespDto> result =
                bookingService.getBookingsForOperator(
                        name, status, busId, 0, 5
                );

        assertEquals(2, result.size());
        assertEquals("Harry Potter", result.get(0).passengerName());
        assertEquals("Ron Weasley", result.get(1).passengerName());

        verify(bookingRepository, times(1))
                .getBookingsForOperator(
                        name, status, busId, PageRequest.of(0, 5)
                );

        verifyNoMoreInteractions(bookingRepository);
    }
}