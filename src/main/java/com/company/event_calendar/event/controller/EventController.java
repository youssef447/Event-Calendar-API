package com.company.event_calendar.event.controller;

import java.time.LocalDateTime;
import java.util.List;

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
import com.company.event_calendar.event.models.Event;
import com.company.event_calendar.event.models.Reminder;
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

    @GetMapping("/getEvents")

    public String getEvents(Model model) {
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        return "events/list";

    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new Event());
        return "events/form";
    }

    @PostMapping("/create")
    public String createEvent(@Valid @ModelAttribute Event event,

            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        Event savedEvent = eventService.createEvent(event, imageFile);
        return "redirect:/events/" + savedEvent.getId();
    }

    @GetMapping("/{eventId}")
    @IsEventOwner
    public String showEventDetails(@PathVariable Long eventId, Model model) {
        Event event = eventService.getEventById(eventId);
        List<Reminder> reminders = reminderService.getRemindersByEventId(eventId);

        model.addAttribute("event", event);
        model.addAttribute("reminders", reminders);
        model.addAttribute("newReminder", new Reminder());

        return "events/event_details";
    }

    @GetMapping("/{eventId}/edit")
    @IsEventOwner
    public String showEditForm(@PathVariable Long eventId, Model model) {
        Event event = eventService.getEventById(eventId);
        model.addAttribute("event", event);
        return "events/edit_form";
    }

    @PostMapping("/{eventId}")
    @IsEventOwner
    public String updateEvent(@PathVariable Long eventId,
            @Valid @ModelAttribute Event event,

            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        eventService.updateEvent(eventId, event, imageFile);

        return "redirect:/events/" + eventId;
    }

    @GetMapping("/{eventId}/delete")
    @IsEventOwner
    public String deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return "redirect:/events/getEvents";
    }

    // ------------------------------------------------------------------------------
    @PostMapping("/{eventId}/reminders/add")
    @IsEventOwner
    public String addReminder(@PathVariable Long eventId,
            @ModelAttribute Reminder reminder) {
        reminderService.addReminderToEvent(eventId, reminder);
        return "redirect:/events/" + eventId;
    }

    @GetMapping("/{eventId}/reminders/delete")
    @IsEventOwner
    public String deleteReminder(@PathVariable Long eventId,
            @RequestParam LocalDateTime reminderTime) {

        reminderService.removeReminderFromEvent(eventId, reminderTime);
        // redirectAttributes.addFlashAttribute("error", "Failed to delete reminder: " +
        // e.getMessage());
        return "redirect:/events/" + eventId;
    }

    @PostMapping("/{eventId}/reminders/quick")
    @IsEventOwner
    public String addQuickReminders(@PathVariable Long eventId) {

        reminderService.addQuickReminders(eventId);

        return "redirect:/events/" + eventId;
    }

    @GetMapping("/api")
    @ResponseBody
    @IsEventOwner
    public List<Event> getEventsForCalendar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return eventService.getEventsByDateRange(start, end);
    }
}
