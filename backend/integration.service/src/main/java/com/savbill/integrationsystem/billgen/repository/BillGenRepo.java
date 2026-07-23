package com.savbill.integrationsystem.billgen.repository;

import com.savbill.integrationsystem.billgen.entity.BillGenRawData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillGenRepo extends JpaRepository<BillGenRawData, Integer> {

}
