package com.springboot.FastX_backend.mapper;

import com.springboot.FastX_backend.dto.request.ReviewDto;
import com.springboot.FastX_backend.dto.response.ReviewRespDto;
import com.springboot.FastX_backend.model.Review;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ReviewMapper {
    public static Review convertDtoToModel(ReviewDto reviewDto){
        Review review = new Review();
        review.setRating(reviewDto.rating());
        review.setComment(reviewDto.comment());
        review.setCreatedAt(Instant.now());
        return review;

    }

    public static ReviewRespDto convertModelToDto(Review review){
        return new ReviewRespDto(
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                review.getBooking().getPassenger().getName(),
                review.getBooking().getSchedule().getBus().getBusName(),
                review.getBooking().getSchedule().getBus().getBusNumber(),
                review.getBooking().getSchedule().getRoute().getSource(),
                review.getBooking().getSchedule().getRoute().getDestination(),
                review.getBooking().getSchedule().getJourneyDate()
        );
    }
}
