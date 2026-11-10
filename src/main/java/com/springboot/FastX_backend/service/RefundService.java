
package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.enums.PaymentStatus;
import com.springboot.FastX_backend.enums.RefundStatus;
import com.springboot.FastX_backend.exception.OwnerShipException;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.model.Booking;
import com.springboot.FastX_backend.model.Payment;
import com.springboot.FastX_backend.model.Refund;
import com.springboot.FastX_backend.repository.PaymentRepository;
import com.springboot.FastX_backend.repository.RefundRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefundService {

    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;

    public Refund getRefund(long refundId, String name) {

        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new ResourceNotFoundException("Refund not found"));

        if (!refund.getCancellation().getBooking().getPassenger()
                .getUser().getUsername().equals(name)) {
            throw new OwnerShipException("You are not authorized to view this refund");
        }

        return refund;
    }


    @Transactional
    public void processRefund(long refundId, String name) {

        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new ResourceNotFoundException("Refund not found"));

        Booking booking = refund.getCancellation().getBooking();

        if (!booking.getSchedule().getBus().getBusOperator()
                .getUser().getUsername().equals(name)) {
            throw new OwnerShipException("You are not authorized to process this refund");
        }

        if (refund.getRefundStatus() != RefundStatus.PENDING) {
            throw new IllegalArgumentException("Refund already processed");
        }

        Payment payment = paymentRepository.findByBooking_Id(booking.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        refund.setRefundStatus(RefundStatus.PROCESSED);
        refund.setProcessedAt(Instant.now());

        payment.setPaymentStatus(PaymentStatus.REFUNDED);

        refundRepository.save(refund);
        paymentRepository.save(payment);
    }

}
