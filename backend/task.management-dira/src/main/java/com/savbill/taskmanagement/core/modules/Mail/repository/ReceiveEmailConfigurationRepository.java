package com.savbill.taskmanagement.core.modules.Mail.repository;

import com.savbill.taskmanagement.core.modules.Mail.domain.ReceiveEmailConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface ReceiveEmailConfigurationRepository extends JpaRepository<ReceiveEmailConfiguration,Long>, QuerydslPredicateExecutor<ReceiveEmailConfiguration> {

    List<ReceiveEmailConfiguration> findAll();

    ReceiveEmailConfiguration findByName(String name);

    ReceiveEmailConfiguration findByUserName(String name);
}
