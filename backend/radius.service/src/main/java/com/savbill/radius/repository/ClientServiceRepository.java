package com.savbill.radius.repository;

import com.savbill.radius.entity.ClientServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
//@JaversSpringDataAuditable
public interface ClientServiceRepository extends JpaRepository<ClientServiceEntity, Integer> {

   // ClientService findByName(String name);
//    ClientService findByNameAndMvnoId(String name, Integer mvnoId);

//    ClientService getByNameAndMvnoIdIn(String name, List mvnoIds);


//    ClientService getByNameAndMvnoIdEquals(String name, Integer mvnoId);
    ClientServiceEntity getByNameAndMvnoId(String name, Integer mvnoIds);

}
