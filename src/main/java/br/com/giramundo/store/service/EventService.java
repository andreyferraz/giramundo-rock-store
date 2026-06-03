package br.com.giramundo.store.service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.com.giramundo.store.model.Event;
import br.com.giramundo.store.repository.EventRepository;
import br.com.giramundo.store.utils.ValidationUtils;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final FileUploadService fileUploadService;

    public EventService(EventRepository eventRepository, FileUploadService fileUploadService) {
        this.eventRepository = eventRepository;
        this.fileUploadService = fileUploadService;
    }

    public Event create(Event event, MultipartFile imageFile) {
        ValidationUtils.validarCampoStringObrigatorio(event.getTitle(), "title");
        ValidationUtils.validarCampoStringObrigatorio(event.getDescription(), "description");

        if (imageFile != null && !imageFile.isEmpty()) {
            String imagemSalva = fileUploadService.salvarImagem(imageFile);
            event.setImage(imagemSalva);
        }

        if (event.getId() == null || event.getId().isBlank()) {
            event.setId(UUID.randomUUID().toString());
        }

        if (event.getPublishedAt() == null || event.getPublishedAt().isBlank()) {
            event.setPublishedAt(OffsetDateTime.now(ZoneId.systemDefault()).toString());
        }

        event.setNew(true);
        return eventRepository.save(event);
    }

    public Event update(String id, Event event, MultipartFile imageFile) {
        ValidationUtils.validarCampoObrigatorio(id, "id");
        Event existing = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event não encontrado."));

        ValidationUtils.validarCampoStringObrigatorio(event.getTitle(), "title");
        ValidationUtils.validarCampoStringObrigatorio(event.getDescription(), "description");

        existing.setTitle(event.getTitle());
        existing.setDescription(event.getDescription());

        if (imageFile != null && !imageFile.isEmpty()) {
            String novoNome = fileUploadService.salvarImagem(imageFile);
            if (existing.getImage() != null && !existing.getImage().isEmpty()) {
                fileUploadService.removerImagem(existing.getImage());
            }
            existing.setImage(novoNome);
        }

        existing.setNew(false);
        return eventRepository.save(existing);
    }

    public Optional<Event> findById(String id) {
        ValidationUtils.validarCampoObrigatorio(id, "id");
        return eventRepository.findById(id);
    }

    public Iterable<Event> findAll() {
        return eventRepository.findAll();
    }

    public void delete(String id) {
        ValidationUtils.validarCampoObrigatorio(id, "id");
        Event existing = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event não encontrado."));

        if (existing.getImage() != null && !existing.getImage().isEmpty()) {
            fileUploadService.removerImagem(existing.getImage());
        }

        eventRepository.deleteById(id);
    }
}