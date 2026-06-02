package br.com.giramundo.store.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.giramundo.store.model.FinancialEntry;
import br.com.giramundo.store.repository.FinancialRepository;

@ExtendWith(MockitoExtension.class)
class FinancialServiceTest {

    @Mock
    private FinancialRepository financialRepository;

    @InjectMocks
    private FinancialService financialService;

    private FinancialEntry sample;

    @BeforeEach
    void setUp() {
        sample = new FinancialEntry();
        sample.setId(UUID.randomUUID().toString());
        sample.setType("IN");
        sample.setPrice(123.45);
        sample.setOccurredAt(OffsetDateTime.now());
        sample.setDescription("teste");
    }

    @Test
    void create_shouldSaveAndReturnEntry_whenValid() {
        when(financialRepository.save(any(FinancialEntry.class))).thenAnswer(i -> i.getArgument(0));

        FinancialEntry created = financialService.create(sample);

        assertNotNull(created.getId());
        assertEquals("IN", created.getType());
        verify(financialRepository).save(any(FinancialEntry.class));
    }

    @Test
    void create_shouldGenerateId_whenIdIsNull() {
        when(financialRepository.save(any(FinancialEntry.class))).thenAnswer(i -> i.getArgument(0));
        sample.setId(null);

        FinancialEntry created = financialService.create(sample);

        assertNotNull(created.getId());
        verify(financialRepository).save(any(FinancialEntry.class));
    }

    @Test
    void create_shouldThrow_whenTypeMissing() {
        sample.setType(null);
        assertThrows(IllegalArgumentException.class, () -> financialService.create(sample));
    }

    @Test
    void update_shouldModifyAndReturn_whenExists() {
        UUID id = UUID.fromString(sample.getId());
        when(financialRepository.findById(id.toString())).thenReturn(Optional.of(sample));
        when(financialRepository.save(any(FinancialEntry.class))).thenAnswer(i -> i.getArgument(0));

        FinancialEntry updated = new FinancialEntry();
        updated.setType("OUT");
        updated.setPrice(50.0);
        updated.setOccurredAt(OffsetDateTime.now());
        updated.setDescription("updated");

        FinancialEntry result = financialService.update(id, updated);

        assertEquals("OUT", result.getType());
        assertEquals(50.0, result.getPrice());
        verify(financialRepository).findById(id.toString());
        verify(financialRepository).save(any(FinancialEntry.class));
    }

    @Test
    void update_shouldThrow_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(financialRepository.findById(id.toString())).thenReturn(Optional.empty());

        FinancialEntry updated = new FinancialEntry();
        updated.setType("OUT");
        updated.setPrice(50.0);
        updated.setOccurredAt(OffsetDateTime.now());

        assertThrows(IllegalArgumentException.class, () -> financialService.update(id, updated));
    }

    @Test
    void findById_shouldReturnOptional_whenExists() {
        UUID id = UUID.fromString(sample.getId());
        when(financialRepository.findById(id.toString())).thenReturn(Optional.of(sample));

        Optional<FinancialEntry> found = financialService.findById(id);
        assertEquals(true, found.isPresent());
    }

    @Test
    void findAll_shouldDelegateToRepository() {
        financialService.findAll();
        verify(financialRepository).findAll();
    }

    @Test
    void delete_shouldCallRepositoryDelete() {
        UUID id = UUID.fromString(sample.getId());
        financialService.delete(id);
        verify(financialRepository).deleteById(id.toString());
    }

}
