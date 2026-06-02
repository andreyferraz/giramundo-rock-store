package br.com.giramundo.store.repository;

import br.com.giramundo.store.model.FinancialEntry;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FinancialRepository extends CrudRepository<FinancialEntry, UUID> {

}
