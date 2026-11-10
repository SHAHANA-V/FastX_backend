package com.springboot.FastX_backend.mapper;

import com.springboot.FastX_backend.dto.request.ScheduleDto;
import com.springboot.FastX_backend.dto.response.ScheduleRespDto;
import com.springboot.FastX_backend.dto.response.ScheduleRouteRespDto;
import com.springboot.FastX_backend.model.Schedule;
import org.springframework.stereotype.Component;

@Component
public class ScheduleMapper {
    public static Schedule convertDtoToModel(ScheduleDto scheduleDto){
        Schedule schedule = new Schedule();
        schedule.setJourneyDate(scheduleDto.journeyDate());
        schedule.setArrivalTime(scheduleDto.arrivalTime());
        schedule.setDepartureTime(scheduleDto.departureTime());
        schedule.setFare(scheduleDto.fare());
        return schedule;
    }

    public static ScheduleRespDto modelToDto(Schedule schedule){
        return new ScheduleRespDto(
                schedule.getId(),
                schedule.getRoute().getId(),
                schedule.getJourneyDate(),
                schedule.getDepartureTime(),
                schedule.getArrivalTime(),
                schedule.getFare(),
                schedule.getScheduleStatus(),
                schedule.getBus().getId(),
                schedule.getBus().getBusNumber(),
                schedule.getBus().getBusName(),
                schedule.getBus().getBusOperator().getName(),
                schedule.getRoute().getSource(),
                schedule.getRoute().getDestination(),
                schedule.getBus().getBusType(),
                schedule.getBus().getAcType()
        );
    }
}
