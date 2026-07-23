package com.savbill.radius.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.savbill.radius.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event>
{

	Optional<Event> findByEventName(String eventName);
	List<Event> findByEventNameContaining(String eventName);
}