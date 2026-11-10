package com.springboot.FastX_backend.mapper;

import com.springboot.FastX_backend.dto.response.NotificationRespDto;
import com.springboot.FastX_backend.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    public static NotificationRespDto modelToDto(Notification notification) {

        return new NotificationRespDto(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getCreatedAt(),
                notification.isRead()
        );
    }
}
