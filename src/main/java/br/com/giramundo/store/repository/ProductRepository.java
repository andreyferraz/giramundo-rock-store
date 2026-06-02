package br.com.giramundo.store.repository;

import br.com.giramundo.store.model.Product;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, String> {

	@Modifying
	@Query("UPDATE product SET id = :newId WHERE id = ''")
	int assignIdToLegacyBlankProduct(String newId);

}
