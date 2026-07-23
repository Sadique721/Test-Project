package com.savbill.salescrmsbss.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

	Optional<Event> findByEventName(String eventName);
    List<Event> findByEventNameContaining(String eventName);

}
