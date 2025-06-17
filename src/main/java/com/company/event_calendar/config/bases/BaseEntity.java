package com.company.event_calendar.config.bases;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)

public abstract class BaseEntity<ID> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private ID id;

    @CreatedDate
    private Date createdDate;


    @LastModifiedDate
    private Date lastModifiedDate;
}
