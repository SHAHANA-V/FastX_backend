package com.springboot.FastX_backend.mapper;

import com.springboot.FastX_backend.dto.request.BusOperatorDto;
import com.springboot.FastX_backend.model.BusOperator;
import org.springframework.stereotype.Component;

@Component
public class BusOperatorMapper {
    public static BusOperator convertDtoToEntity(BusOperatorDto busOperatorDto){
        BusOperator busOperator = new BusOperator();
        busOperator.setName(busOperatorDto.name());
        busOperator.setPhoneNo(busOperatorDto.phoneNo());
        busOperator.setEmail(busOperatorDto.email());
        return busOperator;
    }
}
