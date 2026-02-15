package com.finmate.ai.repository;

import com.finmate.ai.entity.Reminder;
import com.finmate.ai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByUserOrderByReminderDateDesc(User user);
    List<Reminder> findByIsCompletedFalseAndReminderDateBefore(LocalDateTime dateTime);
}
