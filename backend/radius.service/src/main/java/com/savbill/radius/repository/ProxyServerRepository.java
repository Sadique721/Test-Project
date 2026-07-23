package com.savbill.radius.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.savbill.radius.entity.ProxyServer;

@Repository
public interface ProxyServerRepository extends JpaRepository<ProxyServer, Long>, QuerydslPredicateExecutor<ProxyServer> {

    Optional<ProxyServer> findByName(String name);
    
	List<ProxyServer> findAll();

}
