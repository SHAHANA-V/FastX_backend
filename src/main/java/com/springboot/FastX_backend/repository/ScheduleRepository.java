package com.springboot.FastX_backend.repository;

import com.springboot.FastX_backend.dto.response.ScheduleRespDto;
import com.springboot.FastX_backend.enums.ScheduleStatus;
import com.springboot.FastX_backend.model.Schedule;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule,Long> {
    @Query("""
    SELECT COUNT(s)
    FROM Schedule s
    WHERE s.bus.id = ?1
    AND s.journeyDate = ?2
    AND s.departureTime < ?4
    AND s.arrivalTime > ?3
""")
    long countConflictingSchedules(long busId, @NotNull(message = "Journey Date is required") LocalDate localDate, @NotNull(message = "Departure Time is required") LocalTime localTime, @NotNull(message = "Arrival Time is required") LocalTime localTime1);

    @Query("""
select new com.springboot.FastX_backend.dto.response.ScheduleRespDto(s.id,r.id,s.journeyDate,s.departureTime,s.arrivalTime,s.fare,s.scheduleStatus,b.id,b.busNumber,b.busName,bo.name,r.source,r.destination,b.busType, b.acType)
from Schedule s
join s.bus b
join b.busOperator bo
join s.route r
""")
    List<ScheduleRespDto> getAllSchedule(Pageable pageable);

    @Query("""
select new com.springboot.FastX_backend.dto.response.ScheduleRespDto(s.id,r.id,s.journeyDate,s.departureTime,s.arrivalTime,s.fare,s.scheduleStatus,b.id,b.busNumber,b.busName,bo.name,r.source,r.destination,b.busType, b.acType)
from Schedule s
join s.bus b
join b.busOperator bo
join s.route r
join bo.user u
where u.username=?1
and(?2 is null or s.scheduleStatus=?2)
""")

    List<ScheduleRespDto> getAllScheduleOperator(String operatorName, ScheduleStatus scheduleStatus, Pageable pageable);

    @Query("""
SELECT new com.springboot.FastX_backend.dto.response.ScheduleRespDto(
    s.id, r.id, s.journeyDate, s.departureTime, s.arrivalTime,
    s.fare, s.scheduleStatus,b.id,b.busNumber, b.busName,
    bo.name, r.source, r.destination,b.busType, b.acType
)
FROM Schedule s
JOIN s.bus b
JOIN b.busOperator bo
JOIN s.route r
WHERE
(
    ?1 IS NULL OR EXISTS (
        SELECT bs.id
        FROM RouteStop bs
        WHERE bs.route.id = r.id
        AND bs.cityName = ?1
        AND bs.stopType IN (
            com.springboot.FastX_backend.enums.StopType.BOARDING,
            com.springboot.FastX_backend.enums.StopType.BOTH
        )
    )
)
AND
(
    ?2 IS NULL OR EXISTS (
        SELECT ds.id
        FROM RouteStop ds
        WHERE ds.route.id = r.id
        AND ds.cityName = ?2
        AND ds.stopType IN (
            com.springboot.FastX_backend.enums.StopType.DROPPING,
            com.springboot.FastX_backend.enums.StopType.BOTH
        )
        AND (
            ?1 IS NULL OR EXISTS (
                SELECT bs.id
                FROM RouteStop bs
                WHERE bs.route.id = r.id
                AND bs.cityName = ?1
                AND bs.stopType IN (
                    com.springboot.FastX_backend.enums.StopType.BOARDING,
                    com.springboot.FastX_backend.enums.StopType.BOTH
                )
                AND bs.stopOrder < ds.stopOrder
            )
        )
    )
)
AND (?3 IS NULL OR s.journeyDate = ?3)
AND (?4 IS NULL OR s.arrivalTime <= ?4)
AND (?5 IS NULL OR s.departureTime >= ?5)
AND (?6 IS NULL OR b.busName = ?6)
AND (?7 IS NULL OR s.fare <= ?7)
AND s.scheduleStatus = com.springboot.FastX_backend.enums.ScheduleStatus.SCHEDULED
AND b.busStatus = com.springboot.FastX_backend.enums.BusStatus.ACTIVE
ORDER BY s.departureTime ASC
""")

    List<ScheduleRespDto> getScheduleFilter(String source, String destination, LocalDate journeyDate, LocalTime arrivalTime, LocalTime departureTime, String busName, Double fare);
    @Query("""
SELECT COUNT(s)
FROM Schedule s
JOIN s.bus b
JOIN b.busOperator bo
JOIN bo.user u
WHERE u.username = ?1
""")
    long getOperatorScheduleCount(String name);

    List<Schedule> findByBus_IdAndJourneyDateBetween(
            long busId,
            LocalDate startDate,
            LocalDate endDate
    );
}
