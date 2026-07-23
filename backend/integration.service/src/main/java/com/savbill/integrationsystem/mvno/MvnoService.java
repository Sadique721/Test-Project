package com.savbill.integrationsystem.mvno;

import com.savbill.integrationsystem.RestApiService.recordpayment.SearchPaymentPojo;
import com.savbill.integrationsystem.core.CommonConstant;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MvnoService {

    @Autowired
    private MvnoRepository mvnoRepository;

    public MvnoDTO getMvnoDto(IspDto ispDto){
        try{

            MvnoDTO mvnoDTO = new MvnoDTO();
            mvnoDTO.setName(ispDto.getName());
            mvnoDTO.setEmail(ispDto.getEmail());
            mvnoDTO.setClientId(ispDto.getClientId());
            mvnoDTO.setFullName(ispDto.getFullName());
            mvnoDTO.setDescription(ispDto.getDescription());
            mvnoDTO.setAddress(ispDto.getAddress());
            mvnoDTO.setUsername("admin@"+ispDto.getName());
            mvnoDTO.setPassword(CommonConstant.DEFAULT_PASSWORD);
            mvnoDTO.setPhone("1234567890");
            mvnoDTO.setStatus("Active");
            mvnoDTO.setSuffix("");
            mvnoDTO.setLogfile("");
            mvnoDTO.setRoleId(1l);
            mvnoDTO.setMvnoHeader("");
            mvnoDTO.setMvnoFooter("");
            mvnoDTO.setIsTwoFactorEnabled(false);
            mvnoDTO.setIspCommissionPercentage(100d);
            mvnoDTO.setIspBillDay(1);
            mvnoDTO.setMvnoPaymentDueDays(6);
            mvnoDTO.setPasswordPolicyId(1l);
            mvnoDTO.setProfileId(1l);
            return mvnoDTO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SearchPaymentPojo approvePayment(GenericDataDTO dataDTO, PaymentDto paymentDto) {
        SearchPaymentPojo searchPaymentPojo = new SearchPaymentPojo();
        try{

            ObjectMapper objectMapper = new ObjectMapper();
            ApproveDto approveDto = objectMapper.convertValue(dataDTO.getData(), ApproveDto.class);
            searchPaymentPojo.setCustomerid(approveDto.getCustId());
            searchPaymentPojo.setIdlist(approveDto.getCreditDocID().toString());
            searchPaymentPojo.setInvoiceNumber(paymentDto.getInvoiceNo());
            searchPaymentPojo.setPaymode("Online");
            searchPaymentPojo.setPaystatus("pending");
//            searchPaymentPojo.setPaytodate(LocalDate.parse(formatter.format(LocalDate.now())));
            searchPaymentPojo.setReferenceno("");
            searchPaymentPojo.setRemarks("ISP Payment SuccessFully done by NetSuit");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return searchPaymentPojo;
    }

    @Transactional
    public void saveMVNOEntity(SaveMvnoSharedDataMessage mvnoSharedDataMessage) throws Exception{
        try {
            Mvno mvno = new Mvno();
            mvno.setFullName(mvnoSharedDataMessage.getFullName());
            mvno.setId(mvnoSharedDataMessage.getId());
            mvno.setName(mvnoSharedDataMessage.getName());
            mvno.setUsername(mvnoSharedDataMessage.getUsername());
            mvno.setPassword(mvnoSharedDataMessage.getPassword());
            mvno.setSuffix(mvnoSharedDataMessage.getSuffix());
            mvno.setDescription(mvnoSharedDataMessage.getDescription());
            mvno.setEmail(mvnoSharedDataMessage.getEmail());
            mvno.setPhone(mvnoSharedDataMessage.getPhone());
            mvno.setStatus(mvnoSharedDataMessage.getStatus());
            mvno.setLogfile(mvnoSharedDataMessage.getLogfile());
            mvno.setMvnoHeader(mvnoSharedDataMessage.getMvnoHeader());
            mvno.setMvnoFooter(mvnoSharedDataMessage.getMvnoFooter());
            mvno.setIsDelete(mvnoSharedDataMessage.getIsDelete());
            mvno.setMvnoPaymentDueDays(mvnoSharedDataMessage.getMvnoPaymentDueDays());
            mvno.setClientId(mvnoSharedDataMessage.getClientId());
            mvno = mvnoRepository.save(mvno);
            ApplicationLogger.logger.info("MVNO created successfully with name " + mvnoSharedDataMessage.getName());
        } catch (CustomValidationException e) {
            ApplicationLogger.logger.error("Unable to create mvno with name " + mvnoSharedDataMessage.getName(), e.getMessage());
        }
    }
    @Transactional
    public void updateMVNOEntity(UpdateMvnoSharedDataMessage updateMvnoSharedDataMessage) throws Exception {
        try {
            Mvno mvno = mvnoRepository.findById(updateMvnoSharedDataMessage.getId()).orElse(null);
            if(mvno != null) {
                mvno.setFullName(updateMvnoSharedDataMessage.getFullName());
                mvno.setId(updateMvnoSharedDataMessage.getId());
                mvno.setName(updateMvnoSharedDataMessage.getName());
                mvno.setUsername(updateMvnoSharedDataMessage.getUsername());
                mvno.setPassword(updateMvnoSharedDataMessage.getPassword());
                mvno.setSuffix(updateMvnoSharedDataMessage.getSuffix());
                mvno.setDescription(updateMvnoSharedDataMessage.getDescription());
                mvno.setEmail(updateMvnoSharedDataMessage.getEmail());
                mvno.setPhone(updateMvnoSharedDataMessage.getPhone());
                mvno.setStatus(updateMvnoSharedDataMessage.getStatus());
                mvno.setLogfile(updateMvnoSharedDataMessage.getLogfile());
                mvno.setMvnoHeader(updateMvnoSharedDataMessage.getMvnoHeader());
                mvno.setMvnoFooter(updateMvnoSharedDataMessage.getMvnoFooter());
                mvno.setIsDelete(updateMvnoSharedDataMessage.getIsDelete());
                mvno.setMvnoPaymentDueDays(updateMvnoSharedDataMessage.getMvnoPaymentDueDays());
                mvno.setClientId(updateMvnoSharedDataMessage.getClientId());
                mvno = mvnoRepository.save(mvno);
            }
            else{
                Mvno newMvno = new Mvno();
                newMvno.setFullName(updateMvnoSharedDataMessage.getFullName());
                newMvno.setId(updateMvnoSharedDataMessage.getId());
                newMvno.setName(updateMvnoSharedDataMessage.getName());
                newMvno.setUsername(updateMvnoSharedDataMessage.getUsername());
                newMvno.setPassword(updateMvnoSharedDataMessage.getPassword());
                newMvno.setSuffix(updateMvnoSharedDataMessage.getSuffix());
                newMvno.setDescription(updateMvnoSharedDataMessage.getDescription());
                newMvno.setEmail(updateMvnoSharedDataMessage.getEmail());
                newMvno.setPhone(updateMvnoSharedDataMessage.getPhone());
                newMvno.setStatus(updateMvnoSharedDataMessage.getStatus());
                newMvno.setLogfile(updateMvnoSharedDataMessage.getLogfile());
                newMvno.setMvnoHeader(updateMvnoSharedDataMessage.getMvnoHeader());
                newMvno.setMvnoFooter(updateMvnoSharedDataMessage.getMvnoFooter());
                newMvno.setIsDelete(updateMvnoSharedDataMessage.getIsDelete());
                newMvno.setMvnoPaymentDueDays(updateMvnoSharedDataMessage.getMvnoPaymentDueDays());
                newMvno.setClientId(updateMvnoSharedDataMessage.getClientId());
                newMvno = mvnoRepository.save(newMvno);

            }
            ApplicationLogger.logger.info("MVNO updated successfully with name " + updateMvnoSharedDataMessage.getName());
        } catch (CustomValidationException e) {
            ApplicationLogger.logger.error("Unable to update mvno with name " + updateMvnoSharedDataMessage.getName(), e.getMessage());
        }
    }

}

