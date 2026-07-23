package com.savbill.radius.repository;

import com.savbill.radius.entity.CustIpMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustIpMappingRepo extends JpaRepository<CustIpMapping, Integer> {


    boolean existsByIpAddressIn(List<String> ipAddress);

    List<CustIpMapping> getAllByCustid(Integer custId);
}
