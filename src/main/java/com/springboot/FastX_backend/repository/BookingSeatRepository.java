package com.springboot.FastX_backend.repository;

import com.springboot.FastX_backend.dto.response.BookingSeatRespDto;
import com.springboot.FastX_backend.enums.BookingStatus;
import com.springboot.FastX_backend.model.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat,Long> {

    @Query("""
    SELECT new com.springboot.FastX_backend.dto.response.BookingSeatRespDto(
        bs.passengerName, bs.age, bs.gender,
        bs.seat.seatNumber
    )
    FROM BookingSeat bs
    WHERE bs.booking.id = ?1
    """)
    List<BookingSeatRespDto> getBookingSeats(long bookingId);

    @Query("""
    SELECT COUNT(bs)
    FROM BookingSeat bs
    JOIN bs.booking b
    WHERE bs.seat.id = ?1
    AND b.schedule.id = ?2
    AND (
        b.bookingStatus = ?3
        OR (b.bookingStatus = ?4 AND b.paymentDeadline > ?5)
    )
    AND b.boardingStop.stopOrder < ?6
    AND b.droppingStop.stopOrder > ?7
    """)
    long countSeatConflicts(long id, long id1, BookingStatus bookingStatus, BookingStatus bookingStatus1, Instant now, int stopOrder, int stopOrder1);

    @Query("""
select bs.seat.seatNumber
from BookingSeat bs
where bs.booking.id = ?1
""")
    List<String> getSeatNumbersByBookingId(long bookingId);

    boolean existsBySeatId(long seatId);

    List<BookingSeat> findByBooking_Id(long bookingId);
}
