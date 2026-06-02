package br.com.giramundo.store.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.giramundo.store.model.Admin;
import br.com.giramundo.store.repository.AdminRepository;
import br.com.giramundo.store.utils.ValidationUtils;

@Service
@Transactional
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public Admin create(Admin admin) {
        ValidationUtils.validarCampoStringObrigatorio(admin.getUsername(), "username");
        ValidationUtils.validarCampoStringObrigatorio(admin.getPassword(), "password");

        if (admin.getId() == null) {
            admin.setId(UUID.randomUUID());
        }
        admin.setNew(true);
        return adminRepository.save(admin);
    }

    public Admin update(UUID id, Admin admin) {
        ValidationUtils.validarCampoObrigatorio(id, "id");
        Admin existing = adminRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Admin não encontrado."));

        ValidationUtils.validarCampoStringObrigatorio(admin.getUsername(), "username");
        ValidationUtils.validarCampoStringObrigatorio(admin.getPassword(), "password");

        existing.setUsername(admin.getUsername());
        existing.setPassword(admin.getPassword());
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
        ValidationUtils.validarCampoStringObrigatorio(newPassword, "password");

        Admin existing = adminRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Admin não encontrado."));

        existing.setPassword(newPassword);
        existing.setNew(false);
        return adminRepository.save(existing);
    }

}
