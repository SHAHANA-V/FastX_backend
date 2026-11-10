package com.springboot.FastX_backend.controller;

import com.springboot.FastX_backend.dto.request.BusOperatorDto;
import com.springboot.FastX_backend.dto.request.BusOperatorUpdateDto;
import com.springboot.FastX_backend.dto.request.ChangePasswordDto;
import com.springboot.FastX_backend.dto.response.BusOperatorProfileRespDto;
import com.springboot.FastX_backend.model.BusOperator;
import com.springboot.FastX_backend.service.BusOperatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class BusOperatorController {
    private final BusOperatorService busOperatorService;

    @PostMapping("/api/busoperator/add")
    public void addBusOperator(@Valid @RequestBody BusOperatorDto busOperatorDto){
        busOperatorService.addBusOperator(busOperatorDto);
    }

    @GetMapping("/api/busOperator/all")
    public List<BusOperator> getAllBusOperator(@RequestParam(name = "page",required = false,defaultValue = "0") int page,
                                               @RequestParam(name = "size",required = false,defaultValue = "10")int size){
        return busOperatorService.getAllBusOperator(page,size);
    }

    @GetMapping("/api/busOperator/byId/{id}")
    public BusOperator getBusOperatorById(@PathVariable long id){
        return busOperatorService.getBusOperatorById(id);
    }

    @GetMapping("/api/busOperator/byUsername")
    public BusOperator getBusOperatorByUsername(@RequestParam("name") String busOperatorName){
        return busOperatorService.getBusOperatorByUsername(busOperatorName);
    }

    @PutMapping("/api/busOperator/update")
    public void updateBusOperator(Principal principal, @Valid @RequestBody BusOperatorUpdateDto busOperatorUpdateDto){
        busOperatorService.updateBusOperator(principal.getName(),busOperatorUpdateDto);
    }


    @DeleteMapping("/api/busOperator/delete/{id}")
    public void deleteOperatorById(@PathVariable long id){
        busOperatorService.deleteOperatorById(id);
    }

    @GetMapping("/api/busOperator/profile")
    public BusOperatorProfileRespDto getBusOperatorProfile(Principal principal) {
        return busOperatorService.getBusOperatorProfile(principal.getName());
    }

    @PutMapping("/api/busOperator/change-password")
    public void changePassword( Principal principal, @RequestBody ChangePasswordDto changePasswordDto) {

        busOperatorService.changePassword(
                principal.getName(),
                changePasswordDto
        );
    }

}
