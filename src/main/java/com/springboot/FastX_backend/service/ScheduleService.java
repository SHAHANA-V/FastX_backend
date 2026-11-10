package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.dto.request.ScheduleDto;
import com.springboot.FastX_backend.dto.response.ScheduleRespDto;
import com.springboot.FastX_backend.dto.response.ScheduleRouteRespDto;
import com.springboot.FastX_backend.dto.response.ScheduleUpdateDto;
import com.springboot.FastX_backend.dto.response.SeatRespV2Dto;
import com.springboot.FastX_backend.enums.*;
import com.springboot.FastX_backend.exception.OwnerShipException;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.mapper.ScheduleMapper;
import com.springboot.FastX_backend.model.*;
import com.springboot.FastX_backend.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final SeatRepository seatRepository;
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final RefundRepository refundRepository;
    private final CancellationRepository cancellationRepository;
    private final NotificationRepository notificationRepository;
    
    public void addSchedule(String busOperatorName, @Valid ScheduleDto scheduleDto, long busId, long routeId) {

        // Fetch the bus
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Bus Id is invalid"));

        // Check bus status
        if (bus.getBusStatus() != BusStatus.ACTIVE) {
            log.warn("Cannot create schedule. Bus ID: {} is inactive", busId);
            throw new IllegalArgumentException(
                    "Cannot schedule an inactive bus");
        }

        // Check bus ownership
        if (!bus.getBusOperator().getUser().getUsername()
                .equals(busOperatorName)) {
            log.warn("Unauthorized schedule creation attempt by operator: {}", busOperatorName);
            throw new OwnerShipException(
                    "You are not able to access the bus");
        }

        // Fetch the route
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Route Id is invalid"));

        // Validate journey date
        if (scheduleDto.journeyDate().isBefore(LocalDate.now())) {
            log.warn("Invalid journey date: {}", scheduleDto.journeyDate());
            throw new IllegalArgumentException(
                    "Journey date cannot be in the past");
        }


        // Validate departure and arrival time
        if (scheduleDto.arrivalTime()
                .equals(scheduleDto.departureTime())) {
            throw new IllegalArgumentException(
                    "Departure and arrival times cannot be the same");
        }

        // Validate fare
        if (scheduleDto.fare() <= 0) {
            log.warn("Invalid fare: {}", scheduleDto.fare());
            throw new IllegalArgumentException(
                    "Fare must be greater than zero");
        }

        // Calculate new schedule departure and arrival
        LocalDateTime newDeparture = LocalDateTime.of(
                scheduleDto.journeyDate(),
                scheduleDto.departureTime()
        );

        LocalDate arrivalDate = scheduleDto.arrivalTime()
                .isAfter(scheduleDto.departureTime())
                ? scheduleDto.journeyDate()
                : scheduleDto.journeyDate().plusDays(1);

        LocalDateTime newArrival = LocalDateTime.of(
                arrivalDate,
                scheduleDto.arrivalTime()
        );

        // Fetch existing schedules for the bus
        List<Schedule> existingSchedules =
                scheduleRepository.findByBus_IdAndJourneyDateBetween(
                        busId,
                        scheduleDto.journeyDate().minusDays(1),
                        scheduleDto.journeyDate()
                );

        // Check overlapping schedules
        for (Schedule existing : existingSchedules) {

            LocalDateTime existingDeparture = LocalDateTime.of(
                    existing.getJourneyDate(),
                    existing.getDepartureTime()
            );

            LocalDate existingArrivalDate = existing.getArrivalTime()
                    .isAfter(existing.getDepartureTime())
                    ? existing.getJourneyDate()
                    : existing.getJourneyDate().plusDays(1);

            LocalDateTime existingArrival = LocalDateTime.of(
                    existingArrivalDate,
                    existing.getArrivalTime()
            );

            if (newDeparture.isBefore(existingArrival)
                    && newArrival.isAfter(existingDeparture)) {

                throw new IllegalArgumentException(
                        "This bus already has a schedule during this time");
            }
        }

        // Prepare the Schedule object
        Schedule schedule = new Schedule();

        schedule.setJourneyDate(scheduleDto.journeyDate());
        schedule.setDepartureTime(scheduleDto.departureTime());
        schedule.setArrivalTime(scheduleDto.arrivalTime());
        schedule.setFare(scheduleDto.fare());
        schedule.setScheduleStatus(ScheduleStatus.SCHEDULED);

        // Attach bus and route
        schedule.setBus(bus);
        schedule.setRoute(route);

        // Save in the database
        scheduleRepository.save(schedule);
        log.info("Schedule created successfully with ID: {}", schedule.getId());
    }

    public List<ScheduleRespDto> getAllSchedule(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        return scheduleRepository.getAllSchedule(pageable);
    }

    public List<ScheduleRespDto> getAllScheduleOperator(String operatorName, ScheduleStatus scheduleStatus, int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        return scheduleRepository.getAllScheduleOperator(operatorName,scheduleStatus,pageable);
    }

    public void updateSchedule(String busOperatorName, long id, ScheduleUpdateDto scheduleUpdateDto) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Schedule Id is invalid"));

        // Check ownership
        if (!schedule.getBus().getBusOperator().getUser().getUsername()
                .equals(busOperatorName)) {
            throw new OwnerShipException(
                    "You are not allowed to update the schedule");
        }

        // Prevent updating completed journeys
        if (schedule.getScheduleStatus() == ScheduleStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "Completed schedules cannot be updated");
        }

        boolean flag = false;

        // Validate and update journey date
        if (scheduleUpdateDto.journeyDate() != null) {

            if (scheduleUpdateDto.journeyDate().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException(
                        "Journey date cannot be in the past");
            }

            schedule.setJourneyDate(scheduleUpdateDto.journeyDate());
            flag = true;
        }

        // Update departure time
        if (scheduleUpdateDto.departureTime() != null) {
            schedule.setDepartureTime(scheduleUpdateDto.departureTime());
            flag = true;
        }

        // Update arrival time
        if (scheduleUpdateDto.arrivalTime() != null) {
            schedule.setArrivalTime(scheduleUpdateDto.arrivalTime());
            flag = true;
        }

        // Update fare
        if (scheduleUpdateDto.fare() != null) {

            if (scheduleUpdateDto.fare() <= 0) {
                throw new IllegalArgumentException(
                        "Fare must be greater than zero");
            }

            schedule.setFare(scheduleUpdateDto.fare());
            flag = true;
        }

        // Update status
        if (scheduleUpdateDto.scheduleStatus() != null) {
            schedule.setScheduleStatus(scheduleUpdateDto.scheduleStatus());
            flag = true;
        }

        if (!flag) {
            throw new IllegalArgumentException(
                    "No valid fields provided for update");
        }

        // Calculate updated departure and arrival
        LocalDateTime newDeparture = LocalDateTime.of(
                schedule.getJourneyDate(),
                schedule.getDepartureTime()
        );

        LocalDate arrivalDate = schedule.getArrivalTime()
                .isAfter(schedule.getDepartureTime())
                ? schedule.getJourneyDate()
                : schedule.getJourneyDate().plusDays(1);

        LocalDateTime newArrival = LocalDateTime.of(
                arrivalDate,
                schedule.getArrivalTime()
        );

        // Fetch existing schedules for the same bus
        List<Schedule> existingSchedules =
                scheduleRepository.findByBus_IdAndJourneyDateBetween(
                        schedule.getBus().getId(),
                        schedule.getJourneyDate().minusDays(1),
                        schedule.getJourneyDate()
                );

        // Check for overlapping schedules
        for (Schedule existing : existingSchedules) {

            // Ignore the schedule currently being updated
            if (existing.getId() == schedule.getId()) {
                continue;
            }

            LocalDateTime existingDeparture = LocalDateTime.of(
                    existing.getJourneyDate(),
                    existing.getDepartureTime()
            );

            LocalDate existingArrivalDate = existing.getArrivalTime()
                    .isAfter(existing.getDepartureTime())
                    ? existing.getJourneyDate()
                    : existing.getJourneyDate().plusDays(1);

            LocalDateTime existingArrival = LocalDateTime.of(
                    existingArrivalDate,
                    existing.getArrivalTime()
            );

            if (newDeparture.isBefore(existingArrival)
                    && newArrival.isAfter(existingDeparture)) {

                throw new IllegalArgumentException(
                        "This bus already has a schedule during this time");
            }
        }

        // Save updated schedule
        scheduleRepository.save(schedule);
    }

    public List<ScheduleRespDto> getScheduleFilter(String source, String destination, LocalDate journeyDate, LocalTime arrivalTime, LocalTime departureTime, String busName, Double fare) {
        if (source != null && source.isBlank()) {
            source = null;
        }

        if (destination != null && destination.isBlank()) {
            destination = null;
        }
        return scheduleRepository.getScheduleFilter(source, destination, journeyDate, arrivalTime, departureTime, busName, fare);
    }

    public List<SeatRespV2Dto> getSeatsByScheduleId(long scheduleId,int page,int size) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule Id is invalid"));

        Pageable pageable = PageRequest.of(page,size);
        long busId = schedule.getBus().getId();

        return seatRepository.getAllSeatsForBus(busId,pageable);
    }

    public ScheduleRespDto getScheduleRoute(long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule Id is invalid"));
        return ScheduleMapper.modelToDto(schedule);
    }

    public long getOperatorScheduleCount(String name) {
        return scheduleRepository.getOperatorScheduleCount(name);
    }

    @Transactional
    public void cancelSchedule(String busOperatorName, long scheduleId, String reason) {

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Schedule Id is invalid"));

        // Check ownership
        if (!schedule.getBus().getBusOperator().getUser().getUsername()
                .equals(busOperatorName)) {
            throw new OwnerShipException("You are not allowed to cancel this schedule");
        }

        if (schedule.getScheduleStatus() == ScheduleStatus.COMPLETED) {
            throw new IllegalArgumentException("Completed schedules cannot be cancelled");
        }

        if (schedule.getScheduleStatus() == ScheduleStatus.CANCELLED) {
            throw new IllegalArgumentException("Schedule is already cancelled");
        }

        if (reason == null || reason.isBlank() || reason.length() > 500) {
            throw new IllegalArgumentException("Valid cancellation reason is required");
        }

        // Cancel schedule
        schedule.setScheduleStatus(ScheduleStatus.CANCELLED);
        scheduleRepository.save(schedule);

        // Fetch all bookings
        List<Booking> bookings = bookingRepository.findBySchedule_Id(scheduleId);

        for (Booking booking : bookings) {

            if (booking.getBookingStatus() != BookingStatus.PENDING &&
                    booking.getBookingStatus() != BookingStatus.CONFIRMED) {
                continue;
            }

            // Cancel booking
            booking.setBookingStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            double refundAmount = 0;

            Cancellation cancellation = new Cancellation();
            cancellation.setReason(reason);
            cancellation.setCancelledAt(Instant.now());
            cancellation.setCancellationStatus(CancellationStatus.APPROVED);
            cancellation.setBooking(booking);

            Optional<Payment> paymentOptional =
                    paymentRepository.findByBooking_Id(booking.getId());

            if (paymentOptional.isPresent()) {

                Payment payment = paymentOptional.get();

                if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {

                    refundAmount = payment.getAmount();

                    // Update payment status
                    payment.setPaymentStatus(PaymentStatus.REFUNDED);
                    paymentRepository.save(payment);

                    // Create refund
                    Refund refund = new Refund();
                    refund.setAmount(refundAmount);
                    refund.setRefundStatus(RefundStatus.PROCESSED);
                    refund.setCancellation(cancellation);

                    cancellation.setRefundAmount(refundAmount);

                    cancellationRepository.save(cancellation);
                    refundRepository.save(refund);

                } else {
                    cancellation.setRefundAmount(0);
                    cancellationRepository.save(cancellation);
                }

            } else {
                cancellation.setRefundAmount(0);
                cancellationRepository.save(cancellation);
            }

            // Send in-app notification
            // Send in-app notification
            Notification notification = new Notification();

            notification.setTitle("Schedule Cancelled");

            String busName = schedule.getBus().getBusName();
            String busNumber = schedule.getBus().getBusNumber();

            String source = schedule.getRoute().getSource();
            String destination = schedule.getRoute().getDestination();

            String journeyDate = schedule.getJourneyDate().toString();
            String departureTime = schedule.getDepartureTime().toString();

            String message =
                    "We sincerely apologize for the inconvenience. " +
                            "Your booked bus schedule has been cancelled.\n\n" +
                            "Bus Name: " + busName + "\n" +
                            "Bus Number: " + busNumber + "\n" +
                            "Journey: " + source + " to " + destination + "\n" +
                            "Journey Date: " + journeyDate + "\n" +
                            "Departure Time: " + departureTime + "\n" +
                            "Cancellation Reason: " + reason + "\n";

            if (refundAmount > 0) {
                message += "Refund Amount: Rs. " + refundAmount +
                        " has been marked as refunded.";
            } else {
                message += "Please check your booking details for more information.";
            }

            notification.setMessage(message);
            notification.setUser(booking.getPassenger().getUser());

            notificationRepository.save(notification);
        }
    }
}
