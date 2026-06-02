package br.com.giramundo.store.repository;

import br.com.giramundo.store.model.Product;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends CrudRepository<Product, UUID> {

}
