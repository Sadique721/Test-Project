package com.savbill.commonGateway.moules.MasterManagement.BuildingReference.Service;

import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.MasterManagement.BuildingReference.Entity.BuildingRefrence;
import com.savbill.commonGateway.moules.MasterManagement.BuildingReference.Repocitory.BuildingReferenceRepocitory;
import com.savbill.commonGateway.security.dto.LoggedInUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BuildingReferenceService {
    @Autowired
    private BuildingReferenceRepocitory buildingReferenceRepocitory;

    public Boolean isValid(BuildingRefrence buildingRefrence){
      Optional<BuildingRefrence> buildingOptional= buildingReferenceRepocitory.findByNameEqualsIgnoreCaseAndMvnoId(buildingRefrence.getName(),getMvnoIdFromCurrentStaff());
      if(buildingOptional.isPresent()){
          return false;
      }
        return true;
    }

    public BuildingRefrence createBuilding(BuildingRefrence buildingRefrence) {
        buildingRefrence.setMvnoId(getMvnoIdFromCurrentStaff());
       return buildingReferenceRepocitory.save(buildingRefrence);
    }

    public Optional<BuildingRefrence> findById(Long id){
        return buildingReferenceRepocitory.findById(id);
    }

    public Object updateBuilding(BuildingRefrence buildingRefrence, Long id) {
        Optional<BuildingRefrence> building1= buildingReferenceRepocitory.findById(id);
        if(building1.isPresent()){
            building1.get().setName(buildingRefrence.getName());
            building1.get().setMappingFrom(buildingRefrence.getMappingFrom());
           return buildingReferenceRepocitory.save(building1.get());
        }
        return null;
    }

    public String deleteBuilding(Long id) {
        Optional<BuildingRefrence> building1= buildingReferenceRepocitory.findById(id);
        if(building1.isPresent()){
            buildingReferenceRepocitory.delete(building1.get());
        }else{
            return "Error While deleting Building";
        }
        return "Success";
    }

    public List<BuildingRefrence> findAll() {
        List<BuildingRefrence> buildingRefrences=buildingReferenceRepocitory.findAllByMvnoIdEqualsOrderByIdDesc(getMvnoIdFromCurrentStaff());
        return buildingRefrences;
    }
    public Integer getMvnoIdFromCurrentStaff() {
        Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);

        }
        return mvnoId;
    }
}
