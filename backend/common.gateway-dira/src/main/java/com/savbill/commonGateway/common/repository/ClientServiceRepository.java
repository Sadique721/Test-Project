package com.savbill.commonGateway.common.repository;


import com.savbill.commonGateway.common.domain.ClientService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
//@JaversSpringDataAuditable
public interface ClientServiceRepository extends JpaRepository<ClientService, Integer> {


    ClientService findByName(String name);
    List<ClientService>findAllByMvnoIdIn(List<Integer> mvnoIds);
    List<ClientService>findAllByMvnoId(Integer mvnoIds);
    List<ClientService> findByNameContainingIgnoreCase(String name);
    ClientService findByNameContainingIgnoreCaseAndMvnoIdEquals(String name,Integer mvnoId);
    ClientService getByNameAndMvnoIdIn(String name, List<Integer> mvnoIds);
    ClientService getByNameAndMvnoId(String name, Integer mvnoIds);

    List<ClientService> getByNameContainingIgnoreCaseAndMvnoIdIn(String name, List<Integer> mvnoIds);

    List<ClientService> getByNameContainingIgnoreCaseAndMvnoId(String name, Integer mvnoIds);

//    @Query(value = "select value from tblclientservice m where m.name=:name",nativeQuery = true)
//    String findValueByName(@Param(value = "name") String name);



    Integer countByName(String name);
    Integer countByNameAndMvnoIdIn(String name, List<Integer> mvnoId);
    Integer countByNameAndMvnoId(String name, Integer mvnoId);

    Integer countByIdAndName(Integer id, String name);
    Integer countByIdAndNameAndMvnoIdIn(Integer id, String name, List<Integer> mvnoId);
    Integer countByIdAndNameAndMvnoId(Integer id, String name, Integer mvnoId);

    Integer countByNameAndMvnoIdEquals(String name, Integer mvnoId);
    @Query(value = "SELECT  c.value FROM ClientService c WHERE c.mvnoId = :mvnoId AND c.name = :mobile")
    String getValuesByMvnoId(@Param("mvnoId") Integer mvnoId, @Param("mobile") String mobile);

    @Query("SELECT cs FROM ClientService cs WHERE LOWER(cs.name) = LOWER(:name) AND cs.mvnoId IN :mvnoIds")
    List<ClientService> findByNameAndMvnoIds(@Param("name") String name, @Param("mvnoIds") List<Integer> mvnoIds);


}
