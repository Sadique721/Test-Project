package com.savbill.notification.repository;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.notification.entity.Screens;
@JaversSpringDataAuditable
@Repository
public interface ScreenRepository extends JpaRepository<Screens, Long> {
	
}
