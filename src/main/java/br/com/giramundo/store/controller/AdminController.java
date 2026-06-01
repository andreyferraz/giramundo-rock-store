package br.com.giramundo.store.controller;

import br.com.giramundo.store.model.Admin;
import br.com.giramundo.store.repository.AdminRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminRepository adminRepo;

    public AdminController(AdminRepository adminRepo) {
        this.adminRepo = adminRepo;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("admins", adminRepo.findAll());
        return "admin/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("admin", new Admin());
        return "admin/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Admin admin) {
        adminRepo.save(admin);
        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("admin", adminRepo.findById(id));
        return "admin/form";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        adminRepo.deleteById(id);
        return "redirect:/admin";
    }

    @GetMapping("/change-password/{id}")
    public String changePasswordForm(@PathVariable Long id, Model model) {
        model.addAttribute("admin", adminRepo.findById(id));
        return "admin/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam Long id, @RequestParam String password) {
        Admin a = adminRepo.findById(id);
        a.setPassword(password);
        adminRepo.save(a);
        return "redirect:/admin";
    }
}
