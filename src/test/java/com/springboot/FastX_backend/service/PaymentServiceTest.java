package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.dto.request.PaymentDto;
import com.springboot.FastX_backend.dto.response.PaymentRespDto;
import com.springboot.FastX_backend.enums.BookingStatus;
import com.springboot.FastX_backend.enums.PaymentMethod;
import com.springboot.FastX_backend.enums.PaymentStatus;
import com.springboot.FastX_backend.exception.OwnerShipException;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.model.Booking;
import com.springboot.FastX_backend.model.Passenger;
import com.springboot.FastX_backend.model.Payment;
import com.springboot.FastX_backend.model.User;
import com.springboot.FastX_backend.repository.BookingRepository;
import com.springboot.FastX_backend.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private PaymentService paymentService;


    private Booking getBooking() {

        User user = new User();
        user.setUsername("harry");

        Passenger passenger = new Passenger();
        passenger.setName("Harry Potter");
        passenger.setUser(user);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setTotalAmount(1000);
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setPaymentDeadline(Instant.now().plusSeconds(900));
        booking.setPassenger(passenger);

        return booking;
    }


    // 1. Initiate payment successfully
    @Test
    void initiatePaymentTest() {

        Booking booking = getBooking();

        PaymentDto dto = new PaymentDto(PaymentMethod.UPI);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(paymentRepository.existsByBookingId(1L))
                .thenReturn(false);

        paymentService.initiatePayment(1L, dto, "harry");

        verify(paymentRepository, times(1))
                .save(argThat(payment ->
                        payment.getAmount() == 1000 &&
                                payment.getPaymentMethod() == PaymentMethod.UPI &&
                                payment.getPaymentStatus() == PaymentStatus.SUCCESS &&
                                payment.getBooking() == booking &&
                                payment.getPaidAt() != null
                ));

        assertEquals(BookingStatus.CONFIRMED,
                booking.getBookingStatus());

        verify(bookingRepository, times(1)).save(booking);
    }


    // 2. Booking not found
    @Test
    void initiatePaymentBookingNotFoundTest() {

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentService.initiatePayment(
                        1L,
                        new PaymentDto(PaymentMethod.UPI),
                        "harry"
                ));

        verifyNoInteractions(paymentRepository);
    }


    // 3. User cannot pay for another passenger's booking
    @Test
    void initiatePaymentOwnershipTest() {

        Booking booking = getBooking();

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        assertThrows(OwnerShipException.class,
                () -> paymentService.initiatePayment(
                        1L,
                        new PaymentDto(PaymentMethod.UPI),
                        "ron"
                ));

        verifyNoInteractions(paymentRepository);
        verify(bookingRepository, never()).save(any(Booking.class));
    }


    // 4. Booking status is not pending
    @Test
    void initiatePaymentInvalidBookingStatusTest() {

        Booking booking = getBooking();
        booking.setBookingStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class,
                () -> paymentService.initiatePayment(
                        1L,
                        new PaymentDto(PaymentMethod.UPI),
                        "harry"
                ));

        verifyNoInteractions(paymentRepository);
        verify(bookingRepository, never()).save(any(Booking.class));
    }


    // 5. Payment deadline has expired
    @Test
    void initiatePaymentExpiredDeadlineTest() {

        Booking booking = getBooking();
        booking.setPaymentDeadline(Instant.now().minusSeconds(60));

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class,
                () -> paymentService.initiatePayment(
                        1L,
                        new PaymentDto(PaymentMethod.UPI),
                        "harry"
                ));

        verifyNoInteractions(paymentRepository);
        verify(bookingRepository, never()).save(any(Booking.class));
    }


    // 6. Payment already initiated
    @Test
    void initiatePaymentAlreadyExistsTest() {

        Booking booking = getBooking();

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(paymentRepository.existsByBookingId(1L))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> paymentService.initiatePayment(
                        1L,
                        new PaymentDto(PaymentMethod.UPI),
                        "harry"
                ));

        verify(paymentRepository, never()).save(any(Payment.class));
        verify(bookingRepository, never()).save(any(Booking.class));
    }


    // 7. Get payment successfully
    @Test
    void getPaymentTest() {

        User user = new User();
        user.setUsername("harry");

        Passenger passenger = new Passenger();
        passenger.setUser(user);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setPassenger(passenger);

        Payment payment = new Payment();
        payment.setId(10L);
        payment.setBooking(booking);

        PaymentRespDto expected = new PaymentRespDto(
                10L,
                1000,
                PaymentMethod.UPI,
                PaymentStatus.SUCCESS,
                Instant.now(),
                1L
        );

        when(paymentRepository.findById(10L))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.getPayment(10L, "harry"))
                .thenReturn(expected);

        PaymentRespDto result = paymentService.getPayment(10L, "harry");

        assertNotNull(result);
        assertEquals(10L, result.id());
        assertEquals(1000, result.amount());
        assertEquals(PaymentMethod.UPI, result.paymentMethod());
        assertEquals(PaymentStatus.SUCCESS, result.paymentStatus());
        assertEquals(1L, result.bookingId());

        verify(paymentRepository, times(1)).findById(10L);
        verify(paymentRepository, times(1)).getPayment(10L, "harry");
    }


    // 8. Payment not found
    @Test
    void getPaymentNotFoundTest() {

        when(paymentRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentService.getPayment(10L, "harry"));

        verify(paymentRepository, never()).getPayment(anyLong(), anyString());
    }


    // 9. User cannot view another passenger's payment
    @Test
    void getPaymentOwnershipTest() {

        User user = new User();
        user.setUsername("harry");

        Passenger passenger = new Passenger();
        passenger.setUser(user);

        Booking booking = new Booking();
        booking.setPassenger(passenger);

        Payment payment = new Payment();
        payment.setBooking(booking);

        when(paymentRepository.findById(10L))
                .thenReturn(Optional.of(payment));

        assertThrows(OwnerShipException.class,
                () -> paymentService.getPayment(10L, "ron"));

        verify(paymentRepository, never())
                .getPayment(anyLong(), anyString());
    }
}
