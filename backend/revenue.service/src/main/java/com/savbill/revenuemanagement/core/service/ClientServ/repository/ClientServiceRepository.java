package com.savbill.revenuemanagement.core.service.ClientServ.repository;


import com.savbill.revenuemanagement.core.service.ClientServ.domain.ClientService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientServiceRepository extends JpaRepository<ClientService, Integer> {

   // ClientService findByName(String name);
    ClientService findByNameAndMvnoId(String name,Integer mvnoid);

    ClientService getByNameAndMvnoIdIn(String name, List mvnoIds);

    @Query(value = "select value from tblclientservice m where m.name=:name and m.MVNOID=:mvnoId",nativeQuery = true)
    String findValueByNameAndMvnoId(@Param(value = "name") String name,Integer mvnoId);

//    @Query(value = "select value from tblclientservice m where m.name=:name",nativeQuery = true)
//    String findValueByName(@Param(value = "name") String name);

    ClientService getByNameAndMvnoId(String name, Integer mvnoIds);


    ClientService getClientServiceByName(String name);
    @Query(value = "SELECT MAX(m.id) FROM ClientService m")
    Integer findlast();

    List<ClientService> findAllByNameAndMvnoIdIn(String name, List mvnoIds);

}
