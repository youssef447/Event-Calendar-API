package com.company.event_calendar.event.controller;

import java.time.LocalDateTime;
import java.util.List;

import com.company.event_calendar.config.response.ApiResponseBody;
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
    ApiResponseBody createEvent(
            @Valid @ModelAttribute EventEntity event,
            @Parameter(description = "Optional image file")
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        EventEntity res = eventService.createEvent(event, imageFile);
        return new ApiResponseBody("event created successfully", res, true);
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
    public ApiResponseBody updateEvent(
            @PathVariable Long eventId,
            @Valid @ModelAttribute EventEntity event,
            @Parameter(description = "Optional image file")
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        EventEntity result = eventService.updateEvent(eventId, event, imageFile);
        return new ApiResponseBody("event updated successfully", result, true);

    }

    @Operation(summary = "Delete an event by ID")
    @GetMapping("/{eventId}/delete")
    @ResponseBody
    @IsEventOwner
    public ApiResponseBody deleteEvent(@PathVariable Long eventId) {
        return eventService.deleteEvent(eventId);
    }

    @Operation(summary = "Add a reminder to an event")
    @PostMapping("/{eventId}/reminders/add")
    @ResponseBody
    @IsEventOwner
    public ApiResponseBody addReminder(@PathVariable Long eventId, @ModelAttribute ReminderEntity reminder) {
        EventEntity result = reminderService.addReminderToEvent(eventId, reminder);
        return new ApiResponseBody("reminder added successfully", result, true);

    }

    @Operation(summary = "Get count of pending reminders for an event")
    @GetMapping("/{eventId}/reminders/count")
    @ResponseBody
    @IsEventOwner
    public ApiResponseBody getPendingRemindersCount(@PathVariable Long eventId) {
        long result = reminderService.getPendingRemindersCount(eventId);
        return new ApiResponseBody("pending reminders fetched successfully", result, true);

    }@Operation(summary = "Get Total count of pending reminders for all event")
    @GetMapping("/reminders/totalCount")
    @ResponseBody
    public ApiResponseBody getTotalPendingRemindersCount() {
        long result = reminderService.getTotalPendingRemindersCount();
        return new ApiResponseBody("pending reminders fetched successfully", result, true);

    }

    @Operation(summary = "Delete a reminder from an event")
    @GetMapping("/{eventId}/reminders/delete")
    @ResponseBody
    @IsEventOwner
    public ApiResponseBody deleteReminder(
            @PathVariable Long eventId,
            @Parameter(description = "Reminder time to remove")
            @RequestParam LocalDateTime reminderTime) {
        reminderService.removeReminderFromEvent(eventId, reminderTime);
        return new ApiResponseBody("reminder deleted successfully", true);

    }

    @Operation(summary = "Add quick predefined reminders to event")
    @PostMapping("/{eventId}/reminders/quick")
    @ResponseBody
    @IsEventOwner
    public ApiResponseBody addQuickReminders(@PathVariable Long eventId) {
        EventEntity result = reminderService.addQuickReminders(eventId);
        return new ApiResponseBody("reminder added successfully", result, true);

    }

    @Operation(summary = "Get all events in a specific date range (calendar view)")
    @GetMapping("/dateRange")
    @ResponseBody
    @IsEventOwner
    public ApiResponseBody getEventsForCalendar(
            @Parameter(description = "Start datetime")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "End datetime")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<EventEntity> result = eventService.getEventsByDateRange(start, end);
        return new ApiResponseBody("events fetched successfully", result, true);

    }
}
