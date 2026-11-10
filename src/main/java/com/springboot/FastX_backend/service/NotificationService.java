package com.springboot.FastX_backend.service;

import com.springboot.FastX_backend.dto.response.NotificationRespDto;
import com.springboot.FastX_backend.exception.ResourceNotFoundException;
import com.springboot.FastX_backend.mapper.NotificationMapper;
import com.springboot.FastX_backend.model.Notification;
import com.springboot.FastX_backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    public List<NotificationRespDto> getNotifications(String username) {

        List<Notification> notifications =
                notificationRepository.getNotifications(username);

        return notifications.stream()
                .map(NotificationMapper::modelToDto)
                .toList();
    }

    public List<NotificationRespDto> getNotificationForUser(String name) {
        return notificationRepository.getNotificationForUser(name);
    }

    @Transactional
    public void markAsRead(String username, long notificationId) {

        int updated = notificationRepository.markAsRead(notificationId, username);

        if (updated == 0) {
            throw new ResourceNotFoundException("Notification not found");
        }
    }
}
