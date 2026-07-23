package com.savbill.cpm.modules.BuildingMgmt.Service;

import com.savbill.cpm.MicroSeviceDataShare.SharedMessages.BuildingMgmtMessage;
import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.core.service.ExBaseAbstractService;
import com.savbill.cpm.modules.BuildingMgmt.DTO.BuildingManagementDTO;
import com.savbill.cpm.modules.BuildingMgmt.Domain.BuildingManagement;
import com.savbill.cpm.modules.BuildingMgmt.Domain.BuildingMapping;
import com.savbill.cpm.modules.BuildingMgmt.Mapper.BuildingMappingMapper;
import com.savbill.cpm.modules.BuildingMgmt.Mapper.BuildingMgmtMapper;
import com.savbill.cpm.modules.BuildingMgmt.Repository.BuildingMappingRepository;
import com.savbill.cpm.modules.BuildingMgmt.Repository.BuildingMgmtRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BuildingMgmtService extends ExBaseAbstractService<BuildingManagementDTO, BuildingManagement,Long> {


    @Autowired
    BuildingMgmtRepository buildingMgmtRepository;


    @Autowired
    BuildingMgmtMapper buildingMgmtMapper;


    @Autowired
    BuildingMappingRepository buildingMappingRepository;


    @Autowired
    BuildingMappingMapper buildingMappingMapper;



    public BuildingMgmtService(JpaRepository<BuildingManagement, Long> repository, IBaseMapper<BuildingManagementDTO, BuildingManagement> mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "BuildingMgmtService";
    }



    public void saveEntity(BuildingMgmtMessage buildingMgmtMessage){
        try{
            BuildingManagementDTO buildingManagement = new BuildingManagementDTO();
            buildingManagement.setBuildingMgmtId(buildingMgmtMessage.getBuildingMgmtId());
            buildingManagement.setBuildingName(buildingMgmtMessage.getBuildingName());
            buildingManagement.setMvnoId(buildingMgmtMessage.getMvnoId());
            buildingManagement.setBuid(buildingMgmtMessage.getBuid());
            buildingManagement.setIsDeleted(buildingMgmtMessage.getIsDeleted());
            buildingManagement.setAreaId(buildingMgmtMessage.getSubAreaId());
            buildingManagement.setPincodeId(buildingMgmtMessage.getPincodeId());
            buildingManagement.setSubAreaId(buildingMgmtMessage.getSubAreaId());
            buildingManagement.setBuildingMappings(buildingMgmtMessage.getBuildingMappings());
            buildingManagement.setBuildingType(buildingMgmtMessage.getBuildingType());
            BuildingManagement  management = buildingMgmtMapper.dtoToDomain(buildingManagement,new CycleAvoidingMappingContext());
            buildingMgmtRepository.save(management);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void UpdateEntity(BuildingMgmtMessage buildingMgmtMessage){
        try{
            BuildingManagement buildingManagement = buildingMgmtRepository.findById(buildingMgmtMessage.getBuildingMgmtId()).orElse(null);
            if(buildingManagement!=null){
                buildingManagement.setBuildingMgmtId(buildingMgmtMessage.getBuildingMgmtId());
                buildingManagement.setBuildingName(buildingMgmtMessage.getBuildingName());
                buildingManagement.setMvnoId(buildingMgmtMessage.getMvnoId());
                buildingManagement.setBuid(buildingMgmtMessage.getBuid());
                buildingManagement.setIsDeleted(buildingMgmtMessage.getIsDeleted());
                buildingManagement.setAreaId(buildingMgmtMessage.getSubAreaId());
                buildingManagement.setPincodeId(buildingMgmtMessage.getPincodeId());
                buildingManagement.setSubAreaId(buildingMgmtMessage.getSubAreaId());
                buildingManagement.setBuildingType(buildingMgmtMessage.getBuildingType());
                if(buildingMgmtMessage.getBuildingMappings()!=null && !buildingMgmtMessage.getBuildingMappings().isEmpty()){
                    List<Long> mappingIds = buildingManagement.getBuildingMappings().stream().map(BuildingMapping::getId).collect(Collectors.toList());
                    buildingMappingRepository.deleteByIds(mappingIds);
                    buildingManagement.setBuildingMappings(buildingMappingMapper.dtoToDomain(buildingMgmtMessage.getBuildingMappings(),new CycleAvoidingMappingContext()));
                }else{
                    List<BuildingMapping> buildingMappings = buildingMappingRepository.findAllByBuildingManagement(buildingManagement);
                    List<Long> mappingIds = buildingMappings.stream().map(BuildingMapping::getId).collect(Collectors.toList());
                    buildingMappingRepository.deleteByIds(mappingIds);
                    buildingManagement.setBuildingMappings(null);
                }
                buildingMgmtRepository.save(buildingManagement);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
