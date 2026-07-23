package com.savbill.salescrmsbss.repository;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.ClientService;

@JaversSpringDataAuditable
@Repository
public interface ClientServiceRepository extends JpaRepository<ClientService, Integer>{

    ClientService getByNameAndMvnoId(String name, Long mvnoIds);

    ClientService findByName(String name);
    ClientService findByNameAndAndMvnoId(String name,Long mvnoId);

//    @Query(value = "select value from tblclientservice m where m.name=:name",nativeQuery = true)
//    String findValueByName(@Param(value = "name") String name);
@Query(value = "SELECT MAX(m.id) FROM ClientService m")
Integer findlast();
}
