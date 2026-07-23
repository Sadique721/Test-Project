package com.savbill.taskmanagement.core.modules.ClientServ.repository;


import com.savbill.taskmanagement.core.modules.ClientServ.domain.ClientService;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientServiceRepository extends JpaRepository<ClientService, Integer> {

//    ClientService findByName(String name);

    ClientService findByNameAndMvnoId(String name, Integer mvnoIds);
    ClientService findByNameEqualsIgnoreCaseAndMvnoId(String name, Integer mvnoIds);

    ClientService getByNameAndMvnoIdIn(String name, List<Integer> mvnoIds);

//    @Query(value = "select value from tblclientservice m where m.name=:name",nativeQuery = true)
//    String findValueByName(@Param(value = "name") String name);

    @Query(value = "select value from tblclientservice m where m.name=:name and m.mvnoid=:mvnoid",nativeQuery = true)
    String findValueByNameAndMvnoId(@Param(value = "name") String name,@Param(value = "mvnoid") Integer mvnoid);


//    ClientService getClientServiceByName(String name);
    ClientService getClientServiceByNameAndMvnoId(String name,Integer mvnoId);

    @Query(value ="select COALESCE(serviceid + 1)  from tblclientservice t order by serviceid desc limit 1",nativeQuery = true)
    Integer getLatestClientServiceId();

}
