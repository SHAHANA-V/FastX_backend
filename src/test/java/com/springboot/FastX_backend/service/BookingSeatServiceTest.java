package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.dto.response.BookingDetailsRespDto;
import com.springboot.FastX_backend.dto.response.BookingSeatRespDto;
import com.springboot.FastX_backend.enums.*;
import com.springboot.FastX_backend.exception.OwnerShipException;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.model.*;
import com.springboot.FastX_backend.repository.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingSeatServiceTest {

    @InjectMocks
    private BookingSeatService bookingSeatService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingSeatRepository bookingSeatRepository;

    @Mock
    private UserRepository userRepository;


    private Booking getBooking() {

        User passengerUser = new User();
        passengerUser.setUsername("john");

        Passenger passenger = new Passenger();
        passenger.setUser(passengerUser);

        User operatorUser = new User();
        operatorUser.setUsername("albus");

        BusOperator operator = new BusOperator();
        operator.setUser(operatorUser);

        Bus bus = new Bus();
        bus.setBusName("KNP Travels");
        bus.setBusOperator(operator);

        Schedule schedule = new Schedule();
        schedule.setBus(bus);
        schedule.setJourneyDate(LocalDate.now());
        schedule.setDepartureTime(LocalTime.of(5, 0));

        RouteStop boardingStop = new RouteStop();
        boardingStop.setStopName("Chennai");

        RouteStop droppingStop = new RouteStop();
        droppingStop.setStopName("Madurai");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setPassenger(passenger);
        booking.setSchedule(schedule);
        booking.setBoardingStop(boardingStop);
        booking.setDroppingStop(droppingStop);
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setTotalAmount(1000);

        return booking;
    }


    @Test
    public void getBookingDetailsTest_Passenger() {

        long bookingId = 1L;
        String name = "john";

        User user = new User();
        user.setUsername(name);
        user.setRole(Role.PASSENGER);

        Booking booking = getBooking();

        List<BookingSeatRespDto> seats = List.of(
                new BookingSeatRespDto(
                        "Harry Potter", 21, Gender.MALE, "1A"
                )
        );

        when(bookingRepository.getBookingDetails(bookingId))
                .thenReturn(Optional.of(booking));

        when(userRepository.findByUsername(name))
                .thenReturn(Optional.of(user));

        when(bookingSeatRepository.getBookingSeats(bookingId))
                .thenReturn(seats);

        BookingDetailsRespDto result =
                bookingSeatService.getBookingDetails(bookingId, name);

        assertNotNull(result);
        assertEquals(1L, result.bookingId());
        assertEquals("KNP Travels", result.busName());
        assertEquals("Chennai", result.boardingStop());
        assertEquals("Madurai", result.droppingStop());
        assertEquals(1, result.bookingSeats().size());

        verify(bookingRepository, times(1))
                .getBookingDetails(bookingId);

        verify(userRepository, times(1))
                .findByUsername(name);

        verify(bookingSeatRepository, times(1))
                .getBookingSeats(bookingId);

        verifyNoMoreInteractions(
                bookingRepository,
                userRepository,
                bookingSeatRepository
        );
    }


    @Test
    public void getBookingDetailsTest_BusOperator() {

        long bookingId = 1L;
        String name = "albus";

        User user = new User();
        user.setUsername(name);
        user.setRole(Role.BUS_OPERATOR);

        Booking booking = getBooking();

        when(bookingRepository.getBookingDetails(bookingId))
                .thenReturn(Optional.of(booking));

        when(userRepository.findByUsername(name))
                .thenReturn(Optional.of(user));

        when(bookingSeatRepository.getBookingSeats(bookingId))
                .thenReturn(List.of());

        BookingDetailsRespDto result =
                bookingSeatService.getBookingDetails(bookingId, name);

        assertNotNull(result);
        assertEquals(1L, result.bookingId());

        verify(bookingRepository, times(1))
                .getBookingDetails(bookingId);

        verify(userRepository, times(1))
                .findByUsername(name);

        verify(bookingSeatRepository, times(1))
                .getBookingSeats(bookingId);

        verifyNoMoreInteractions(
                bookingRepository,
                userRepository,
                bookingSeatRepository
        );
    }


    @Test
    public void getBookingDetailsTest_Admin() {

        long bookingId = 1L;
        String name = "admin";

        User user = new User();
        user.setUsername(name);
        user.setRole(Role.ADMIN);

        Booking booking = getBooking();

        when(bookingRepository.getBookingDetails(bookingId))
                .thenReturn(Optional.of(booking));

        when(userRepository.findByUsername(name))
                .thenReturn(Optional.of(user));

        when(bookingSeatRepository.getBookingSeats(bookingId))
                .thenReturn(List.of());

        BookingDetailsRespDto result =
                bookingSeatService.getBookingDetails(bookingId, name);

        assertNotNull(result);

        verify(bookingRepository, times(1))
                .getBookingDetails(bookingId);

        verify(userRepository, times(1))
                .findByUsername(name);

        verify(bookingSeatRepository, times(1))
                .getBookingSeats(bookingId);

        verifyNoMoreInteractions(
                bookingRepository,
                userRepository,
                bookingSeatRepository
        );
    }


    @Test
    public void getBookingDetailsTest_InvalidBooking() {

        long bookingId = 10L;
        String name = "john";

        when(bookingRepository.getBookingDetails(bookingId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> bookingSeatService.getBookingDetails(bookingId, name)
        );

        verify(bookingRepository, times(1))
                .getBookingDetails(bookingId);

        verifyNoMoreInteractions(
                bookingRepository,
                userRepository,
                bookingSeatRepository
        );
    }


    @Test
    public void getBookingDetailsTest_InvalidPassenger() {

        long bookingId = 1L;
        String name = "harry";

        User user = new User();
        user.setUsername(name);
        user.setRole(Role.PASSENGER);

        Booking booking = getBooking();

        when(bookingRepository.getBookingDetails(bookingId))
                .thenReturn(Optional.of(booking));

        when(userRepository.findByUsername(name))
                .thenReturn(Optional.of(user));

        assertThrows(
                OwnerShipException.class,
                () -> bookingSeatService.getBookingDetails(bookingId, name)
        );

        verify(bookingRepository, times(1))
                .getBookingDetails(bookingId);

        verify(userRepository, times(1))
                .findByUsername(name);

        verifyNoMoreInteractions(
                bookingRepository,
                userRepository,
                bookingSeatRepository
        );
    }


    @Test
    public void uploadIdDocumentTest_EmptyFile() {

        assertThrows(
                RuntimeException.class,
                () -> bookingSeatService.uploadIdDocument(null)
        );
    }

}
