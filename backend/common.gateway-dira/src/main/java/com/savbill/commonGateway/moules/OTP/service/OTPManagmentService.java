package com.savbill.commonGateway.moules.OTP.service;

import com.savbill.commonGateway.common.service.AbstractService;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.kafka.KafkaMessageData;
import com.savbill.commonGateway.kafka.KafkaMessageSender;
import com.savbill.commonGateway.moules.OTP.domain.OTPManagement;
import com.savbill.commonGateway.moules.OTP.domain.QOTPManagement;
import com.savbill.commonGateway.moules.OTP.repository.OTPManagementRepository;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.MvnoRepository;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserRepository;
import com.savbill.commonGateway.rabbitmq.MessageSender;
import com.savbill.commonGateway.rabbitmq.messages.OTPProfileMessage;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OTPManagmentService extends AbstractService {

    @Autowired
    OTPManagementRepository otpManagementRepository;

    @Autowired
    StaffUserRepository staffUserRepository;

    @Autowired
    MvnoRepository mvnoRepository;

    @Autowired
    MessageSender messageSender;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    public OTPManagement findByProfileName(String profileName, Boolean isUpdateOrDelete, Integer mvnoId) {
        try {
            QOTPManagement qotpManagement = QOTPManagement.oTPManagement;
            BooleanExpression exp = qotpManagement.isNotNull();
            exp = exp.and(qotpManagement.profileName.eq(profileName));
//            if(mvnoId != 1) {
//                exp = exp.and(qotpManagement.mvnoId.in(mvnoId, 1));
//            }else{
                exp = exp.and(qotpManagement.mvnoId.eq(mvnoId));
//            }
            Optional<OTPManagement> otpManagementOptional = otpManagementRepository.findOne(exp);
            if (otpManagementOptional.isPresent()) return otpManagementOptional.get();
            else
                throw new IllegalArgumentException("No record found for otp profile with profile name : '" + profileName + "'. OR You do not have access to update or delete this record.");
        }catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected JpaRepository getRepository() {
        return null;
    }


    public void saveOTPProfile(OTPProfileMessage  otpProfileMessage){
        try{
            OTPManagement otpManagement = otpProfileMessage.getOtpManagement();
            if(otpManagement!=null){
                OTPManagement otp = otpManagementRepository.findByProfileNameAndMvnoId(otpProfileMessage.getOtpManagement().getProfileName(), otpProfileMessage.getOtpManagement().getMvnoId());
                if(otp == null){
                    otpManagementRepository.save(otpManagement);
                }
            }
        }catch (Exception e){
            ApplicationLogger.logger.error("Something went wrong otp profile not saved successfully!!",e.getMessage());
        }

    }


    public void updateOTPProfile(OTPProfileMessage  otpProfileMessage){
        try{
            OTPManagement otpManagement = otpManagementRepository.findById(otpProfileMessage.getOtpManagement().getProfileId()).orElse(null);
            if(otpManagement!=null) {
                otpManagementRepository.delete(otpManagement);
                OTPManagement updatedOTPProfile = new OTPManagement(otpProfileMessage.getOtpManagement());
                otpManagementRepository.save(updatedOTPProfile);
            }else{
                otpManagementRepository.save(otpManagement);
            }
        }catch (Exception e){
            ApplicationLogger.logger.error("Something went wrong otp profile not updated successfully!!",e.getMessage());
        }

    }

    public void createDefaultOTPProfile (Integer newMvnoId){
        OTPManagement otpManagement = new OTPManagement();
        OTPManagement otpForNewMvno = new OTPManagement();
        otpManagement = otpManagementRepository.findByProfileNameAndMvnoId(CommonConstants.DEFAULT_OTP_PROFILE,2);
        if(otpManagement!=null){
            OTPManagement newOTPMgmt = new OTPManagement(otpManagement);
            newOTPMgmt.setMvnoId(newMvnoId);
            OTPProfileMessage otpProfileMessage = new OTPProfileMessage(newOTPMgmt);
            newOTPMgmt.setProfileId(null);
//            messageSender.send(otpProfileMessage,RabbitMqConstants.QUEUE_OTP_PROFILE_TO_CMS);
            kafkaMessageSender.send(new KafkaMessageData(otpProfileMessage, OTPProfileMessage.class.getSimpleName()));
        }else{
            otpManagement = otpManagementRepository.findByMvnoId(2);
            OTPManagement newOTPMgmt = new OTPManagement(otpManagement);
            newOTPMgmt.setMvnoId(newMvnoId);
            OTPProfileMessage otpProfileMessage = new OTPProfileMessage(newOTPMgmt);
            newOTPMgmt.setProfileId(null);
//            messageSender.send(otpProfileMessage,RabbitMqConstants.QUEUE_OTP_PROFILE_TO_CMS);
            kafkaMessageSender.send(new KafkaMessageData(otpProfileMessage, OTPProfileMessage.class.getSimpleName()));
        }
    }


}
