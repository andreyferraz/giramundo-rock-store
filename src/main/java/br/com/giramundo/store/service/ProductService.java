package br.com.giramundo.store.service;

import java.util.Optional;
import java.util.UUID;

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

        if (product.getId() == null) {
            product.setId(UUID.randomUUID());
        }
        product.setNew(true);
        return productRepository.save(product);
    }

    public Product update(UUID id, Product product, MultipartFile imageFile) {
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
        return productRepository.findById(id);
    }

    public Iterable<Product> findAll() {
        return productRepository.findAll();
    }

    public void delete(UUID id) {
        ValidationUtils.validarCampoObrigatorio(id, "id");
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product não encontrado."));

        if (existing.getImage() != null && !existing.getImage().isEmpty()) {
            fileUploadService.removerImagem(existing.getImage());
        }

        productRepository.deleteById(id);
    }

}
