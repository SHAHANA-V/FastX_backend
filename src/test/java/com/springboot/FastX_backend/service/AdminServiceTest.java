package com.springboot.FastX_backend.service;
import com.springboot.FastX_backend.dto.request.AdminBookingDataDto;
import com.springboot.FastX_backend.dto.response.*;
import com.springboot.FastX_backend.enums.BookingStatus;
import com.springboot.FastX_backend.enums.PaymentStatus;
import com.springboot.FastX_backend.model.*;
import com.springboot.FastX_backend.repository.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private BusOperatorRepository busOperatorRepository;

    @Mock
    private BusRepository busRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingSeatRepository bookingSeatRepository;

    @Mock
    private PaymentRepository paymentRepository;


    @Test
    public void getDashboardDetailsTest() {

        when(busOperatorRepository.count()).thenReturn(5L);
        when(busRepository.count()).thenReturn(10L);
        when(routeRepository.count()).thenReturn(8L);
        when(bookingRepository.count()).thenReturn(20L);

        AdminDashboardRespDto result = adminService.getDashboardDetails();

        assertEquals(5L, result.totalOperators());
        assertEquals(10L, result.totalBuses());
        assertEquals(8L, result.totalRoutes());
        assertEquals(20L, result.totalBookings());

        verify(busOperatorRepository, times(1)).count();
        verify(busRepository, times(1)).count();
        verify(routeRepository, times(1)).count();
        verify(bookingRepository, times(1)).count();

        verifyNoMoreInteractions(
                busOperatorRepository,
                busRepository,
                routeRepository,
                bookingRepository
        );
    }


    @Test
    public void getAllBusOperatorsTest() {

        User user = new User();
        user.setUsername("albus");
        user.setActive(true);

        BusOperator operator = new BusOperator();
        operator.setId(1L);
        operator.setName("Albus Dumbledore");
        operator.setPhoneNo("9876543210");
        operator.setEmail("albus@gmail.com");
        operator.setUser(user);

        when(busOperatorRepository.findAll(PageRequest.of(0, 5)))
                .thenReturn(new PageImpl<>(List.of(operator)));

        List<AdminBusOperatorRespDto> result =
                adminService.getAllBusOperators(0, 5);

        assertEquals(1, result.size());
        assertEquals("Albus Dumbledore", result.get(0).name());
        assertEquals("albus", result.get(0).username());
        assertTrue(result.get(0).active());

        verify(busOperatorRepository, times(1))
                .findAll(PageRequest.of(0, 5));

        verifyNoMoreInteractions(busOperatorRepository);
    }


    @Test
    public void updateOperatorStatusTest() {

        long operatorId = 1L;

        User user = new User();
        user.setUsername("albus");
        user.setActive(true);

        BusOperator operator = new BusOperator();
        operator.setId(operatorId);
        operator.setUser(user);

        when(busOperatorRepository.findById(operatorId))
                .thenReturn(Optional.of(operator));

        adminService.updateOperatorStatus(operatorId, false);

        assertFalse(user.isActive());

        verify(busOperatorRepository, times(1))
                .findById(operatorId);

        verify(userRepository, times(1))
                .save(user);

        verifyNoMoreInteractions(busOperatorRepository, userRepository);
    }


    @Test
    public void updateOperatorStatusTest_Invalid() {

        long operatorId = 10L;

        when(busOperatorRepository.findById(operatorId))
                .thenReturn(Optional.empty());

        assertThrows(
                com.springboot.FastX_backend.exception.ResourceNotFoundException.class,
                () -> adminService.updateOperatorStatus(operatorId, false)
        );

        verify(busOperatorRepository, times(1))
                .findById(operatorId);

        verifyNoMoreInteractions(busOperatorRepository, userRepository);
    }


    @Test
    public void getAllBookingsForAdminTest() {

        LocalDate journeyDate = LocalDate.of(2026, 11, 9);

        AdminBookingDataDto booking = new AdminBookingDataDto(
                1L,
                "Harry Potter",
                "9876543210",
                "KNP Travels",
                "TN5679",
                "Albus Dumbledore",
                "Chennai",
                "Madurai",
                journeyDate,
                1000.0,
                BookingStatus.CONFIRMED
        );

        Seat seat = new Seat();
        seat.setSeatNumber("1B");

        BookingSeat bookingSeat = new BookingSeat();
        bookingSeat.setSeat(seat);

        Payment payment = new Payment();
        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        when(bookingRepository.getAllBookingsForAdmin(
                "Harry", "Albus", BookingStatus.CONFIRMED,
                PaymentStatus.SUCCESS, journeyDate, PageRequest.of(0, 5)
        )).thenReturn(List.of(booking));

        when(bookingSeatRepository.findByBooking_Id(1L))
                .thenReturn(List.of(bookingSeat));

        when(paymentRepository.findByBooking_Id(1L))
                .thenReturn(Optional.of(payment));

        List<AdminBookingRespDto> result =
                adminService.getAllBookingsForAdmin(
                        0, 5, "Harry", "Albus",
                        BookingStatus.CONFIRMED,
                        PaymentStatus.SUCCESS, journeyDate
                );

        assertEquals(1, result.size());
        assertEquals("Harry Potter", result.get(0).passengerName());
        assertEquals("KNP Travels", result.get(0).busName());
        assertEquals("1B", result.get(0).seatNumbers());
        assertEquals(PaymentStatus.SUCCESS, result.get(0).paymentStatus());

        verify(bookingRepository, times(1))
                .getAllBookingsForAdmin(
                        "Harry", "Albus", BookingStatus.CONFIRMED,
                        PaymentStatus.SUCCESS, journeyDate, PageRequest.of(0, 5)
                );

        verify(bookingSeatRepository, times(1))
                .findByBooking_Id(1L);

        verify(paymentRepository, times(1))
                .findByBooking_Id(1L);

        verifyNoMoreInteractions(
                bookingRepository,
                bookingSeatRepository,
                paymentRepository
        );
    }


    @Test
    public void getMonthlyBookingStatsTest() {

        List<Object[]> data = List.of(
                new Object[]{1, 10L},
                new Object[]{2, 15L},
                new Object[]{3, 20L}
        );

        when(bookingRepository.getMonthlyBookingStatsForAdmin(2026))
                .thenReturn(data);

        List<MonthlyBookingStatsDto> result =
                adminService.getMonthlyBookingStats(2026);

        assertEquals(3, result.size());

        assertEquals("Jan", result.get(0).month());
        assertEquals(10L, result.get(0).totalBookings());

        assertEquals("Feb", result.get(1).month());
        assertEquals(15L, result.get(1).totalBookings());

        assertEquals("Mar", result.get(2).month());
        assertEquals(20L, result.get(2).totalBookings());

        verify(bookingRepository, times(1))
                .getMonthlyBookingStatsForAdmin(2026);

        verifyNoMoreInteractions(bookingRepository);
    }


    @Test
    public void getBookingStatusStatsTest() {

        List<Object[]> data = List.of(
                new Object[]{BookingStatus.CONFIRMED, 10L},
                new Object[]{BookingStatus.CANCELLED, 5L},
                new Object[]{BookingStatus.PENDING, 3L}
        );

        when(bookingRepository.getBookingStatusStatsForAdmin())
                .thenReturn(data);

        List<BookingStatusStatsDto> result =
                adminService.getBookingStatusStats();

        assertEquals(3, result.size());

        assertEquals(BookingStatus.CONFIRMED, result.get(0).bookingStatus());
        assertEquals(10L, result.get(0).totalBookings());

        assertEquals(BookingStatus.CANCELLED, result.get(1).bookingStatus());
        assertEquals(5L, result.get(1).totalBookings());

        assertEquals(BookingStatus.PENDING, result.get(2).bookingStatus());
        assertEquals(3L, result.get(2).totalBookings());

        verify(bookingRepository, times(1))
                .getBookingStatusStatsForAdmin();

        verifyNoMoreInteractions(bookingRepository);
    }
}
