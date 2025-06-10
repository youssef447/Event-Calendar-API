package com.company.event_calendar.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.company.event_calendar.event.models.Event;
import com.company.event_calendar.user.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

        List<Event> findByUser(UserEntity user);

        List<Event> findByUserAndStartTimeBetween(UserEntity user, LocalDateTime start, LocalDateTime end);

        List<Event> findByUserAndAddressContainingIgnoreCase(UserEntity user, String address);

        List<Event> findByUserAndTitleContainingIgnoreCase(UserEntity user, String title);

        List<Event> findByUserAndAllDayTrue(UserEntity user);

        @Query("SELECT DISTINCT e FROM Event e JOIN e.reminders r WHERE  r.reminderTime <= :now AND r.sent = false")

        List<Event> findEventsWithPendingReminders(@Param("now") LocalDateTime now);

        @Query("SELECT DISTINCT e FROM Event e JOIN e.reminders r WHERE e.user = :user AND r.reminderTime <= :now AND r.sent = false")
        List<Event> findEventsByUserWithPendingReminders(@Param("user") UserEntity user, @Param("now") LocalDateTime now);

        @Query("SELECT DISTINCT e FROM Event e JOIN e.reminders r WHERE e.user = :user AND r.reminderTime < :now AND r.sent = false")
        List<Event> findEventsByUserWithOverdueReminders(@Param("user") UserEntity user, @Param("now") LocalDateTime now);


        @Query("SELECT e FROM Event e WHERE e.user = :user AND " +
                        "((e.startTime BETWEEN :start AND :end) OR " +
                        "(e.endTime BETWEEN :start AND :end) OR " +
                        "(e.startTime <= :start AND e.endTime >= :end))")
        Optional<List<Event>> findEventsByUserAndDateRange(@Param("user") UserEntity user,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);

       
        @Query("SELECT e FROM Event e WHERE e.user = :user AND SIZE(e.reminders) > 0")
        List<Event> findEventsByUserWithReminders(@Param("user") UserEntity user);

        @Query("SELECT e FROM Event e WHERE e.user = :user AND SIZE(e.reminders) = 0")
        List<Event> findEventsByUserWithoutReminders(@Param("user") UserEntity user);

        @Query("SELECT COUNT(r) FROM Event e JOIN e.reminders r WHERE e.user = :user AND e.id = :eventId AND r.sent = false")
        long countPendingRemindersByUserAndEventId(@Param("user") UserEntity user, @Param("eventId") Long eventId);

}