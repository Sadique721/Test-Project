package com.savbill.salescrmsbss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.PlanGroup;

import java.util.List;

@Repository
public interface PlanGroupRepository extends JpaRepository<PlanGroup, Integer>{

    @Query(name = "select * from tblmplangroup where is_delete = false and planGroupName =:name",nativeQuery = true)
    List<PlanGroup> findByplanGroupName(String name);

    @Query("select t.planGroupName from PlanGroup t where t.planGroupId=:id")
    String findAllByPlanGroupId(Integer id);

}
