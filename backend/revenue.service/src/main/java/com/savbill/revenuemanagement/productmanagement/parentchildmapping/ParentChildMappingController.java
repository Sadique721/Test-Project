package com.savbill.revenuemanagement.productmanagement.parentchildmapping;

import com.savbill.revenuemanagement.core.constants.APIConstants;
import com.savbill.revenuemanagement.core.constants.UrlConstants;
import com.savbill.revenuemanagement.core.dto.common.GenericDataDTO;
import com.savbill.revenuemanagement.core.dto.common.PaginationRequestDTO;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping(UrlConstants.REVENUE+UrlConstants.PARENT_CHILD_MANAGEMENT)
public class ParentChildMappingController {

    @Autowired
    private ParentChildMappinService parentChildMappinService;

    private static final Logger logger = LoggerFactory.getLogger(ParentChildMappingController.class);

    @GetMapping(value = "/getwallet")
    public GenericDataDTO getwallet(@RequestParam("childId") Integer childId){
        GenericDataDTO dataDTO = new GenericDataDTO();
        try{
            Double walletAmount = parentChildMappinService.getWalletAmount(childId);
            dataDTO.setResponseCode(APIConstants.SUCCESS);
            dataDTO.setData(walletAmount);
            dataDTO.setResponseMessage("Wallet amount found successfully.");
        } catch (CustomValidationException e) {
            dataDTO.setResponseCode(e.getErrCode());
            dataDTO.setResponseMessage(e.getMessage());
        }
        catch (Exception e) {
            dataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            dataDTO.setResponseMessage(e.getMessage());
        }finally {
            logger.info(":::::::parent child wallet ended :::::::::");
        }
        return dataDTO;
    }

    @PostMapping(value = "/getchildledger")
    public GenericDataDTO getChildLedger(@RequestParam("childId") Integer childId, @RequestBody PaginationRequestDTO paginationRequestDTO){
        GenericDataDTO dataDTO = new GenericDataDTO();
        try{
            GenericDataDTO genericDataDTO =  parentChildMappinService.getCustLedgerByChild(childId,paginationRequestDTO);
            if(genericDataDTO.getPageRecords() > 0) {
                dataDTO = genericDataDTO;
                dataDTO.setResponseCode(APIConstants.SUCCESS);
                dataDTO.setResponseMessage("Ledger for child management found.");
            }
            else {
                dataDTO.setResponseCode(204);
                dataDTO.setData(new ArrayList<>());
                dataDTO.setResponseMessage("No Record found.");
            }
        } catch (CustomValidationException e) {
            dataDTO.setResponseCode(e.getErrCode());
            dataDTO.setResponseMessage(e.getMessage());
        }
        catch (Exception e) {
            dataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            dataDTO.setResponseMessage(e.getMessage());
        }finally {
            logger.info(":::::::parent child ledger ended :::::::::");
        }
        return dataDTO;
    }

}
