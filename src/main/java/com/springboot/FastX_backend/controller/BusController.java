package com.springboot.FastX_backend.controller;

import com.springboot.FastX_backend.dto.request.BusDto;
import com.springboot.FastX_backend.dto.request.BusUpdateDto;
import com.springboot.FastX_backend.dto.response.BusRespForAdminDto;
import com.springboot.FastX_backend.dto.response.BusRespForOptDto;
import com.springboot.FastX_backend.enums.AcType;
import com.springboot.FastX_backend.enums.BusStatus;
import com.springboot.FastX_backend.enums.BusType;
import com.springboot.FastX_backend.model.Bus;
import com.springboot.FastX_backend.model.BusOperator;
import com.springboot.FastX_backend.repository.BusRepository;
import com.springboot.FastX_backend.service.BusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class BusController {

    private final BusService busService;
    private final BusRepository busRepository;

    @PostMapping("/api/add/bus")
    public Long addBus(Principal principal,@Valid @RequestBody BusDto busDto){
        return busService.addBus(principal.getName(),busDto);
    }

    @GetMapping("/api/bus/byId/{id}")
    public Bus getBusById(@PathVariable long id){
        return busService.getBusOperatorById(id);
    }

    //display all the buses for admin
    @GetMapping("/api/bus/all")
    public List<BusRespForAdminDto> getAllBusForAdmin(@RequestParam(name = "page",required = false,defaultValue = "0")int page,
                                                      @RequestParam(name = "size",required = false,defaultValue = "5") int size,
                                                      @RequestParam(required = false) String search,
                                                      @RequestParam(required = false) String operatorName,
                                                      @RequestParam(required = false) BusType busType,
                                                      @RequestParam(required = false) AcType acType,
                                                      @RequestParam(required = false) BusStatus busStatus){
        return busService.getAllBusForAdmin(page,size,search, operatorName, busType, acType, busStatus);
    }

    //display all the buses under the busOperator with filters[for operator]
    @GetMapping("/api/bus/dynamicFilter")
    public List<BusRespForOptDto> getDynamicFilter(Principal principal,
                                                   @RequestParam(name = "page",required = false,defaultValue = "0")int page,
                                                   @RequestParam(name = "size",required = false,defaultValue = "5") int size,
                                                   @RequestParam(name = "busType",required = false)BusType busType,
                                                   @RequestParam(name = "status",required = false)BusStatus busStatus,
                                                   @RequestParam(name = "acType",required = false)AcType acType){
        return busService.getDynamicFilter(principal.getName(),page,size,busType,busStatus,acType);
    }

    //update the bus done by bus operator[only if the bus is under bus operator]
    @PutMapping("/api/bus/update/{id}")
    public void updateBus(Principal principal,@PathVariable long id,@Valid @RequestBody BusUpdateDto busUpdateDto){
        busService.updateBus(principal.getName(),id,busUpdateDto);
    }

    @GetMapping("/api/operator/bus/count")
    public long getOperatorBusCount(Principal principal) {
        return busService.getOperatorBusCount(principal.getName());
    }
}
