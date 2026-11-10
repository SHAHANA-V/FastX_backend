package com.springboot.FastX_backend.repository;

import com.springboot.FastX_backend.dto.response.BusRespForAdminDto;
import com.springboot.FastX_backend.dto.response.BusRespForOptDto;
import com.springboot.FastX_backend.enums.AcType;
import com.springboot.FastX_backend.enums.BusStatus;
import com.springboot.FastX_backend.enums.BusType;
import com.springboot.FastX_backend.model.Bus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BusRepository extends JpaRepository<Bus,Long> {
    @Query("""
select new com.springboot.FastX_backend.dto.response.BusRespForOptDto(b.id,b.busNumber as busNumber,b.busName as busName,b.totalSeats as totalSeats,b.acType as acType,b.busType as busType,b.busStatus as busStatus)
from Bus b
join b.busOperator bo
join bo.user u
where u.username = ?1
and (?2 IS NULL OR b.busType =?2)
and (?3 IS NULL OR b.busStatus =?3)
and (?4 IS NULL OR b.acType =?4)
""")
    List<BusRespForOptDto> getDynamicFilter(String busOperatorName, BusType busType, BusStatus busStatus, AcType acType, Pageable pageable);

    @Query("""
select new com.springboot.FastX_backend.dto.response.BusRespForAdminDto(
b.id,
b.busName,
b.busNumber,
b.busType,
b.totalSeats,
b.acType,
b.busStatus,
bo.name,
bo.phoneNo,
bo.email
)
from Bus b
join b.busOperator bo
where (?1 is null or ?1 = '' or
       lower(b.busName) like lower(concat('%',?1,'%')) or
       lower(b.busNumber) like lower(concat('%',?1,'%')))
and (?2 is null or ?2 = '' or
     lower(bo.name) like lower(concat('%',?2,'%')))
and (?3 is null or b.busType = ?3)
and (?4 is null or b.acType = ?4)
and (?5 is null or b.busStatus = ?5)
""")
    List<BusRespForAdminDto> getAllBusForAdmin(
            String search,
            String operatorName,
            BusType busType,
            AcType acType,
            BusStatus busStatus,
            Pageable pageable
    );
    @Query("""
SELECT COUNT(b)
FROM Bus b
JOIN b.busOperator bo
JOIN bo.user u
WHERE u.username = ?1
""")
    long getOperatorBusCount(String name);
}
