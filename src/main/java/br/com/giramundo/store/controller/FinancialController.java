package br.com.giramundo.store.controller;

import br.com.giramundo.store.model.FinancialEntry;
import br.com.giramundo.store.repository.FinancialRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/financial")
public class FinancialController {

    private final FinancialRepository repo;

    public FinancialController(FinancialRepository repo) { this.repo = repo; }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("entries", repo.findAll());
        return "admin/financial/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("entry", new FinancialEntry());
        return "admin/financial/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute FinancialEntry entry) {
        repo.save(entry);
        return "redirect:/admin/financial";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("entry", repo.findById(id));
        return "admin/financial/form";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/admin/financial";
    }
}
