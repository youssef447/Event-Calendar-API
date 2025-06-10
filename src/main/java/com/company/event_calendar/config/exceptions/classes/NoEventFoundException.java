package com.company.event_calendar.config.exceptions.classes;

public class NoEventFoundException extends RuntimeException {

    public NoEventFoundException() {
        super("No Event Found");
    }

}
