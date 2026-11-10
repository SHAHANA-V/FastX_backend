package com.springboot.FastX_backend.mapper;

import com.springboot.FastX_backend.dto.request.SeatDto;
import com.springboot.FastX_backend.dto.response.SeatRespDto;
import com.springboot.FastX_backend.dto.response.SeatRespV2Dto;
import com.springboot.FastX_backend.dto.response.SeatSummaryRespDto;
import com.springboot.FastX_backend.model.Bus;
import com.springboot.FastX_backend.model.Seat;
import com.springboot.FastX_backend.repository.SeatRepository;
import org.springframework.stereotype.Component;

@Component
public class SeatMapper {
    public static Seat convertDtoToModel(SeatDto seatDto){
        Seat seat = new Seat();
        seat.setSeatNumber(seatDto.seatNumber());
        seat.setSeatType(seatDto.seatType());
        return seat;
    }

    public  static SeatRespDto convertModelToDto(Seat seat){
        return new SeatRespDto(
                seat.getSeatNumber(),
                seat.getSeatType(),
                seat.getBus().getBusName(),
                seat.getBus().getBusNumber()
        );
    }
    public static SeatRespV2Dto convertModelToDtoV2(Seat seat) {
        return new SeatRespV2Dto(
                seat.getId(),
                seat.getSeatNumber(),
                seat.getSeatType(),
                false
        );
    }

    public  static SeatRespV2Dto convertModelToDtoV2(Seat seat,boolean booked){
        return new SeatRespV2Dto(
                seat.getId(),
                seat.getSeatNumber(),
                seat.getSeatType(),
                booked
        );
    }
    public static SeatSummaryRespDto convertSummaryModelToDto(
            Bus bus,
            long createdSeats,
            long windowSeats,
            long aisleSeats) {

        return new SeatSummaryRespDto(
                bus.getBusName(),
                bus.getBusNumber(),
                bus.getTotalSeats(),
                createdSeats,
                windowSeats,
                aisleSeats
        );
    }

}
