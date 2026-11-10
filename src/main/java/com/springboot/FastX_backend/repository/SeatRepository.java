package com.springboot.FastX_backend.repository;

import com.springboot.FastX_backend.dto.response.SeatRespDto;
import com.springboot.FastX_backend.dto.response.SeatRespV2Dto;
import com.springboot.FastX_backend.enums.SeatType;
import com.springboot.FastX_backend.model.Seat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface
SeatRepository extends JpaRepository<Seat,Long> {

    @Query("""
SELECT COUNT(s)
FROM Seat s
WHERE s.seatNumber = ?1
AND s.bus.id = ?2
""")

    long countSeatBySeatNumberAndBusId(@NotBlank(message = "Seat number is required") @NotNull(message = "Seat number is required") String s, long busId);

    @Query("""
select count(s)
from Seat s
where s.bus.id=?1
""")
    long countSeatsByBusId(long busId);

    @Query("""
select new com.springboot.FastX_backend.dto.response.SeatRespDto(s.seatNumber,s.seatType,b.busName,b.busNumber)
from Seat s
join s.bus b
where s.seatNumber=?1 and b.id=?2
""")
    Optional<SeatRespDto> getSeatInABus(String seatNumber, long busId);

    @Query("""
select new com.springboot.FastX_backend.dto.response.SeatRespV2Dto(s.id,s.seatNumber,s.seatType,false)
from Seat s
where s.bus.id=?1
""")
    List<SeatRespV2Dto> getAllSeatsForBus(long busId, Pageable pageable);

    List<Seat> findBySeatType(SeatType seatType, Pageable pageable);

    @Query("""
SELECT COUNT(s)
FROM Seat s
WHERE s.bus.id = ?1
AND s.seatType = ?2
""")
    long countSeatsByBusIdAndSeatType(long busId, SeatType seatType);

    Optional<Seat> findByBusIdAndSeatNumber(long busId, String seatNumber);
}
