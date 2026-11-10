package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.dto.response.BookingDetailsRespDto;
import com.springboot.FastX_backend.dto.response.BookingSeatRespDto;
import com.springboot.FastX_backend.enums.Role;
import com.springboot.FastX_backend.exception.OwnerShipException;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.mapper.BookingMapper;
import com.springboot.FastX_backend.model.Booking;
import com.springboot.FastX_backend.model.User;
import com.springboot.FastX_backend.repository.BookingRepository;
import com.springboot.FastX_backend.repository.BookingSeatRepository;
import com.springboot.FastX_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Book;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingSeatService {
    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final UserRepository userRepository;

    public BookingDetailsRespDto getBookingDetails(long bookingId, String name) {
        Booking booking = bookingRepository.getBookingDetails(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() == Role.PASSENGER &&
                !(booking.getPassenger().getUser().getUsername().equals(name))) {
            throw new OwnerShipException("You are not authorized to access this booking");
        }

        if (user.getRole() == Role.BUS_OPERATOR &&
                !(booking.getSchedule().getBus().getBusOperator().getUser().getUsername().equals(name))) {
            throw new OwnerShipException("You are not authorized to access this booking");
        }

        if (user.getRole() != Role.PASSENGER &&
                user.getRole() != Role.BUS_OPERATOR &&
                user.getRole() != Role.ADMIN) {
            throw new OwnerShipException("You are not authorized to access this booking");
        }

        List<BookingSeatRespDto> bookingSeats =
                bookingSeatRepository.getBookingSeats(bookingId);

        return BookingMapper.convertToDetailsDto(booking, bookingSeats);
    }

    public String uploadIdDocument(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please select an ID document");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("File size must not exceed 5 MB");
        }
        try {

            String contentType = file.getContentType();
            if (!List.of("application/pdf", "image/jpeg", "image/png").contains(contentType)) {
                throw new RuntimeException("Only PDF, JPG and PNG files are allowed");
            }

            String uploadDirectory = "uploads/id-documents/";
            Path directoryPath = Paths.get(uploadDirectory);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = directoryPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload ID document");
        }
    }
}
