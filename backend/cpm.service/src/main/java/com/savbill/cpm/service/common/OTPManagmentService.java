package com.savbill.cpm.service.common;
import com.savbill.cpm.constants.Constants;
import com.savbill.cpm.exception.CustomValidationException;
import com.savbill.cpm.kafka.KafkaConstant;
import com.savbill.cpm.kafka.KafkaMessageData;
import com.savbill.cpm.kafka.KafkaMessageSender;
import com.savbill.cpm.modules.Mvno.repository.MvnoRepository;
import com.savbill.cpm.rabbitMq.MessageSender;
import com.savbill.cpm.rabbitMq.message.OTPProfileMessage;
import com.savbill.cpm.service.radius.AbstractService;

import com.savbill.cpm.model.common.OTPManagement;
import com.savbill.cpm.model.common.QOTPManagement;
import com.savbill.cpm.pojo.api.OTPManagementDto;
import com.savbill.cpm.pojo.api.UpdateOTPManagementDto;
import com.savbill.cpm.repository.common.OTPManagementRepository;
import com.savbill.cpm.repository.common.StaffUserRepository;
import com.savbill.cpm.utils.APIConstants;
import com.savbill.cpm.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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


    public List<OTPManagement> getOtpProfileByProfileName(String name) {
        try {
            QOTPManagement qOTPManagement = QOTPManagement.oTPManagement;
            BooleanExpression boolExp = qOTPManagement.isNotNull();
            if(getMvnoIdFromCurrentStaff() != 1)
                boolExp = boolExp.and(qOTPManagement.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(name))
                boolExp = boolExp.and(qOTPManagement.profileName.contains(name));
            return (List<OTPManagement>) otpManagementRepository.findAll(boolExp);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public OTPManagement getOtpProfileById(Long id) {
        try {
            return findByProfileId(id, false);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    private OTPManagement findByProfileId(Long profileId, Boolean isUpdateOrDelete) {
        try {
            QOTPManagement qotpManagement = QOTPManagement.oTPManagement;
            BooleanExpression exp = qotpManagement.isNotNull();
            exp = exp.and(qotpManagement.profileId.eq(profileId));
            if(getMvnoIdFromCurrentStaff() != 1)
                exp = exp.and(qotpManagement.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));

            OTPManagement otpManagement = (OTPManagement) otpManagementRepository.findOne(exp).orElse(null);
            if(isUpdateOrDelete) {
                if (otpManagement == null || (!(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == otpManagement.getMvnoId().intValue())))
                    throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
                else
                    return otpManagement;
            } else
                if (getMvnoIdFromCurrentStaff() == 1 || ((otpManagement.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || otpManagement.getMvnoId() == 1)))
                    return otpManagement;
            if (Objects.nonNull(otpManagement)) return otpManagement;
            else
                throw new IllegalArgumentException("No record found for otp profile with id : '" + profileId + "'. OR You do not have access to update or delete this record");
        }
        catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public OTPManagement findByProfileName(String profileName, Boolean isUpdateOrDelete) {
        try {
            QOTPManagement qotpManagement = QOTPManagement.oTPManagement;
            BooleanExpression exp = qotpManagement.isNotNull();
            exp = exp.and(qotpManagement.profileName.eq(profileName));
            if(getMvnoIdFromCurrentStaff() != 1)
                exp = exp.and(qotpManagement.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
            List<OTPManagement> otpManagementList= IterableUtils.toList( otpManagementRepository.findAll(exp));
            if (otpManagementList.size()>0) {
                return otpManagementList.stream()
                        .filter(i -> i.getMvnoId() == getMvnoIdFromCurrentStaff())
                        .findFirst()
                        .orElseGet(() ->
                                otpManagementList.stream()
                                        .filter(i -> i.getMvnoId() == 1)
                                        .findFirst()
                                        .orElse(null)
                        );
            }
            else
                throw new IllegalArgumentException("No record found for otp profile with profile name : '" + profileName + "'. OR You do not have access to update or delete this record.");
        }catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public List<OTPManagement> findAll() {
        try {
            List<OTPManagement> otpManagementList = new ArrayList<>();
            otpManagementList = otpManagementRepository.findAll().stream().filter(otpManagement -> (otpManagement.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || otpManagement.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1)).collect(Collectors.toList());

            for (OTPManagement otpManagement : otpManagementList){
                if(otpManagement.getMvnoId()!= null){
                    otpManagement.setMvnoName(mvnoRepository.getOne(otpManagement.getMvnoId().longValue()).getName());
                }
            }
            return otpManagementList;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }





/*
    public PageableResponse getAllOtpProfile(Long mvnoId, PaginationDTO paginationDTO, String profileName) {
        PageableResponse<OTPManagement> pageableResponse = new PageableResponse<>();
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(WifiConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            } else {
                QOTPManagement qOtpManagement = QOTPManagement.oTPManagement;
                BooleanExpression exp = qOtpManagement.isNotNull();
                // check mvnoid for superadmin
                if (mvnoId != 1) {
                    exp = exp.and(qOtpManagement.mvnoId.in(mvnoId, 1));
                }
                if (paginationDTO.getPage() > 0) {
                    paginationDTO.setPage(paginationDTO.getPage() - 1);
                }
                Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "createDate"));
                //Check search filter
                if (!StringUtils.isBlank(profileName)) {
                    exp = exp.and(qOtpManagement.profileName.like("%" + profileName + "%"));
                }
                Page<OTPManagement> page = otpManagementRepo.findAll(exp, pageable);
                return pageableResponse.convert(new PageImpl<>(page.getContent(), pageable, page.getTotalElements()));
            }

        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }*/

    public void deleteOtpProfileById(Long profileId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(profileId)) {
                throw new IllegalArgumentException("Please enter valid profile id.");
            } else {
                Optional<OTPManagement> optionalOtpManagement = Optional.of(findByProfileId(profileId, true));
                if (optionalOtpManagement.isPresent()) {
                    otpManagementRepository.deleteById(profileId);
                } else {
                    throw new IllegalArgumentException("Profile not found for Id " + profileId);
                }

            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public OTPManagement saveOtpProfile(OTPManagementDto otpMang, HttpServletRequest request) {
        try {
             OTPManagement otp = new OTPManagement(otpMang);
            checkForUniqueProfileName(otp, null, false);
            validateOTPProfileData(otp, false);
            if(getMvnoIdFromCurrentStaff()!=null){
                otp.setMvnoId(getMvnoIdFromCurrentStaff());
            }
            otp.setCreatedById(getLoggedInUserId());
            otp.setLastModifiedById(getLoggedInUserId());
            OTPManagement otpManagement=otpManagementRepository.save(otp);

            OTPProfileMessage  otpProfileMessage = new OTPProfileMessage(otpManagement);
//            messageSender.send(otpProfileMessage,RabbitMqConstants.QUEUE_OTP_PROFILE_TO_COMMON);
            kafkaMessageSender.send(new KafkaMessageData(otpProfileMessage,OTPProfileMessage.class.getSimpleName(), KafkaConstant.OPT_PROFILE_SAVE));
            return otpManagement;

        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


//    private void checkForUniqueProfileName(OTPManagement otpMang, Long profileId, boolean isUpdate) {
//        try {
//            QOTPManagement qOTPManagement = QOTPManagement.oTPManagement;
//            BooleanExpression boolExp = qOTPManagement.isNotNull();
//            boolExp = boolExp.and(qOTPManagement.mvnoId.in(getLoggedInMvnoId(),1));
//            if (isUpdate) {
//                boolExp = boolExp.and(qOTPManagement.profileId.ne(profileId));
//            } else {
//                boolExp = boolExp.and(qOTPManagement.profileName.eq(otpMang.getProfileName()));
//                Optional<OTPManagement> optionalOtpMgmt = otpManagementRepository.findOne(boolExp);
//                if (optionalOtpMgmt.isPresent()) {
//                    throw new RuntimeException("Profile exist with the same name : '" + otpMang.getProfileName() + "'");
//                }
//            }
//        } catch (Throwable e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }

    private void checkForUniqueProfileName(OTPManagement otpMang, Long profileId, boolean isUpdate) {
        try {
            QOTPManagement qOTPManagement = QOTPManagement.oTPManagement;
            BooleanExpression boolExp = qOTPManagement.isNotNull();
            if(otpMang.getMvnoId() == null){
                boolExp = boolExp.and(qOTPManagement.mvnoId.eq(getLoggedInMvnoId()));
            }else{
                boolExp = boolExp.and(qOTPManagement.mvnoId.eq(otpMang.getMvnoId()));
            }
            if (isUpdate) {
                boolExp = boolExp.and(qOTPManagement.profileId.ne(profileId));
            } else {
                boolExp = boolExp.and(qOTPManagement.profileName.eq(otpMang.getProfileName()));
                Optional<OTPManagement> optionalOtpMgmt = otpManagementRepository.findOne(boolExp);
                if (optionalOtpMgmt.isPresent()) {
                    throw new RuntimeException("Profile exist with the same name : '" + otpMang.getProfileName() + "'");
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    @Transactional
    public OTPManagement updateOtpProfile(UpdateOTPManagementDto updateOTPManagementDto,HttpServletRequest request) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(updateOTPManagementDto.getProfileId())) {
                throw new IllegalArgumentException("Please enter valid profile id.");
            } else {
                OTPManagement oldOTPManagement = findByProfileId(updateOTPManagementDto.getProfileId(), true);
                OTPManagement otpM = otpManagementRepository.findById(updateOTPManagementDto.getProfileId()).orElse(null);
                if (Objects.isNull(otpM)) {
                    throw new RuntimeException("Given OTP not available with given Id: " + updateOTPManagementDto.getProfileId());
                }
                OTPManagement updatedOtp = new OTPManagement(updateOTPManagementDto);
                if(getMvnoIdFromCurrentStaff()!=null){
                    updatedOtp.setMvnoId(getMvnoIdFromCurrentStaff());
                }
                updatedOtp.setProfileName(oldOTPManagement.getProfileName());
                updatedOtp.setCreatedById(getLoggedInUserId());
                updatedOtp.setLastModifiedById(getLoggedInUserId());
                updatedOtp = otpManagementRepository.save(updatedOtp);
                OTPProfileMessage  otpProfileMessage = new OTPProfileMessage(updatedOtp);
//                messageSender.send(otpProfileMessage,RabbitMqConstants.QUEUE_OTP_PROFILE_TO_COMMON_UPDATE);
                kafkaMessageSender.send(new KafkaMessageData(otpProfileMessage,OTPProfileMessage.class.getSimpleName(),KafkaConstant.OPT_PROFILE_UPDATE));
                return updatedOtp;
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validateOTPProfileData(OTPManagement otpManagement, boolean isUpdateOrDelete) {
        try {
            if (!isUpdateOrDelete && !ValidateCrudTransactionData.validateStringTypeFieldValue(otpManagement.getProfileName())) {
                throw new RuntimeException("OTP Profile name is mandatory. Please enter valid profile name");
            } else if (otpManagement.getOtpLength() == null || otpManagement.getOtpLength() == 0) {
                throw new RuntimeException("OTP length is mandatory.Please enter valid OTP length");
            } else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(otpManagement.getOtpValidityInMin())) {
                throw new RuntimeException("Validity is mandatory.");
            } else if (APIConstants.OTP_GENERATION_TYPE.stream().noneMatch(otpGenerationType -> otpManagement.getGenerationType().equals(otpGenerationType))) {
                throw new RuntimeException("Please enter valid generation type allowed values are :-" + APIConstants.OTP_GENERATION_TYPE.toString());
            }/* else if (!(otpManagement.getType().size() > 0)) {
                throw new RuntimeException("Please enter allowed value for OTP. Ex:-Upper Case,Lower Case");
            } else if (!isUpdateOrDelete && !ValidateCrudTransactionData.validateStringTypeFieldValue(otpManagement.getCreatedBy())) {
                throw new RuntimeException("Please enter created by value.");
            } else if (isUpdateOrDelete && !ValidateCrudTransactionData.validateStringTypeFieldValue(otpManagement.getLastModifiedBy())) {
                throw new RuntimeException("Please enter last modified value");
            }*/

        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    protected JpaRepository getRepository() {
        return null;
    }



    public OTPManagement saveOtpProfileFromRabbitMq(OTPManagement otpMang) {
        try {
            OTPManagement otp = new OTPManagement(otpMang);
            checkForUniqueProfileName(otp, null, false);
            validateOTPProfileData(otp, false);
            if(getMvnoIdFromCurrentStaff()!=null){
                otp.setMvnoId(getMvnoIdFromCurrentStaff());
            }
            otp.setCreatedById(getLoggedInUserId());
            otp.setLastModifiedById(getLoggedInUserId());
            OTPManagement otpManagement=otpManagementRepository.save(otp);

            OTPProfileMessage  otpProfileMessage = new OTPProfileMessage(otpManagement);
//            messageSender.send(otpProfileMessage,RabbitMqConstants.QUEUE_OTP_PROFILE_TO_COMMON);
            kafkaMessageSender.send(new KafkaMessageData(otpProfileMessage,OTPProfileMessage.class.getSimpleName(),KafkaConstant.OPT_PROFILE_SAVE));
            return otpManagement;

        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


//    public OTPManagementDto domainToDTO (OTPManagement otpManagement){
//        OTPManagementDto otpDTO = new OTPManagementDto();
//        otpDTO.setOtpLength(otpManagement.getOtpLength());
//        otpDTO.setStaticOtp(otpManagement.getStaticOtp());
//        otpDTO.setProfileName(otpManagement.getProfileName());
//        otpDTO.setOtpValidityInMin(otpManagement.getOtpValidityInMin());
//        otpDTO.setGenerationType(otpManagement.getGenerationType());
//        otpDTO.
//        return otpDTO;
//    }
}
