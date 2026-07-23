package com.savbill.inventorymanagement.modules.ClientService;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
//@JaversSpringDataAuditable
public interface ClientServiceRepository extends JpaRepository<ClientService, Integer>, QuerydslPredicateExecutor<ClientService> {

    ClientService findByName(String name);

    ClientService getByNameAndMvnoIdIn(String name, List mvnoIds);

    @Query(value = "select value from tblmclientservice m where m.name=:name and m.mvnoId=:mvnoId",nativeQuery = true)
    String findValueByNameAndMvnoId(@Param(value = "name") String name,@Param(value = "mvnoId") Integer mvnoId);

    ClientService getByNameAndMvnoId(String name, Integer mvnoIds);


    ClientService findByNameAndMvnoId(String name, Integer mvnoId);
}
