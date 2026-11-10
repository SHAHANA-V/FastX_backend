package com.springboot.FastX_backend.mapper;

import com.springboot.FastX_backend.dto.request.RouteStopDto;
import com.springboot.FastX_backend.dto.response.RouteStopRespDto;
import com.springboot.FastX_backend.model.RouteStop;
import org.springframework.stereotype.Component;

@Component
public class RouteStopMapper {
    public static RouteStop covertDtoToModel(RouteStopDto routeStopDto){
        RouteStop routeStop = new RouteStop();
        routeStop.setStopName(routeStopDto.stopName());
        routeStop.setCityName(routeStopDto.cityName());
        routeStop.setStopType(routeStopDto.stopType());
        routeStop.setStopOrder(routeStopDto.stopOrder());
        return routeStop;
    }

    public static RouteStopRespDto covertModelToDto(RouteStop routeStop){
        return new RouteStopRespDto(
                routeStop.getId(),
                routeStop.getStopName(),
                routeStop.getCityName(),
                routeStop.getStopType(),
                routeStop.getStopOrder()
        );
    }
}
