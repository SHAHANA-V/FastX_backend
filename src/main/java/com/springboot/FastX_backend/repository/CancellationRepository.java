package com.springboot.FastX_backend.repository;

import com.springboot.FastX_backend.model.Cancellation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CancellationRepository extends JpaRepository<Cancellation,Long> {
    boolean existsByBookingId(long id);
    List<Cancellation> findByBooking_Schedule_Bus_BusOperator_User_Username(String username);
}
