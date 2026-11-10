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
import com.springboot.FastX_backend.model.Bus;
import com.springboot.FastX_backend.model.Seat;
import com.springboot.FastX_backend.repository.BookingSeatRepository;
import com.springboot.FastX_backend.repository.BusRepository;
import com.springboot.FastX_backend.repository.SeatRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Answers;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SeatServiceTest {
    @Mock
    private BookingSeatRepository bookingSeatRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private BusRepository busRepository;

    @InjectMocks
    private SeatService seatService;


    // 1. Add seat successfully
    @Test
    void addSeatTest() {

        Bus bus = mock(Bus.class, Answers.RETURNS_DEEP_STUBS);

        when(busRepository.findById(1L))
                .thenReturn(Optional.of(bus));

        when(bus.getBusOperator().getUser().getUsername())
                .thenReturn("albus");

        when(bus.getTotalSeats()).thenReturn(30);

        when(seatRepository.countSeatBySeatNumberAndBusId("1A", 1L))
                .thenReturn(0L);

        when(seatRepository.countSeatsByBusId(1L))
                .thenReturn(10L);

        SeatDto dto = new SeatDto("1A", SeatType.WINDOW);

        seatService.addSeat("albus", dto, 1L);

        ArgumentCaptor<Seat> captor =
                ArgumentCaptor.forClass(Seat.class);

        verify(seatRepository).save(captor.capture());

        Seat savedSeat = captor.getValue();

        assertEquals("1A", savedSeat.getSeatNumber());
        assertEquals(SeatType.WINDOW, savedSeat.getSeatType());
        assertSame(bus, savedSeat.getBus());
    }


    // 2. Add seat when bus does not exist
    @Test
    void addSeatBusNotFoundTest() {

        when(busRepository.findById(1L))
                .thenReturn(Optional.empty());

        SeatDto dto = new SeatDto("1A", SeatType.WINDOW);

        assertThrows(ResourceNotFoundException.class, () ->
                seatService.addSeat("albus", dto, 1L));

        verify(seatRepository, never()).save(any(Seat.class));
    }


    // 3. Add seat when operator does not own the bus
    @Test
    void addSeatOwnershipTest() {

        Bus bus = mock(Bus.class, Answers.RETURNS_DEEP_STUBS);

        when(busRepository.findById(1L))
                .thenReturn(Optional.of(bus));

        when(bus.getBusOperator().getUser().getUsername())
                .thenReturn("harry");

        SeatDto dto = new SeatDto("1A", SeatType.WINDOW);

        assertThrows(OwnerShipException.class, () ->
                seatService.addSeat("albus", dto, 1L));

        verify(seatRepository, never()).save(any(Seat.class));
    }


    // 4. Add seat when seat number already exists
    @Test
    void addSeatDuplicateTest() {

        Bus bus = mock(Bus.class, Answers.RETURNS_DEEP_STUBS);

        when(busRepository.findById(1L))
                .thenReturn(Optional.of(bus));

        when(bus.getBusOperator().getUser().getUsername())
                .thenReturn("albus");

        when(seatRepository.countSeatBySeatNumberAndBusId("1A", 1L))
                .thenReturn(1L);

        SeatDto dto = new SeatDto("1A", SeatType.WINDOW);

        assertThrows(RuntimeException.class, () ->
                seatService.addSeat("albus", dto, 1L));

        verify(seatRepository, never()).save(any(Seat.class));
    }


    // 5. Add seats in bulk successfully
    @Test
    void addSeatBulkTest() {

        Bus bus = mock(Bus.class, Answers.RETURNS_DEEP_STUBS);

        when(busRepository.findById(1L))
                .thenReturn(Optional.of(bus));

        when(bus.getBusOperator().getUser().getUsername())
                .thenReturn("albus");

        when(bus.getTotalSeats()).thenReturn(30);

        when(seatRepository.countSeatsByBusId(1L))
                .thenReturn(5L);

        when(seatRepository.countSeatBySeatNumberAndBusId(anyString(), eq(1L)))
                .thenReturn(0L);

        List<SeatDto> dtoList = List.of(
                new SeatDto("1A", SeatType.WINDOW),
                new SeatDto("1B", SeatType.AISLE)
        );

        seatService.addSeatBulk("albus", dtoList, 1L);

        ArgumentCaptor<List<Seat>> captor =
                ArgumentCaptor.forClass(List.class);

        verify(seatRepository).saveAll(captor.capture());

        List<Seat> savedSeats = captor.getValue();

        assertEquals(2, savedSeats.size());
        assertEquals("1A", savedSeats.get(0).getSeatNumber());
        assertEquals("1B", savedSeats.get(1).getSeatNumber());

        assertSame(bus, savedSeats.get(0).getBus());
        assertSame(bus, savedSeats.get(1).getBus());
    }


    // 6. Bulk add when duplicate seat numbers exist in request
    @Test
    void addSeatBulkDuplicateTest() {

        Bus bus = mock(Bus.class, Answers.RETURNS_DEEP_STUBS);

        when(busRepository.findById(1L))
                .thenReturn(Optional.of(bus));

        when(bus.getBusOperator().getUser().getUsername())
                .thenReturn("albus");

        when(seatRepository.countSeatsByBusId(1L))
                .thenReturn(5L);

        List<SeatDto> dtoList = List.of(
                new SeatDto("1A", SeatType.WINDOW),
                new SeatDto("1a", SeatType.AISLE)
        );

        assertThrows(RuntimeException.class, () ->
                seatService.addSeatBulk("albus", dtoList, 1L));

        verify(seatRepository, never()).saveAll(anyList());
    }


    // 7. Get seat in a bus successfully
    @Test
    void getSeatInABusTest() {

        Bus bus = mock(Bus.class);

        SeatRespDto expected = mock(SeatRespDto.class);

        when(busRepository.findById(1L))
                .thenReturn(Optional.of(bus));

        when(seatRepository.getSeatInABus("1A", 1L))
                .thenReturn(Optional.of(expected));

        SeatRespDto result = seatService.getSeatInABus("1A", 1L);

        assertSame(expected, result);

        verify(seatRepository).getSeatInABus("1A", 1L);
    }


    // 8. Get seat when seat number does not exist
    @Test
    void getSeatInABusNotFoundTest() {

        Bus bus = mock(Bus.class);

        when(busRepository.findById(1L))
                .thenReturn(Optional.of(bus));

        when(seatRepository.getSeatInABus("10A", 1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                seatService.getSeatInABus("10A", 1L));
    }


    // 9. Get all seats for a bus with booking status
    @Test
    void getAllSeatsForBusTest() {

        Bus bus = mock(Bus.class);

        SeatRespV2Dto seatDto =
                new SeatRespV2Dto(1L, "1A", SeatType.WINDOW, false);

        Seat seat = mock(Seat.class);

        when(busRepository.findById(1L))
                .thenReturn(Optional.of(bus));

        when(seatRepository.getAllSeatsForBus(1L, PageRequest.of(0, 10)))
                .thenReturn(List.of(seatDto));

        when(seatRepository.findByBusIdAndSeatNumber(1L, "1A"))
                .thenReturn(Optional.of(seat));

        when(seat.getId()).thenReturn(1L);

        when(bookingSeatRepository.countSeatConflicts(
                eq(1L),
                eq(5L),
                eq(BookingStatus.CONFIRMED),
                eq(BookingStatus.PENDING),
                any(),
                eq(10),
                eq(2)
        )).thenReturn(1L);

        List<SeatRespV2Dto> result =
                seatService.getAllSeatsForBus(1L, 5L, 2, 10, 0, 10);

        assertEquals(1, result.size());
        assertEquals("1A", result.get(0).seatNumber());
        assertTrue(result.get(0).booked());
    }


    // 10. Update seat successfully
    @Test
    void updateSeatTest() {

        Bus bus = mock(Bus.class, Answers.RETURNS_DEEP_STUBS);

        Seat seat = mock(Seat.class, Answers.RETURNS_DEEP_STUBS);

        when(busRepository.findById(1L))
                .thenReturn(Optional.of(bus));

        when(bus.getBusOperator().getUser().getUsername())
                .thenReturn("albus");

        when(seatRepository.findById(1L))
                .thenReturn(Optional.of(seat));

        when(seat.getBus().getId()).thenReturn(1L);

        when(bookingSeatRepository.existsBySeatId(1L))
                .thenReturn(false);

        SeatUpdateDto dto = new SeatUpdateDto("2A", SeatType.AISLE);

        seatService.updateSeat("albus", 1L, 1L, dto);

        verify(seat).setSeatNumber("2A");
        verify(seat).setSeatType(SeatType.AISLE);
        verify(seatRepository).save(seat);
    }


    // 11. Update seat when booking history exists
    @Test
    void updateSeatBookingHistoryTest() {

        Bus bus = mock(Bus.class, Answers.RETURNS_DEEP_STUBS);

        Seat seat = mock(Seat.class, Answers.RETURNS_DEEP_STUBS);

        when(busRepository.findById(1L))
                .thenReturn(Optional.of(bus));

        when(bus.getBusOperator().getUser().getUsername())
                .thenReturn("albus");

        when(seatRepository.findById(1L))
                .thenReturn(Optional.of(seat));

        when(seat.getBus().getId()).thenReturn(1L);

        when(bookingSeatRepository.existsBySeatId(1L))
                .thenReturn(true);

        SeatUpdateDto dto = new SeatUpdateDto("2A", SeatType.AISLE);

        assertThrows(RuntimeException.class, () ->
                seatService.updateSeat("albus", 1L, 1L, dto));

        verify(seatRepository, never()).save(any(Seat.class));
    }


    // 12. Delete seat successfully
    @Test
    void deleteSeatTest() {

        Bus bus = mock(Bus.class, Answers.RETURNS_DEEP_STUBS);

        Seat seat = mock(Seat.class, Answers.RETURNS_DEEP_STUBS);

        when(busRepository.findById(1L))
                .thenReturn(Optional.of(bus));

        when(bus.getBusOperator().getUser().getUsername())
                .thenReturn("albus");

        when(seatRepository.findById(1L))
                .thenReturn(Optional.of(seat));

        when(seat.getBus().getId()).thenReturn(1L);

        when(bookingSeatRepository.existsBySeatId(1L))
                .thenReturn(false);

        seatService.deleteSeat("albus", 1L, 1L);

        verify(seatRepository).delete(seat);
    }


    // 13. Delete seat when booking history exists
    @Test
    void deleteSeatBookingHistoryTest() {

        Bus bus = mock(Bus.class, Answers.RETURNS_DEEP_STUBS);

        Seat seat = mock(Seat.class, Answers.RETURNS_DEEP_STUBS);

        when(busRepository.findById(1L))
                .thenReturn(Optional.of(bus));

        when(bus.getBusOperator().getUser().getUsername())
                .thenReturn("albus");

        when(seatRepository.findById(1L))
                .thenReturn(Optional.of(seat));

        when(seat.getBus().getId()).thenReturn(1L);

        when(bookingSeatRepository.existsBySeatId(1L))
                .thenReturn(true);

        assertThrows(RuntimeException.class, () ->
                seatService.deleteSeat("albus", 1L, 1L));

        verify(seatRepository, never()).delete(any(Seat.class));
    }


    // 14. Filter seats by seat type
    @Test
    void filterBySeatTypeTest() {

        Bus bus = mock(Bus.class);

        Seat seat = mock(Seat.class);

        when(busRepository.findById(1L))
                .thenReturn(Optional.of(bus));

        when(seatRepository.findBySeatType(
                SeatType.WINDOW, PageRequest.of(0, 5)))
                .thenReturn(List.of(seat));

        when(seat.getId()).thenReturn(1L);
        when(seat.getSeatNumber()).thenReturn("1A");
        when(seat.getSeatType()).thenReturn(SeatType.WINDOW);

        List<SeatRespV2Dto> result =
                seatService.filterBySeatType(SeatType.WINDOW, 1L, 0, 5);

        assertEquals(1, result.size());
        assertEquals("1A", result.get(0).seatNumber());
        assertEquals(SeatType.WINDOW, result.get(0).seatType());
        assertFalse(result.get(0).booked());
    }


    // 15. Get seat summary
    @Test
    void getSeatSummaryTest() {

        Bus bus = mock(Bus.class);

        when(busRepository.findById(1L))
                .thenReturn(Optional.of(bus));

        when(bus.getBusName()).thenReturn("KNP Travels");
        when(bus.getBusNumber()).thenReturn("TN5679");
        when(bus.getTotalSeats()).thenReturn(30);

        when(seatRepository.countSeatsByBusId(1L))
                .thenReturn(20L);

        when(seatRepository.countSeatsByBusIdAndSeatType(1L, SeatType.WINDOW))
                .thenReturn(10L);

        when(seatRepository.countSeatsByBusIdAndSeatType(1L, SeatType.AISLE))
                .thenReturn(10L);

        SeatSummaryRespDto result = seatService.getSeatSummary(1L);

        assertEquals("KNP Travels", result.busName());
        assertEquals("TN5679", result.busNumber());
        assertEquals(30, result.totalSeats());
        assertEquals(20L, result.createdSeats());
        assertEquals(10L, result.windowSeats());
        assertEquals(10L, result.aisleSeats());
    }


    // 16. Get seats for operator
    @Test
    void getSeatsForOperatorTest() {

        SeatRespV2Dto seatDto =
                new SeatRespV2Dto(1L, "1A", SeatType.WINDOW, false);

        when(seatRepository.getAllSeatsForBus(1L, PageRequest.of(0, 100)))
                .thenReturn(List.of(seatDto));

        List<SeatRespV2Dto> result = seatService.getSeatsForOperator(1L);

        assertEquals(1, result.size());
        assertEquals("1A", result.get(0).seatNumber());

        verify(seatRepository).getAllSeatsForBus(
                1L, PageRequest.of(0, 100));
    }
}
