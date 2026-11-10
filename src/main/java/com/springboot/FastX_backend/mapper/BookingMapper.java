package com.springboot.FastX_backend.mapper;

import com.springboot.FastX_backend.dto.request.BookingSeatDto;
import com.springboot.FastX_backend.dto.response.*;
import com.springboot.FastX_backend.model.Booking;
import com.springboot.FastX_backend.model.BookingSeat;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class BookingMapper {

    public static BookingDetailsRespDto convertToDetailsDto(
            Booking booking,
            List<BookingSeatRespDto> bookingSeats) {

        return new BookingDetailsRespDto(
                booking.getId(),
                booking.getBookedAt(),
                booking.getBookingStatus(),
                booking.getTotalAmount(),
                booking.getSchedule().getBus().getBusName(),
                booking.getSchedule().getJourneyDate(),
                booking.getSchedule().getDepartureTime(),
                booking.getBoardingStop().getStopName(),
                booking.getDroppingStop().getStopName(),
                bookingSeats
        );
    }
    public static BookingSeat convertDtoToModel(BookingSeatDto dto) {

        BookingSeat bookingSeat = new BookingSeat();

        bookingSeat.setPassengerName(dto.passengerName());
        bookingSeat.setAge(dto.age());
        bookingSeat.setGender(dto.gender());
        bookingSeat.setIdType(dto.idType());
        bookingSeat.setIdDocument(dto.idDocument());

        return bookingSeat;
    }

    public static BookingRespDto convertModelToDto(Booking booking){
        return new BookingRespDto(
                booking.getId(),
                booking.getBookedAt(),
                booking.getBookingStatus(),
                booking.getTotalAmount(),
                booking.getSchedule().getBus().getBusName(),
                booking.getSchedule().getJourneyDate(),
                booking.getSchedule().getDepartureTime(),
                booking.getBoardingStop().getStopName(),
                booking.getDroppingStop().getStopName()
        );
    }

    public static TicketDetailsRespDto convertModelToTicketDto(
            Booking booking,
            PaymentRespDto payment,
            List<BookingSeatRespDto> passengers) {

        return new TicketDetailsRespDto(
                booking.getId(),
                booking.getBookedAt(),
                booking.getBookingStatus(),
                booking.getTotalAmount(),
                booking.getSchedule().getBus().getBusName(),
                booking.getSchedule().getBus().getBusNumber(),
                booking.getSchedule().getBus().getBusType(),
                booking.getSchedule().getBus().getAcType(),
                booking.getSchedule().getBus().getBusOperator().getName(),
                booking.getSchedule().getJourneyDate(),
                booking.getSchedule().getDepartureTime(),
                booking.getSchedule().getArrivalTime(),
                booking.getBoardingStop().getStopName(),
                booking.getDroppingStop().getStopName(),
                payment != null ? payment.paymentMethod() : null,
                payment != null ? payment.paymentStatus() : null,
                payment != null ? payment.paidAt() : null,
                passengers
        );
    }

    public static MyBookingRespDto convertModelToMyBookingDto(
            MyBookingRespDto booking,
            List<BookingSeatRespDto> passengers) {

        return new MyBookingRespDto(
                booking.bookingId(),
                booking.bookedAt(),
                booking.bookingStatus(),
                booking.totalAmount(),
                booking.scheduleStatus(),
                booking.busName(),
                booking.busNumber(),
                booking.journeyDate(),
                booking.departureTime(),
                booking.boardingStop(),
                booking.droppingStop(),
                passengers
        );
    }
}
