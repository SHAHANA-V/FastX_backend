package com.springboot.FastX_backend.controller;

import com.springboot.FastX_backend.dto.request.ScheduleDto;
import com.springboot.FastX_backend.dto.response.ScheduleRespDto;
import com.springboot.FastX_backend.dto.response.ScheduleRouteRespDto;
import com.springboot.FastX_backend.dto.response.ScheduleUpdateDto;
import com.springboot.FastX_backend.dto.response.SeatRespV2Dto;
import com.springboot.FastX_backend.enums.ScheduleStatus;
import com.springboot.FastX_backend.model.Schedule;
import com.springboot.FastX_backend.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/api/add/schedule")
    public void addSchedule(@Valid @RequestBody ScheduleDto scheduleDto,
                            @RequestParam(name = "bus")long busId,
                            @RequestParam(name = "route")long routeId,
                            Principal principal){
        scheduleService.addSchedule(principal.getName(),scheduleDto,busId,routeId);
    }

    @GetMapping("/api/get/all/schedule")
    public List<ScheduleRespDto> getAllSchedule(@RequestParam(name = "page",required = false,defaultValue = "0")int page,
                                                @RequestParam(name = "size",required = false,defaultValue = "10")int size){
        return scheduleService.getAllSchedule(page,size);
    }

    @GetMapping("/api/get/schedule/operator")
    public List<ScheduleRespDto> getAllScheduleOperator(Principal principal,
                                                        @RequestParam(name = "status",required = false)ScheduleStatus scheduleStatus,
                                                        @RequestParam(name = "page",required = false,defaultValue = "0")int page,
                                                        @RequestParam(name = "size",required = false,defaultValue = "10")int size){
        return scheduleService.getAllScheduleOperator(principal.getName(),scheduleStatus,page,size);
    }

    @PutMapping("/api/update/schedule/{id}")
    public void updateSchedule(Principal principal, @PathVariable long id, @RequestBody ScheduleUpdateDto scheduleUpdateDto){
        scheduleService.updateSchedule(principal.getName(),id,scheduleUpdateDto);
    }

    @GetMapping("/api/schedule/dynamic/filter")
    public List<ScheduleRespDto> getScheduleFilter(@RequestParam(name = "source",required = false)String source,
                                                   @RequestParam(name = "destination",required = false)String destination,
                                                   @RequestParam(name = "journeyDate",required = false)LocalDate journeyDate,
                                                   @RequestParam(name = "arrivalTime",required = false)LocalTime arrivalTime,
                                                   @RequestParam(name = "departureTime",required = false)LocalTime departureTime,
                                                   @RequestParam(name = "busName",required = false)String busName,
                                                   @RequestParam(name = "fare",required = false)Double fare){
        return scheduleService.getScheduleFilter(source,destination,journeyDate,arrivalTime,departureTime,busName,fare);
    }

    @GetMapping("/api/get/seats/{scheduleId}")
    public List<SeatRespV2Dto> getSeatsByScheduleId(@PathVariable long scheduleId,
                                                    @RequestParam(name = "page",required = false,defaultValue = "0")int page,
                                                    @RequestParam(name = "size",required = false,defaultValue = "20")int size)
            {
        return scheduleService.getSeatsByScheduleId(scheduleId,page,size);
    }

    @GetMapping("/api/get/schedule/{scheduleId}")
    public ScheduleRespDto getScheduleRoute(@PathVariable long scheduleId) {
        return scheduleService.getScheduleRoute(scheduleId);
    }

    @GetMapping("/api/operator/schedule/count")
    public long getOperatorScheduleCount(Principal principal) {
        return scheduleService.getOperatorScheduleCount(principal.getName());
    }

    @PutMapping("/api/cancel/schedule/{scheduleId}")
    public void cancelSchedule(
            @PathVariable long scheduleId,
            @RequestParam String reason,
            Principal principal) {

        scheduleService.cancelSchedule(
                principal.getName(), scheduleId, reason);
    }
}
