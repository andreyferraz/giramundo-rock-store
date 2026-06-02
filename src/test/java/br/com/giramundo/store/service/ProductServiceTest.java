package br.com.giramundo.store.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import br.com.giramundo.store.model.Product;
import br.com.giramundo.store.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private FileUploadService fileUploadService;

    @InjectMocks
    private ProductService productService;

    private Product sample;

    @BeforeEach
    void setUp() {
        sample = new Product();
        sample.setId(UUID.randomUUID().toString());
        sample.setName("Guitar");
        sample.setDescription("An electric guitar");
        sample.setPrice(999.0);
        sample.setQuantity(5);
        sample.setImage(null);
    }

    @Test
    void create_shouldSaveWithoutImage_whenNoImageProvided() {
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product created = productService.create(sample, null);

        assertNotNull(created.getId());
        assertEquals("Guitar", created.getName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void create_shouldGenerateIdAndMarkNew_whenIdIsNull() {
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));
        sample.setId(null);

        Product created = productService.create(sample, null);

        assertNotNull(created.getId());
        // Persistable.isNew() is implemented on the entity
        assertEquals(true, created.isNew());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void create_shouldIgnoreEmptyMultipartFile_andNotCallUpload() {
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));
        MockMultipartFile empty = new MockMultipartFile("file", "empty.png", "image/png", new byte[0]);

        Product created = productService.create(sample, empty);

        assertNotNull(created.getId());
        verify(fileUploadService, org.mockito.Mockito.never()).salvarImagem(any(MultipartFile.class));
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void create_shouldSaveImageAndSetOnProduct_whenImageProvided() {
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));
        MultipartFile file = new MockMultipartFile("file", "img.png", "image/png", "bytes".getBytes());
        when(fileUploadService.salvarImagem(file)).thenReturn("uploaded.webp");

        Product created = productService.create(sample, file);

        assertEquals("uploaded.webp", created.getImage());
        verify(fileUploadService).salvarImagem(file);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void create_shouldThrow_whenNameMissing() {
        sample.setName(null);
        assertThrows(IllegalArgumentException.class, () -> productService.create(sample, null));
    }

    @Test
    void update_shouldReplaceImageAndRemoveOld_whenNewImageProvided() {
        String id = sample.getId();
        Product existing = new Product();
        existing.setId(id);
        existing.setName("Old");
        existing.setPrice(10.0);
        existing.setQuantity(1);
        existing.setImage("old.webp");

        when(productRepository.findById(id)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        MultipartFile newFile = new MockMultipartFile("file", "img2.png", "image/png", "bytes".getBytes());
        when(fileUploadService.salvarImagem(newFile)).thenReturn("new.webp");

        Product update = new Product();
        update.setName("New");
        update.setDescription("desc");
        update.setPrice(20.0);
        update.setQuantity(2);

        Product result = productService.update(id, update, newFile);

        assertEquals("new.webp", result.getImage());
        verify(fileUploadService).removerImagem("old.webp");
        verify(fileUploadService).salvarImagem(newFile);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void update_shouldSaveNewImage_whenExistingImageIsNull() {
        String id = sample.getId();
        Product existing = new Product();
        existing.setId(id);
        existing.setName("Old");
        existing.setPrice(10.0);
        existing.setQuantity(1);
        existing.setImage(null);

        when(productRepository.findById(id)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        MultipartFile newFile = new MockMultipartFile("file", "img2.png", "image/png", "bytes".getBytes());
        when(fileUploadService.salvarImagem(newFile)).thenReturn("new.webp");

        Product update = new Product();
        update.setName("New");
        update.setDescription("desc");
        update.setPrice(20.0);
        update.setQuantity(2);

        Product result = productService.update(id, update, newFile);

        assertEquals("new.webp", result.getImage());
        verify(fileUploadService).salvarImagem(newFile);
        verify(fileUploadService, org.mockito.Mockito.never()).removerImagem(any(String.class));
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void update_shouldNotCallUpload_whenImageFileIsEmpty() {
        String id = sample.getId();
        Product existing = new Product();
        existing.setId(id);
        existing.setName("Old");
        existing.setPrice(10.0);
        existing.setQuantity(1);
        existing.setImage("old.webp");

        when(productRepository.findById(id)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        MockMultipartFile empty = new MockMultipartFile("file", "empty.png", "image/png", new byte[0]);

        Product update = new Product();
        update.setName("New");
        update.setDescription("desc");
        update.setPrice(20.0);
        update.setQuantity(2);

        productService.update(id, update, empty);

        // upload should not be called, old image remains
        verify(fileUploadService, org.mockito.Mockito.never()).salvarImagem(any(MultipartFile.class));
        verify(fileUploadService, org.mockito.Mockito.never()).removerImagem(any(String.class));
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void delete_shouldRemoveImageAndDeleteById() {
        String id = sample.getId();
        Product existing = new Product();
        existing.setId(id);
        existing.setImage("to-remove.webp");

        when(productRepository.findById(id)).thenReturn(Optional.of(existing));

        productService.delete(id);

        verify(fileUploadService).removerImagem("to-remove.webp");
        verify(productRepository).deleteById(id);
    }

    @Test
    void delete_shouldDeleteEvenWhenNoImage() {
        String id = sample.getId();
        Product existing = new Product();
        existing.setId(id);
        existing.setImage(null);

        when(productRepository.findById(id)).thenReturn(Optional.of(existing));

        productService.delete(id);

        verify(fileUploadService, org.mockito.Mockito.never()).removerImagem(any(String.class));
        verify(productRepository).deleteById(id);
    }

}
