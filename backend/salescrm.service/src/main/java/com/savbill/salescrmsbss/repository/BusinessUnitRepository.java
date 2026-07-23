package com.savbill.salescrmsbss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.BusinessUnit;

@Repository
public interface BusinessUnitRepository extends JpaRepository<BusinessUnit, Long>{

}
