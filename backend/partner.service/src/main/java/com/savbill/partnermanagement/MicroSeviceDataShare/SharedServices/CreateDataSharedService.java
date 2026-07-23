package com.savbill.partnermanagement.MicroSeviceDataShare.SharedServices;


//import com.savbill.partnermanagement.MicroSeviceDataShare.MessageSender.DataSharedMessageSender;
import com.savbill.partnermanagement.MicroSeviceDataShare.SharedMessages.SavePartnerSharedDataMessage;
import com.savbill.partnermanagement.MicroSeviceDataShare.SharedMessages.UpdatePartnerSharedDataMessage;
import com.savbill.partnermanagement.core.utillity.log.ApplicationLogger;
import com.savbill.partnermanagement.kafka.KafkaConstant;
import com.savbill.partnermanagement.kafka.KafkaMessageData;
import com.savbill.partnermanagement.kafka.KafkaMessageSender;
import com.savbill.partnermanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.partnermanagement.modules.partner.dto.PartnerPojo;
import com.savbill.partnermanagement.modules.partner.entity.Partner;
import com.savbill.partnermanagement.security.dto.LoggedInUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
public class CreateDataSharedService {

//    @Autowired
//    DataSharedMessageSender messageSender;

    @Autowired
    KafkaMessageSender kafkaMessageSender;

    //SAVE ENTITY COMMON SERVICE
    public void sendEntitySaveDataForAllMicroService(Object object) throws JsonProcessingException {

         if (Objects.nonNull(object) && object.getClass().equals(Partner.class)) {
            //All data of Partner entity while saving
            SavePartnerSharedDataMessage savePartnerSharedDataMessage = new SavePartnerSharedDataMessage();
            savePartnerSharedDataMessage.setId(((Partner) object).getId());
            savePartnerSharedDataMessage.setName(((Partner) object).getName());
            savePartnerSharedDataMessage.setPrcode(((Partner) object).getPrcode());
            savePartnerSharedDataMessage.setStatus(((Partner) object).getStatus());
            savePartnerSharedDataMessage.setCommtype(((Partner) object).getCommtype());
            savePartnerSharedDataMessage.setCommrelvalue(((Partner) object).getCommrelvalue());
            savePartnerSharedDataMessage.setBalance(((Partner) object).getBalance());
            savePartnerSharedDataMessage.setCommdueday(((Partner) object).getCommdueday());
            savePartnerSharedDataMessage.setCommissionInterval(((Partner) object).getCommissionInterval());
            if(((Partner) object).getNextbilldate()!=null)
                 savePartnerSharedDataMessage.setNextbilldate(String.valueOf(((Partner) object).getNextbilldate()).toString());
            else
                savePartnerSharedDataMessage.setNextbilldate(String.valueOf(LocalDate.now()).toString());

            if(((Partner) object).getLastbilldate()!=null)
                 savePartnerSharedDataMessage.setLastbilldate(String.valueOf(((Partner) object).getLastbilldate()).toString());
            else
                savePartnerSharedDataMessage.setLastbilldate(String.valueOf(LocalDate.now()).toString());
            savePartnerSharedDataMessage.setTaxid(((Partner) object).getTaxid());
            savePartnerSharedDataMessage.setAddresstype(((Partner) object).getAddresstype());
            savePartnerSharedDataMessage.setAddress1(((Partner) object).getAddress1());
            savePartnerSharedDataMessage.setAddress2(((Partner) object).getAddress2());
            savePartnerSharedDataMessage.setCredit(((Partner) object).getCredit());
            savePartnerSharedDataMessage.setCity(((Partner) object).getCity());
            savePartnerSharedDataMessage.setState(((Partner) object).getState());
            savePartnerSharedDataMessage.setCountry(((Partner) object).getCountry());
            savePartnerSharedDataMessage.setPincode(((Partner) object).getPincode());
            savePartnerSharedDataMessage.setMobile(((Partner) object).getMobile());
            savePartnerSharedDataMessage.setCountryCode(((Partner) object).getCountryCode());
            savePartnerSharedDataMessage.setEmail(((Partner) object).getEmail());
            savePartnerSharedDataMessage.setPartnerType(((Partner) object).getPartnerType());
            savePartnerSharedDataMessage.setCpName(((Partner) object).getCpName());
            savePartnerSharedDataMessage.setCname(((Partner) object).getCname());
            savePartnerSharedDataMessage.setPanName(((Partner) object).getPanName());
             List<ServiceArea> serviceAreaList=new ArrayList<>();
             for (ServiceArea area:((Partner) object).getServiceAreaList())
             {
                 serviceAreaList.add(new ServiceArea(area));
             }
             savePartnerSharedDataMessage.setServiceAreaList(serviceAreaList);

            savePartnerSharedDataMessage.setPartnerLedgerDetails(((Partner) object).getPartnerLedgerDetails());
            savePartnerSharedDataMessage.setPartnerPayments(((Partner) object).getPartnerPayments());
            savePartnerSharedDataMessage.setIsDelete(((Partner) object).getIsDelete());
            savePartnerSharedDataMessage.setMvnoId(((Partner) object).getMvnoId());
            savePartnerSharedDataMessage.setCommissionShareType(((Partner) object).getCommissionShareType());
            savePartnerSharedDataMessage.setBuId(((Partner) object).getBuId());
            savePartnerSharedDataMessage.setNewCustomerCount(((Partner) object).getNewCustomerCount());
            savePartnerSharedDataMessage.setRenewCustomerCount(((Partner) object).getRenewCustomerCount());
            savePartnerSharedDataMessage.setTotalCustomerCount(((Partner) object).getTotalCustomerCount());
            savePartnerSharedDataMessage.setCalendarType(((Partner) object).getCalendarType());
            savePartnerSharedDataMessage.setResetDate(String.valueOf(((Partner) object).getResetDate()));
            savePartnerSharedDataMessage.setCreditConsume(((Partner) object).getCreditConsume());
            savePartnerSharedDataMessage.setRegion(((Partner) object).getRegion());
            savePartnerSharedDataMessage.setBranch(((Partner) object).getBranch());
            savePartnerSharedDataMessage.setDunningActivateFor(((Partner) object).getDunningActivateFor());
            savePartnerSharedDataMessage.setLastDunningDate(String.valueOf(((Partner) object).getLastDunningDate()));
            savePartnerSharedDataMessage.setIsDunningEnable(((Partner) object).getIsDunningEnable());
            savePartnerSharedDataMessage.setPriceBookId(((Partner) object).getPriceBookId().getId());
            savePartnerSharedDataMessage.setDunningAction(((Partner) object).getDunningAction());
            savePartnerSharedDataMessage.setIsVisibleToIsp(((Partner) object).getIsVisibleToIsp());
            savePartnerSharedDataMessage.setParentPartner(null);
            if(((Partner) object).getParentPartner()!=null)
                savePartnerSharedDataMessage.setParentPartnerId(((Partner) object).getParentPartner().getId());
            else
                savePartnerSharedDataMessage.setParentPartnerId(null);
            savePartnerSharedDataMessage.setCreatedById(((Partner) object).getCreatedById());
            savePartnerSharedDataMessage.setLastModifiedById(((Partner) object).getLastModifiedById());
            //All the message from microservices are to be sent from here
//            messageSender.send(savePartnerSharedDataMessage, SharedDataConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_INVENTORY);
//            messageSender.send(savePartnerSharedDataMessage, SharedDataConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            //messageSender.send(savePartnerSharedDataMessage, SharedDataConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_API_COMMON);
            kafkaMessageSender.send(new KafkaMessageData(savePartnerSharedDataMessage,savePartnerSharedDataMessage.getClass().getSimpleName(), KafkaConstant.CREATE_PARTNER));

//            messageSender.send(savePartnerSharedDataMessage, SharedDataConstants.QUEUE_CREATE_PARTNER_REVENUE);
//            messageSender.send(savePartnerSharedDataMessage,SharedDataConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_CMS);
//             messageSender.send(savePartnerSharedDataMessage,SharedDataConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_SALESCRM);
//            SavePartnerSharedDataMessage message = new SavePartnerSharedDataMessage((Partner) object);
//            messageSender.send(message, SharedDataConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_TICKET);
        }
    }



    //UPDATE ENTITY COMMON SERVICE
    public void updateEntityDataForAllMicroService(Object object, PartnerPojo pojo) {

        if (Objects.nonNull(object) && object.getClass().equals(Partner.class)) {

            //All data of Partner entity while updating
            UpdatePartnerSharedDataMessage updatePartnerSharedDataMessage = new UpdatePartnerSharedDataMessage();
            updatePartnerSharedDataMessage.setId(((Partner) object).getId());
            updatePartnerSharedDataMessage.setName(((Partner) object).getName());
            updatePartnerSharedDataMessage.setPrcode(((Partner) object).getPrcode());
            updatePartnerSharedDataMessage.setStatus(((Partner) object).getStatus());
            updatePartnerSharedDataMessage.setCommtype(((Partner) object).getCommtype());
            updatePartnerSharedDataMessage.setCommrelvalue(((Partner) object).getCommrelvalue());
            updatePartnerSharedDataMessage.setBalance(((Partner) object).getBalance());
            updatePartnerSharedDataMessage.setCommdueday(((Partner) object).getCommdueday());
            updatePartnerSharedDataMessage.setIsVisibleToIsp(((Partner) object).getIsVisibleToIsp());
            if(pojo.getServiceAreaIds()!=null)
                updatePartnerSharedDataMessage.setServiceAreaIds(pojo.getServiceAreaIds());
            else
                updatePartnerSharedDataMessage.setServiceAreaIds(null);
            if(((Partner) object).getNextbilldate()!=null)
                updatePartnerSharedDataMessage.setNextbilldate(String.valueOf(((Partner) object).getNextbilldate()).toString());
            else
                updatePartnerSharedDataMessage.setNextbilldate(String.valueOf(LocalDate.now()).toString());
            if(((Partner) object).getLastbilldate()!=null)
                updatePartnerSharedDataMessage.setLastbilldate(String.valueOf(((Partner) object).getLastbilldate()).toString());
            else
                updatePartnerSharedDataMessage.setNextbilldate(String.valueOf(LocalDate.now()).toString());

            updatePartnerSharedDataMessage.setTaxid(((Partner) object).getTaxid());
            updatePartnerSharedDataMessage.setAddresstype(((Partner) object).getAddresstype());
            updatePartnerSharedDataMessage.setAddress1(((Partner) object).getAddress1());
            updatePartnerSharedDataMessage.setAddress2(((Partner) object).getAddress2());
            updatePartnerSharedDataMessage.setCredit(((Partner) object).getCredit());
            updatePartnerSharedDataMessage.setCity(((Partner) object).getCity());
            updatePartnerSharedDataMessage.setState(((Partner) object).getState());
            updatePartnerSharedDataMessage.setCountry(((Partner) object).getCountry());
            updatePartnerSharedDataMessage.setPincode(((Partner) object).getPincode());
            updatePartnerSharedDataMessage.setMobile(((Partner) object).getMobile());
            updatePartnerSharedDataMessage.setCountryCode(((Partner) object).getCountryCode());
            updatePartnerSharedDataMessage.setEmail(((Partner) object).getEmail());
            updatePartnerSharedDataMessage.setPartnerType(((Partner) object).getPartnerType());
            updatePartnerSharedDataMessage.setCpName(((Partner) object).getCpName());
            updatePartnerSharedDataMessage.setCname(((Partner) object).getCname());
            updatePartnerSharedDataMessage.setPanName(((Partner) object).getPanName());
            updatePartnerSharedDataMessage.setCommissionInterval(((Partner) object).getCommissionInterval());

            List<ServiceArea>serviceAreaList=new ArrayList<>();
            for(ServiceArea area:((Partner) object).getServiceAreaList()){
                serviceAreaList.add(new ServiceArea(area));
            }
            updatePartnerSharedDataMessage.setServiceAreaList(serviceAreaList);
            updatePartnerSharedDataMessage.setPartnerLedgerDetails(((Partner) object).getPartnerLedgerDetails());
            updatePartnerSharedDataMessage.setPartnerPayments(((Partner) object).getPartnerPayments());
            updatePartnerSharedDataMessage.setIsDelete(((Partner) object).getIsDelete());
            updatePartnerSharedDataMessage.setMvnoId(((Partner) object).getMvnoId());
            updatePartnerSharedDataMessage.setCommissionShareType(((Partner) object).getCommissionShareType());
            updatePartnerSharedDataMessage.setBuId(((Partner) object).getBuId());
            updatePartnerSharedDataMessage.setNewCustomerCount(((Partner) object).getNewCustomerCount());
            updatePartnerSharedDataMessage.setRenewCustomerCount(((Partner) object).getRenewCustomerCount());
            updatePartnerSharedDataMessage.setTotalCustomerCount(((Partner) object).getTotalCustomerCount());
            updatePartnerSharedDataMessage.setCalendarType(((Partner) object).getCalendarType());
            updatePartnerSharedDataMessage.setResetDate(String.valueOf(((Partner) object).getResetDate()));
            updatePartnerSharedDataMessage.setCreditConsume(((Partner) object).getCreditConsume());
            updatePartnerSharedDataMessage.setRegion(((Partner) object).getRegion());
            updatePartnerSharedDataMessage.setBranch(((Partner) object).getBranch());
            updatePartnerSharedDataMessage.setDunningActivateFor(((Partner) object).getDunningActivateFor());
            updatePartnerSharedDataMessage.setLastDunningDate(String.valueOf(((Partner) object).getLastDunningDate()));
            updatePartnerSharedDataMessage.setIsDunningEnable(((Partner) object).getIsDunningEnable());
            updatePartnerSharedDataMessage.setDunningAction(((Partner) object).getDunningAction());
            updatePartnerSharedDataMessage.setParentPartner(null);
            updatePartnerSharedDataMessage.setPriceBookId(((Partner) object).getPriceBookId().getId());
            if(((Partner) object).getParentPartner()!=null)
                updatePartnerSharedDataMessage.setParentPartnerId(((Partner) object).getParentPartner().getId());
            else
                updatePartnerSharedDataMessage.setParentPartnerId(null);

            if(((Partner) object).getPriceBookId()!=null)
                updatePartnerSharedDataMessage.setPriceBookId(((Partner) object).getPriceBookId().getId());
            updatePartnerSharedDataMessage.setCreatedById(((Partner) object).getCreatedById());
            updatePartnerSharedDataMessage.setLastModifiedById(((Partner) object).getLastModifiedById());
            //All the message from microservices are to be sent from here
//            messageSender.send(updatePartnerSharedDataMessage, SharedDataConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_INVENTORY);
//            messageSender.send(updatePartnerSharedDataMessage, SharedDataConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            //messageSender.send(updatePartnerSharedDataMessage, SharedDataConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_API_COMMON);
            kafkaMessageSender.send(new KafkaMessageData(updatePartnerSharedDataMessage,updatePartnerSharedDataMessage.getClass().getSimpleName(),KafkaConstant.UPDATE_PARTNER));

//            UpdatePartnerSharedDataMessage message = new UpdatePartnerSharedDataMessage((Partner) object);
//            messageSender.send(message, SharedDataConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(updatePartnerSharedDataMessage, SharedDataConstants.QUEUE_UPDATE_PARTNER_REVENUE);
//            messageSender.send(updatePartnerSharedDataMessage,SharedDataConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_CMS);
//            messageSender.send(updatePartnerSharedDataMessage,SharedDataConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_SALESCRM);
        }
    }


    //DELETE ENTITY COMMON SERVICE
    public void deleteEntityDataForAllMicroService(Object object){

       if (Objects.nonNull(object) && object.getClass().equals(Partner.class)) {
            //All data of Partner entity while deleting
            UpdatePartnerSharedDataMessage updatePartnerSharedDataMessage = new UpdatePartnerSharedDataMessage();
            updatePartnerSharedDataMessage.setId(((Partner) object).getId());
            updatePartnerSharedDataMessage.setName(((Partner) object).getName());
            updatePartnerSharedDataMessage.setPrcode(((Partner) object).getPrcode());
            updatePartnerSharedDataMessage.setStatus(((Partner) object).getStatus());
            updatePartnerSharedDataMessage.setCommtype(((Partner) object).getCommtype());
            updatePartnerSharedDataMessage.setCommrelvalue(((Partner) object).getCommrelvalue());
            updatePartnerSharedDataMessage.setBalance(((Partner) object).getBalance());
            updatePartnerSharedDataMessage.setCommdueday(((Partner) object).getCommdueday());
           if(((Partner) object).getNextbilldate()!=null)
               updatePartnerSharedDataMessage.setNextbilldate(String.valueOf(((Partner) object).getNextbilldate()));
           else
               updatePartnerSharedDataMessage.setNextbilldate(String.valueOf(LocalDate.now()).toString());
           if(((Partner) object).getLastbilldate()!=null)
               updatePartnerSharedDataMessage.setLastbilldate(String.valueOf(((Partner) object).getLastbilldate()));
           else
               updatePartnerSharedDataMessage.setLastbilldate(String.valueOf(LocalDate.now()).toString());
            updatePartnerSharedDataMessage.setTaxid(((Partner) object).getTaxid());
            updatePartnerSharedDataMessage.setAddresstype(((Partner) object).getAddresstype());
            updatePartnerSharedDataMessage.setAddress1(((Partner) object).getAddress1());
            updatePartnerSharedDataMessage.setAddress2(((Partner) object).getAddress2());
            updatePartnerSharedDataMessage.setCredit(((Partner) object).getCredit());
            updatePartnerSharedDataMessage.setCity(((Partner) object).getCity());
            updatePartnerSharedDataMessage.setState(((Partner) object).getState());
            updatePartnerSharedDataMessage.setCountry(((Partner) object).getCountry());
            updatePartnerSharedDataMessage.setPincode(((Partner) object).getPincode());
            updatePartnerSharedDataMessage.setMobile(((Partner) object).getMobile());
            updatePartnerSharedDataMessage.setCountryCode(((Partner) object).getCountryCode());
            updatePartnerSharedDataMessage.setEmail(((Partner) object).getEmail());
            updatePartnerSharedDataMessage.setPartnerType(((Partner) object).getPartnerType());
            updatePartnerSharedDataMessage.setCpName(((Partner) object).getCpName());
            updatePartnerSharedDataMessage.setCname(((Partner) object).getCname());
            updatePartnerSharedDataMessage.setPanName(((Partner) object).getPanName());
            updatePartnerSharedDataMessage.setServiceAreaList(((Partner) object).getServiceAreaList());
            updatePartnerSharedDataMessage.setPartnerLedgerDetails(((Partner) object).getPartnerLedgerDetails());
            updatePartnerSharedDataMessage.setPartnerPayments(((Partner) object).getPartnerPayments());
            updatePartnerSharedDataMessage.setIsDelete(((Partner) object).getIsDelete());
            updatePartnerSharedDataMessage.setMvnoId(((Partner) object).getMvnoId());
            updatePartnerSharedDataMessage.setCommissionShareType(((Partner) object).getCommissionShareType());
            updatePartnerSharedDataMessage.setBuId(((Partner) object).getBuId());
            updatePartnerSharedDataMessage.setNewCustomerCount(((Partner) object).getNewCustomerCount());
            updatePartnerSharedDataMessage.setRenewCustomerCount(((Partner) object).getRenewCustomerCount());
            updatePartnerSharedDataMessage.setTotalCustomerCount(((Partner) object).getTotalCustomerCount());
            updatePartnerSharedDataMessage.setCalendarType(((Partner) object).getCalendarType());
            updatePartnerSharedDataMessage.setResetDate(String.valueOf(((Partner) object).getResetDate()));
            updatePartnerSharedDataMessage.setCreditConsume(((Partner) object).getCreditConsume());
            updatePartnerSharedDataMessage.setRegion(((Partner) object).getRegion());
            updatePartnerSharedDataMessage.setBranch(((Partner) object).getBranch());
            updatePartnerSharedDataMessage.setDunningActivateFor(((Partner) object).getDunningActivateFor());
            updatePartnerSharedDataMessage.setLastDunningDate(String.valueOf(((Partner) object).getLastDunningDate()));
            updatePartnerSharedDataMessage.setIsDunningEnable(((Partner) object).getIsDunningEnable());
            updatePartnerSharedDataMessage.setDunningAction(((Partner) object).getDunningAction());
            updatePartnerSharedDataMessage.setParentPartner(null);
            if(((Partner) object).getParentPartner()!=null)
                updatePartnerSharedDataMessage.setParentPartnerId(((Partner) object).getParentPartner().getId());
            else
                updatePartnerSharedDataMessage.setParentPartnerId(null);
            updatePartnerSharedDataMessage.setCreatedById(((Partner) object).getCreatedById());
            updatePartnerSharedDataMessage.setLastModifiedById(((Partner) object).getLastModifiedById());

            //All the message from microservices are to be sent from here
//            messageSender.send(updatePartnerSharedDataMessage, SharedDataConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_INVENTORY);
//            messageSender.send(updatePartnerSharedDataMessage, SharedDataConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            //messageSender.send(updatePartnerSharedDataMessage, SharedDataConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_API_COMMON);
            kafkaMessageSender.send(new KafkaMessageData(updatePartnerSharedDataMessage,updatePartnerSharedDataMessage.getClass().getSimpleName()));

//            UpdatePartnerSharedDataMessage message = new UpdatePartnerSharedDataMessage((Partner) object);
//            messageSender.send(message, SharedDataConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(updatePartnerSharedDataMessage, SharedDataConstants.QUEUE_UPDATE_PARTNER_REVENUE);
//            messageSender.send(updatePartnerSharedDataMessage,SharedDataConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_CMS);
//           messageSender.send(updatePartnerSharedDataMessage,SharedDataConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_SALESCRM);
       }
    }


    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
                ApplicationLogger.logger.info("User logged in: {}", user);
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("Exception while getting logged in user", e);
            user = null;
        }
        return user;
    }
}
