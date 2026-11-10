package com.springboot.FastX_backend.controller;

import com.springboot.FastX_backend.dto.request.ReviewDto;
import com.springboot.FastX_backend.dto.response.ReviewRespDto;
import com.springboot.FastX_backend.dto.response.ReviewSummaryRespDto;
import com.springboot.FastX_backend.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")

public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/api/add/review")
    public void addReview(Principal principal, @Valid @RequestBody ReviewDto reviewDto) {
        reviewService.addReview(principal.getName(), reviewDto);
    }

    @GetMapping("/api/reviews/bus/{busId}")
    public List<ReviewRespDto> getReviewsByBus(@PathVariable long busId) {
        return reviewService.getReviewsByBus(busId);
    }

    @GetMapping("/api/reviews/summary/{busId}")
    public ReviewSummaryRespDto getReviewSummaryByBus(@PathVariable long busId) {
        return reviewService.getReviewSummaryByBus(busId);
    }

    @GetMapping("/api/operator/reviews")
    public List<ReviewRespDto> getReviewsByOperator(Principal principal) {
        return reviewService.getReviewsByOperator(principal.getName());
    }

    @GetMapping("/api/operator/reviews/summary")
    public ReviewSummaryRespDto getReviewSummaryByOperator(Principal principal) {
        return reviewService.getReviewSummaryByOperator(principal.getName());
    }
}
