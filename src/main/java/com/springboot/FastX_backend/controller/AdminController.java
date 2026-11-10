package com.springboot.FastX_backend.controller;

import com.springboot.FastX_backend.dto.request.AdminDto;
import com.springboot.FastX_backend.dto.response.*;
import com.springboot.FastX_backend.enums.BookingStatus;
import com.springboot.FastX_backend.enums.PaymentStatus;
import com.springboot.FastX_backend.service.AdminService;
import com.springboot.FastX_backend.service.BusOperatorService;
import com.springboot.FastX_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class AdminController {
    private final UserService userService;
    private final AdminService adminService;
    private final BusOperatorService busOperatorService;

    @PostMapping("/api/admin/add")
    public void addAdmin(@RequestBody AdminDto adminDto){
        userService.addAdmin(adminDto);
    }

    @GetMapping("/api/admin/dashboard")
    public AdminDashboardRespDto getDashboardDetails() {
        return adminService.getDashboardDetails();
    }

    @GetMapping("/api/admin/operators")
    public List<AdminBusOperatorRespDto> getAllBusOperators(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return adminService.getAllBusOperators(page, size);
    }

    @PutMapping("/api/admin/operators/{operatorId}/status")
    public void updateOperatorStatus(
            @PathVariable long operatorId,
            @RequestParam boolean active) {

        adminService.updateOperatorStatus(operatorId, active);
    }

    @GetMapping("/api/admin/bookings")
    public List<AdminBookingRespDto> getAllBookingsForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String operatorName,
            @RequestParam(required = false) BookingStatus bookingStatus,
            @RequestParam(required = false) PaymentStatus paymentStatus,
            @RequestParam(required = false) LocalDate journeyDate) {

        return adminService.getAllBookingsForAdmin(
                page, size, search, operatorName,
                bookingStatus, paymentStatus, journeyDate
        );
    }

    @GetMapping("/api/admin/monthly-bookings")
    public List<MonthlyBookingStatsDto> getMonthlyBookingStats(
            @RequestParam(defaultValue = "2026") int year) {
        return adminService.getMonthlyBookingStats(year);
    }

    @GetMapping("/api/admin/booking-status")
    public List<BookingStatusStatsDto> getBookingStatusStats() {
        return adminService.getBookingStatusStats();
    }
}
