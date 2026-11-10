package com.springboot.FastX_backend.controller;

import com.springboot.FastX_backend.dto.request.ChangePasswordDto;
import com.springboot.FastX_backend.dto.request.PassengerDto;
import com.springboot.FastX_backend.dto.request.PassengerUpdateDto;
import com.springboot.FastX_backend.dto.response.PassengerProfileRespDto;
import com.springboot.FastX_backend.mapper.PassengerMapper;
import com.springboot.FastX_backend.model.Passenger;
import com.springboot.FastX_backend.service.PassengerService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173/")
public class PassengerController {
    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @PostMapping("/api/passenger/add")
    public void addPassenger(@Valid @RequestBody PassengerDto passengerDto){
        passengerService.addPassenger(passengerDto);
    }

    @GetMapping("/api/passenger/{passengerId}")
    public Passenger getPassengerById(@PathVariable long passengerId){
        return passengerService.getPassengerById(passengerId);
    }

    @GetMapping("/api/passenger/all")
    public List<Passenger> getAllPassenger(@RequestParam(name = "page",required = false,defaultValue = "0")int page,
                                           @RequestParam(name = "size",required = false,defaultValue = "20")int size){
        return passengerService.getAllPassenger(page,size);
    }

    @DeleteMapping("/api/passenger/delete/{id}")
    public void deletePassengerById(@PathVariable long id){
        passengerService.deletePassengerById(id);
    }

    @PutMapping("/api/passenger/update")
    public void updatePassenger(Principal principal,@Valid @RequestBody PassengerUpdateDto passengerUpdateDto){
        passengerService.updatePassenger(principal.getName(),passengerUpdateDto);
    }

    @GetMapping("/api/passenger/profile")
    public PassengerProfileRespDto getPassengerProfile(Principal principal){
       Passenger passenger= passengerService.getPassengerProfile(principal.getName());
       return PassengerMapper.convertModelToDto(passenger);
    }
    @PutMapping("/api/passenger/change-password")
    public void changePassword(Principal principal,@RequestBody ChangePasswordDto changePasswordDto) {

        passengerService.changePassword(principal.getName(), changePasswordDto);
    }

}
