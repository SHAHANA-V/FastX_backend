package com.springboot.FastX_backend.controller;

import com.springboot.FastX_backend.dto.request.SeatDto;
import com.springboot.FastX_backend.dto.request.SeatUpdateDto;
import com.springboot.FastX_backend.dto.response.SeatRespDto;
import com.springboot.FastX_backend.dto.response.SeatRespV2Dto;
import com.springboot.FastX_backend.dto.response.SeatSummaryRespDto;
import com.springboot.FastX_backend.enums.SeatType;
import com.springboot.FastX_backend.service.ScheduleService;
import com.springboot.FastX_backend.service.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class SeatController {

    private final SeatService seatService;


    @PostMapping("/api/add/seat/{busId}")
    public void addSeat(@Valid @RequestBody SeatDto seatDto, @PathVariable long busId, Principal principal){
        seatService.addSeat(principal.getName(),seatDto,busId);
    }

    @PostMapping("/api/add/seat/bulk/{busId}")
    public void addSeatBulk(@Valid @RequestBody List<SeatDto> seatDtoList,@PathVariable long busId,Principal principal){
        seatService.addSeatBulk(principal.getName(),seatDtoList,busId);
    }

    @GetMapping("/api/get/seat/{busId}")
    public SeatRespDto getSeatInABus(@RequestParam(name = "seatNumber")String seatNumber,@PathVariable long busId){
        return seatService.getSeatInABus(seatNumber,busId);
    }

    @GetMapping("/api/get/all/seats/bus/{busId}")
    public List<SeatRespV2Dto> getAllSeatsForBus(@PathVariable long busId,
                                                 @RequestParam(name = "scheduleId") long scheduleId,
                                                 @RequestParam(name = "boardingStopOrder") int boardingStopOrder,
                                                 @RequestParam(name = "droppingStopOrder") int droppingStopOrder,
                                                 @RequestParam(name = "page",required = false,defaultValue = "0")int page,
                                                 @RequestParam(name = "size",required = false,defaultValue = "10")int size){
        return seatService.getAllSeatsForBus(busId,scheduleId,boardingStopOrder,droppingStopOrder,page,size);
    }

    @PutMapping("/api/seat/update/{seatId}/{busId}")
    public void updateSeat(Principal principal, @PathVariable long seatId, @PathVariable long busId,@RequestBody SeatUpdateDto seatUpdateDto){
        seatService.updateSeat(principal.getName(),seatId,busId,seatUpdateDto);
    }

    @DeleteMapping("/api/seat/delete/{seatId}/{busId}")
    public void deleteSeat(Principal principal,@PathVariable long seatId, @PathVariable long busId){
        seatService.deleteSeat(principal.getName(),seatId,busId);
    }

    @GetMapping("/api/seat/filter/bySeatType/{busId}")
    public List<SeatRespV2Dto> filterBySeatType(@PathVariable long busId,
                                                @RequestParam(name = "seatType")SeatType seatType,
                                                @RequestParam(name = "page",required = false,defaultValue = "0")int page,
                                                @RequestParam(name = "size",required = false,defaultValue = "10") int size){
        return seatService.filterBySeatType(seatType,busId,page,size);
    }

    @GetMapping("/api/seats/bus/summary/{busId}")
    public SeatSummaryRespDto getSummarySeats(@PathVariable long busId) {
        return seatService.getSeatSummary(busId);
    }

    @GetMapping("/api/operator/seats/{busId}")
    public List<SeatRespV2Dto> getSeatsForOperator(@PathVariable long busId) {
        return seatService.getSeatsForOperator(busId);
    }
}
