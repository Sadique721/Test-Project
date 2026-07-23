package com.savbill.revenuemanagement.core.entity.staff;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffRoleRelRepo extends JpaRepository<StaffRoleRel, Long>{

    List<StaffRoleRel> findByRoleId(Long roleId);
    @Query(value = "select t.roleId from StaffRoleRel t where t.staffId=:staffId")
    List<Long> findRoleIdByStaffId(Long staffId);

    StaffRoleRel findByStaffId(Long staffId);

}
