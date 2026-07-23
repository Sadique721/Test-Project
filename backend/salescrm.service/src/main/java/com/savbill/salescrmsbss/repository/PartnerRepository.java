package com.savbill.salescrmsbss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.Partner;

import java.util.List;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Integer>{

//    @Query(name = "select * from tblpartners where is_delete = false and name =:name",nativeQuery = true)
//    List<Partner> findByname(String name);

    List<Partner>findByNameAndIsDeleteFalse(String partnerName);

}
