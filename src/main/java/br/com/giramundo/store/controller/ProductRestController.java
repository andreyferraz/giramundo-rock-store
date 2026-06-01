package br.com.giramundo.store.controller;

import br.com.giramundo.store.repository.ProductRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRestController {

    private final ProductRepository repo;

    public ProductRestController(ProductRepository repo) { this.repo = repo; }

    @GetMapping("/api/products")
    public Object list() {
        return repo.findAll();
    }
}
