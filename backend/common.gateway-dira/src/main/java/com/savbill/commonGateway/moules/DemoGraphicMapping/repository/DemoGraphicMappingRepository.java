package com.savbill.commonGateway.moules.DemoGraphicMapping.repository;

import com.savbill.commonGateway.moules.DemoGraphicMapping.domain.DemoGraphicMappingTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DemoGraphicMappingRepository extends JpaRepository<DemoGraphicMappingTable, Long> {

////    @Query(value = "select * from tblmdemographicmapping t ", nativeQuery = true)
//List<DemoGraphicMappingTable> findAll();

    Optional<DemoGraphicMappingTable> findAllByNewName(@Param("currentName") String currentname);

}
