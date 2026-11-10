package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.dto.request.BusOperatorDto;
import com.springboot.FastX_backend.dto.request.BusOperatorUpdateDto;
import com.springboot.FastX_backend.dto.request.ChangePasswordDto;
import com.springboot.FastX_backend.dto.response.BusOperatorProfileRespDto;
import com.springboot.FastX_backend.enums.Role;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.mapper.BusOperatorMapper;
import com.springboot.FastX_backend.model.Bus;
import com.springboot.FastX_backend.model.BusOperator;
import com.springboot.FastX_backend.model.User;
import com.springboot.FastX_backend.repository.BusOperatorRepository;
import com.springboot.FastX_backend.repository.UserRepository;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.module.ResolutionException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BusOperatorService {
    private final BusOperatorRepository busOperatorRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void addBusOperator(@Valid BusOperatorDto busOperatorDto) {

        //STEP 1:prepare user objects
        User user = new User();
        user.setUsername(busOperatorDto.username());
        user.setPassword(passwordEncoder.encode(busOperatorDto.password()));
        user.setRole(Role.BUS_OPERATOR);
        user = userRepository.save(user);

        //STEP 2:prepare bus operator object
        BusOperator busOperator = BusOperatorMapper.convertDtoToEntity(busOperatorDto);

        //STEP 3:Attach the user to bus operator
        busOperator.setUser(user);

        //STEP 4:save the bus operator in db
        busOperatorRepository.save(busOperator);

    }

    public List<BusOperator> getAllBusOperator(int page , int size) {

        Pageable pageable = PageRequest.of(page,size);
        return busOperatorRepository.findAll(pageable).getContent();
    }

    public BusOperator getBusOperatorById(long id) {
        return busOperatorRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Bus Operator Id does not exist"));
    }


    public BusOperator getBusOperatorByUsername(String busOperatorName) {

        BusOperator busOperator =busOperatorRepository.getBusOperatorByUsername(busOperatorName);
        if (busOperator == null)
            throw new ResourceNotFoundException("Bus Operator is not valid");
        return busOperator;

    }

    public void deleteOperatorById(long id) {
        getBusOperatorById(id);
        busOperatorRepository.deleteById(id);
    }


    public void updateBusOperator(String busOperatorName, @Valid BusOperatorUpdateDto busOperatorUpdateDto) {
        BusOperator busOperator = busOperatorRepository.getBusOperatorByUsername(busOperatorName);
        boolean flag = false;

        //update name
        if(!(busOperatorUpdateDto.name() ==null) && !(busOperatorUpdateDto.name().trim().isEmpty())){
            busOperator.setName(busOperatorUpdateDto.name());
            flag = true;
        }

        //update email
        if(!(busOperatorUpdateDto.email()==null) && !(busOperatorUpdateDto.email().trim().isEmpty())){
            busOperator.setEmail(busOperatorUpdateDto.email());
            flag = true;
        }

        //update phoneNo
        if(!(busOperatorUpdateDto.phoneNo()==null) && !(busOperatorUpdateDto.phoneNo().trim().isEmpty())){
            busOperator.setPhoneNo(busOperatorUpdateDto.phoneNo());
            flag = true;
        }


        if(!flag)
            throw new RuntimeException("No valid fields provided for update");
        busOperatorRepository.save(busOperator);
    }

    @Transactional
    public void changePassword(String username, ChangePasswordDto changePasswordDto) {

        BusOperator busOperator =
                busOperatorRepository.getBusOperatorByUsername(username);

        if (busOperator == null) {
            throw new ResourceNotFoundException("Bus Operator not found");
        }

        User user = busOperator.getUser();

        // Check current password
        if (!passwordEncoder.matches(
                changePasswordDto.currentPassword(),
                user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Check new password confirmation
        if (!changePasswordDto.newPassword().equals(
                changePasswordDto.confirmPassword())) {
            throw new IllegalArgumentException("New passwords do not match");
        }

        // Prevent reusing current password
        if (passwordEncoder.matches(
                changePasswordDto.newPassword(),
                user.getPassword())) {
            throw new IllegalArgumentException("New password must be different");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDto.newPassword()));

        userRepository.save(user);
    }

    public BusOperatorProfileRespDto getBusOperatorProfile(String username) {

        BusOperator busOperator =
                busOperatorRepository.getBusOperatorByUsername(username);

        if (busOperator == null) {
            throw new ResourceNotFoundException("Bus Operator not found");
        }

        return new BusOperatorProfileRespDto(
                busOperator.getName(),
                busOperator.getPhoneNo(),
                busOperator.getEmail(),
                busOperator.getUser().getUsername()
        );
    }
}
