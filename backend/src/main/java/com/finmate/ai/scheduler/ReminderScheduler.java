package com.finmate.ai.scheduler;

import com.finmate.ai.service.ReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReminderScheduler {
    
    private final ReminderService reminderService;
    
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void checkReminders() {
        log.info("Checking reminders...");
        reminderService.checkAndTriggerReminders();
    }
}
