package com.springboot.FastX_backend.mapper;

import com.springboot.FastX_backend.dto.response.AdminBusOperatorRespDto;
import com.springboot.FastX_backend.model.BusOperator;
import org.springframework.stereotype.Component;

@Component
public class AdminMapper {
    public static AdminBusOperatorRespDto modelToDto(BusOperator busOperator) {
        return new AdminBusOperatorRespDto(
                busOperator.getId(),
                busOperator.getName(),
                busOperator.getPhoneNo(),
                busOperator.getEmail(),
                busOperator.getUser().getUsername(),
                busOperator.getUser().isActive()
        );
    }
}
