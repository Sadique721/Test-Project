package com.savbill.commonGateway.moules.MasterManagement.BuildingReference.Repocitory;

import com.savbill.commonGateway.moules.MasterManagement.BuildingReference.Entity.BuildingRefrence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildingReferenceRepocitory extends JpaRepository<BuildingRefrence,Long> {
    Optional<BuildingRefrence>findByNameEqualsIgnoreCase(String name);
    Optional<BuildingRefrence>findByNameEqualsIgnoreCaseAndMvnoId(String name,Integer mvnoId);
    List<BuildingRefrence>findAllByMvnoIdEqualsOrderByIdDesc(Integer mvnoId);

    BuildingRefrence findByMvnoId(Integer mvnoId);
}
