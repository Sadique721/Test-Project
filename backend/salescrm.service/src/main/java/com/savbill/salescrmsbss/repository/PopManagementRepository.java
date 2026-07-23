package com.savbill.salescrmsbss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.PopManagement;

@Repository
public interface PopManagementRepository extends JpaRepository<PopManagement, Long>{

}
