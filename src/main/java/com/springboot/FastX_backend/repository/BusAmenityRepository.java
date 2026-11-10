package com.springboot.FastX_backend.repository;

import com.springboot.FastX_backend.dto.response.BusAmenityRespDto;
import com.springboot.FastX_backend.model.Bus;
import com.springboot.FastX_backend.model.BusAmenity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusAmenityRepository extends JpaRepository<BusAmenity,Long> {
    @Query("""
select ba
from BusAmenity ba
where ba.bus.id=?1 and ba.amenity.id=?2
""")
    Optional<BusAmenity> findByBusAndAmenity(long busId, long amenityId);

    @Query("""
select ba.amenity.name from BusAmenity ba
where ba.bus.id=?1
""")
    List<String> getAmenityForSpecificBus(long busId);

    Optional<BusAmenity> findByBus_IdAndAmenity_Id(long busId, long amenityId);
}
