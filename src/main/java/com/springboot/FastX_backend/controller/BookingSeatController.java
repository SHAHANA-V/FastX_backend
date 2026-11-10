package com.springboot.FastX_backend.controller;

import com.springboot.FastX_backend.dto.response.BookingDetailsRespDto;
import com.springboot.FastX_backend.service.BookingSeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class BookingSeatController {
    private final BookingSeatService bookingSeatService;
    @GetMapping("/api/get/booking/details/{bookingId}")
    public BookingDetailsRespDto getBookingDetails(
            @PathVariable long bookingId,
            Principal principal) {

        return bookingSeatService.getBookingDetails(bookingId, principal.getName());
    }

    @PostMapping("/api/upload/id-document")
    public String uploadIdDocument(@RequestParam("file") MultipartFile file) {

        return bookingSeatService.uploadIdDocument(file);
    }
}
