package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.repository;

import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.PolyGone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolyGoneRepository extends JpaRepository<PolyGone, Long>, QuerydslPredicateExecutor<PolyGone> {

    List<PolyGone> findAllByServiceAreaIdAndMvnoid(Integer serviceAreaId, Integer mvnoid);


    List<PolyGone> findAllByMvnoid(Integer mvnoid);

    @Query("SELECT p from PolyGone p left join ServiceArea s on p.serviceAreaId=s.id where s.siteName =:siteName")
    List<PolyGone> findAllByServiceAreaIdSiteName(String siteName);


    Boolean existsByPolygoneNameAndMvnoidAndServiceAreaId(String siteName, Integer mvnoId,Integer serviceAreaId);
}
