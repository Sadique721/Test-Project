package com.savbill.taskmanagement.core.modules.tasks.repository;

import com.savbill.taskmanagement.core.modules.tasks.domain.CustomerTaskFileMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerTaskFileMappingRepo extends JpaRepository<CustomerTaskFileMapping, Long> {

    @Query(value = "SELECT * FROM tbltcustomer_task_file_mapping WHERE customer_task_mapping_id = :customerTaskMappingId",
            nativeQuery = true)
    List<CustomerTaskFileMapping> findByCustomerTaskMappingId(@Param("customerTaskMappingId") Long customerTaskMappingId);

    @Query(value = "SELECT * FROM tbltcustomer_task_file_mapping WHERE uniquename = :uniquename",
            nativeQuery = true)
    CustomerTaskFileMapping findByCustomerTaskByUniqueName(@Param("uniquename") String uniquename);

    Optional<CustomerTaskFileMapping> findByCustomerTaskMappingAndSection(Long customerTaskMappingId, String section);


}