package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.dto.request.AdminDto;
import com.springboot.FastX_backend.dto.response.DetailsDto;
import com.springboot.FastX_backend.enums.Role;
import com.springboot.FastX_backend.exception.InactiveUserException;
import com.springboot.FastX_backend.model.User;
import com.springboot.FastX_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void addAdmin(AdminDto adminDto) {
        //step 1: prepare user object
        User user = new User();
        user.setUsername(adminDto.username());
        user.setPassword(passwordEncoder.encode(adminDto.password()));
        user.setRole(Role.ADMIN);
        userRepository.save(user);
    }

    public DetailsDto getUserDetails(String loggedInUsername) {
        DetailsDto detailsDto = userRepository.getUserDetails(loggedInUsername);
        if(!detailsDto.isActive()){
            throw new InactiveUserException("User is disabled");
        }
        return detailsDto;
    }
}
