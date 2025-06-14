package com.company.event_calendar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EventCalendarApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventCalendarApplication.class, args);
    }

}
