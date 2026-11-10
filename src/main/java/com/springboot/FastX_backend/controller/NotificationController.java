package com.springboot.FastX_backend.controller;

import com.springboot.FastX_backend.dto.response.NotificationRespDto;
import com.springboot.FastX_backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/api/passenger/notifications")
    public List<NotificationRespDto> getNotification(Principal principal){
        return notificationService.getNotificationForUser(principal.getName());
    }

    @PutMapping("/api/passenger/notification/read/{notificationId}")
    public void markAsRead(@PathVariable long notificationId,
                           Principal principal) {

        notificationService.markAsRead(principal.getName(), notificationId);
    }
}
