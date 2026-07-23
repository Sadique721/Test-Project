package com.savbill.salescrmsbss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.Area;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long>{

}
