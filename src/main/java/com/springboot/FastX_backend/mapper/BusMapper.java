package com.springboot.FastX_backend.mapper;

import com.springboot.FastX_backend.dto.request.BusDto;
import com.springboot.FastX_backend.model.Bus;
import org.springframework.stereotype.Component;

@Component
public class BusMapper {
    public static Bus convertDtoToModel(BusDto busDto){
        Bus bus = new Bus();
        bus.setBusName(busDto.busName());
        bus.setBusNumber(busDto.busNumber());
        bus.setBusType(busDto.busType());
        bus.setTotalSeats(busDto.totalSeats());
        bus.setAcType(busDto.acType());
        bus.setBusStatus(busDto.busStatus());
        return bus;

    }
}
