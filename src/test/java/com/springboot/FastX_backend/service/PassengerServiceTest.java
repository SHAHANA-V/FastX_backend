package com.springboot.FastX_backend.service;
import com.springboot.FastX_backend.dto.request.ChangePasswordDto;
import com.springboot.FastX_backend.dto.request.PassengerDto;
import com.springboot.FastX_backend.dto.request.PassengerUpdateDto;
import com.springboot.FastX_backend.enums.Role;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.model.Passenger;
import com.springboot.FastX_backend.model.User;
import com.springboot.FastX_backend.repository.PassengerRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PassengerServiceTest {
    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PassengerService passengerService;


    private Passenger getPassenger() {

        User user = new User();
        user.setId(1L);
        user.setUsername("harry");
        user.setPassword("encodedPassword");
        user.setRole(Role.PASSENGER);

        Passenger passenger = new Passenger();
        passenger.setId(1L);
        passenger.setName("Harry Potter");
        passenger.setPhoneNo("9876543210");
        passenger.setEmail("harry@gmail.com");
        passenger.setUser(user);

        return passenger;
    }


    // 1. Add passenger successfully
    @Test
    void addPassengerTest() {

        PassengerDto dto = new PassengerDto(
                "Harry Potter",
                "9876543210",
                "harry@gmail.com",
                "harry",
                "password123"
        );

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        passengerService.addPassenger(dto);

        verify(passwordEncoder, times(1)).encode("password123");

        verify(userRepository, times(1))
                .save(argThat(user ->
                        user.getUsername().equals("harry") &&
                                user.getPassword().equals("encodedPassword") &&
                                user.getRole() == Role.PASSENGER
                ));

        verify(passengerRepository, times(1))
                .save(argThat(passenger ->
                        passenger.getName().equals("Harry Potter") &&
                                passenger.getPhoneNo().equals("9876543210") &&
                                passenger.getUser() != null
                ));
    }


    // 2. Get passenger by valid ID
    @Test
    void getPassengerByIdTest() {

        Passenger passenger = getPassenger();

        when(passengerRepository.findById(1L))
                .thenReturn(Optional.of(passenger));

        Passenger result = passengerService.getPassengerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Harry Potter", result.getName());

        verify(passengerRepository, times(1)).findById(1L);
    }


    // 3. Get passenger by invalid ID
    @Test
    void getPassengerByIdNotFoundTest() {

        when(passengerRepository.findById(100L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> passengerService.getPassengerById(100L));

        verify(passengerRepository, times(1)).findById(100L);
    }


    // 4. Get all passengers with pagination
    @Test
    void getAllPassengerTest() {

        List<Passenger> passengers = List.of(
                getPassenger(),
                getPassenger()
        );

        when(passengerRepository.findAll(PageRequest.of(0, 5)))
                .thenReturn(new PageImpl<>(passengers));

        List<Passenger> result = passengerService.getAllPassenger(0, 5);

        assertEquals(2, result.size());

        verify(passengerRepository, times(1))
                .findAll(PageRequest.of(0, 5));
    }


    // 5. Delete passenger successfully
    @Test
    void deletePassengerByIdTest() {

        Passenger passenger = getPassenger();

        when(passengerRepository.findById(1L))
                .thenReturn(Optional.of(passenger));

        passengerService.deletePassengerById(1L);

        verify(passengerRepository, times(1)).findById(1L);
        verify(passengerRepository, times(1)).deleteById(1L);
    }


    // 6. Update passenger successfully
    @Test
    void updatePassengerTest() {

        Passenger passenger = getPassenger();

        PassengerUpdateDto dto = new PassengerUpdateDto(
                "Harry James Potter",
                "9876543211",
                "harryj@gmail.com"
        );

        when(passengerRepository.getPassengerByUsername("harry"))
                .thenReturn(passenger);

        passengerService.updatePassenger("harry", dto);

        assertEquals("Harry James Potter", passenger.getName());
        assertEquals("9876543211", passenger.getPhoneNo());
        assertEquals("harryj@gmail.com", passenger.getEmail());

        verify(passengerRepository, times(1)).save(passenger);
    }


    // 7. Update passenger without valid fields
    @Test
    void updatePassengerNoFieldsTest() {

        Passenger passenger = getPassenger();

        PassengerUpdateDto dto = new PassengerUpdateDto(
                " ",
                null,
                ""
        );

        when(passengerRepository.getPassengerByUsername("harry"))
                .thenReturn(passenger);

        assertThrows(RuntimeException.class,
                () -> passengerService.updatePassenger("harry", dto));

        verify(passengerRepository, never()).save(any(Passenger.class));
    }


    // 8. Get passenger profile
    @Test
    void getPassengerProfileTest() {

        Passenger passenger = getPassenger();

        when(passengerRepository.getPassengerByUsername("harry"))
                .thenReturn(passenger);

        Passenger result = passengerService.getPassengerProfile("harry");

        assertNotNull(result);
        assertEquals("Harry Potter", result.getName());
        assertEquals("harry", result.getUser().getUsername());

        verify(passengerRepository, times(1))
                .getPassengerByUsername("harry");
    }


    // 9. Change password successfully
    @Test
    void changePasswordTest() {

        Passenger passenger = getPassenger();

        ChangePasswordDto dto = new ChangePasswordDto(
                "oldPassword",
                "newPassword",
                "newPassword"
        );

        when(passengerRepository.getPassengerByUsername("harry"))
                .thenReturn(passenger);

        when(passwordEncoder.matches("oldPassword", "encodedPassword"))
                .thenReturn(true);

        when(passwordEncoder.encode("newPassword"))
                .thenReturn("newEncodedPassword");

        passengerService.changePassword("harry", dto);

        assertEquals("newEncodedPassword",
                passenger.getUser().getPassword());

        verify(userRepository, times(1)).save(passenger.getUser());
    }


    // 10. Change password with incorrect current password
    @Test
    void changePasswordIncorrectCurrentPasswordTest() {

        Passenger passenger = getPassenger();

        ChangePasswordDto dto = new ChangePasswordDto(
                "wrongPassword",
                "newPassword",
                "newPassword"
        );

        when(passengerRepository.getPassengerByUsername("harry"))
                .thenReturn(passenger);

        when(passwordEncoder.matches("wrongPassword", "encodedPassword"))
                .thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> passengerService.changePassword("harry", dto));

        verify(userRepository, never()).save(any(User.class));
    }


    // 11. New password and confirm password do not match
    @Test
    void changePasswordMismatchTest() {

        Passenger passenger = getPassenger();

        ChangePasswordDto dto = new ChangePasswordDto(
                "oldPassword",
                "newPassword",
                "differentPassword"
        );

        when(passengerRepository.getPassengerByUsername("harry"))
                .thenReturn(passenger);

        when(passwordEncoder.matches("oldPassword", "encodedPassword"))
                .thenReturn(true);

        assertThrows(RuntimeException.class,
                () -> passengerService.changePassword("harry", dto));

        verify(userRepository, never()).save(any(User.class));
    }


    // 12. New password should be different from current password
    @Test
    void changePasswordSamePasswordTest() {

        Passenger passenger = getPassenger();

        ChangePasswordDto dto = new ChangePasswordDto(
                "oldPassword",
                "oldPassword",
                "oldPassword"
        );

        when(passengerRepository.getPassengerByUsername("harry"))
                .thenReturn(passenger);

        when(passwordEncoder.matches("oldPassword", "encodedPassword"))
                .thenReturn(true);

        assertThrows(RuntimeException.class,
                () -> passengerService.changePassword("harry", dto));

        verify(userRepository, never()).save(any(User.class));
    }
}
