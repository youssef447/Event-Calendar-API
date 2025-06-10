package com.company.event_calendar.config.exceptions.classes;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("User Already Exists, Please Login");
    }
}
