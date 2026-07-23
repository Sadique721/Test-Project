package com.savbill.revenuemanagement.PaymentTransfer;

import com.savbill.revenuemanagement.core.constants.APIConstants;
import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.constants.UrlConstants;
import com.savbill.revenuemanagement.core.dto.common.GenericDataDTO;
import com.savbill.revenuemanagement.core.dto.common.PaginationRequestDTO;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Objects;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.TRANSFER_MANAGEMENT)
public class PaymentTransferController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentTransferController.class);

    @Autowired
    private PaymentTransferService paymentTransferService;

    @PostMapping(value = "/wallettransfer")
    public GenericDataDTO transferWalletAmount(@Valid @RequestBody PaymentTransferDTO paymentTransferDTO){
        GenericDataDTO dataDTO = new GenericDataDTO();
        try{
            if(Objects.isNull(paymentTransferDTO)){
                dataDTO.setData(Constants.TRANSFER_RESPONSE_MESSAGES.TRANSFER_NULL);
                dataDTO.setResponseCode(APIConstants.NO_CONTENT_FOUND);
                dataDTO.setResponseMessage(Constants.SCHEDULER_RESPONSE_MESSAGES.SUCCESS);
                return dataDTO;
            }
            paymentTransferService.verifyTransfer(paymentTransferDTO);
            paymentTransferService.createTransferPayment(paymentTransferDTO);
            dataDTO.setResponseCode(APIConstants.SUCCESS);
            dataDTO.setResponseMessage("Payment transfer successfully.");
        } catch (CustomValidationException e) {
            dataDTO.setResponseCode(e.getErrCode());
            dataDTO.setResponseMessage(e.getMessage());
        }
        catch (Exception e) {
            dataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            dataDTO.setResponseMessage(e.getMessage());
        }finally {
            logger.info(":::::::Transfer ended :::::::::");
        }
        return dataDTO;
    }

    @PostMapping(value = "/getTransferAudit")
    public GenericDataDTO getTransferAudit(@RequestParam("custId") Integer custId, @RequestBody PaginationRequestDTO paginationRequestDTO){
        GenericDataDTO dataDTO = new GenericDataDTO();
        try{
            GenericDataDTO genericDataDTO =  paymentTransferService.getPaginatedAuditLog(paginationRequestDTO,custId);
            if(genericDataDTO.getPageRecords() > 0) {
                dataDTO = genericDataDTO;
                dataDTO.setResponseCode(APIConstants.SUCCESS);
                dataDTO.setResponseMessage("Transfer audit found.");
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
            logger.info(":::::::Payment transfer started ended :::::::::");
        }
        return dataDTO;
    }

}
