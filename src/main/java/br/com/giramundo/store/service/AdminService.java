package br.com.giramundo.store.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.giramundo.store.model.Admin;
import br.com.giramundo.store.repository.AdminRepository;
import br.com.giramundo.store.utils.ValidationUtils;

@Service
@Transactional
public class AdminService {

    private static final String PASSWORD_FIELD = "password";

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Admin create(Admin admin) {
        ValidationUtils.validarCampoStringObrigatorio(admin.getUsername(), "username");
        ValidationUtils.validarCampoStringObrigatorio(admin.getPassword(), PASSWORD_FIELD);

        if (admin.getId() == null) {
            admin.setId(UUID.randomUUID());
        }
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setNew(true);
        return adminRepository.save(admin);
    }

    public Admin update(UUID id, Admin admin) {
        ValidationUtils.validarCampoObrigatorio(id, "id");
        Admin existing = adminRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Admin não encontrado."));

        ValidationUtils.validarCampoStringObrigatorio(admin.getUsername(), "username");
        ValidationUtils.validarCampoStringObrigatorio(admin.getPassword(), PASSWORD_FIELD);

        existing.setUsername(admin.getUsername());
        existing.setPassword(passwordEncoder.encode(admin.getPassword()));
        existing.setNew(false);
        return adminRepository.save(existing);
    }

    public Optional<Admin> findById(UUID id) {
        ValidationUtils.validarCampoObrigatorio(id, "id");
        return adminRepository.findById(id);
    }

    public Iterable<Admin> findAll() {
        return adminRepository.findAll();
    }

    public void delete(UUID id) {
        ValidationUtils.validarCampoObrigatorio(id, "id");
        adminRepository.deleteById(id);
    }

    public Admin changePassword(UUID id, String newPassword) {
        ValidationUtils.validarCampoObrigatorio(id, "id");
        ValidationUtils.validarCampoStringObrigatorio(newPassword, PASSWORD_FIELD);

        Admin existing = adminRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Admin não encontrado."));

        existing.setPassword(passwordEncoder.encode(newPassword));
        existing.setNew(false);
        return adminRepository.save(existing);
    }

}
