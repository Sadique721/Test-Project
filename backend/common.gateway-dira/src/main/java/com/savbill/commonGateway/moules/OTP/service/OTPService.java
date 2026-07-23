package com.savbill.commonGateway.moules.OTP.service;

import com.savbill.commonGateway.common.FieldType;
import com.savbill.commonGateway.common.service.AbstractService;
import com.savbill.commonGateway.constants.SubscriberConstants;
import com.savbill.commonGateway.constants.UrlConstants;
import com.savbill.commonGateway.kafka.KafkaConstant;
import com.savbill.commonGateway.kafka.KafkaMessageData;
import com.savbill.commonGateway.kafka.KafkaMessageSender;
import com.savbill.commonGateway.moules.Customers.Service.CustomersService;
import com.savbill.commonGateway.moules.Customers.domain.Customers;
import com.savbill.commonGateway.moules.Customers.repository.CustomerRepository;
import com.savbill.commonGateway.moules.OTP.constants.OTPStatus;
import com.savbill.commonGateway.moules.OTP.domain.OTP;
import com.savbill.commonGateway.moules.OTP.domain.OTPManagement;
import com.savbill.commonGateway.moules.OTP.domain.QOTP;
import com.savbill.commonGateway.moules.OTP.dto.GenerateOtpDto;
import com.savbill.commonGateway.moules.OTP.dto.ValidateOtpDto;
import com.savbill.commonGateway.moules.OTP.repository.OTPManagementRepository;
import com.savbill.commonGateway.moules.OTP.repository.OTPRepository;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUser;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserRepository;
import com.savbill.commonGateway.moules.Template.repository.NotificationTemplateRepository;
import com.savbill.commonGateway.rabbitmq.MessageSender;
import com.savbill.commonGateway.rabbitmq.RabbitMqConstants;
import com.savbill.commonGateway.rabbitmq.messages.OtpMessage;
import com.savbill.commonGateway.utils.RandomStringGenerator;
import com.savbill.commonGateway.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class OTPService extends AbstractService {

    private static final Logger logger = LoggerFactory.getLogger(OTPService.class);
    private static final String OTP_GENERATED = "OTP Generated";
    private static final String CUSTOMER_REGISTRATION="Customer Otp Registration";

    @Autowired
    private OTPManagmentService otpManagementService;
    @Autowired
    private OTPRepository otpRepository;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    NotificationTemplateRepository templateRepository;

    @Autowired
    CustomersService customersService;

    @Autowired
    StaffUserRepository staffUserRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    OTPManagementRepository otpManagementRepository;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;


    @Transactional
    public void generateOTP(GenerateOtpDto generateOtpDto,Integer mvnoId, Long buId, String authEventName) {
        try {
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(generateOtpDto.getCountryCode()) && !generateOtpDto.getCountryCode().contains("+")) {
                throw new IllegalArgumentException("Please enter valid country code with prefix '+' sign.");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(generateOtpDto.getMobileNumber()) && !ValidateCrudTransactionData.validateStringTypeFieldValue(generateOtpDto.getEmailId())) {
                throw new IllegalArgumentException("Please enter valid mobile number or email id.");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(generateOtpDto.getProfile())) {
                throw new IllegalArgumentException("Please enter valid otp profile name.");
            } else if (ValidateCrudTransactionData.validateStringTypeFieldValue(generateOtpDto.getEmailId()) && !ValidateCrudTransactionData.validateEmailAddress(generateOtpDto.getEmailId())) {
                throw new IllegalArgumentException("Invalid email id.");
            } else {
                OTPManagement management = Optional.ofNullable(otpManagementService.findByProfileName(generateOtpDto.getProfile(), false,mvnoId)).orElseThrow(() -> new RuntimeException(UrlConstants.EXPIRED_USER + " OTP profile not found"));
                try {
//                    Integer mvnoId = staffUser.getMvnoId();
//                    Long buId;
//                    if(staffUser.getBusinessUnitNameList() != null && !staffUser.getBusinessUnitNameList().isEmpty()){
//                        buId = (Long)staffUser.getBusinessUnitNameList().get(0).getId();
//                    } else {
//                        buId = null;
//                    }
                    generateOTPAsync(management, generateOtpDto,mvnoId,buId,authEventName);
                } catch (Exception ex) {
                    logger.error("Error while generate OTP: "+generateOtpDto.getMobileNumber());
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void generateOTPAsync(OTPManagement management, GenerateOtpDto generateOtpDto, Integer mvnoId, Long buId,String authEventName) {
        try {
            if (management.getGenerationType().equals("ALWAYS_NEW")) {
                if(ValidateCrudTransactionData.validateStringTypeFieldValue(generateOtpDto.getMobileNumber()) && ValidateCrudTransactionData.validateStringTypeFieldValue(generateOtpDto.getEmailId())) {
                    if(mvnoId!=null && buId!=null){
                        createNewOTP(generateOtpDto.getCountryCode(), generateOtpDto.getMobileNumber(), management, generateOtpDto.getEmailId(), mvnoId,buId.intValue(),authEventName);
                    }else if(mvnoId!=null){
                        createNewOTP(generateOtpDto.getCountryCode(), generateOtpDto.getMobileNumber(), management, generateOtpDto.getEmailId(),mvnoId,null,authEventName);

                    }
                }
               else if (ValidateCrudTransactionData.validateStringTypeFieldValue(generateOtpDto.getMobileNumber()) && generateOtpDto.getEmailId().isEmpty()) {
                    if(mvnoId!=null && buId!=null){
                        createNewOTP(generateOtpDto.getCountryCode(), generateOtpDto.getMobileNumber(), management, null,mvnoId,buId.intValue(),authEventName);
                    }else if(mvnoId!=null){
                        createNewOTP(generateOtpDto.getCountryCode(), generateOtpDto.getMobileNumber(), management, null,mvnoId,null,authEventName);

                    }

                }
                else if (ValidateCrudTransactionData.validateStringTypeFieldValue(generateOtpDto.getEmailId()) && generateOtpDto.getMobileNumber().isEmpty()) {
                    if(mvnoId!=null && buId!=null){
                        createNewOTP(generateOtpDto.getCountryCode(), null, management, generateOtpDto.getEmailId(),mvnoId,buId.intValue(),authEventName);
                    }else if(mvnoId!=null){
                        createNewOTP(generateOtpDto.getCountryCode(), null, management, generateOtpDto.getEmailId(),mvnoId,null,authEventName);

                    }

                }
            } else if (management.getGenerationType().equals("REUSE")) {
                if (ValidateCrudTransactionData.validateStringTypeFieldValue(generateOtpDto.getMobileNumber())) {
                    Optional<OTP> otpList = findByMvnoIdAndMobileEmailOrderByGeneratedTimeDesc(generateOtpDto.getMobileNumber()).stream().findFirst().filter(checkOTPValidity()).filter(otp -> otpRepository.updateValidTillTime(ZonedDateTime.now(), ZonedDateTime.now().plusMinutes(management.getOtpValidityInMin()), otp.getOtp()) == 1);
                    if(otpList.isPresent()){
                        sendOtpGenerationMessage(otpList.get(),generateOtpDto.getEmailId() , mvnoId , buId,authEventName);
                    }
                    else{
                        if(buId!=null)
                            findByMvnoIdAndMobileEmailOrderByGeneratedTimeDesc(generateOtpDto.getMobileNumber()).stream().findFirst().filter(checkOTPValidity()).filter(otp -> otpRepository.updateValidTillTime(ZonedDateTime.now(), ZonedDateTime.now().plusMinutes(management.getOtpValidityInMin()), otp.getOtp()) == 1).map(otp -> otp.getOtp()).orElseGet(() -> createNewOTP(generateOtpDto.getCountryCode(), generateOtpDto.getMobileNumber(), management, null,mvnoId,buId.intValue(),authEventName));
                        else
                            findByMvnoIdAndMobileEmailOrderByGeneratedTimeDesc(generateOtpDto.getMobileNumber()).stream().findFirst().filter(checkOTPValidity()).filter(otp -> otpRepository.updateValidTillTime(ZonedDateTime.now(), ZonedDateTime.now().plusMinutes(management.getOtpValidityInMin()), otp.getOtp()) == 1).map(otp -> otp.getOtp()).orElseGet(() -> createNewOTP(generateOtpDto.getCountryCode(), generateOtpDto.getMobileNumber(), management, null,mvnoId,null,authEventName));

                    }

                }

                if (ValidateCrudTransactionData.validateStringTypeFieldValue(generateOtpDto.getEmailId())) {
                    Optional<OTP> otpList = findByMvnoIdAndMobileEmailOrderByGeneratedTimeDesc(generateOtpDto.getEmailId()).stream().findFirst().filter(checkOTPValidity()).filter(otp -> otpRepository.updateValidTillTime(ZonedDateTime.now(), ZonedDateTime.now().plusMinutes(management.getOtpValidityInMin()), otp.getOtp()) == 1);
                    if(otpList.isPresent()){
                        sendOtpGenerationMessage(otpList.get(),generateOtpDto.getEmailId(),mvnoId,buId,authEventName);
                    }
                    else{
                        if(buId!=null){
                            findByMvnoIdAndMobileEmailOrderByGeneratedTimeDesc(generateOtpDto.getEmailId()).stream().findFirst().filter(checkOTPValidity()).filter(otp -> otpRepository.updateValidTillTime(ZonedDateTime.now(), ZonedDateTime.now().plusMinutes(management.getOtpValidityInMin()), otp.getOtp()) == 1).map(otp -> otp.getOtp()).orElseGet(() -> createNewOTP(null, null, management, generateOtpDto.getEmailId(),mvnoId,buId.intValue(),authEventName));
                        }else{
                            findByMvnoIdAndMobileEmailOrderByGeneratedTimeDesc(generateOtpDto.getEmailId()).stream().findFirst().filter(checkOTPValidity()).filter(otp -> otpRepository.updateValidTillTime(ZonedDateTime.now(), ZonedDateTime.now().plusMinutes(management.getOtpValidityInMin()), otp.getOtp()) == 1).map(otp -> otp.getOtp()).orElseGet(() -> createNewOTP(null, null, management, generateOtpDto.getEmailId(),mvnoId,null,authEventName));
                        }
                    }
                }
            }
            else if (management.getGenerationType().equalsIgnoreCase(SubscriberConstants.OTP_CONSTANT_STATIC)) {
                if (ValidateCrudTransactionData.validateStringTypeFieldValue(generateOtpDto.getMobileNumber())) {
                    saveStaticOTP(generateOtpDto.getCountryCode(), generateOtpDto.getMobileNumber(), management, null, management.getStaticOtp() ,mvnoId , buId);
                }
                if (ValidateCrudTransactionData.validateStringTypeFieldValue(generateOtpDto.getEmailId())) {
                    saveStaticOTP(generateOtpDto.getCountryCode(), null, management, generateOtpDto.getEmailId(), management.getStaticOtp() ,mvnoId , buId);
                }
            }
        } catch (Exception ex) {
            logger.error("Error While generate OTP: "+ex.getMessage());
            ex.printStackTrace();
        }
    }

    public String createNewOTP(String countryCode, String mobileNumber, OTPManagement management, String emailId,Integer mvnoId, Integer buId,String authEventName) {
        return saveOTP(countryCode, mobileNumber, management, emailId,mvnoId,buId,authEventName).get(0).getOtp();
    }

    private List<OTP> saveOTP(String countryCode, String mobileNumber, OTPManagement management, String emailId, Integer mvnoId, Integer buId,String authEventName) {
        List<OTP> otpList = new ArrayList<>();
        String mobileEmailOTP = generateOTP(management);
        if (ValidateCrudTransactionData.validateStringTypeFieldValue(mobileNumber))
            otpList.add(otpForMobileNo(countryCode, mobileNumber, management, mobileEmailOTP));
        else if (ValidateCrudTransactionData.validateStringTypeFieldValue(emailId))
            otpList.add(otpForEmailId(emailId, management, mobileEmailOTP));

        if(buId!=null)
            sendOtpGenerationMessage(otpList.get(0), emailId , mvnoId , buId.longValue(),authEventName);
        else
            sendOtpGenerationMessage(otpList.get(0), emailId , mvnoId , null,authEventName);
        return otpList;
    }

    private List<OTP> findByMvnoIdAndMobileEmailOrderByGeneratedTimeDesc(String mobileEmail) {
        QOTP qOtp = QOTP.oTP;
        BooleanExpression exp = qOtp.isNotNull();
        exp = exp.and(qOtp.mobile_email.eq(mobileEmail));
        return (List<OTP>) otpRepository.findAll(exp, new QSort(qOtp.generatedTime.desc()));
    }

    private void sendOtpGenerationMessage(OTP otp, String emailId , Integer mvnoId , Long buId, String authEventName) {
        try {
            OtpMessage otpMessage = new OtpMessage(otp, OTP_GENERATED, RabbitMqConstants.SOURCE_NAME_SAVBILL_BSS_GATEWAY, emailId,mvnoId,otp.getGeneratedTime().toString(), otp.getValidTillTime().toString(), buId,authEventName);
//            messageSender.send(otpMessage, RabbitMqConstants.QUEUE_OTP_GENERATION_COMMON);
            kafkaMessageSender.send(new KafkaMessageData(otpMessage, OtpMessage.class.getSimpleName(), KafkaConstant.OPT_FOR_LOGIN_2FA));

        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private List<OTP> saveStaticOTP(String countryCode, String mobileNumber, OTPManagement management, String emailId , String staticOTP , Integer mvnoId  , Long buId) {
        List<OTP> otpList = new ArrayList<>();
        String mobileEmailOTP = staticOTP;
        if (ValidateCrudTransactionData.validateStringTypeFieldValue(mobileNumber))
            otpList.add(otpForMobileNo(countryCode, mobileNumber, management, mobileEmailOTP));
        else if (ValidateCrudTransactionData.validateStringTypeFieldValue(emailId))
            otpList.add(otpForEmailId(emailId, management, mobileEmailOTP));
        ForkJoinPool.commonPool().submit(() -> {
            sendOtpGenerationMessage(otpList.get(0), emailId, mvnoId, buId,null);
        });
        return otpList;
    }

    private OTP otpForMobileNo(String countryCode, String mobileNumber, OTPManagement management, String otpNumber) {
        try {
            OTP otp = new OTP();
            otp.setMobile_email(mobileNumber);
            otp.setGeneratedTime(ZonedDateTime.now());
            otp.setValidTillTime(ZonedDateTime.now().plusMinutes(management.getOtpValidityInMin()));
            otp.setOtpStatus(OTPStatus.GENERATED);
            otp.setOtp(otpNumber);
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(countryCode)) otp.setCountryCode(countryCode);
            else otp.setCountryCode(null);
            return otpRepository.save(otp);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private OTP otpForEmailId(String emailId, OTPManagement management, String otpNumber) {
        try {
            OTP otp = new OTP();
            otp.setMobile_email(emailId);
            otp.setGeneratedTime(ZonedDateTime.now());
            otp.setValidTillTime(ZonedDateTime.now().plusMinutes(management.getOtpValidityInMin()));
            otp.setOtpStatus(OTPStatus.GENERATED);
            otp.setOtp(otpNumber);
            return otpRepository.save(otp);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String generateOTP(OTPManagement management) {
        return RandomStringGenerator.generate(getAllowedValues(management.getType()), management.getOtpLength());
    }

    private String getAllowedValues(List<FieldType> otpTypes) {
        return otpTypes.stream().map(otpType -> otpType.getAllowedValues()).collect(Collectors.joining());
    }

    private Predicate<OTP> checkOTPValidity() {
        return otp -> otp.getOtpStatus() == OTPStatus.GENERATED && ZonedDateTime.now().isBefore(otp.getValidTillTime());
    }

    @Override
    protected JpaRepository getRepository() {
        return null;
    }



    public void validateOTP(ValidateOtpDto validateOtpDto, Integer mvnoId) {
        try {
            StaffUser staffUser = new StaffUser();
            Customers customer = new Customers();
            String mobilNumber = "";
            String emaiId = "";
            if(validateOtpDto.getOtpForStaff()){
                staffUser = staffUserRepository.findStaffUserByUsername(validateOtpDto.getUsername());
                mobilNumber = staffUser.getPhone();
                emaiId = staffUser.getEmail();
            }else{
                customer = customerRepository.findCustomersByUsernameAndMvnoId(validateOtpDto.getUsername(), mvnoId);
                mobilNumber = customer.getMobile();
                emaiId = customer.getEmail();
            }

            if(staffUser!=null){
                if (!ValidateCrudTransactionData.validateStringTypeFieldValue(mobilNumber) && !ValidateCrudTransactionData.validateStringTypeFieldValue(emaiId)) {
                    throw new IllegalArgumentException("Please enter valid mobile number or email id.");
                } else if (ValidateCrudTransactionData.validateStringTypeFieldValue(emaiId) && !ValidateCrudTransactionData.validateEmailAddress(emaiId)) {
                    throw new IllegalArgumentException("Invalid email id.");
                } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(validateOtpDto.getOtp())) {
                    throw new IllegalArgumentException("Please enter valid otp.");
                } else {
                    OTP matchedOtp = new OTP();
                    if (ValidateCrudTransactionData.validateStringTypeFieldValue(mobilNumber)) {
                        matchedOtp = findByMvnoIdAndMobileEmailOrderByGeneratedTimeDesc(mobilNumber).stream().findFirst().filter(otp -> OTPStatus.GENERATED == otp.getOtpStatus()).filter(otp -> otp.getOtp().equals(validateOtpDto.getOtp())).filter(otp -> ZonedDateTime.now().isBefore(otp.getValidTillTime())).orElse(null);
//                            .orElseThrow(() -> new RuntimeException(WifiConstants.OTP_NOT_MATCH + "OTP is invalid or OTP is expired."));
                    }
                    if (ValidateCrudTransactionData.validateStringTypeFieldValue(emaiId) && matchedOtp == null) {
                        matchedOtp = findByMvnoIdAndMobileEmailOrderByGeneratedTimeDesc(emaiId).stream().findFirst().filter(otp -> OTPStatus.GENERATED == otp.getOtpStatus()).filter(otp -> otp.getOtp().equals(validateOtpDto.getOtp())).filter(otp -> ZonedDateTime.now().isBefore(otp.getValidTillTime())).orElse(null);
//                            .orElseThrow(() -> new RuntimeException(WifiConstants.OTP_NOT_MATCH + "OTP is invalid or OTP is expired."));
                    }
                    if (Objects.nonNull(matchedOtp)) {
                        matchedOtp.setOtpStatus(OTPStatus.USED);
                        otpRepository.save(matchedOtp);
                    } else {
                        throw new RuntimeException( "OTP is invalid or OTP is expired.");
                    }
                }
            }

        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String maskEmail(String email) {
        String[] parts = email.split("@");
        if (parts[0].length() > 2) {
            String localPart = parts[0];
            String domain = parts[1];
            String maskedPart = new String(new char[localPart.length() - 2]).replace("\0", "*");
            return localPart.substring(0, 2) + maskedPart + "@" + domain;
        }
        return email; // fallback for short local parts
    }







}
