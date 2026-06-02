package br.com.giramundo.store.repository;

import br.com.giramundo.store.model.Admin;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends CrudRepository<Admin, String> {

	Optional<Admin> findByUsername(String username);


}
