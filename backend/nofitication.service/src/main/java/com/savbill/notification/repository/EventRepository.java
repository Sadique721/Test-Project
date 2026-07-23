package com.savbill.notification.repository;

import com.savbill.notification.entity.Event;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@JaversSpringDataAuditable
@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event>, JpaSpecificationExecutor<Event> {

    Optional<Event> findByEventName(String eventName);

    List<Event> findByEventNameContaining(String eventName);

    Page<Event> findAllByIsDeleteIsFalseAndSystemGeneratedIsFalseAndServiceTypeAndMvnoIdIn(String serviceType, Pageable pageable, List<Long> mvnoIds);

    Page<Event> findAllByIsDeleteIsFalseAndSystemGeneratedIsFalseAndServiceTypeAndMvnoIdIn(String serviceType, List<Long> mvnoIds, Pageable pageable);

    List<Event> findAllByIsDeleteIsFalseAndSystemGeneratedIsFalseAndServiceTypeAndStatusAndMvnoIdIn(String serviceType, String status, List<Long> mvnoIds);

    Page<Event> findAllByIsDeleteIsFalseAndSystemGeneratedIsFalseAndServiceTypeAndEventNameIsContainingIgnoreCase(String serviceType, String eventName, Pageable pageable);

    Page<Event> findAllByIsDeleteIsFalseAndSystemGeneratedIsFalseAndServiceTypeAndEventNameIsContainingIgnoreCaseAndMvnoIdIn(String serviceType, String eventName, List<Long> mvnoIds, Pageable pageable);

    Event findByIsDeleteIsFalseAndStatusAndServiceTypeAndEventId(String status, String serviceType, Long eventId);

    List<Event> findAllByEmailConfigId(Long emailConfigId);

    List<Event> findAllByIsDeleteIsFalseAndServiceTypeAndSystemGeneratedIsFalse(String serviceType);

    List<Event> findAllByIsDeleteIsFalseAndServiceTypeAndMvnoIdInAndSystemGeneratedIsFalse(String serviceType, List<Long> mvnoId);
}
