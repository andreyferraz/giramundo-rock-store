package br.com.giramundo.store.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.giramundo.store.model.FinancialEntry;
import br.com.giramundo.store.repository.FinancialRepository;
import br.com.giramundo.store.utils.ValidationUtils;

@Service
@Transactional
public class FinancialService {

    private final FinancialRepository financialRepository;

    public FinancialService(FinancialRepository financialRepository) {
        this.financialRepository = financialRepository;
    }

    public FinancialEntry create(FinancialEntry entry) {
        ValidationUtils.validarCampoStringObrigatorio(entry.getType(), "type");
        ValidationUtils.validarCampoObrigatorio(entry.getPrice(), "price");
        ValidationUtils.validarCampoObrigatorio(entry.getOccurredAt(), "occurred_at");

        if (entry.getId() == null) {
            entry.setId(UUID.randomUUID().toString());
        }
        entry.setNew(true);
        return financialRepository.save(entry);
    }

    public FinancialEntry update(UUID id, FinancialEntry entry) {
        ValidationUtils.validarCampoObrigatorio(id, "id");
        FinancialEntry existing = financialRepository.findById(id.toString())
                .orElseThrow(() -> new IllegalArgumentException("FinancialEntry não encontrado."));

        ValidationUtils.validarCampoStringObrigatorio(entry.getType(), "type");
        ValidationUtils.validarCampoObrigatorio(entry.getPrice(), "price");
        ValidationUtils.validarCampoObrigatorio(entry.getOccurredAt(), "occurred_at");

        existing.setType(entry.getType());
        existing.setPrice(entry.getPrice());
        existing.setOccurredAt(entry.getOccurredAt());
        existing.setDescription(entry.getDescription());

        existing.setNew(false);
        return financialRepository.save(existing);
    }

    public Optional<FinancialEntry> findById(UUID id) {
        ValidationUtils.validarCampoObrigatorio(id, "id");
        return financialRepository.findById(id.toString());
    }

    public Iterable<FinancialEntry> findAll() {
        return financialRepository.findAll();
    }

    public void delete(UUID id) {
        ValidationUtils.validarCampoObrigatorio(id, "id");
        financialRepository.deleteById(id.toString());
    }

}
