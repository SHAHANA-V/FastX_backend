package com.springboot.FastX_backend.mapper;

import com.springboot.FastX_backend.dto.request.RouteDto;
import com.springboot.FastX_backend.model.Route;

public class RouteMapper {
    public static Route convertDtoToModel(RouteDto routeDto){
        Route route = new Route();
        route.setSource(routeDto.source());
        route.setDestination(routeDto.destination());
        return route;
    }
}
