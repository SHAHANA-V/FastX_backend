package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.dto.request.BusDto;
import com.springboot.FastX_backend.dto.request.BusUpdateDto;
import com.springboot.FastX_backend.dto.response.BusRespForAdminDto;
import com.springboot.FastX_backend.dto.response.BusRespForOptDto;
import com.springboot.FastX_backend.enums.AcType;
import com.springboot.FastX_backend.enums.BusStatus;
import com.springboot.FastX_backend.enums.BusType;
import com.springboot.FastX_backend.exception.OwnerShipException;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.mapper.BusMapper;
import com.springboot.FastX_backend.model.Bus;
import com.springboot.FastX_backend.model.BusOperator;
import com.springboot.FastX_backend.repository.BusOperatorRepository;
import com.springboot.FastX_backend.repository.BusRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusService {

    private final BusRepository busRepository;
    private final BusOperatorRepository busOperatorRepository;

    public long addBus(String operatorUsername, @Valid BusDto busDto) {
        //Step 1: fetch the bus operator from db
        BusOperator busOperator = busOperatorRepository.getBusOperatorByUsername(operatorUsername);

        //Step 2:Prepare the bus Object
        Bus bus = BusMapper.convertDtoToModel(busDto);

        //Step 3:Attach the busOperator to bus
        bus.setBusOperator(busOperator);

        //Step 4:save in db
        bus =busRepository.save(bus);

        return bus.getId();
    }

    public Bus getBusOperatorById(long id) {
        return busRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Bus Id does not exist"));
    }

    public List<BusRespForOptDto> getDynamicFilter(String busOperatorName, int page, int size, BusType busType, BusStatus busStatus, AcType acType) {
        Pageable pageable = PageRequest.of(page,size);
        return busRepository.getDynamicFilter(busOperatorName,busType,busStatus,acType,pageable);
    }

    public void updateBus(String busOperatorName, long id, @Valid BusUpdateDto busUpdateDto) {
        //Step 1: Fetch the bus using id from the db
        Bus bus = busRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Bus Id is invalid..."));

        //Step 2:Check whether the bus operator has the access to update the bus details
        if(!(bus.getBusOperator().getUser().getUsername().equals(busOperatorName))){
            throw new OwnerShipException("Bus not found for this operator");
        }

        boolean flag = false;

        //update bus name
        if(!(busUpdateDto.busName()==null) && !(busUpdateDto.busName().trim().isEmpty())){
            bus.setBusName(busUpdateDto.busName());
            flag = true;
        }

        //update ac type of bus
        if(!(busUpdateDto.acType()==null)){
            bus.setAcType(busUpdateDto.acType());
            flag = true;
        }

        //update status
        if(!(busUpdateDto.busStatus()==null)){
            bus.setBusStatus(busUpdateDto.busStatus());
            flag = true;
        }

        if(!flag){
            throw new RuntimeException("No valid fields provided for update");
        }

        //save the updates
        busRepository.save(bus);
    }


    public List<BusRespForAdminDto> getAllBusForAdmin(
            int page,
            int size,
            String search,
            String operatorName,
            BusType busType,
            AcType acType,
            BusStatus busStatus) {

        Pageable pageable = PageRequest.of(page, size);

        return busRepository.getAllBusForAdmin(
                search,
                operatorName,
                busType,
                acType,
                busStatus,
                pageable
        );
    }

    public long getOperatorBusCount(String name) {
        return busRepository.getOperatorBusCount(name);
    }
}
