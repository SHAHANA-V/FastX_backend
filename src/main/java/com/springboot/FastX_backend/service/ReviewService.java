package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.dto.request.ReviewDto;
import com.springboot.FastX_backend.dto.response.ReviewRespDto;
import com.springboot.FastX_backend.dto.response.ReviewSummaryRespDto;
import com.springboot.FastX_backend.enums.BookingStatus;
import com.springboot.FastX_backend.exception.OwnerShipException;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.mapper.ReviewMapper;
import com.springboot.FastX_backend.model.Booking;
import com.springboot.FastX_backend.model.Review;
import com.springboot.FastX_backend.repository.BookingRepository;
import com.springboot.FastX_backend.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;

    public void addReview(String username, ReviewDto reviewDto) {

        Booking booking = bookingRepository.findById(reviewDto.bookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getPassenger().getUser().getUsername().equals(username)) {
            throw new OwnerShipException("You cannot review someone else's booking");
        }

        if (booking.getBookingStatus() != BookingStatus.JOURNEY_COMPLETED) {
            throw new IllegalArgumentException("You can review only completed journeys");
        }

        if (reviewRepository.existsByBooking_Id(booking.getId())) {
            throw new IllegalArgumentException("You have already reviewed this booking");
        }

        Review review = ReviewMapper.convertDtoToModel(reviewDto);
        review.setBooking(booking);

        reviewRepository.save(review);
    }

    public List<ReviewRespDto> getReviewsByBus(long busId) {

        List<Review> reviewList = reviewRepository.findByBooking_Schedule_Bus_IdOrderByCreatedAtDesc(busId);

        return reviewList.stream()
                .map(ReviewMapper::convertModelToDto)
                .toList();
    }

    public ReviewSummaryRespDto getReviewSummaryByBus(long busId) {
        return reviewRepository.getReviewSummaryByBus(busId);
    }

    public List<ReviewRespDto> getReviewsByOperator(String username) {
        List<Review> reviewList =
                reviewRepository.findByBooking_Schedule_Bus_BusOperator_User_UsernameOrderByCreatedAtDesc(username);

        return reviewList.stream()
                .map(ReviewMapper::convertModelToDto)
                .toList();
    }

    public ReviewSummaryRespDto getReviewSummaryByOperator(String username) {
        return reviewRepository.getReviewSummaryByOperator(username);
    }
}
