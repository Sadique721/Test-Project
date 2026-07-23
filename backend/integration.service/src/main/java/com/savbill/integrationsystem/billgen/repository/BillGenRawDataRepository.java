package com.savbill.integrationsystem.billgen.repository;

import com.savbill.integrationsystem.billgen.entity.BillGenRawData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillGenRawDataRepository extends JpaRepository<BillGenRawData, Integer>, QuerydslPredicateExecutor<BillGenRawData> {

    @Query(value = "select * from tblmbillgenrawdata where debit_doc_id =:debit_doc_id",nativeQuery = true)
    List<BillGenRawData> findBydebitDocId(@Param("debit_doc_id") Integer debit_doc_id);
}
