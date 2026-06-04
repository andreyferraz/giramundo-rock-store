package br.com.giramundo.store.controller;

import br.com.giramundo.store.model.Admin;
import br.com.giramundo.store.model.Event;
import br.com.giramundo.store.model.FinancialEntry;
import br.com.giramundo.store.model.Product;
import br.com.giramundo.store.repository.AdminRepository;
import br.com.giramundo.store.service.AdminService;
import br.com.giramundo.store.service.EventService;
import br.com.giramundo.store.service.FinancialService;
import br.com.giramundo.store.service.ProductService;
import java.security.Principal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final String VIEW_PANEL = "admin/panel";
    private static final String ATTR_ACTIVE_TAB = "activeTab";
    private static final String ATTR_CURRENT_ADMIN = "currentAdmin";
    private static final String ATTR_ADMIN = "admin";
    private static final String ATTR_PRODUCTS = "products";
    private static final String ATTR_PRODUCT = "product";
    private static final String ATTR_EVENTS = "events";
    private static final String ATTR_EVENT = "event";
    private static final String ATTR_ENTRIES = "entries";
    private static final String ATTR_ENTRY = "entry";
    private static final String ATTR_FINANCIAL_MONTH = "financialMonth";
    private static final String ATTR_FINANCIAL_YEAR = "financialYear";
    private static final String ATTR_FINANCIAL_SUMMARY = "financialSummary";
    private static final String ATTR_SUCCESS_MESSAGE = "successMessage";
    private static final String ATTR_ERROR_MESSAGE = "errorMessage";
    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String TAB_SETTINGS = "settings";
    private static final String TAB_PRODUCTS = "products";
    private static final String TAB_EVENTS = "events";
    private static final String TAB_FINANCIAL = "financial";
    private static final String REDIRECT_PRODUCTS = "redirect:/admin/products";
    private static final String REDIRECT_EVENTS = "redirect:/admin/events";
    private static final DateTimeFormatter FINANCIAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final AdminRepository adminRepository;
    private final AdminService adminService;
    private final ProductService productService;
    private final EventService eventService;
    private final FinancialService financialService;

    public AdminController(AdminRepository adminRepository, AdminService adminService,
            ProductService productService, EventService eventService, FinancialService financialService) {
        this.adminRepository = adminRepository;
        this.adminService = adminService;
        this.productService = productService;
        this.eventService = eventService;
        this.financialService = financialService;
    }

    @GetMapping
    public String root() {
        return "redirect:/admin/settings";
    }

    @GetMapping("/settings")
    public String settings(Principal principal, Model model) {
        Admin currentAdmin = currentAdmin(principal);
        model.addAttribute(ATTR_ACTIVE_TAB, TAB_SETTINGS);
        model.addAttribute(ATTR_CURRENT_ADMIN, currentAdmin.getUsername());
        model.addAttribute(ATTR_ADMIN, currentAdmin);
        model.addAttribute(ATTR_PRODUCTS, productService.findAll());
        model.addAttribute(ATTR_PRODUCT, new Product());
        model.addAttribute(ATTR_EVENTS, eventService.findAll());
        model.addAttribute(ATTR_EVENT, new Event());
        model.addAttribute(ATTR_ENTRIES, financialService.findAll());
        model.addAttribute(ATTR_ENTRY, new FinancialEntry());
        addSharedMessages(model, null, null);
        return VIEW_PANEL;
    }

    @PostMapping("/settings/password")
    public String updatePassword(Principal principal,
            @RequestParam String newPassword,
            RedirectAttributes redirectAttributes) {
        try {
            Admin currentAdmin = currentAdmin(principal);
            adminService.changePassword(UUID.fromString(currentAdmin.getId()), newPassword);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, "Senha atualizada com sucesso.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ex.getMessage());
        }

        return "redirect:/admin/settings";
    }

    @GetMapping("/products")
    public String products(Principal principal, Model model) {
        return renderProductsPage(principal, model, new Product(), null, null);
    }

    @GetMapping("/products/new")
    public String newProduct(Principal principal, Model model) {
        return renderProductsPage(principal, model, new Product(), null, null);
    }

    @GetMapping("/products/edit")
    public String editProductWithoutId(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, "Produto inválido para edição.");
        return REDIRECT_PRODUCTS;
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(Principal principal, @PathVariable String id, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Product não encontrado."));
            return renderProductsPage(principal, model, product, null, null);
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, "Produto inválido para edição.");
            return REDIRECT_PRODUCTS;
        }
    }

    @PostMapping("/products/save")
    public String saveProduct(Principal principal,
            @ModelAttribute Product product,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {
        try {
            if (!StringUtils.hasText(product.getId())) {
                productService.create(product, imageFile);
            } else {
                productService.update(product.getId(), product, imageFile);
            }
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, "Produto salvo com sucesso.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ex.getMessage());
        }

        return REDIRECT_PRODUCTS;
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            productService.delete(id);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, "Produto removido com sucesso.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ex.getMessage());
        }

        return REDIRECT_PRODUCTS;
    }

    @GetMapping("/events")
    public String events(Principal principal, Model model) {
        return renderEventsPage(principal, model, new Event(), null, null);
    }

    @GetMapping("/events/new")
    public String newEvent(Principal principal, Model model) {
        return renderEventsPage(principal, model, new Event(), null, null);
    }

    @GetMapping("/events/edit/{id}")
    public String editEvent(Principal principal, @PathVariable String id, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Event event = eventService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Event não encontrado."));
            return renderEventsPage(principal, model, event, null, null);
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, "Evento inválido para edição.");
            return REDIRECT_EVENTS;
        }
    }

    @PostMapping("/events/save")
    public String saveEvent(Principal principal,
            @ModelAttribute Event event,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {
        try {
            if (!StringUtils.hasText(event.getId())) {
                eventService.create(event, imageFile);
            } else {
                eventService.update(event.getId(), event, imageFile);
            }
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, "Evento salvo com sucesso.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ex.getMessage());
        }

        return REDIRECT_EVENTS;
    }

    @PostMapping("/events/delete/{id}")
    public String deleteEvent(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            eventService.delete(id);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, "Evento removido com sucesso.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ex.getMessage());
        }

        return REDIRECT_EVENTS;
    }

    @GetMapping("/financial")
    public String financial(Principal principal,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) Integer year,
            Model model) {
        return renderFinancialPage(principal, model, new FinancialEntry(), null, null, month, year);
    }

    @GetMapping("/financial/new")
    public String newFinancial(Principal principal, Model model) {
        return renderFinancialPage(principal, model, new FinancialEntry(), null, null, null, null);
    }

    @GetMapping("/financial/edit/{id}")
    public String editFinancial(Principal principal, @PathVariable UUID id, Model model) {
        FinancialEntry entry = financialService.findById(id).orElseThrow(() -> new IllegalArgumentException("FinancialEntry não encontrado."));
        return renderFinancialPage(principal, model, entry, null, null, null, null);
    }

    @PostMapping("/financial/save")
    public String saveFinancial(Principal principal,
            @RequestParam(required = false) UUID id,
            @RequestParam String type,
            @RequestParam Double price,
            @RequestParam String occurredAt,
            @RequestParam(required = false) String description,
            RedirectAttributes redirectAttributes) {
        try {
            FinancialEntry entry = new FinancialEntry();
            entry.setId(id == null ? null : id.toString());
            entry.setType(type);
            entry.setPrice(price);
            entry.setOccurredAt(normalizeFinancialDate(occurredAt));
            entry.setDescription(description);

            if (entry.getId() == null) {
                financialService.create(entry);
            } else {
                financialService.update(UUID.fromString(entry.getId()), entry);
            }
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, "Lançamento salvo com sucesso.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ex.getMessage());
        }

        return "redirect:/admin/financial";
    }

    @PostMapping("/financial/delete/{id}")
    public String deleteFinancial(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            financialService.delete(id);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, "Lançamento removido com sucesso.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, ex.getMessage());
        }

        return "redirect:/admin/financial";
    }

    private String renderProductsPage(Principal principal, Model model, Product product,
            String successMessage, String errorMessage) {
        Admin currentAdmin = currentAdmin(principal);
        model.addAttribute(ATTR_ACTIVE_TAB, TAB_PRODUCTS);
        model.addAttribute(ATTR_CURRENT_ADMIN, currentAdmin.getUsername());
        model.addAttribute(ATTR_ADMIN, currentAdmin);
        model.addAttribute(ATTR_PRODUCTS, productService.findAll());
        model.addAttribute(ATTR_PRODUCT, product);
        model.addAttribute(ATTR_EVENTS, eventService.findAll());
        model.addAttribute(ATTR_EVENT, new Event());
        model.addAttribute(ATTR_ENTRIES, financialService.findAll());
        model.addAttribute(ATTR_ENTRY, new FinancialEntry());
        addSharedMessages(model, successMessage, errorMessage);
        return VIEW_PANEL;
    }

    private String renderEventsPage(Principal principal, Model model, Event event,
            String successMessage, String errorMessage) {
        Admin currentAdmin = currentAdmin(principal);
        model.addAttribute(ATTR_ACTIVE_TAB, TAB_EVENTS);
        model.addAttribute(ATTR_CURRENT_ADMIN, currentAdmin.getUsername());
        model.addAttribute(ATTR_ADMIN, currentAdmin);
        model.addAttribute(ATTR_EVENTS, eventService.findAll());
        model.addAttribute(ATTR_EVENT, event);
        model.addAttribute(ATTR_PRODUCTS, productService.findAll());
        model.addAttribute(ATTR_PRODUCT, new Product());
        model.addAttribute(ATTR_ENTRIES, financialService.findAll());
        model.addAttribute(ATTR_ENTRY, new FinancialEntry());
        addSharedMessages(model, successMessage, errorMessage);
        return VIEW_PANEL;
    }

    private String renderFinancialPage(Principal principal, Model model, FinancialEntry entry,
            String successMessage, String errorMessage, String monthFilter, Integer yearFilter) {
        Admin currentAdmin = currentAdmin(principal);
        YearMonth currentMonth = YearMonth.now();
        String effectiveMonth = StringUtils.hasText(monthFilter) ? monthFilter : String.valueOf(currentMonth.getMonthValue());
        Integer effectiveYear = yearFilter != null ? yearFilter : currentMonth.getYear();
        model.addAttribute(ATTR_ACTIVE_TAB, TAB_FINANCIAL);
        model.addAttribute(ATTR_CURRENT_ADMIN, currentAdmin.getUsername());
        model.addAttribute(ATTR_ADMIN, currentAdmin);
        List<FinancialEntry> allEntries = toList(financialService.findAll());
        List<FinancialEntry> filteredEntries = filterByMonth(allEntries, effectiveMonth, effectiveYear);
        model.addAttribute(ATTR_ENTRIES, filteredEntries);
        model.addAttribute(ATTR_ENTRY, entry);
        model.addAttribute(ATTR_PRODUCTS, productService.findAll());
        model.addAttribute(ATTR_PRODUCT, new Product());
        model.addAttribute(ATTR_FINANCIAL_MONTH, effectiveMonth);
        model.addAttribute(ATTR_FINANCIAL_YEAR, effectiveYear);
        model.addAttribute(ATTR_FINANCIAL_SUMMARY, buildFinancialSummary(filteredEntries));
        addSharedMessages(model, successMessage, errorMessage);
        return VIEW_PANEL;
    }

    private List<FinancialEntry> toList(Iterable<FinancialEntry> entries) {
        List<FinancialEntry> list = new ArrayList<>();
        entries.forEach(list::add);
        return list;
    }

    private List<FinancialEntry> filterByMonth(List<FinancialEntry> entries, String monthFilter, Integer yearFilter) {
        if (!StringUtils.hasText(monthFilter) || yearFilter == null) {
            return entries;
        }

        YearMonth selectedMonth = YearMonth.of(yearFilter, Integer.parseInt(monthFilter));
        return entries.stream()
                .filter(entry -> StringUtils.hasText(entry.getOccurredAt()))
                .filter(entry -> extractYearMonth(entry.getOccurredAt()).map(selectedMonth::equals).orElse(false))
            .toList();
    }

    private FinancialSummary buildFinancialSummary(List<FinancialEntry> entries) {
        double totalIn = entries.stream()
                .filter(entry -> "IN".equalsIgnoreCase(entry.getType()))
                .mapToDouble(entry -> entry.getPrice() == null ? 0D : entry.getPrice())
                .sum();

        double totalOut = entries.stream()
                .filter(entry -> "OUT".equalsIgnoreCase(entry.getType()))
                .mapToDouble(entry -> entry.getPrice() == null ? 0D : entry.getPrice())
                .sum();

        return new FinancialSummary(totalIn, totalOut, totalIn - totalOut);
    }

    private record FinancialSummary(double totalIn, double totalOut, double balance) {
    }

    private String normalizeFinancialDate(String occurredAt) {
        LocalDate localDate = LocalDate.parse(occurredAt, FINANCIAL_DATE_FORMATTER);
        return localDate.format(FINANCIAL_DATE_FORMATTER);
    }

    private Optional<YearMonth> extractYearMonth(String occurredAt) {
        if (!StringUtils.hasText(occurredAt)) {
            return Optional.empty();
        }

        try {
            LocalDate localDate = parseFinancialDate(occurredAt);
            return Optional.of(YearMonth.from(localDate));
        } catch (DateTimeParseException ex) {
            return Optional.empty();
        }
    }

    private LocalDate parseFinancialDate(String occurredAt) {
        try {
            return LocalDate.parse(occurredAt, FINANCIAL_DATE_FORMATTER);
        } catch (DateTimeParseException firstError) {
            try {
                return OffsetDateTime.parse(occurredAt).toLocalDate();
            } catch (DateTimeParseException secondError) {
                return LocalDate.parse(occurredAt);
            }
        }
    }

    private void addSharedMessages(Model model, String successMessage, String errorMessage) {
        if (successMessage != null) {
            model.addAttribute(ATTR_SUCCESS_MESSAGE, successMessage);
        }

        if (errorMessage != null) {
            model.addAttribute(ATTR_ERROR_MESSAGE, errorMessage);
        }
    }

    private Admin currentAdmin(Principal principal) {
        String username = principal != null ? principal.getName() : DEFAULT_ADMIN_USERNAME;
        Optional<Admin> admin = adminRepository.findByUsername(username);
        return admin.orElseGet(() -> {
            Admin fallback = new Admin();
            fallback.setId(UUID.randomUUID().toString());
            fallback.setUsername(username);
            fallback.setPassword("");
            fallback.setNew(false);
            return fallback;
        });
    }

}