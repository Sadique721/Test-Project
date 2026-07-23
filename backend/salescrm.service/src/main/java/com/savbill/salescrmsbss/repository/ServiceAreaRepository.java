package com.savbill.salescrmsbss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.ServiceArea;

import java.util.List;

@Repository
public interface ServiceAreaRepository extends JpaRepository<ServiceArea, Long>{

    List<ServiceArea> findAllByNameContainingIgnoreCase(String name);
}
