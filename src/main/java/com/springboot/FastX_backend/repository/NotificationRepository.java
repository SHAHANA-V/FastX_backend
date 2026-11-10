package com.springboot.FastX_backend.repository;

import com.springboot.FastX_backend.dto.response.NotificationRespDto;
import com.springboot.FastX_backend.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findByUser_UsernameOrderByCreatedAtDesc(String username);

    @Query("""
select n from Notification n
where n.user.username = ?1
order by n.createdAt desc
""")
    List<Notification> getNotifications(String username);

    @Query("""
select new com.springboot.FastX_backend.dto.response.NotificationRespDto(n.id,n.title,n.message,n.createdAt,n.isRead)
from Notification n
join n.user u
where u.username=?1
""")
    List<NotificationRespDto> getNotificationForUser(String name);
    @Modifying
    @Query("""
        UPDATE Notification n
        SET n.isRead = true
        WHERE n.id = ?1
        AND n.user.username = ?2
        """)

    int markAsRead(long notificationId, String username);
}
