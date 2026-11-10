package com.springboot.FastX_backend.repository;

import com.springboot.FastX_backend.dto.request.AdminBookingDataDto;
import com.springboot.FastX_backend.dto.response.AdminBookingRespDto;
import com.springboot.FastX_backend.dto.response.MyBookingRespDto;
import com.springboot.FastX_backend.dto.response.OperatorBookingRespDto;
import com.springboot.FastX_backend.enums.BookingStatus;
import com.springboot.FastX_backend.enums.PaymentStatus;
import com.springboot.FastX_backend.model.Booking;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {
    @Query("""
select new com.springboot.FastX_backend.dto.response.MyBookingRespDto(
b.id,b.bookedAt,b.bookingStatus,b.totalAmount,s.scheduleStatus,bu.busName,bu.busNumber,s.journeyDate,s.departureTime,b.boardingStop.stopName,b.droppingStop.stopName,null
)
from Booking b
join b.schedule s
join s.bus bu
join b.passenger p
join p.user u
where u.username=?1
order by b.bookedAt desc
""")
    List<MyBookingRespDto> getBookingForPassenger(String passengerName, Pageable pageable);

    @Query("""
    SELECT b
    FROM Booking b
    JOIN b.schedule s
    JOIN s.bus bu
    WHERE b.id = ?1
    """)
    Optional<Booking> getBookingDetails(long bookingId);

    @Modifying
    @Query("""
    UPDATE Booking b
    SET b.bookingStatus = ?1
    WHERE b.bookingStatus = ?2
    AND b.paymentDeadline <= ?3
    """)
    int expirePendingBookings(BookingStatus expiredStatus,BookingStatus pendingStatus,Instant now);

    @Query("""
    SELECT b
    FROM Booking b
    JOIN b.passenger p
    JOIN p.user u
    JOIN b.schedule s
    JOIN s.bus bu
    JOIN bu.busOperator buo
    WHERE b.id = ?1
    AND u.username = ?2
""")
    Optional<Booking> getBookingDetailsV2(long bookingId, String username);

    @Query("""
SELECT b.bookingStatus, COUNT(b)
FROM Booking b
JOIN b.schedule s
JOIN s.bus bu
JOIN bu.busOperator bo
JOIN bo.user u
WHERE u.username = ?1
GROUP BY b.bookingStatus
""")
    List<Object[]> getBookingStatusStats(String name);

    @Query("""
SELECT YEAR(b.bookedAt), MONTH(b.bookedAt), COUNT(b)
FROM Booking b
JOIN b.schedule s
JOIN s.bus bu
JOIN bu.busOperator bo
JOIN bo.user u
WHERE u.username = ?1
AND b.bookedAt >= ?2
GROUP BY YEAR(b.bookedAt), MONTH(b.bookedAt)
ORDER BY YEAR(b.bookedAt), MONTH(b.bookedAt)
""")
    List<Object[]> getMonthlyBookingStats(String name, Instant startInstant);

    @Query("""
SELECT COUNT(b)
FROM Booking b
JOIN b.schedule s
JOIN s.bus bu
JOIN bu.busOperator bo
JOIN bo.user u
WHERE u.username = ?1
""")
    long getOperatorBookingCount(String name);
    List<Booking> findBySchedule_Id(long scheduleId);
    @Query("""
    SELECT new com.springboot.FastX_backend.dto.response.OperatorBookingRespDto(
        b.id,
        b.bookedAt,
        b.bookingStatus,
        b.totalAmount,
        s.scheduleStatus,
        bu.busName,
        bu.busNumber,
        s.journeyDate,
        s.departureTime,
        b.boardingStop.stopName,
        b.droppingStop.stopName,
        p.name,
        p.phoneNo,
        p.email
    )
    FROM Booking b
    JOIN b.schedule s
    JOIN s.bus bu
    JOIN bu.busOperator bo
    JOIN bo.user operatorUser
    JOIN b.passenger p
    WHERE operatorUser.username = ?1
    AND (?2 IS NULL OR b.bookingStatus = ?2)
    AND (?3 IS NULL OR bu.id = ?3)
    ORDER BY b.bookedAt DESC
    """)
    List<OperatorBookingRespDto> getBookingsForOperator(String name, BookingStatus status, Long busId, Pageable pageable);

    @Query("""
SELECT new com.springboot.FastX_backend.dto.request.AdminBookingDataDto(
    b.id,
    p.name,
    p.phoneNo,
    bu.busName,
    bu.busNumber,
    bo.name,
    r.source,
    r.destination,
    s.journeyDate,
    b.totalAmount,
    b.bookingStatus
)
FROM Booking b
JOIN b.passenger p
JOIN b.schedule s
JOIN s.bus bu
JOIN bu.busOperator bo
JOIN s.route r
WHERE (?1 IS NULL OR ?1 = '' OR
       LOWER(p.name) LIKE LOWER(CONCAT('%', ?1, '%')))
AND (?2 IS NULL OR ?2 = '' OR
     LOWER(bo.name) LIKE LOWER(CONCAT('%', ?2, '%')))
AND (?3 IS NULL OR b.bookingStatus = ?3)
AND (?4 IS NULL OR EXISTS (
    SELECT pay.id FROM Payment pay
    WHERE pay.booking.id = b.id
    AND pay.paymentStatus = ?4
))
AND (?5 IS NULL OR s.journeyDate = ?5)
ORDER BY b.bookedAt DESC
""")
    List<AdminBookingDataDto> getAllBookingsForAdmin(
            String search,
            String operatorName,
            BookingStatus bookingStatus,
            PaymentStatus paymentStatus,
            LocalDate journeyDate,
            Pageable pageable
    );

    @Query("""
SELECT MONTH(b.bookedAt), COUNT(b)
FROM Booking b
WHERE YEAR(b.bookedAt) = ?1
GROUP BY MONTH(b.bookedAt)
ORDER BY MONTH(b.bookedAt)
""")
    List<Object[]> getMonthlyBookingStatsForAdmin(int year);

    @Query("""
SELECT b.bookingStatus, COUNT(b)
FROM Booking b
GROUP BY b.bookingStatus
""")
    List<Object[]> getBookingStatusStatsForAdmin();
}
