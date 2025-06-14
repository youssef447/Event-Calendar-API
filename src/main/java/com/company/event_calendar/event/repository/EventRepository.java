package com.company.event_calendar.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.company.event_calendar.event.entities.EventEntity;
import com.company.event_calendar.user.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

        List<EventEntity> findByUser(UserEntity user);

        List<EventEntity> findByUserAndStartTimeBetween(UserEntity user, LocalDateTime start, LocalDateTime end);

        
        @Query("SELECT DISTINCT e FROM EventEntity e JOIN e.reminders r WHERE  r.reminderTime <= :now AND r.sent = false")
        List<EventEntity> findEventsWithPendingReminders(@Param("now") LocalDateTime now);

        @Query("SELECT DISTINCT e FROM EventEntity e JOIN e.reminders r WHERE e.user = :user AND r.reminderTime <= :now AND r.sent = false")
        List<EventEntity> findByUserWithPendingReminders(@Param("user") UserEntity user, @Param("now") LocalDateTime now);

        @Query("SELECT DISTINCT e FROM EventEntity e JOIN e.reminders r WHERE e.user = :user AND r.reminderTime < :now AND r.sent = false")
        List<EventEntity> findByUserWithOverdueReminders(@Param("user") UserEntity user, @Param("now") LocalDateTime now);


        @Query("SELECT e FROM EventEntity e WHERE e.user = :user AND " +
                        "((e.startTime BETWEEN :start AND :end) OR " +
                        "(e.endTime BETWEEN :start AND :end) OR " +
                        "(e.startTime <= :start AND e.endTime >= :end))")
        Optional<List<EventEntity>> findByUserAndDateRange(@Param("user") UserEntity user,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);

       

     

        @Query("SELECT COUNT(r) FROM EventEntity e JOIN e.reminders r WHERE e.user = :user AND e.id = :eventId AND r.sent = false")
        long countPendingRemindersByUserAndEventId(@Param("user") UserEntity user, @Param("eventId") Long eventId);

}