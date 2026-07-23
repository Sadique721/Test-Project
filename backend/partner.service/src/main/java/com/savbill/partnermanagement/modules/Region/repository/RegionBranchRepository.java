package com.savbill.partnermanagement.modules.Region.repository;


import com.savbill.partnermanagement.modules.Region.domain.RegionBranchMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionBranchRepository extends JpaRepository<RegionBranchMapping, Long> {

    @Query(value = "select * from tbltregionbranchmapping t where t.branchid=:bid",nativeQuery = true)
    List<RegionBranchMapping> findAllBybranchId(@Param("bid")Long bid);

}
