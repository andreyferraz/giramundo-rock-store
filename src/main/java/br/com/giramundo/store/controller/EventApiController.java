package br.com.giramundo.store.controller;

import br.com.giramundo.store.model.Event;
import br.com.giramundo.store.service.EventService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class EventApiController {

    private final EventService eventService;

    public EventApiController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<Event> findAll() {
        return toList(eventService.findAll());
    }

    private List<Event> toList(Iterable<Event> events) {
        return java.util.stream.StreamSupport.stream(events.spliterator(), false).toList();
    }
}