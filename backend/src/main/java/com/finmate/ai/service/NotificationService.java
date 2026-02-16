package com.finmate.ai.service;

import com.finmate.ai.dto.NotificationDTO;
import com.finmate.ai.entity.Notification;
import com.finmate.ai.entity.NotificationType;
import com.finmate.ai.entity.User;
import com.finmate.ai.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final UserService userService;
    
    public void createNotification(User user, String message, NotificationType type) {
        // Prevent duplicate notifications within 1 hour
        List<Notification> recentNotifications = notificationRepository
            .findByUserOrderByCreatedAtDesc(user)
            .stream()
            .filter(n -> n.getCreatedAt().isAfter(java.time.LocalDateTime.now().minusHours(1)))
            .filter(n -> n.getMessage().equals(message))
            .toList();
        
        if (!recentNotifications.isEmpty()) {
            return; // Skip duplicate
        }
        
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setIsRead(false);
        notificationRepository.save(notification);
    }
    
    public List<NotificationDTO> getAllNotifications() {
        User user = userService.getCurrentUser();
        return notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<NotificationDTO> getUnreadNotifications() {
        User user = userService.getCurrentUser();
        return notificationRepository.findByUserAndIsReadFalse(user)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public long getUnreadCount() {
        User user = userService.getCurrentUser();
        return notificationRepository.countByUserAndIsReadFalse(user);
    }
    
    @Transactional
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
    
    @Transactional
    public void markAllAsRead() {
        User user = userService.getCurrentUser();
        List<Notification> notifications = notificationRepository.findByUserAndIsReadFalse(user);
        notifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }
    
    private NotificationDTO mapToDTO(Notification notification) {
        return new NotificationDTO(
                notification.getId(),
                notification.getMessage(),
                notification.getType(),
                notification.getIsRead(),
                notification.getCreatedAt()
        );
    }
}
