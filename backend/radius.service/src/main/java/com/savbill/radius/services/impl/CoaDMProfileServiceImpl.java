package com.savbill.radius.services.impl;

import com.savbill.radius.entity.CoaDMProfile;
import com.savbill.radius.entity.CoaDMProfileAttribute;
import com.savbill.radius.entity.QCoaDMProfile;
import com.savbill.radius.helper.CoaDMProfileAttributeDto;
import com.savbill.radius.helper.CoaDMProfileDto;
import com.savbill.radius.repository.CoaDMProfileAttributeRepository;
import com.savbill.radius.repository.CoaDMProfileRepository;
import com.savbill.radius.repository.RadiusProfileRepository;
import com.savbill.radius.services.CoaDMProfileAttributeService;
import com.savbill.radius.services.CoaDMProfileService;
import com.savbill.radius.services.DeviceService;
import com.savbill.radius.utils.LogConstants;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.RadiusUtils;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CoaDMProfileServiceImpl implements CoaDMProfileService {

    @Autowired
    private CoaDMProfileRepository coaDMProfileRepository;
    @Autowired
    private CoaDMProfileAttributeService coaDMProfileAttributeService;
    @Autowired
    private RadiusProfileRepository radiusProfileRepository;

    @Autowired
    CoaDMProfileAttributeRepository coaDMProfileAttributeRepository;

    @Autowired
    private DeviceService deviceService;

    private static final Logger log = LoggerFactory.getLogger(CoaDMProfileServiceImpl.class);

    @Override
    public CoaDMProfile findCoaDMProfileById(Long id, Integer mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id))
                throw new IllegalArgumentException(
                        RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid CoA/DM profile id.");
            QCoaDMProfile qCoaDmProfile = QCoaDMProfile.coaDMProfile;
            BooleanExpression boolExp = qCoaDmProfile.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qCoaDmProfile.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
            boolExp = boolExp.and(qCoaDmProfile.coaDMProfileId.eq(id));

            Optional<CoaDMProfile> coaDMProfile = coaDMProfileRepository.findOne(boolExp);
            if (!coaDMProfile.isPresent()) {
                throw new IllegalArgumentException(
                        "No record found with CoA/DM id " + id + " . Please enter valid CoA/DM id.");
            }
         //   log.info("found coaDMProfile "+coaDMProfile.get());
            return coaDMProfile.get();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private CoaDMProfile validateCoaDMProfileToDeleteOrUpdate(Long id, Integer mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id))
                throw new IllegalArgumentException(
                        RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid CoA/DM profile id.");
            QCoaDMProfile qCoaDmProfile = QCoaDMProfile.coaDMProfile;
            BooleanExpression boolExp = qCoaDmProfile.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qCoaDmProfile.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
            boolExp = boolExp.and(qCoaDmProfile.coaDMProfileId.eq(id));

            Optional<CoaDMProfile> coaDMProfile = coaDMProfileRepository.findOne(boolExp);
            if (!coaDMProfile.isPresent()) {
                throw new IllegalArgumentException("You do not have access to update or delete this record.");
            }
            return coaDMProfile.get();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Optional<CoaDMProfile> findCoaDMProfileByName(String name, Integer mvnoId) {
        try {

            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(name))
                throw new IllegalArgumentException(
                        RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid CoA/DM profile name.");
            QCoaDMProfile qCoaDmProfile = QCoaDMProfile.coaDMProfile;
            BooleanExpression boolExp = qCoaDmProfile.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qCoaDmProfile.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
            boolExp = boolExp.and(qCoaDmProfile.name.eq(name));

            Optional<CoaDMProfile> coaDMProfile = coaDMProfileRepository.findOne(boolExp);
            if (!coaDMProfile.isPresent()) {
                throw new IllegalArgumentException(
                        "No record found with CoA/DM name " + name + " . Please enter valid CoA/DM name.");
            }
            return coaDMProfile;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public CoaDMProfile validateCoaDMProfileByName(String name, Integer mvnoId) {
        try {

            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(name))
                throw new IllegalArgumentException(
                        RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid CoA/DM profile name.");
            QCoaDMProfile qCoaDmProfile = QCoaDMProfile.coaDMProfile;
            BooleanExpression boolExp = qCoaDmProfile.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qCoaDmProfile.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
            boolExp = boolExp.and(qCoaDmProfile.name.eq(name));

            List<CoaDMProfile> coaDMProfile = IterableUtils.toList(coaDMProfileRepository.findAll(boolExp));
            if (coaDMProfile.isEmpty()) {
                throw new IllegalArgumentException(
                        "No record found with CoA/DM name " + name + " . Please enter valid CoA/DM name.");
            }
            return coaDMProfile.get(0);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void checkDuplicateEntity(CoaDMProfile coaDMProfile, Integer mvnoId, Boolean isUpdate) {
        try {

            QCoaDMProfile qCoaDmProfile = QCoaDMProfile.coaDMProfile;
            BooleanExpression boolExp = qCoaDmProfile.isNotNull();
            String msg = "CoA/DM with name '" + coaDMProfile.getName() + "' is already exist. Please enter unique Coa/DM name.";

            if (isUpdate) {
                boolExp = boolExp.and(qCoaDmProfile.coaDMProfileId.ne(coaDMProfile.getCoaDMProfileId()));
            }

            if (mvnoId == 1) {
                boolExp = boolExp.and(qCoaDmProfile.name.eq(coaDMProfile.getName()));
                List<CoaDMProfile> coaDMProfileList = (List<CoaDMProfile>) coaDMProfileRepository.findAll(boolExp);
                if (!coaDMProfileList.isEmpty()) {
                    throw new IllegalArgumentException(msg);
                }
            } else {
                boolExp = boolExp.and(qCoaDmProfile.name.eq(coaDMProfile.getName())).and((qCoaDmProfile.mvnoId.eq(mvnoId)).or(qCoaDmProfile.mvnoId.eq(1)));
                Optional<CoaDMProfile> optionalCoaDMProfile = coaDMProfileRepository.findOne(boolExp);
                if (optionalCoaDMProfile.isPresent()) {
                    throw new IllegalArgumentException(msg);
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<CoaDMProfile> findByType(String type, Integer mvnoId) {
        try {
            QCoaDMProfile qCoaDMProfile = QCoaDMProfile.coaDMProfile;
            BooleanExpression exp = qCoaDMProfile.isNotNull();
            if(type != null && !type.equalsIgnoreCase ("Both"))
                exp = exp.and(qCoaDMProfile.type.eq(type));
            if (mvnoId == null || mvnoId != 1)
                exp = exp.and(qCoaDMProfile.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
            return (List<CoaDMProfile>) coaDMProfileRepository.findAll(exp);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<CoaDMProfile> findAllCoaDMProfiles(Integer mvnoId) {
        try {
            QCoaDMProfile qCoaDMProfile = QCoaDMProfile.coaDMProfile;
            BooleanExpression exp = qCoaDMProfile.isNotNull();
            if (mvnoId != null && mvnoId == 1)
                return coaDMProfileRepository.findAll();
            else {
                exp = exp.and(qCoaDMProfile.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
                return (List<CoaDMProfile>) coaDMProfileRepository.findAll(exp);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteCoaDMProfileById(Long id, Integer mvnoId) {
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        try {
            CoaDMProfile coaDMProfile = validateCoaDMProfileToDeleteOrUpdate(id, mvnoId);

            coaDMProfileAttributeService.deleteCoaDMProfileAttributeByCoaDmProfileId(id, mvnoId);
            coaDMProfileRepository.deleteById(id);
          //  log.info("CoaDMProfile deleted successfully: " + coaDMProfile.getName());

        } catch (RuntimeException e) {
           // log.error("Error while deleting CoaDMProfile: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }

    @Override
    public CoaDMProfile saveCoaDMProfile(CoaDMProfileDto coaDMProfileDto, Integer mvnoId) {
        try {
            CoaDMProfile coaDMProfile = new CoaDMProfile(coaDMProfileDto);
            coaDMProfile.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
            validateCoaDMProfileData(coaDMProfile, false, ValidateCrudTransactionData.validateMvnoId(mvnoId));
            coaDMProfile.setCreatedOn(new Timestamp(new Date().getTime()));
            coaDMProfile.setLastModifiedOn(new Timestamp(new Date().getTime()));
            coaDMProfile.setTimevar(coaDMProfile.getTimevar());

//		coaDMProfile.setUnitsOftime(coaDMProfile.getUnitsOftime());
            if (coaDMProfileDto.getCoaDMProfileAttributeDtoList() != null) {
                for (CoaDMProfileAttributeDto coaDMProfileAttributeDto : coaDMProfileDto
                        .getCoaDMProfileAttributeDtoList()) {
                    CoaDMProfileAttribute coaDMProfileAttribute = new CoaDMProfileAttribute(coaDMProfileAttributeDto);
                    validateProfileAttributData(coaDMProfileAttribute, false);
                }
            }
            CoaDMProfile save = coaDMProfileRepository.save(coaDMProfile);
            if (coaDMProfileDto.getCoaDMProfileAttributeDtoList() != null) {
                for (CoaDMProfileAttributeDto coaDMProfileAttributeDto : coaDMProfileDto
                        .getCoaDMProfileAttributeDtoList()) {
                    coaDMProfileAttributeDto.setCoaDMProfileId(save.getCoaDMProfileId());
                    coaDMProfileAttributeService.saveCoaDMProfileAttribute(coaDMProfileAttributeDto,
                            ValidateCrudTransactionData.validateMvnoId(mvnoId));
                }
            }
            return save;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public CoaDMProfile updateCoaDMProfile(CoaDMProfile coaDMProfile, Integer mvnoId, HttpServletRequest request) {
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        try {
            CoaDMProfile oldCoaDMProfile = validateCoaDMProfileToDeleteOrUpdate(coaDMProfile.getCoaDMProfileId(), mvnoId);
            if (mvnoId != null && mvnoId == 1)
                coaDMProfile.setMvnoId(oldCoaDMProfile.getMvnoId());
            else
                coaDMProfile.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
            validateCoaDMProfileData(coaDMProfile, true, ValidateCrudTransactionData.validateMvnoId(mvnoId));
            coaDMProfile.setCreatedOn(oldCoaDMProfile.getCreatedOn());
            coaDMProfile.setLastModifiedOn(new Timestamp(new Date().getTime()));
//			coaDMProfile.setUnitsOftime(coaDMProfile.getUnitsOftime());
            coaDMProfile.setTimevar(coaDMProfile.getTimevar());
            String updates = RadiusUtils.getUpdatedDiff(oldCoaDMProfile, coaDMProfile);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "COA/DM Profile has been updated successfully updated:,"+updates+ LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
          //  log.info("CoaDM Profile has been update successfully by " + MDC.get("userName") + " the updated object is " + updates);
            return coaDMProfileRepository.save(coaDMProfile);
        } catch (RuntimeException e) {
         //   log.error("Error while updating CoaDM Profile: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }

    private void validateCoaDMProfileData(CoaDMProfile coaDMProfile, boolean isUpdate, Integer mvnoId) {

        if (!ValidateCrudTransactionData.validateStringTypeFieldValue(coaDMProfile.getName())) {
            throw new IllegalArgumentException(
                    "COA/DM Profile name is mandatory. Please enter valid COA/DM Profile name.");
        }
//	else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(coaDMProfile.getGateway())) {
//	    throw new IllegalArgumentException("COA/DM Profile gateway is mandatory. Please enter valid gateway.");
//	}
        else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(coaDMProfile.getSharedkey())) {
            throw new IllegalArgumentException(
                    "COA/DM Profile sharedkey is mandatory. Please enter valid COA/DM Profile sharedKey.");
        } else if (!ValidateCrudTransactionData.validateIntegerTypeFieldValue(coaDMProfile.getPort())) {
            throw new IllegalArgumentException(
                    "COA/DM Profile port is mandatory. Please enter valid COA/DM Profile port.");
        } else if (!coaDMProfile.getType().equalsIgnoreCase(RadiusConstants.COA)
                && !coaDMProfile.getType().equalsIgnoreCase(RadiusConstants.DM)) {
            throw new IllegalArgumentException("Please enter valid COA/DM Profile type. It should be '"
                    + RadiusConstants.COA + "' or '" + RadiusConstants.DM + "'");
        } else if (!isUpdate) {
            checkDuplicateEntity(coaDMProfile, coaDMProfile.getMvnoId(), false);
        } else if (isUpdate) {
            checkDuplicateEntity(coaDMProfile, coaDMProfile.getMvnoId(), true);
        }
    }

    @Override
    public List<CoaDMProfile> searchCoaDMProfile(String coaDMProfileName, String coaDMProfileType, Integer mvnoId) {

        try {
            if (coaDMProfileName == null || coaDMProfileName.equals("null")) {
                coaDMProfileName = "";
            }
            if (coaDMProfileType == null || coaDMProfileType.equals("null")) {
                coaDMProfileType = "";
            }
            QCoaDMProfile qCoaDMProfile = QCoaDMProfile.coaDMProfile;
            BooleanExpression boolExp = qCoaDMProfile.isNotNull();
            if (!coaDMProfileName.isEmpty() && !coaDMProfileType.isEmpty())
                boolExp = boolExp.and(qCoaDMProfile.name.like("%" + coaDMProfileName + "%"))
                        .and(qCoaDMProfile.type.like("%" + coaDMProfileType + "%"));
            else if (!coaDMProfileName.isEmpty())
                boolExp = boolExp.and(qCoaDMProfile.name.like("%" + coaDMProfileName + "%"));
            else
                boolExp = boolExp.and(qCoaDMProfile.type.like("%" + coaDMProfileType + "%"));

            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qCoaDMProfile.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
            List coaDmProfileList = (List<CoaDMProfile>) coaDMProfileRepository.findAll(boolExp);
//	    if(coaDmProfileList.isEmpty())
//		{
//		if (!StringUtils.isBlank(coaDMProfileName) && !StringUtils.isBlank(coaDMProfileType)) {
//			throw new IllegalArgumentException(
//					"No record found by with profile name: "+coaDMProfileName+" profile type: "+coaDMProfileType);
//        } else if (!StringUtils.isBlank(coaDMProfileName) && StringUtils.isBlank(coaDMProfileType)) {
//        	throw new IllegalArgumentException(
//					"No record found by with profile name: "+coaDMProfileName);
//        } else if (StringUtils.isBlank(coaDMProfileName) && !StringUtils.isBlank(coaDMProfileType)) {
//        	throw new IllegalArgumentException(
//					"No record found by with profile type: "+coaDMProfileType);
//        } else {
//        	throw new IllegalArgumentException("No record found!");
//         }
//		}
            return coaDmProfileList;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validateProfileAttributData(CoaDMProfileAttribute coaDMProfileAttribute, boolean isUpdate) {
        if (!ValidateCrudTransactionData.validateStringTypeFieldValue(coaDMProfileAttribute.getRadiusAtt())) {
            throw new IllegalArgumentException(
                    "COA/DM Profile Radius Attribute is mandatory. Please enter valid COA/DM Profile Radius Attribute.");
        } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(coaDMProfileAttribute.getProfileAtt())) {
            throw new IllegalArgumentException(
                    "Profile Attribute is mandatory. Please enter valid COA/DM Profile Attribute.");
        }
    }

    @Override
    public List<CoaDMProfile> findCoaProfiles(Integer mvnoId) {
        try {
            QCoaDMProfile qCoaDmProfile = QCoaDMProfile.coaDMProfile;
            BooleanExpression exp = qCoaDmProfile.isNotNull();
            exp = exp.and(qCoaDmProfile.type.eq(RadiusConstants.COA));
            if (mvnoId == null || mvnoId != 1)
                exp = exp.and(qCoaDmProfile.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
            return (List<CoaDMProfile>) coaDMProfileRepository.findAll(exp);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
