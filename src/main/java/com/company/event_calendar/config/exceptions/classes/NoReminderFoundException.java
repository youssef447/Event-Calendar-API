package com.company.event_calendar.config.exceptions.classes;

public class NoReminderFoundException extends RuntimeException {
    public NoReminderFoundException() {
        super("No Reminder Found");
    }

}
