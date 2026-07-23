package com.savbill.integrationsystem.integrationMenuMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThirdPartyIntegrationMenuMappingRepository extends JpaRepository<ThirdPartyIntegrationMenuMapping,Long> {


    @Query(nativeQuery = true,value = "select * from tblmthirdpartymenumappinng t where t.third_party_menu_id = :id")
    List<ThirdPartyIntegrationMenuMapping> findAllByThirdPartyMenuId(Long id) ;

}
