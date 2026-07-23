package com.savbill.cpm.repository.common;

import com.savbill.cpm.model.postpaid.CustServiceChargeIPDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustServiceChargeIPDetailsRepo extends JpaRepository<CustServiceChargeIPDetails, Integer>, QuerydslPredicateExecutor<CustServiceChargeIPDetails> {

    CustServiceChargeIPDetails findByStaticIPAdrress(String staticIPAdrress);

    @Query(value = "select count(*) from tblcustservicechargipedtls m where m.static_ip_address=:staticIPAddress",nativeQuery = true)
    Integer duplicateIPCheckAtSave(@Param("staticIPAddress")String staticIPAddress);

    @Query(value = "select * from tblcustservicechargipedtls m where m.custid=:custid",nativeQuery = true)
    List<CustServiceChargeIPDetails> findAllByCustid(@Param("custid") Integer custid);
}
