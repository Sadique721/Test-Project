package com.savbill.partnermanagement.modules.ClientServ.repository;


import com.savbill.partnermanagement.modules.ClientServ.domain.ClientService;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientServiceRepository extends JpaRepository<ClientService, Integer> {

//    ClientService findByName(String name);

    ClientService getByNameAndMvnoIdIn(String name, List mvnoIds);

//    @Query(value = "select value from tblmclientservice m where m.name=:name",nativeQuery = true)
//    String findValueByName(@Param(value = "name") String name);

    ClientService getByNameAndMvnoId(String name, Integer mvnoIds);

//    ClientService getClientServiceByName(String name);
}
