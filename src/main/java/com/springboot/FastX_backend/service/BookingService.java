package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.dto.request.BookingDto;
import com.springboot.FastX_backend.dto.request.BookingSeatDto;
import com.springboot.FastX_backend.dto.response.*;
import com.springboot.FastX_backend.enums.BookingStatus;
import com.springboot.FastX_backend.enums.BusStatus;
import com.springboot.FastX_backend.enums.ScheduleStatus;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.mapper.BookingMapper;
import com.springboot.FastX_backend.mapper.BookingSeatMapper;
import com.springboot.FastX_backend.model.*;
import com.springboot.FastX_backend.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final PassengerRepository passengerRepository;
    private final ScheduleRepository scheduleRepository;
    private final RouteStopRepository routeStopRepository;
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public BookingRespDto addBooking(String loggedInUser, @Valid BookingDto bookingDto) {
        Passenger passenger = passengerRepository.getPassengerByUsername(loggedInUser);
        log.info("Booking creation initiated by passenger: {}", loggedInUser);

        // Get the schedule
        Schedule schedule = scheduleRepository
                .findById(bookingDto.scheduleId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Schedule Id is invalid"));
        log.info("Creating booking for schedule ID: {}", bookingDto.scheduleId());

        if (schedule.getScheduleStatus() != ScheduleStatus.SCHEDULED) {
            log.warn("Schedule ID: {} is not available", bookingDto.scheduleId());
            throw new IllegalArgumentException("Schedule is not available");
        }

        if (schedule.getBus().getBusStatus() != BusStatus.ACTIVE) {
            log.warn("Bus is inactive for schedule ID: {}", schedule.getId());
            throw new IllegalArgumentException("Bus is not active");
        }

        // Get boarding stop
        RouteStop boardingStop = routeStopRepository
                .findById(bookingDto.boardingStopId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Boarding stop not found"));

        // Get dropping stop
        RouteStop droppingStop = routeStopRepository
                .findById(bookingDto.droppingStopId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Dropping stop not found"));

        // Check that both stops belong to this schedule's route
        if (boardingStop.getRoute().getId() != schedule.getRoute().getId()
                || droppingStop.getRoute().getId() != schedule.getRoute().getId()) {
            throw new IllegalArgumentException(
                    "Boarding and dropping stops must belong to the schedule route");
        }

        // Check stop order
        if (boardingStop.getStopOrder() >= droppingStop.getStopOrder()) {
            log.warn("Invalid stop order. Boarding stop must come before dropping stop");
            throw new IllegalArgumentException(
                    "Boarding stop must come before dropping stop");
        }

        // Calculate the total fare
        double totalAmount = schedule.getFare() * bookingDto.bookingSeats().size();
        log.info("Calculated booking amount: {}", totalAmount);

        // Convert DTO to Booking entity using mapper
        Booking booking = new Booking();

        booking.setPassenger(passenger);
        booking.setSchedule(schedule);
        booking.setBoardingStop(boardingStop);
        booking.setDroppingStop(droppingStop);
        booking.setTotalAmount(totalAmount);
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setPaymentDeadline(Instant.now().plus(15, ChronoUnit.MINUTES));

        // Save Booking separately
        bookingRepository.save(booking);

        // Save each BookingSeat separately
        for (BookingSeatDto seatDto : bookingDto.bookingSeats()) {

            Seat seat = seatRepository.findById(seatDto.seatId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Seat Id is invalid"));

            // Ensure the seat belongs to the scheduled bus
            if (seat.getBus().getId() != schedule.getBus().getId()) {
                throw new IllegalArgumentException(
                        "Selected seat does not belong to this bus");
            }

            // Check seat availability
            long conflicts = bookingSeatRepository.countSeatConflicts(
                    seat.getId(),
                    schedule.getId(),
                    BookingStatus.CONFIRMED,
                    BookingStatus.PENDING,
                    Instant.now(),
                    droppingStop.getStopOrder(),
                    boardingStop.getStopOrder()
            );

            if (conflicts > 0) {
                throw new IllegalArgumentException(
                        "Seat " + seat.getSeatNumber() + " is already booked");
            }

            // Convert DTO to BookingSeat entity using mapper
            BookingSeat bookingSeat =
                    BookingSeatMapper.convertDtoToModel(seatDto);

            bookingSeat.setBooking(booking);
            bookingSeat.setSeat(seat);

            // Save BookingSeat separately
            bookingSeatRepository.save(bookingSeat);


        }
        return BookingMapper.convertModelToDto(booking);
    }

    public List<MyBookingRespDto> getBookingForPassenger(
            String passengerName, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return bookingRepository.getBookingForPassenger(passengerName, pageable)
                .stream()
                .map(booking -> BookingMapper.convertModelToMyBookingDto(
                        booking,
                        bookingSeatRepository.getBookingSeats(booking.bookingId())
                ))
                .toList();
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expireBookings() {
        bookingRepository.expirePendingBookings(
                BookingStatus.EXPIRED,
                BookingStatus.PENDING,
                Instant.now()
        );
    }


    public TicketDetailsRespDto getTicketDetails(long bookingId, String name) {
        Booking booking = bookingRepository.getBookingDetailsV2(bookingId, name)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Booking not found"));

        PaymentRespDto payment =
                paymentRepository.getPaymentByBookingId(bookingId);

        List<BookingSeatRespDto> passengers =
                bookingSeatRepository.getBookingSeats(bookingId);

        return BookingMapper.convertModelToTicketDto(
                booking, payment, passengers);
    }

    public ChartDto getBookingStatusStats(String name) {
        List<Object[]> result = bookingRepository.getBookingStatusStats(name);

        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        for (Object[] row : result) {
            labels.add(row[0].toString());
            values.add((Long) row[1]);
        }

        return new ChartDto(labels, values);
    }

    public ChartDto getMonthlyBookingStats(String name) {
        LocalDate currentDate = LocalDate.now();

        LocalDate startDate = currentDate.minusMonths(5)
                .withDayOfMonth(1);

        Instant startInstant = startDate.atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        List<Object[]> result = bookingRepository.getMonthlyBookingStats(name, startInstant);

        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        for (int i = 0; i < 6; i++) {

            LocalDate month = startDate.plusMonths(i);

            labels.add(month.getMonth().toString().substring(0, 1)
                    + month.getMonth().toString().substring(1).toLowerCase());

            long count = 0;

            for (Object[] row : result) {
                int year = ((Number) row[0]).intValue();
                int monthValue = ((Number) row[1]).intValue();

                if (year == month.getYear() && monthValue == month.getMonthValue()) {
                    count = ((Number) row[2]).longValue();
                    break;
                }
            }

            values.add(count);
        }

        return new ChartDto(labels, values);
    }

    public long getOperatorBookingCount(String name) {
        return bookingRepository.getOperatorBookingCount(name);
    }

    public List<OperatorBookingRespDto> getBookingsForOperator(String name, BookingStatus status, Long busId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return bookingRepository.getBookingsForOperator(
                name,
                status,
                busId,
                pageable
        );
    }
}
