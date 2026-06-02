package br.com.giramundo.store.service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.com.giramundo.store.model.Product;
import br.com.giramundo.store.repository.ProductRepository;
import br.com.giramundo.store.utils.ValidationUtils;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final FileUploadService fileUploadService;

    public ProductService(ProductRepository productRepository, FileUploadService fileUploadService) {
        this.productRepository = productRepository;
        this.fileUploadService = fileUploadService;
    }

    public Product create(Product product, MultipartFile imageFile) {
        ValidationUtils.validarCampoStringObrigatorio(product.getName(), "name");
        ValidationUtils.validarCampoObrigatorio(product.getPrice(), "price");
        ValidationUtils.validarCampoObrigatorio(product.getQuantity(), "quantity");

        if (imageFile != null && !imageFile.isEmpty()) {
            String imagemSalva = fileUploadService.salvarImagem(imageFile);
            product.setImage(imagemSalva);
        }

        String productId = product.getId();
        if (productId == null || productId.isBlank()) {
            product.setId(UUID.randomUUID().toString());
        }
        product.setNew(true);
        return productRepository.save(product);
    }

    public Product update(String id, Product product, MultipartFile imageFile) {
        ValidationUtils.validarCampoObrigatorio(id, "id");
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product não encontrado."));

        ValidationUtils.validarCampoStringObrigatorio(product.getName(), "name");
        ValidationUtils.validarCampoObrigatorio(product.getPrice(), "price");
        ValidationUtils.validarCampoObrigatorio(product.getQuantity(), "quantity");

        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setQuantity(product.getQuantity());

        if (imageFile != null && !imageFile.isEmpty()) {
            // salvar nova imagem e remover a antiga se existir
            String novoNome = fileUploadService.salvarImagem(imageFile);
            if (existing.getImage() != null && !existing.getImage().isEmpty()) {
                fileUploadService.removerImagem(existing.getImage());
            }
            existing.setImage(novoNome);
        }

        existing.setNew(false);
        return productRepository.save(existing);
    }

    public Optional<Product> findById(UUID id) {
        ValidationUtils.validarCampoObrigatorio(id, "id");
        return productRepository.findById(id.toString());
    }

    public Optional<Product> findById(String id) {
        ValidationUtils.validarCampoObrigatorio(id, "id");
        return productRepository.findById(id);
    }

    public Iterable<Product> findAll() {
        Iterable<Product> products = productRepository.findAll();
        StreamSupport.stream(products.spliterator(), false)
                .filter(product -> product.getId() != null && product.getId().isBlank())
                .findFirst()
                .ifPresent(product -> {
                    String generatedId = UUID.randomUUID().toString();
                    if (productRepository.assignIdToLegacyBlankProduct(generatedId) > 0) {
                        product.setId(generatedId);
                    }
                });
        return products;
    }

    public void delete(String id) {
        ValidationUtils.validarCampoObrigatorio(id, "id");
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product não encontrado."));

        if (existing.getImage() != null && !existing.getImage().isEmpty()) {
            fileUploadService.removerImagem(existing.getImage());
        }

        productRepository.deleteById(id);
    }

}
