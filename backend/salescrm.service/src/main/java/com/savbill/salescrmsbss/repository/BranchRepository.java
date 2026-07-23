package com.savbill.salescrmsbss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.Branch;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Integer>{

//    @Query(name = "select * from tblmbranch where isDeleted = false and name =:name",nativeQuery = true)
//    List<Branch> findByname(String name);

      List<Branch> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name);

      List<Branch> findAllByIsDeletedFalseAndMvnoId(Integer mvnoId);

}
