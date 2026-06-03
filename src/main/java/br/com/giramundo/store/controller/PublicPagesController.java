package br.com.giramundo.store.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicPagesController {

    @GetMapping({"/", "/home"})
    public String store() {
        return "index";
    }

    @GetMapping("/loja")
    public String shop() {
        return "loja";
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
