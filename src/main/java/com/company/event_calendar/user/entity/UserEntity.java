package com.company.event_calendar.user.entity;

import java.util.Collection;
import java.util.List;

import com.company.event_calendar.config.bases.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.company.event_calendar.event.entities.EventEntity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserEntity extends BaseEntity<Long> implements UserDetails {


    @Column(unique = true, nullable = false)
    private String username;

    private String password;
    private String profileUrl;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventEntity> events;
   // private LocalDateTime createdAt;
   /* @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }*/

    // Spring Security requirements
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.getRole().name()));
    }
}


