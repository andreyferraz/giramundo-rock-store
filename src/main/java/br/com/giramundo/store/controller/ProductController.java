package br.com.giramundo.store.controller;

import br.com.giramundo.store.model.Product;
import br.com.giramundo.store.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/products")
public class ProductController {

    private final ProductRepository repo;

    public ProductController(ProductRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", repo.findAll());
        return "admin/products/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        return "admin/products/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Product product) {
        repo.save(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("product", repo.findById(id));
        return "admin/products/form";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/admin/products";
    }
}
