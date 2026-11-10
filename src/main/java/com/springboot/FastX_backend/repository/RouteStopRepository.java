package com.springboot.FastX_backend.repository;

import com.springboot.FastX_backend.dto.response.RouteStopRespDto;
import com.springboot.FastX_backend.dto.response.RouteStopRespV2Dto;
import com.springboot.FastX_backend.model.RouteStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteStopRepository extends JpaRepository<RouteStop,Long> {
    @Query("""
select new com.springboot.FastX_backend.dto.response.RouteStopRespDto(r.id,r.stopName,r.cityName,r.stopType,r.stopOrder)
from RouteStop r
where r.route.id=?1
order by r.stopOrder asc
""")
    List<RouteStopRespDto> getRouteStopForRoute(long routeId);

    @Query("""
select new com.springboot.FastX_backend.dto.response.RouteStopRespV2Dto(r.source,r.destination,rs.stopName,rs.cityName,rs.stopType,rs.stopOrder)
from RouteStop rs
join rs.route r
where r.id=?1
and rs.stopOrder=?2
""")
    Optional<RouteStopRespV2Dto> getRouteStopByStopOrder(long routeId, int stopOrder);

    @Query("""
select r
from RouteStop r
where r.id=?1 and r.route.id=?2
""")
    Optional<RouteStop> findByIdAndRoute_Id(long routeStopId, long routeId);

    @Query("""
    select new com.springboot.FastX_backend.dto.response.RouteStopRespDto(
        rs.id,rs.stopName, rs.cityName, rs.stopType, rs.stopOrder
    )
    from RouteStop rs
    where rs.route.id = ?1
    and rs.cityName = ?2
    and rs.stopType in ( com.springboot.FastX_backend.enums.StopType.BOARDING,
                                              com.springboot.FastX_backend.enums.StopType.BOTH)
    order by rs.stopOrder asc
    """)
    List<RouteStopRespDto> getBoardingStops(long routeId, String sourceCity);
    @Query("""
    select new com.springboot.FastX_backend.dto.response.RouteStopRespDto(
        rs.id,rs.stopName, rs.cityName, rs.stopType, rs.stopOrder
    )
    from RouteStop rs
    where rs.route.id = ?1
    and rs.cityName = ?2
    and rs.stopType in (com.springboot.FastX_backend.enums.StopType.DROPPING,
                                              com.springboot.FastX_backend.enums.StopType.BOTH)
    order by rs.stopOrder asc
    """)
    List<RouteStopRespDto> getDroppingStops(long routeId, String destinationCity);


    List<RouteStop> findAllByRoute_IdOrderByStopOrderAscIdAsc(long routeId);
}
