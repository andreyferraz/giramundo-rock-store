package br.com.giramundo.store.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

import br.com.giramundo.store.model.Event;
import br.com.giramundo.store.service.EventService;

@Controller
public class PublicPagesController {

    private final EventService eventService;

    public PublicPagesController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping({"/", "/home"})
    public String store() {
        return "index";
    }

    @GetMapping("/loja")
    public String shop() {
        return "loja";
    }

    @GetMapping("/eventos")
    public String events(Model model) {
        model.addAttribute("events", eventService.findAll());
        return "eventos";
    }

    @GetMapping("/eventos/{id}")
    public String eventDetail(@PathVariable String id, Model model) {
        Event event = eventService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado."));

        model.addAttribute("event", event);
        return "evento-detalhe";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/sobre")
    public String about() {
        return "about";
    }

    @GetMapping("/contato")
    public String contact() {
        return "contact";
    }
}
