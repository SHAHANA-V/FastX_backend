package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.dto.request.PaymentDto;
import com.springboot.FastX_backend.dto.response.PaymentRespDto;
import com.springboot.FastX_backend.enums.BookingStatus;
import com.springboot.FastX_backend.enums.PaymentStatus;
import com.springboot.FastX_backend.exception.OwnerShipException;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.mapper.PaymentMapper;
import com.springboot.FastX_backend.model.Booking;
import com.springboot.FastX_backend.model.Payment;
import com.springboot.FastX_backend.repository.BookingRepository;
import com.springboot.FastX_backend.repository.PaymentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    public void initiatePayment(long id, @Valid PaymentDto paymentDto, String name) {
        Booking booking = bookingRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Booking Not found"));

        if(!booking.getPassenger().getUser().getUsername().equals(name)){
            throw new OwnerShipException("You are not authorized to pay for this booking");
        }

        if(booking.getBookingStatus() != BookingStatus.PENDING){
            throw new IllegalArgumentException("Booking is not pending");
        }

        if(booking.getPaymentDeadline().isBefore(Instant.now())){
            throw new IllegalArgumentException("Payment deadline has expired");
        }

        if (paymentRepository.existsByBookingId(id)) {
            throw new IllegalArgumentException("Payment already initiated for this booking");
        }
        Payment payment = PaymentMapper.dtoToModel(paymentDto);
        payment.setAmount(booking.getTotalAmount());
        payment.setBooking(booking);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(Instant.now());

        paymentRepository.save(payment);

        booking.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
    }

    public PaymentRespDto getPayment(long id, String name) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (!payment.getBooking().getPassenger().getUser().getUsername().equals(name)) {
            throw new OwnerShipException("You are not authorized to view this payment");
        }

        return paymentRepository.getPayment(id,name);
    }
}
