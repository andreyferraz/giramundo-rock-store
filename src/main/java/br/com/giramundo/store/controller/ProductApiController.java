package br.com.giramundo.store.controller;

import br.com.giramundo.store.model.Product;
import br.com.giramundo.store.service.ProductService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    private final ProductService productService;

    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> findAll() {
        return toList(productService.findAll());
    }

    private List<Product> toList(Iterable<Product> products) {
        return java.util.stream.StreamSupport.stream(products.spliterator(), false).toList();
    }
}