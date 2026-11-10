package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.dto.response.NotificationRespDto;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.model.Notification;
import com.springboot.FastX_backend.model.User;
import com.springboot.FastX_backend.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;


    // 1. Get notifications successfully
    @Test
    void getNotificationsTest() {

        User user = new User();
        user.setUsername("harry");

        Notification notification = new Notification();
        notification.setId(1L);
        notification.setTitle("Booking Confirmed");
        notification.setMessage("Your booking is confirmed");
        notification.setCreatedAt(Instant.now());
        notification.setRead(false);
        notification.setUser(user);

        when(notificationRepository.getNotifications("harry"))
                .thenReturn(List.of(notification));

        List<NotificationRespDto> result =
                notificationService.getNotifications("harry");

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("Booking Confirmed", result.get(0).title());
        assertEquals("Your booking is confirmed", result.get(0).message());
        assertFalse(result.get(0).isRead());

        verify(notificationRepository, times(1))
                .getNotifications("harry");
    }


    // 2. Return empty list when user has no notifications
    @Test
    void getNotificationsEmptyTest() {

        when(notificationRepository.getNotifications("harry"))
                .thenReturn(List.of());

        List<NotificationRespDto> result =
                notificationService.getNotifications("harry");

        assertTrue(result.isEmpty());

        verify(notificationRepository, times(1))
                .getNotifications("harry");
    }


    // 3. Get notification DTOs directly from repository
    @Test
    void getNotificationForUserTest() {

        NotificationRespDto dto = new NotificationRespDto(
                1L,
                "Payment Successful",
                "Your payment was successful",
                Instant.now(),
                false
        );

        when(notificationRepository.getNotificationForUser("harry"))
                .thenReturn(List.of(dto));

        List<NotificationRespDto> result =
                notificationService.getNotificationForUser("harry");

        assertEquals(1, result.size());
        assertEquals("Payment Successful", result.get(0).title());
        assertEquals("Your payment was successful", result.get(0).message());

        verify(notificationRepository, times(1))
                .getNotificationForUser("harry");
    }


    // 4. Mark notification as read successfully
    @Test
    void markAsReadTest() {

        when(notificationRepository.markAsRead(1L, "harry"))
                .thenReturn(1);

        assertDoesNotThrow(() ->
                notificationService.markAsRead("harry", 1L));

        verify(notificationRepository, times(1))
                .markAsRead(1L, "harry");
    }


    // 5. Notification not found while marking as read
    @Test
    void markAsReadNotFoundTest() {

        when(notificationRepository.markAsRead(1L, "harry"))
                .thenReturn(0);

        assertThrows(ResourceNotFoundException.class,
                () -> notificationService.markAsRead("harry", 1L));

        verify(notificationRepository, times(1))
                .markAsRead(1L, "harry");
    }
}
