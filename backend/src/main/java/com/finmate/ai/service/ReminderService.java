package com.finmate.ai.service;

import com.finmate.ai.dto.ReminderDTO;
import com.finmate.ai.dto.ReminderRequest;
import com.finmate.ai.entity.NotificationType;
import com.finmate.ai.entity.Reminder;
import com.finmate.ai.entity.User;
import com.finmate.ai.exception.ResourceNotFoundException;
import com.finmate.ai.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReminderService {
    
    private final ReminderRepository reminderRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    
    public ReminderDTO createReminder(ReminderRequest request) {
        User user = userService.getCurrentUser();
        
        Reminder reminder = new Reminder();
        reminder.setUser(user);
        reminder.setTitle(request.getTitle());
        reminder.setMessage(request.getMessage());
        reminder.setReminderDate(request.getReminderDate());
        reminder.setIsCompleted(false);
        
        reminderRepository.save(reminder);
        return mapToDTO(reminder);
    }
    
    public ReminderDTO updateReminder(Long id, ReminderRequest request) {
        User user = userService.getCurrentUser();
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found"));
        
        if (!reminder.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Reminder not found");
        }
        
        reminder.setTitle(request.getTitle());
        reminder.setMessage(request.getMessage());
        reminder.setReminderDate(request.getReminderDate());
        
        reminderRepository.save(reminder);
        return mapToDTO(reminder);
    }
    
    public void deleteReminder(Long id) {
        User user = userService.getCurrentUser();
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found"));
        
        if (!reminder.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Reminder not found");
        }
        
        reminderRepository.delete(reminder);
    }
    
    public List<ReminderDTO> getAllReminders() {
        User user = userService.getCurrentUser();
        return reminderRepository.findByUserOrderByReminderDateDesc(user)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void checkAndTriggerReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fiveMinutesAgo = now.minusMinutes(5);
        
        List<Reminder> dueReminders = reminderRepository.findByIsCompletedFalseAndReminderDateBefore(now);
        
        for (Reminder reminder : dueReminders) {
            if (reminder.getReminderDate().isAfter(fiveMinutesAgo)) {
                notificationService.createNotification(
                        reminder.getUser(),
                        "Reminder: " + reminder.getTitle() + " - " + reminder.getMessage(),
                        NotificationType.INFO
                );
                reminder.setIsCompleted(true);
                reminderRepository.save(reminder);
            }
        }
    }
    
    private ReminderDTO mapToDTO(Reminder reminder) {
        return new ReminderDTO(
                reminder.getId(),
                reminder.getTitle(),
                reminder.getMessage(),
                reminder.getReminderDate(),
                reminder.getIsCompleted(),
                reminder.getCreatedAt()
        );
    }
}
