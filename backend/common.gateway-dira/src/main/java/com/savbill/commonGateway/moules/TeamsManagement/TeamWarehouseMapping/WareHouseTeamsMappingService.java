package com.savbill.commonGateway.moules.TeamsManagement.TeamWarehouseMapping;

import com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages.SaveWarehouseTeamMappingSharedMessage;
import com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages.UpdateWarehouseTeamMappingSharedMessage;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WareHouseTeamsMappingService {
    @Autowired
    WareHouseTeamsMappingRepo wareHouseTeamsMappingRepo;
    private static final Logger logger = LoggerFactory.getLogger(WareHouseTeamsMappingService.class);
    public void saveWarehouseTeamMapping (SaveWarehouseTeamMappingSharedMessage message) {
        try {
            for (WareHouseTeamsMapping item : message.getWareHouseTeamsMappingList()) {
                WareHouseTeamsMapping wareHouseTeamsMapping = new WareHouseTeamsMapping();
                wareHouseTeamsMapping.setTeamId(item.getTeamId());
                wareHouseTeamsMapping.setWarehouseId(item.getWarehouseId());
                wareHouseTeamsMappingRepo.save(wareHouseTeamsMapping);
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to create warehouse team mapping", e.getMessage());
        }
    }

    public void updateWarehouseTeamMapping (UpdateWarehouseTeamMappingSharedMessage message) {
        try {
            if (message.getOperation().equals(CommonConstants.OPERATION_UPDATE)) {
                List<WareHouseTeamsMapping> wareHouseTeamsMappingList = wareHouseTeamsMappingRepo.findAllByWarehouseId(message.getWarehouseId());
                wareHouseTeamsMappingRepo.deleteAll(wareHouseTeamsMappingList);
                for (WareHouseTeamsMapping item : message.getWareHouseTeamsMappingList()) {
                    WareHouseTeamsMapping wareHouseTeamsMapping = new WareHouseTeamsMapping();
                    wareHouseTeamsMapping.setTeamId(item.getTeamId());
                    wareHouseTeamsMapping.setWarehouseId(item.getWarehouseId());
                    wareHouseTeamsMappingRepo.save(wareHouseTeamsMapping);
                }
            } else if (message.getOperation().equals(CommonConstants.OPERATION_DELETE)) {
                List<WareHouseTeamsMapping> wareHouseTeamsMappingList = wareHouseTeamsMappingRepo.findAllByWarehouseId(message.getWarehouseId());
                wareHouseTeamsMappingRepo.deleteAll(wareHouseTeamsMappingList);
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update warehouse team mapping", e.getMessage());
        }
    }
}
