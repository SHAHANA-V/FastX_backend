package com.springboot.FastX_backend.repository;

import com.springboot.FastX_backend.model.BusOperator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusOperatorRepository extends JpaRepository<BusOperator,Long> {

    @Query("""
select b from BusOperator b
join b.user u
where u.username=?1
""")
    BusOperator getBusOperatorByUsername(String busOperatorName);
}
