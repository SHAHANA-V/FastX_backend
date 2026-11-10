package com.springboot.FastX_backend.repository;

import com.springboot.FastX_backend.dto.response.DetailsDto;
import com.springboot.FastX_backend.enums.Role;
import com.springboot.FastX_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    @Query("""
select u
from User u
where u.username = ?1 and u.isActive = ?2
""")
    UserDetails loadUserByUsername(String username,boolean b);


    Optional<User> findByUsername(String name);

    @Query("""
select u.username,u.isActive,u.role
from User u
where u.username=?1
""")
    DetailsDto getUserDetails(String loggedInUsername);
}
