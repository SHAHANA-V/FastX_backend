package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.dto.request.AdminBookingDataDto;
import com.springboot.FastX_backend.dto.response.*;
import com.springboot.FastX_backend.enums.BookingStatus;
import com.springboot.FastX_backend.enums.PaymentStatus;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.mapper.AdminMapper;
import com.springboot.FastX_backend.model.BusOperator;
import com.springboot.FastX_backend.model.Payment;
import com.springboot.FastX_backend.model.User;
import com.springboot.FastX_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final BusOperatorRepository busOperatorRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final PaymentRepository paymentRepository;

    public AdminDashboardRespDto getDashboardDetails() {

        return new AdminDashboardRespDto(
                busOperatorRepository.count(),
                busRepository.count(),
                routeRepository.count(),
                bookingRepository.count()
        );
    }

    public List<AdminBusOperatorRespDto> getAllBusOperators(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return busOperatorRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(AdminMapper::modelToDto)
                .toList();
    }
    public void updateOperatorStatus(long operatorId, boolean active) {

        BusOperator busOperator = busOperatorRepository.findById(operatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus Operator not found"));

        User user = busOperator.getUser();
        user.setActive(active);

        userRepository.save(user);
    }

    public List<AdminBookingRespDto> getAllBookingsForAdmin(
            int page,
            int size,
            String search,
            String operatorName,
            BookingStatus bookingStatus,
            PaymentStatus paymentStatus,
            LocalDate journeyDate) {

        Pageable pageable = PageRequest.of(page, size);

        List<AdminBookingDataDto> bookings =
                bookingRepository.getAllBookingsForAdmin(
                        search, operatorName, bookingStatus,
                        paymentStatus, journeyDate, pageable
                );

        return bookings.stream()
                .map(booking -> new AdminBookingRespDto(
                        booking.bookingId(),
                        booking.passengerName(),
                        booking.passengerPhone(),
                        booking.busName(),
                        booking.busNumber(),
                        booking.operatorName(),
                        booking.source(),
                        booking.destination(),
                        booking.journeyDate(),
                        bookingSeatRepository.findByBooking_Id(booking.bookingId())
                                .stream()
                                .map(bs -> bs.getSeat().getSeatNumber())
                                .collect(Collectors.joining(", ")),
                        booking.totalAmount(),
                        booking.bookingStatus(),
                        paymentRepository.findByBooking_Id(booking.bookingId())
                                .map(Payment::getPaymentStatus)
                                .orElse(null)
                ))
                .toList();
    }

    public List<MonthlyBookingStatsDto> getMonthlyBookingStats(int year) {

        return bookingRepository.getMonthlyBookingStatsForAdmin(year)
                .stream()
                .map(row -> new MonthlyBookingStatsDto(
                        Month.of(((Number) row[0]).intValue())
                                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                        ((Number) row[1]).longValue()
                ))
                .toList();
    }
    public List<BookingStatusStatsDto> getBookingStatusStats() {

        return bookingRepository.getBookingStatusStatsForAdmin()
                .stream()
                .map(row -> new BookingStatusStatsDto(
                        (BookingStatus) row[0],
                        ((Number) row[1]).longValue()
                ))
                .toList();
    }
}
