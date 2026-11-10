package com.springboot.FastX_backend.repository;

import com.springboot.FastX_backend.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PassengerRepository extends JpaRepository<Passenger,Long> {
    @Query("""
select p from Passenger p
join p.user u
where u.username=?1
""")
    Passenger getPassengerByUsername(String passengerUsername);
}
