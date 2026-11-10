package com.springboot.FastX_backend.service;
import com.springboot.FastX_backend.dto.request.CancellationDto;
import com.springboot.FastX_backend.dto.response.CancellationRespDto;
import com.springboot.FastX_backend.enums.BookingStatus;
import com.springboot.FastX_backend.enums.CancellationStatus;
import com.springboot.FastX_backend.enums.RefundStatus;
import com.springboot.FastX_backend.exception.OwnerShipException;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.model.*;
import com.springboot.FastX_backend.repository.BookingRepository;
import com.springboot.FastX_backend.repository.CancellationRepository;
import com.springboot.FastX_backend.repository.RefundRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CancellationServiceTest {
    @Mock
    private CancellationRepository cancellationRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RefundRepository refundRepository;

    @InjectMocks
    private CancellationService cancellationService;


    // Helper method to create a confirmed booking
    private Booking getBooking() {

        User passengerUser = new User();
        passengerUser.setUsername("harry");

        Passenger passenger = new Passenger();
        passenger.setName("Harry Potter");
        passenger.setPhoneNo("9876543210");
        passenger.setUser(passengerUser);

        User operatorUser = new User();
        operatorUser.setUsername("albus");

        BusOperator operator = new BusOperator();
        operator.setUser(operatorUser);

        Bus bus = new Bus();
        bus.setBusName("KNP Travels");
        bus.setBusOperator(operator);

        Schedule schedule = new Schedule();
        schedule.setJourneyDate(LocalDate.now().plusDays(3));
        schedule.setDepartureTime(LocalTime.of(10, 0));
        schedule.setBus(bus);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setTotalAmount(1000);
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setPassenger(passenger);
        booking.setSchedule(schedule);

        return booking;
    }


    // 1. Request cancellation successfully
    @Test
    void requestCancellationTest() {

        Booking booking = getBooking();

        CancellationDto dto = new CancellationDto("Personal reason");

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(cancellationRepository.existsByBookingId(1L))
                .thenReturn(false);

        cancellationService.requestCancellation(1L, "harry", dto);

        verify(bookingRepository, times(1)).findById(1L);

        verify(cancellationRepository, times(1))
                .existsByBookingId(1L);

        verify(cancellationRepository, times(1))
                .save(any(Cancellation.class));

        verifyNoInteractions(refundRepository);
    }


    // 2. Booking not found
    @Test
    void requestCancellationBookingNotFoundTest() {

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> cancellationService.requestCancellation(
                        1L,
                        "harry",
                        new CancellationDto("Personal reason")
                ));

        verify(bookingRepository, times(1)).findById(1L);

        verifyNoInteractions(cancellationRepository);
    }


    // 3. Passenger cannot cancel another passenger's booking
    @Test
    void requestCancellationOwnershipTest() {

        Booking booking = getBooking();

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        assertThrows(OwnerShipException.class,
                () -> cancellationService.requestCancellation(
                        1L,
                        "ron",
                        new CancellationDto("Personal reason")
                ));

        verify(bookingRepository, times(1)).findById(1L);

        verifyNoInteractions(cancellationRepository);
    }


    // 4. Only confirmed bookings can be cancelled
    @Test
    void requestCancellationInvalidBookingStatusTest() {

        Booking booking = getBooking();
        booking.setBookingStatus(BookingStatus.PENDING);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class,
                () -> cancellationService.requestCancellation(
                        1L,
                        "harry",
                        new CancellationDto("Personal reason")
                ));

        verifyNoInteractions(cancellationRepository);
    }


    // 5. Duplicate cancellation request
    @Test
    void requestCancellationAlreadyExistsTest() {

        Booking booking = getBooking();

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(cancellationRepository.existsByBookingId(1L))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> cancellationService.requestCancellation(
                        1L,
                        "harry",
                        new CancellationDto("Personal reason")
                ));

        verify(cancellationRepository, times(1))
                .existsByBookingId(1L);

        verify(cancellationRepository, never())
                .save(any(Cancellation.class));
    }


    // 6. Approve cancellation and create refund
    @Test
    void updateCancellationApprovedTest() {

        Booking booking = getBooking();

        Cancellation cancellation = new Cancellation();
        cancellation.setId(5L);
        cancellation.setBooking(booking);
        cancellation.setCancellationStatus(CancellationStatus.REQUESTED);
        cancellation.setRefundAmount(900);

        when(cancellationRepository.findById(5L))
                .thenReturn(Optional.of(cancellation));

        cancellationService.updateCancellation(
                5L,
                CancellationStatus.APPROVED,
                "albus"
        );

        assertEquals(CancellationStatus.APPROVED,
                cancellation.getCancellationStatus());

        assertEquals(BookingStatus.CANCELLED,
                booking.getBookingStatus());

        verify(bookingRepository, times(1)).save(booking);

        verify(refundRepository, times(1))
                .save(argThat(refund ->
                        refund.getAmount() == 900 &&
                                refund.getRefundStatus() == RefundStatus.PENDING &&
                                refund.getCancellation() == cancellation
                ));

        verify(cancellationRepository, times(1))
                .save(cancellation);
    }


    // 7. Reject cancellation without creating refund
    @Test
    void updateCancellationRejectedTest() {

        Booking booking = getBooking();

        Cancellation cancellation = new Cancellation();
        cancellation.setId(5L);
        cancellation.setBooking(booking);
        cancellation.setCancellationStatus(CancellationStatus.REQUESTED);
        cancellation.setRefundAmount(900);

        when(cancellationRepository.findById(5L))
                .thenReturn(Optional.of(cancellation));

        cancellationService.updateCancellation(
                5L,
                CancellationStatus.REJECTED,
                "albus"
        );

        assertEquals(CancellationStatus.REJECTED,
                cancellation.getCancellationStatus());

        assertEquals(BookingStatus.CONFIRMED,
                booking.getBookingStatus());

        verify(cancellationRepository, times(1))
                .save(cancellation);

        verify(bookingRepository, never()).save(any(Booking.class));

        verifyNoInteractions(refundRepository);
    }



}
