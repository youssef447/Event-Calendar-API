package com.company.event_calendar.event.controller;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.company.event_calendar.config.security.IsEventOwner;
import com.company.event_calendar.event.entities.EventEntity;
import com.company.event_calendar.event.entities.ReminderEntity;
import com.company.event_calendar.event.services.EventService;
import com.company.event_calendar.event.services.ReminderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final ReminderService reminderService;

    @Operation(summary = "Get all events", description = "Returns a list of all available events")
    @GetMapping("/getEvents")
    @ResponseBody
    public List<EventEntity> getEvents() {

        return eventService.getAllEvents();
    }

    @Operation(summary = "Show form to create new event")
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new EventEntity());
        return "events/form";
    }

    @Operation(summary = "Create a new event")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping("/create")
    @ResponseBody
    public EventEntity createEvent(
            @Valid @ModelAttribute EventEntity event,
            @Parameter(description = "Optional image file")
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        return eventService.createEvent(event, imageFile);
    }

    @Operation(summary = "Get details of an event by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event found"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping("/{eventId}")
    @ResponseBody
    @IsEventOwner
    public EventEntity showEventDetails(@PathVariable Long eventId) {
        return eventService.getEventById(eventId);
    }

    @Operation(summary = "Show form to edit event")
    @GetMapping("/{eventId}/edit")
    @IsEventOwner
    public String showEditForm(@PathVariable Long eventId, Model model) {
        EventEntity event = eventService.getEventById(eventId);
        model.addAttribute("event", event);
        return "events/edit_form";
    }

    @Operation(summary = "Update an existing event")
    @PostMapping("/{eventId}/update")
    @ResponseBody
    @IsEventOwner
    public EventEntity updateEvent(
            @PathVariable Long eventId,
            @Valid @ModelAttribute EventEntity event,
            @Parameter(description = "Optional image file")
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        return eventService.updateEvent(eventId, event, imageFile);
    }

    @Operation(summary = "Delete an event by ID")
    @GetMapping("/{eventId}/delete")
    @ResponseBody
    @IsEventOwner
    public List<EventEntity> deleteEvent(@PathVariable Long eventId) {
        return eventService.deleteEvent(eventId);
    }

    @Operation(summary = "Add a reminder to an event")
    @PostMapping("/{eventId}/reminders/add")
    @ResponseBody
    @IsEventOwner
    public EventEntity addReminder(@PathVariable Long eventId, @ModelAttribute ReminderEntity reminder) {
        return reminderService.addReminderToEvent(eventId, reminder);
    }

    @Operation(summary = "Delete a reminder from an event")
    @GetMapping("/{eventId}/reminders/delete")
    @ResponseBody
    @IsEventOwner
    public EventEntity deleteReminder(
            @PathVariable Long eventId,
            @Parameter(description = "Reminder time to remove")
            @RequestParam LocalDateTime reminderTime) {
        return reminderService.removeReminderFromEvent(eventId, reminderTime);
    }

    @Operation(summary = "Add quick predefined reminders to event")
    @PostMapping("/{eventId}/reminders/quick")
    @ResponseBody
    @IsEventOwner
    public EventEntity addQuickReminders(@PathVariable Long eventId) {
        return reminderService.addQuickReminders(eventId);
    }

    @Operation(summary = "Get all events in a specific date range (calendar view)")
    @GetMapping("/api")
    @ResponseBody
    @IsEventOwner
    public List<EventEntity> getEventsForCalendar(
            @Parameter(description = "Start datetime")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "End datetime")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return eventService.getEventsByDateRange(start, end);
    }
}
