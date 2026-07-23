package com.savbill.revenuemanagement.core.repository.dbr;

import com.savbill.revenuemanagement.core.entity.DBR.TempCustomerDBR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TempCustomerDBRRepository extends JpaRepository<TempCustomerDBR, Long>
{
    @Query(value ="SELECT * FROM tbltmpcustomerdbr t WHERE t.cprid IN :cprIds ", nativeQuery = true)
    List<TempCustomerDBR> findAll(@Param("cprIds") List<Long> cprIds);
}

