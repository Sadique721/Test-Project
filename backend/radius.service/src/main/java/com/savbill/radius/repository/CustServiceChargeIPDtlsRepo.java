package com.savbill.radius.repository;

import com.savbill.radius.entity.CustServiceChargeIPDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustServiceChargeIPDtlsRepo extends JpaRepository<CustServiceChargeIPDetails, Integer> {
    CustServiceChargeIPDetails findByStaticIPAdrress(String staticIPAdrress);
}

