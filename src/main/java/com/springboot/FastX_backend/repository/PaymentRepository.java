package com.springboot.FastX_backend.repository;

import com.springboot.FastX_backend.dto.response.PaymentRespDto;
import com.springboot.FastX_backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {
    boolean existsByBookingId(long id);

    @Query("""
    SELECT new com.springboot.FastX_backend.dto.response.PaymentRespDto(
        p.id,
        p.amount,
        p.paymentMethod,
        p.paymentStatus,
        p.paidAt,
        b.id
    )
    FROM Payment p
    JOIN p.booking b
    JOIN b.passenger pa
    JOIN pa.user u
    WHERE p.id = ?1
    AND u.username = ?2
""")
    PaymentRespDto getPayment(long id, String name);

    @Query("""
    SELECT new com.springboot.FastX_backend.dto.response.PaymentRespDto(
        p.id,
        p.amount,
        p.paymentMethod,
        p.paymentStatus,
        p.paidAt,
        b.id
    )
    FROM Payment p
    JOIN p.booking b
    WHERE b.id = ?1
""")
    PaymentRespDto getPaymentByBookingId(long bookingId);
    Optional<Payment> findByBooking_Id(long bookingId);
}
