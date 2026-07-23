package com.savbill.cpm.repository;

import com.savbill.cpm.model.common.CustomerNotes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerNotesRepository extends JpaRepository<CustomerNotes, Integer>{
    @Query(value = "SELECT * FROM tbltcustomernotes cn WHERE cn.custid = :customerId order by cn.customer_notes_id DESC",nativeQuery = true)
    Page<CustomerNotes> findByCustomerId(@Param("customerId") Integer customerId, Pageable pageable);

    // New method: fetch all customer notes without pagination
    @Query(value = "SELECT * FROM tbltcustomernotes cn WHERE cn.custid = :customerId ORDER BY cn.customer_notes_id DESC", nativeQuery = true)
    List<CustomerNotes> findAllByCustomerId(@Param("customerId") Integer customerId);

}