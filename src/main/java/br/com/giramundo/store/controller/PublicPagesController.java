package br.com.giramundo.store.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicPagesController {

    @GetMapping({"/", "/home", "/loja"})
    public String store() {
        return "store";
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
