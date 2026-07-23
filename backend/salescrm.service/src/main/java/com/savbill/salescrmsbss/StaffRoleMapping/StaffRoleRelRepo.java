package com.savbill.salescrmsbss.StaffRoleMapping;


import com.savbill.salescrmsbss.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffRoleRelRepo extends JpaRepository<StaffRoleRel, Long>, QuerydslPredicateExecutor<Role> {

    List<StaffRoleRel> findByRoleId(Long roleId);
    @Query(value = "select t.roleId from StaffRoleRel t where t.staffId=:staffId")
    List<Long> findRoleIdByStaffId(Long staffId);

    StaffRoleRel findByStaffId(Long staffId);

    List<StaffRoleRel> findAllByStaffIdIn(List<Long> staffId);


}
