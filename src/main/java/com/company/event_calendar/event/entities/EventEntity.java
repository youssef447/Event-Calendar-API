package com.company.event_calendar.event.entities;

import com.company.event_calendar.config.bases.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import com.company.event_calendar.user.entity.UserEntity;
import org.springframework.data.annotation.CreatedBy;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "events")
//@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity extends BaseEntity<Long>{


    @NotBlank(message = "Title is required")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Start time is required")

    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    // @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endTime;

    private String address;

    private String imageFileUrl;

    /*
     * (mappedBy) says:
     * “Don’t manage the foreign key from this side (Event).
     * The event field in Attendee is the one that holds the actual relationship
     * (foreign key).”
     * 
     * @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval =
     * true)
     */
    @ElementCollection
    @CollectionTable(name = "event_reminders", joinColumns = @JoinColumn(name = "event_id"))
    private List<ReminderEntity> reminders;

    private String color;

    private boolean allDay;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    @CreatedBy
    private UserEntity user;
}
