package com.savbill.radius.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.radius.entity.Screens;

@Repository
public interface ScreenRepository extends JpaRepository<Screens, Long> {
	
}
