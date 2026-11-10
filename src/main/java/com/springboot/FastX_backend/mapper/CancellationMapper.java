package com.springboot.FastX_backend.mapper;

import com.springboot.FastX_backend.dto.response.CancellationRespDto;
import com.springboot.FastX_backend.model.Booking;
import com.springboot.FastX_backend.model.Cancellation;
import org.springframework.stereotype.Component;

@Component
public class CancellationMapper
{

    public static CancellationRespDto modelToDto(Cancellation cancellation) {

        Booking booking = cancellation.getBooking();

        return new CancellationRespDto(
                cancellation.getId(),
                booking.getId(),
                booking.getPassenger().getName(),
                booking.getPassenger().getPhoneNo(),
                booking.getSchedule().getBus().getBusName(),
                booking.getSchedule().getRoute().getSource(),
                booking.getSchedule().getRoute().getDestination(),
                booking.getSchedule().getJourneyDate(),
                cancellation.getReason(),
                cancellation.getRefundAmount(),
                cancellation.getCancellationStatus(),
                cancellation.getRequestedAt()
        );
    }
}
