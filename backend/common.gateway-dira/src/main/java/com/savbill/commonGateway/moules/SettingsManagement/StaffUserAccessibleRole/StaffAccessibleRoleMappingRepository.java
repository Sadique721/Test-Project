package com.savbill.commonGateway.moules.SettingsManagement.StaffUserAccessibleRole;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffAccessibleRoleMappingRepository extends JpaRepository<StaffAccessibleRoleMapping,Long> {

    @Query(nativeQuery = true,value = "select staffaccessibleroleid from tbltstaffaccessiblerole t where t.staffid = :staffId")
    Optional<List<Long>> findAccessibleRolesByStaffId(@Param("staffId") Integer staffId);


    List<StaffAccessibleRoleMapping> findAllByStaffId(Integer staffId);
}
