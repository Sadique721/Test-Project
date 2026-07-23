package com.savbill.cpm.modules.InventoryManagement.CommonInterfaces;

import com.savbill.cpm.model.common.ClientService;
import com.savbill.cpm.repository.common.ClientServiceRepository;
import com.savbill.cpm.spring.LoggedInUser;
import com.savbill.cpm.utils.CommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class InventoryClientService {


    @Autowired
    private final InventoryClient inventoryClient;

    @Autowired
    ClientServiceRepository clientServiceRepository;


    public InventoryClientService(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }


    public String getManufacturerName(String Token, Integer customerId,String connectionNumber){
        return  inventoryClient.getManufacturerName(Token,customerId,connectionNumber);
    }
    public String getVerifiedManufacturerName(String token,Integer customerId, String connectionNumber){
        return getManufacturerName(token,customerId,connectionNumber);
    }



    public boolean getProductVarifiedWithCDATAManufacturer(String Token, Integer customerId,String connectionNumber, String manufacturerName){
        return  inventoryClient.getProductVarifiedWithCDATAManufacturer(Token,customerId,connectionNumber,manufacturerName);
    }
    public boolean verifyAndInitiateCdataCreateRequest(String token,Integer customerId, String connectionNumber){

        // get checked C-DATA manufacturer system configuration.
        ClientService clientService = clientServiceRepository.getByNameAndMvnoId(CommonConstants.CDATA_CONSTANTS.CDATA_MANUFACTURER, getMvnoIdFromCurrentStaff());

        return getProductVarifiedWithCDATAManufacturer(token,customerId,connectionNumber,clientService.getValue());
    }


    public Integer getMvnoIdFromCurrentStaff() {
        Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            //        ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoId;
    }
}
