package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.enums.PaymentStatus;
import com.springboot.FastX_backend.enums.RefundStatus;
import com.springboot.FastX_backend.exception.OwnerShipException;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.model.*;
import com.springboot.FastX_backend.repository.PaymentRepository;
import com.springboot.FastX_backend.repository.RefundRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefundServiceTest {
    @Mock
    private RefundRepository refundRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private RefundService refundService;


    private Refund getRefund() {

        User passengerUser = new User();
        passengerUser.setUsername("harry");

        Passenger passenger = new Passenger();
        passenger.setUser(passengerUser);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setPassenger(passenger);

        User operatorUser = new User();
        operatorUser.setUsername("albus");

        BusOperator operator = new BusOperator();
        operator.setUser(operatorUser);

        Bus bus = new Bus();
        bus.setBusOperator(operator);

        Schedule schedule = new Schedule();
        schedule.setBus(bus);

        booking.setSchedule(schedule);

        Cancellation cancellation = new Cancellation();
        cancellation.setId(5L);
        cancellation.setBooking(booking);

        Refund refund = new Refund();
        refund.setId(10L);
        refund.setAmount(900);
        refund.setRefundStatus(RefundStatus.PENDING);
        refund.setCancellation(cancellation);

        return refund;
    }


    // 1. Get refund successfully
    @Test
    void getRefundTest() {

        Refund refund = getRefund();

        when(refundRepository.findById(10L))
                .thenReturn(Optional.of(refund));

        Refund result = refundService.getRefund(10L, "harry");

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(900, result.getAmount());
        assertEquals(RefundStatus.PENDING, result.getRefundStatus());

        verify(refundRepository, times(1)).findById(10L);
    }


    // 2. Refund not found
    @Test
    void getRefundNotFoundTest() {

        when(refundRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> refundService.getRefund(10L, "harry"));

        verify(refundRepository, times(1)).findById(10L);
    }


    // 3. Passenger cannot view another passenger's refund
    @Test
    void getRefundOwnershipTest() {

        Refund refund = getRefund();

        when(refundRepository.findById(10L))
                .thenReturn(Optional.of(refund));

        assertThrows(OwnerShipException.class,
                () -> refundService.getRefund(10L, "ron"));

        verify(refundRepository, times(1)).findById(10L);
    }


    // 4. Process refund successfully
    @Test
    void processRefundTest() {

        Refund refund = getRefund();

        Payment payment = new Payment();
        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        when(refundRepository.findById(10L))
                .thenReturn(Optional.of(refund));

        when(paymentRepository.findByBooking_Id(1L))
                .thenReturn(Optional.of(payment));

        refundService.processRefund(10L, "albus");

        assertEquals(RefundStatus.PROCESSED, refund.getRefundStatus());
        assertNotNull(refund.getProcessedAt());
        assertEquals(PaymentStatus.REFUNDED, payment.getPaymentStatus());

        verify(refundRepository, times(1)).save(refund);
        verify(paymentRepository, times(1)).save(payment);
    }


    // 5. Refund not found while processing
    @Test
    void processRefundNotFoundTest() {

        when(refundRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> refundService.processRefund(10L, "albus"));

        verifyNoInteractions(paymentRepository);
        verify(refundRepository, never()).save(any(Refund.class));
    }


    // 6. Only the bus operator can process the refund
    @Test
    void processRefundOwnershipTest() {

        Refund refund = getRefund();

        when(refundRepository.findById(10L))
                .thenReturn(Optional.of(refund));

        assertThrows(OwnerShipException.class,
                () -> refundService.processRefund(10L, "ron"));

        verifyNoInteractions(paymentRepository);
        verify(refundRepository, never()).save(any(Refund.class));
    }


    // 7. Already processed refund cannot be processed again
    @Test
    void processRefundAlreadyProcessedTest() {

        Refund refund = getRefund();
        refund.setRefundStatus(RefundStatus.PROCESSED);

        when(refundRepository.findById(10L))
                .thenReturn(Optional.of(refund));

        assertThrows(IllegalArgumentException.class,
                () -> refundService.processRefund(10L, "albus"));

        verifyNoInteractions(paymentRepository);
        verify(refundRepository, never()).save(any(Refund.class));
    }


    // 8. Payment not found while processing refund
    @Test
    void processRefundPaymentNotFoundTest() {

        Refund refund = getRefund();

        when(refundRepository.findById(10L))
                .thenReturn(Optional.of(refund));

        when(paymentRepository.findByBooking_Id(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> refundService.processRefund(10L, "albus"));

        assertEquals(RefundStatus.PENDING, refund.getRefundStatus());

        verify(refundRepository, never()).save(any(Refund.class));
        verify(paymentRepository, never()).save(any(Payment.class));
    }
}
