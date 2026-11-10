package com.springboot.FastX_backend.controller;

import com.springboot.FastX_backend.dto.request.BookingDto;
import com.springboot.FastX_backend.dto.response.*;
import com.springboot.FastX_backend.enums.BookingStatus;
import com.springboot.FastX_backend.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping("/api/add/booking")
    public BookingRespDto addBooking(Principal principal, @Valid @RequestBody BookingDto bookingDto){
        return bookingService.addBooking(principal.getName(),bookingDto);
    }

    // Get all bookings of the logged-in passenger
    @GetMapping("/api/get/booking")
    public List<MyBookingRespDto> getBookingForPassenger(
            Principal principal,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size) {

        return bookingService.getBookingForPassenger(principal.getName(), page, size);
    }

    @GetMapping("/api/get/ticket/{bookingId}")
    public TicketDetailsRespDto getTicketDetails(
            @PathVariable long bookingId,
            Principal principal) {

        return bookingService.getTicketDetails(bookingId, principal.getName());
    }

    @GetMapping("/api/operator/booking/status")
    public ChartDto getBookingStatusStats(Principal principal) {
        return bookingService.getBookingStatusStats(principal.getName());
    }

    @GetMapping("/api/operator/booking/monthly")
    public ChartDto getMonthlyBookingStats(Principal principal) {
        return bookingService.getMonthlyBookingStats(principal.getName());
    }

    @GetMapping("/api/operator/booking/count")
    public long getOperatorBookingCount(Principal principal) {
        return bookingService.getOperatorBookingCount(principal.getName());
    }

    @GetMapping("/api/operator/bookings")
    public List<OperatorBookingRespDto> getBookingsForOperator(
            Principal principal,
            @RequestParam(name = "status", required = false) BookingStatus status,
            @RequestParam(name = "busId", required = false) Long busId,
            @RequestParam(name = "page",required = false, defaultValue = "0") int page,
            @RequestParam(name = "size",required = false,defaultValue = "10") int size) {

        return bookingService.getBookingsForOperator(
                principal.getName(),status,busId,page,size
        );
    }

}
