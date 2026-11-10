package com.springboot.FastX_backend.repository;

import com.springboot.FastX_backend.dto.response.AdminRouteRespDto;
import com.springboot.FastX_backend.enums.RouteStatus;
import com.springboot.FastX_backend.model.Route;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route,Long> {
    @Query("""
select r from Route r
where r.routeStatus='ACTIVE'
""")
    List<Route> getAllActiveRoutes(Pageable pageable);

    @Query("""
select new com.springboot.FastX_backend.dto.response.AdminRouteRespDto(
r.id,
r.source,
r.destination,
r.routeStatus,
(select count(rs) from RouteStop rs where rs.route.id=r.id)
)
from Route r
where (?1 is null or ?1 = '' or
       lower(r.source) like lower(concat('%',?1,'%')) or
       lower(r.destination) like lower(concat('%',?1,'%')))
and (?2 is null or r.source=?2)
and (?3 is null or r.destination=?3)
and (?4 is null or r.routeStatus=?4)
""")
    List<AdminRouteRespDto> getAllRoutesForAdmin(
            String search,
            String source,
            String destination,
            RouteStatus routeStatus,
            Pageable pageable
    );
}
