package br.com.giramundo.store.repository;

import br.com.giramundo.store.model.Event;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends CrudRepository<Event, String> {

}