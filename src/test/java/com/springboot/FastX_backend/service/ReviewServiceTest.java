package com.springboot.FastX_backend.service;
import com.springboot.FastX_backend.dto.request.ReviewDto;
import com.springboot.FastX_backend.dto.response.ReviewRespDto;
import com.springboot.FastX_backend.dto.response.ReviewSummaryRespDto;
import com.springboot.FastX_backend.enums.BookingStatus;
import com.springboot.FastX_backend.exception.OwnerShipException;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.model.*;
import com.springboot.FastX_backend.repository.BookingRepository;
import com.springboot.FastX_backend.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {
    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ReviewService reviewService;


    private Booking getBooking() {

        User user = new User();
        user.setUsername("harry");

        Passenger passenger = new Passenger();
        passenger.setName("Harry Potter");
        passenger.setUser(user);

        BusOperator operator = new BusOperator();

        Bus bus = new Bus();
        bus.setBusName("KNP Travels");
        bus.setBusNumber("TN5679");
        bus.setBusOperator(operator);

        Route route = new Route();
        route.setSource("Chennai");
        route.setDestination("Madurai");

        Schedule schedule = new Schedule();
        schedule.setBus(bus);
        schedule.setRoute(route);
        schedule.setJourneyDate(LocalDate.now().minusDays(1));

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setPassenger(passenger);
        booking.setSchedule(schedule);
        booking.setBookingStatus(BookingStatus.JOURNEY_COMPLETED);

        return booking;
    }


    private Review getReview() {

        Review review = new Review();
        review.setId(1L);
        review.setRating(5);
        review.setComment("Excellent journey");
        review.setCreatedAt(Instant.now());
        review.setBooking(getBooking());

        return review;
    }


    // 1. Add review successfully
    @Test
    void addReviewTest() {

        Booking booking = getBooking();

        ReviewDto dto = new ReviewDto(
                5,
                "Excellent journey",
                1L
        );

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(reviewRepository.existsByBooking_Id(1L))
                .thenReturn(false);

        reviewService.addReview("harry", dto);

        verify(bookingRepository, times(1)).findById(1L);

        verify(reviewRepository, times(1))
                .existsByBooking_Id(1L);

        verify(reviewRepository, times(1))
                .save(argThat(review ->
                        review.getRating() == 5 &&
                                review.getComment().equals("Excellent journey") &&
                                review.getBooking() == booking &&
                                review.getCreatedAt() != null
                ));
    }


    // 2. Booking not found
    @Test
    void addReviewBookingNotFoundTest() {

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.empty());

        ReviewDto dto = new ReviewDto(
                5,
                "Excellent journey",
                1L
        );

        assertThrows(ResourceNotFoundException.class,
                () -> reviewService.addReview("harry", dto));

        verify(reviewRepository, never())
                .save(any(Review.class));
    }


    // 3. Passenger cannot review another passenger's booking
    @Test
    void addReviewOwnershipTest() {

        Booking booking = getBooking();

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        ReviewDto dto = new ReviewDto(
                5,
                "Excellent journey",
                1L
        );

        assertThrows(OwnerShipException.class,
                () -> reviewService.addReview("ron", dto));

        verify(reviewRepository, never())
                .existsByBooking_Id(anyLong());

        verify(reviewRepository, never())
                .save(any(Review.class));
    }


    // 4. Review allowed only for completed journeys
    @Test
    void addReviewJourneyNotCompletedTest() {

        Booking booking = getBooking();
        booking.setBookingStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        ReviewDto dto = new ReviewDto(
                5,
                "Excellent journey",
                1L
        );

        assertThrows(IllegalArgumentException.class,
                () -> reviewService.addReview("harry", dto));

        verify(reviewRepository, never())
                .existsByBooking_Id(anyLong());

        verify(reviewRepository, never())
                .save(any(Review.class));
    }


    // 5. Passenger cannot submit a duplicate review
    @Test
    void addReviewAlreadyExistsTest() {

        Booking booking = getBooking();

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(reviewRepository.existsByBooking_Id(1L))
                .thenReturn(true);

        ReviewDto dto = new ReviewDto(
                5,
                "Excellent journey",
                1L
        );

        assertThrows(IllegalArgumentException.class,
                () -> reviewService.addReview("harry", dto));

        verify(reviewRepository, never())
                .save(any(Review.class));
    }


    // 6. Get reviews by bus
    @Test
    void getReviewsByBusTest() {

        Review review = getReview();

        when(reviewRepository
                .findByBooking_Schedule_Bus_IdOrderByCreatedAtDesc(10L))
                .thenReturn(List.of(review));

        List<ReviewRespDto> result =
                reviewService.getReviewsByBus(10L);

        assertEquals(1, result.size());
        assertEquals(5, result.get(0).rating());
        assertEquals("Excellent journey", result.get(0).comment());
        assertEquals("Harry Potter", result.get(0).passengerName());
        assertEquals("KNP Travels", result.get(0).busName());
        assertEquals("TN5679", result.get(0).busNumber());
        assertEquals("Chennai", result.get(0).source());
        assertEquals("Madurai", result.get(0).destination());

        verify(reviewRepository, times(1))
                .findByBooking_Schedule_Bus_IdOrderByCreatedAtDesc(10L);
    }


    // 7. Get reviews by operator
    @Test
    void getReviewsByOperatorTest() {

        Review review = getReview();

        when(reviewRepository
                .findByBooking_Schedule_Bus_BusOperator_User_UsernameOrderByCreatedAtDesc(
                        "albus"
                ))
                .thenReturn(List.of(review));

        List<ReviewRespDto> result =
                reviewService.getReviewsByOperator("albus");

        assertEquals(1, result.size());
        assertEquals(5, result.get(0).rating());
        assertEquals("Harry Potter", result.get(0).passengerName());
        assertEquals("KNP Travels", result.get(0).busName());

        verify(reviewRepository, times(1))
                .findByBooking_Schedule_Bus_BusOperator_User_UsernameOrderByCreatedAtDesc(
                        "albus"
                );
    }


    // 8. Get review summary by bus
    @Test
    void getReviewSummaryByBusTest() {

        ReviewSummaryRespDto summary =
                new ReviewSummaryRespDto(4.5, 10);

        when(reviewRepository.getReviewSummaryByBus(10L))
                .thenReturn(summary);

        ReviewSummaryRespDto result =
                reviewService.getReviewSummaryByBus(10L);

        assertNotNull(result);
        assertEquals(4.5, result.averageRating());
        assertEquals(10, result.totalReviews());

        verify(reviewRepository, times(1))
                .getReviewSummaryByBus(10L);
    }


    // 9. Get review summary by operator
    @Test
    void getReviewSummaryByOperatorTest() {

        ReviewSummaryRespDto summary =
                new ReviewSummaryRespDto(4.2, 15);

        when(reviewRepository.getReviewSummaryByOperator("albus"))
                .thenReturn(summary);

        ReviewSummaryRespDto result =
                reviewService.getReviewSummaryByOperator("albus");

        assertNotNull(result);
        assertEquals(4.2, result.averageRating());
        assertEquals(15, result.totalReviews());

        verify(reviewRepository, times(1))
                .getReviewSummaryByOperator("albus");
    }
}
