package com.savbill.cpm.modules.DemoGraphicMapping.repository;

import com.savbill.cpm.modules.DemoGraphicMapping.domain.DemoGraphicMappingTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface DemoGraphicMappingRepository extends JpaRepository<DemoGraphicMappingTable, Long> {

//    @Query(value = "select * from tblmdemographicmapping t ", nativeQuery = true)
List<DemoGraphicMappingTable> findAll();

}
