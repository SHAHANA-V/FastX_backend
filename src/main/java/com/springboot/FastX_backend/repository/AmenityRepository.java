package com.springboot.FastX_backend.repository;

import com.springboot.FastX_backend.model.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity,Long> {
}
