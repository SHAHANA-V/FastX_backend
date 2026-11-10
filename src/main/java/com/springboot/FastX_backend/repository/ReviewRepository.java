package com.springboot.FastX_backend.repository;

import com.springboot.FastX_backend.dto.response.ReviewSummaryRespDto;
import com.springboot.FastX_backend.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    boolean existsByBooking_Id(long id);

    List<Review> findByBooking_Schedule_Bus_IdOrderByCreatedAtDesc(long busId);

    @Query("""
    select new com.springboot.FastX_backend.dto.response.ReviewSummaryRespDto(
        coalesce(avg(r.rating), 0.0),
        count(r.id)
    )
    from Review r
    where r.booking.schedule.bus.id = ?1
""")
    ReviewSummaryRespDto getReviewSummaryByBus(long busId);

    List<Review> findByBooking_Schedule_Bus_BusOperator_User_UsernameOrderByCreatedAtDesc(
            String username
    );

    @Query("""
    SELECT new com.springboot.FastX_backend.dto.response.ReviewSummaryRespDto(
        COALESCE(AVG(r.rating), 0.0),
        COUNT(r.id)
    )
    FROM Review r
    JOIN r.booking b
    JOIN b.schedule s
    JOIN s.bus bus
    JOIN bus.busOperator bo
    JOIN bo.user u
    WHERE u.username = ?1
    """)
    ReviewSummaryRespDto getReviewSummaryByOperator(String username);
}
