
package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.dto.request.CancellationDto;
import com.springboot.FastX_backend.dto.response.CancellationRespDto;
import com.springboot.FastX_backend.enums.BookingStatus;
import com.springboot.FastX_backend.enums.CancellationStatus;
import com.springboot.FastX_backend.enums.RefundStatus;
import com.springboot.FastX_backend.exception.OwnerShipException;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.mapper.CancellationMapper;
import com.springboot.FastX_backend.model.Booking;
import com.springboot.FastX_backend.model.Cancellation;
import com.springboot.FastX_backend.model.Refund;
import com.springboot.FastX_backend.repository.BookingRepository;
import com.springboot.FastX_backend.repository.CancellationRepository;
import com.springboot.FastX_backend.repository.RefundRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CancellationService {

    private final CancellationRepository cancellationRepository;
    private final BookingRepository bookingRepository;
    private final RefundRepository refundRepository;

    @Transactional
    public void requestCancellation(long id, String name, CancellationDto cancellationDto) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getPassenger().getUser().getUsername().equals(name)) {
            throw new OwnerShipException("You are not authorized to cancel this booking");
        }

        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalArgumentException("Only confirmed bookings can be cancelled");
        }

        if (cancellationRepository.existsByBookingId(id)) {
            throw new IllegalArgumentException("Cancellation request already exists");
        }

        Cancellation cancellation = new Cancellation();

        cancellation.setBooking(booking);
        cancellation.setCancellationStatus(CancellationStatus.REQUESTED);
        cancellation.setReason(cancellationDto.reason());
        cancellation.setRefundAmount(calculateRefund(booking));
        cancellation.setRequestedAt(Instant.now());

        cancellationRepository.save(cancellation);
    }

    private double calculateRefund(Booking booking) {

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));

        LocalDateTime departure = LocalDateTime.of(
                booking.getSchedule().getJourneyDate(),
                booking.getSchedule().getDepartureTime()
        );

        long minutes = ChronoUnit.MINUTES.between(now, departure);

        if (minutes > 24 * 60) {
            return booking.getTotalAmount() * 0.90;
        } else if (minutes >= 12 * 60) {
            return booking.getTotalAmount() * 0.50;
        }

        return 0;
    }

    @Transactional
    public void updateCancellation(long cancellationId, CancellationStatus status, String name) {

        Cancellation cancellation = cancellationRepository.findById(cancellationId)
                .orElseThrow(() -> new ResourceNotFoundException("Cancellation not found"));

        Booking booking = cancellation.getBooking();

        if (!booking.getSchedule().getBus().getBusOperator()
                .getUser().getUsername().equals(name)) {
            throw new OwnerShipException("You are not authorized to update this cancellation");
        }

        if (cancellation.getCancellationStatus() != CancellationStatus.REQUESTED) {
            throw new IllegalArgumentException("Cancellation already processed");
        }

        if (status != CancellationStatus.APPROVED &&
                status != CancellationStatus.REJECTED) {
            throw new IllegalArgumentException("Invalid cancellation status");
        }

        cancellation.setCancellationStatus(status);

        if (status == CancellationStatus.APPROVED) {

            cancellation.setCancelledAt(Instant.now());

            booking.setBookingStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            if (cancellation.getRefundAmount() > 0) {

                Refund refund = new Refund();

                refund.setAmount(cancellation.getRefundAmount());
                refund.setRefundStatus(RefundStatus.PENDING);
                refund.setCancellation(cancellation);

                refundRepository.save(refund);
            }
        }

        cancellationRepository.save(cancellation);
    }
    public List<CancellationRespDto> getOperatorCancellations(String name) {

        return cancellationRepository
                .findByBooking_Schedule_Bus_BusOperator_User_Username(name)
                .stream()
                .map(CancellationMapper::modelToDto)
                .toList();
    }
}
