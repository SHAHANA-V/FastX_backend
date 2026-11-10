package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.dto.request.SeatDto;
import com.springboot.FastX_backend.dto.request.SeatUpdateDto;
import com.springboot.FastX_backend.dto.response.SeatRespDto;
import com.springboot.FastX_backend.dto.response.SeatRespV2Dto;
import com.springboot.FastX_backend.dto.response.SeatSummaryRespDto;
import com.springboot.FastX_backend.enums.BookingStatus;
import com.springboot.FastX_backend.enums.SeatType;
import com.springboot.FastX_backend.exception.OwnerShipException;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.mapper.SeatMapper;
import com.springboot.FastX_backend.model.Bus;
import com.springboot.FastX_backend.model.Seat;
import com.springboot.FastX_backend.repository.BookingSeatRepository;
import com.springboot.FastX_backend.repository.BusRepository;
import com.springboot.FastX_backend.repository.SeatRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final BookingSeatRepository bookingSeatRepository;
    private final SeatRepository seatRepository;
    private final BusRepository busRepository;

    public void addSeat(String busOperatorName, @Valid SeatDto seatDto, long busId) {
        //check whether the bus exists
        Bus bus = busRepository.findById(busId).orElseThrow(()->new ResourceNotFoundException("Bus Id Invalid..."));

        //see the operator has access on the bus
        if(!(bus.getBusOperator().getUser().getUsername().equals(busOperatorName))){
            throw new OwnerShipException("You cannot access the bus");
        }

        //check whether the seat already exists
        if (seatRepository.countSeatBySeatNumberAndBusId(seatDto.seatNumber(), busId) > 0) {
            throw new RuntimeException("Seat number already exists for this bus");
        }

        //check the capacity of the bus [not creating more than that]
        if (seatRepository.countSeatsByBusId(busId) >= bus.getTotalSeats()) {
            throw new RuntimeException("Bus has reached its maximum seat capacity");
        }
        //prepare the seat
        Seat seat = SeatMapper.convertDtoToModel(seatDto);

        //attach bus to seat
        seat.setBus(bus);

        //save the seat
        seatRepository.save(seat);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addSeatBulk(String busOperatorName, @Valid List<SeatDto> seatDtoList, long busId) {
        // Check whether the bus exists
        Bus bus = busRepository.findById(busId).orElseThrow(() ->new ResourceNotFoundException("Bus Id Invalid..."));

        // Check whether the operator owns the bus
        if (!bus.getBusOperator().getUser().getUsername().equals(busOperatorName)) {
            throw new OwnerShipException("You cannot access the bus");
        }

        // Check whether the seat count exceeds bus capacity
        long existingSeats = seatRepository.countSeatsByBusId(busId);

        if (existingSeats + seatDtoList.size() > bus.getTotalSeats()) {
            throw new RuntimeException( "Adding these seats exceeds the bus capacity");
        }

        // Check for duplicate seat numbers within this request
        long distinctSeatNumbers = seatDtoList.stream()
                .map(dto -> dto.seatNumber().trim().toUpperCase())
                .distinct()
                .count();

        if (distinctSeatNumbers != seatDtoList.size()) {
            throw new RuntimeException(
                    "Duplicate seat numbers in the request");
        }

        // Check whether seat numbers already exist for this bus
        for (SeatDto dto : seatDtoList) {
            if (seatRepository.countSeatBySeatNumberAndBusId(dto.seatNumber(), busId) > 0) {
                throw new RuntimeException("Seat number already exists: " + dto.seatNumber());
            }
        }

        // Convert DTOs into Seat entities and attach the bus
        List<Seat> seatList = seatDtoList.stream()
                .map(dto -> {
                    Seat seat = SeatMapper.convertDtoToModel(dto);
                    seat.setBus(bus);
                    return seat;
                }).toList();

        // Save all seats
        seatRepository.saveAll(seatList);
    }

    public SeatRespDto getSeatInABus(String seatNumber, long busId) {
        Bus bus = busRepository.findById(busId).orElseThrow(()->new ResourceNotFoundException("Bus Id Invalid..."));
        return seatRepository.getSeatInABus(seatNumber, busId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Seat number is not found"));
    }

    public List<SeatRespV2Dto> getAllSeatsForBus(long busId, long scheduleId, int boardingStopOrder, int droppingStopOrder, int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        Bus bus = busRepository.findById(busId).orElseThrow(()->new ResourceNotFoundException("Bus Id Invalid..."));
        List<SeatRespV2Dto> seatList = seatRepository.getAllSeatsForBus(busId, pageable);

        return seatList.stream().map(seat -> {

            long count = bookingSeatRepository.countSeatConflicts(
                    seatRepository.findByBusIdAndSeatNumber(busId, seat.seatNumber())
                            .orElseThrow(() -> new ResourceNotFoundException("Seat not found"))
                            .getId(),
                    scheduleId,
                    BookingStatus.CONFIRMED,
                    BookingStatus.PENDING,
                    Instant.now(),
                    droppingStopOrder,
                    boardingStopOrder
            );
            return new SeatRespV2Dto(
                    seat.id(),
                    seat.seatNumber(),
                    seat.seatType(),
                    count > 0
            );

        }).toList();
    }


    public void updateSeat(String busOperatorName, long seatId, long busId, SeatUpdateDto seatUpdateDto) {
        //check whether bus exist
        Bus bus = busRepository.findById(busId).orElseThrow(()->new ResourceNotFoundException("Bus Id Invalid..."));

        //ownership check
        if(!(bus.getBusOperator().getUser().getUsername().equals(busOperatorName))){
            throw new OwnerShipException("You are not allowed to update the bus seat");
        }


        //check whether the seat exists or not
        Seat seat = seatRepository.findById(seatId).orElseThrow(()->new ResourceNotFoundException("Seat Id Invalid..."));

        if (seat.getBus().getId() != busId) {
            throw new OwnerShipException("This seat does not belong to this bus");
        }
        if (bookingSeatRepository.existsBySeatId(seatId)) {
            throw new RuntimeException("Cannot update a seat that has booking history");
        }

        boolean flag = false;

        if(!(seatUpdateDto.seatNumber()==null) && !(seatUpdateDto.seatNumber().trim().isEmpty())){
            seat.setSeatNumber(seatUpdateDto.seatNumber());
            flag = true;
        }

        if(!(seatUpdateDto.seatType()==null)){
            seat.setSeatType(seatUpdateDto.seatType());
            flag=true;
        }

        if(!flag){
            throw new RuntimeException("No valid fields are provided for update");
        }
        seatRepository.save(seat);
    }

    public void deleteSeat(String name, long seatId, long busId) {
        //check whether bus exist
        Bus bus = busRepository.findById(busId).orElseThrow(()->new ResourceNotFoundException("Bus Id Invalid..."));

        //ownership check
        if(!(bus.getBusOperator().getUser().getUsername().equals(name))){
            throw new OwnerShipException("You are not allowed to update the bus seat");
        }

        //check whether the seat exists or not
        Seat seat = seatRepository.findById(seatId).orElseThrow(()->new ResourceNotFoundException("Seat Id Invalid..."));

        if (seat.getBus().getId() != busId) {
            throw new OwnerShipException("This seat does not belong to this bus");
        }
        if (bookingSeatRepository.existsBySeatId(seatId)) {
            throw new RuntimeException("Cannot update a seat that has booking history");
        }
        seatRepository.delete(seat);
    }

    public List<SeatRespV2Dto> filterBySeatType(SeatType seatType, long busId, int page, int size) {
        Pageable pageable=PageRequest.of(page,size);
        busRepository.findById(busId).orElseThrow(()->new ResourceNotFoundException("Bus Id Invalid..."));
        List<Seat> list = seatRepository.findBySeatType(seatType,pageable);
        return list.stream().map(SeatMapper::convertModelToDtoV2).toList();
    }

    public SeatSummaryRespDto getSeatSummary(long busId) {
        // Check whether the bus exists
        Bus bus = busRepository.findById(busId).orElseThrow(() -> new ResourceNotFoundException("Bus Id Invalid"));

        // Count all created seats
        long createdSeats = seatRepository.countSeatsByBusId(busId);

        // Count window seats
        long windowSeats = seatRepository.countSeatsByBusIdAndSeatType(busId, SeatType.WINDOW);
        // Count aisle seats
        long aisleSeats = seatRepository.countSeatsByBusIdAndSeatType(busId, SeatType.AISLE);

        return SeatMapper.convertSummaryModelToDto(bus,createdSeats,windowSeats,aisleSeats);

    }

    public List<SeatRespV2Dto> getSeatsForOperator(long busId) {
        return seatRepository.getAllSeatsForBus(
                busId,
                PageRequest.of(0, 100)
        );
    }
}
