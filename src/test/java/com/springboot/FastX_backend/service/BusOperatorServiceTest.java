package com.springboot.FastX_backend.service;
import com.springboot.FastX_backend.dto.request.BusOperatorDto;
import com.springboot.FastX_backend.dto.request.BusOperatorUpdateDto;
import com.springboot.FastX_backend.dto.request.ChangePasswordDto;
import com.springboot.FastX_backend.enums.Role;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.model.BusOperator;
import com.springboot.FastX_backend.model.User;
import com.springboot.FastX_backend.repository.BusOperatorRepository;
import com.springboot.FastX_backend.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BusOperatorServiceTest {
    @InjectMocks
    private BusOperatorService busOperatorService;

    @Mock
    private BusOperatorRepository busOperatorRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;


    @Test
    public void addBusOperatorTest() {

        BusOperatorDto dto = new BusOperatorDto(
                "Albus Dumbledore",
                "9876543210",
                "albus@gmail.com",
                "albus",
                "password123"
        );

        User user = new User();
        user.setUsername("albus");
        user.setPassword("encodedPassword");
        user.setRole(Role.BUS_OPERATOR);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        busOperatorService.addBusOperator(dto);

        verify(passwordEncoder, times(1))
                .encode("password123");

        verify(userRepository, times(1))
                .save(any(User.class));

        verify(busOperatorRepository, times(1))
                .save(any(BusOperator.class));

        verifyNoMoreInteractions(
                passwordEncoder,
                userRepository,
                busOperatorRepository
        );
    }


    @Test
    public void getAllBusOperatorTest() {

        BusOperator operator1 = new BusOperator(
                1L,
                "Albus Dumbledore",
                "9876543210",
                "albus@gmail.com",
                null
        );

        BusOperator operator2 = new BusOperator(
                2L,
                "Harry Potter",
                "9876543211",
                "harry@gmail.com",
                null
        );

        when(busOperatorRepository.findAll(PageRequest.of(0, 5)))
                .thenReturn(new PageImpl<>(List.of(operator1, operator2)));

        List<BusOperator> result =
                busOperatorService.getAllBusOperator(0, 5);

        assertEquals(2, result.size());
        assertEquals("Albus Dumbledore", result.get(0).getName());
        assertEquals("Harry Potter", result.get(1).getName());

        verify(busOperatorRepository, times(1))
                .findAll(PageRequest.of(0, 5));

        verifyNoMoreInteractions(busOperatorRepository);
    }


    @Test
    public void getBusOperatorByIdTest() {

        long id = 1L;

        BusOperator operator = new BusOperator(
                id,
                "Albus Dumbledore",
                "9876543210",
                "albus@gmail.com",
                null
        );

        when(busOperatorRepository.findById(id))
                .thenReturn(Optional.of(operator));

        BusOperator result =
                busOperatorService.getBusOperatorById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Albus Dumbledore", result.getName());

        verify(busOperatorRepository, times(1))
                .findById(id);

        verifyNoMoreInteractions(busOperatorRepository);
    }


    @Test
    public void getBusOperatorByUsernameTest() {

        String username = "albus";

        User user = new User();
        user.setUsername(username);

        BusOperator operator = new BusOperator(
                1L,
                "Albus Dumbledore",
                "9876543210",
                "albus@gmail.com",
                user
        );

        when(busOperatorRepository.getBusOperatorByUsername(username))
                .thenReturn(operator);

        BusOperator result =
                busOperatorService.getBusOperatorByUsername(username);

        assertNotNull(result);
        assertEquals("Albus Dumbledore", result.getName());
        assertEquals(username, result.getUser().getUsername());

        verify(busOperatorRepository, times(1))
                .getBusOperatorByUsername(username);

        verifyNoMoreInteractions(busOperatorRepository);
    }


    @Test
    public void updateBusOperatorTest() {

        String username = "albus";

        BusOperator operator = new BusOperator(
                1L,
                "Albus",
                "9876543210",
                "old@gmail.com",
                null
        );

        BusOperatorUpdateDto dto = new BusOperatorUpdateDto(
                "Albus Dumbledore",
                "9876543211",
                "albus@gmail.com"
        );

        when(busOperatorRepository.getBusOperatorByUsername(username))
                .thenReturn(operator);

        busOperatorService.updateBusOperator(username, dto);

        assertEquals("Albus Dumbledore", operator.getName());
        assertEquals("9876543211", operator.getPhoneNo());
        assertEquals("albus@gmail.com", operator.getEmail());

        verify(busOperatorRepository, times(1))
                .getBusOperatorByUsername(username);

        verify(busOperatorRepository, times(1))
                .save(operator);

        verifyNoMoreInteractions(busOperatorRepository);
    }


    @Test
    public void changePasswordTest() {

        String username = "albus";

        User user = new User();
        user.setUsername(username);
        user.setPassword("oldEncodedPassword");

        BusOperator operator = new BusOperator(
                1L,
                "Albus Dumbledore",
                "9876543210",
                "albus@gmail.com",
                user
        );

        ChangePasswordDto dto = new ChangePasswordDto(
                "oldPassword",
                "newPassword",
                "newPassword"
        );

        when(busOperatorRepository.getBusOperatorByUsername(username))
                .thenReturn(operator);

        when(passwordEncoder.matches(
                "oldPassword", "oldEncodedPassword"))
                .thenReturn(true);

        when(passwordEncoder.matches(
                "newPassword", "oldEncodedPassword"))
                .thenReturn(false);

        when(passwordEncoder.encode("newPassword"))
                .thenReturn("newEncodedPassword");

        busOperatorService.changePassword(username, dto);

        assertEquals("newEncodedPassword", user.getPassword());

        verify(busOperatorRepository, times(1))
                .getBusOperatorByUsername(username);

        verify(passwordEncoder, times(1))
                .matches("oldPassword", "oldEncodedPassword");

        verify(passwordEncoder, times(1))
                .matches("newPassword", "oldEncodedPassword");

        verify(passwordEncoder, times(1))
                .encode("newPassword");

        verify(userRepository, times(1))
                .save(user);

        verifyNoMoreInteractions(
                busOperatorRepository,
                passwordEncoder,
                userRepository
        );
    }


    @Test
    public void getBusOperatorProfileTest() {

        String username = "albus";

        User user = new User();
        user.setUsername(username);

        BusOperator operator = new BusOperator(
                1L,
                "Albus Dumbledore",
                "9876543210",
                "albus@gmail.com",
                user
        );

        when(busOperatorRepository.getBusOperatorByUsername(username))
                .thenReturn(operator);

        var result =
                busOperatorService.getBusOperatorProfile(username);

        assertNotNull(result);
        assertEquals("Albus Dumbledore", result.name());
        assertEquals("9876543210", result.phoneNo());
        assertEquals("albus@gmail.com", result.email());
        assertEquals(username, result.username());

        verify(busOperatorRepository, times(1))
                .getBusOperatorByUsername(username);

        verifyNoMoreInteractions(busOperatorRepository);
    }
}
