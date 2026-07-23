package com.savbill.integrationsystem.rms.repository;

import com.savbill.integrationsystem.rms.entity.WareHouse;
import org.springframework.data.jpa.repository.JpaRepository;


public interface WareHouseRepo extends JpaRepository<WareHouse,Long> {
    WareHouse findByName(String wareHouseName);
}
