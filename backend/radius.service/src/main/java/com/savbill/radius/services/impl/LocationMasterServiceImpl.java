package com.savbill.radius.services.impl;

import com.savbill.radius.entity.LocationMaster;
import com.savbill.radius.entity.QLocationMaster;
import com.savbill.radius.kafka.message.LocationMessage;
import com.savbill.radius.repository.LocationMasterRepository;
import com.savbill.radius.services.LocationMasterService;
import com.savbill.radius.utils.LogConstants;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.UpdateDiffFinder;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Service
public class LocationMasterServiceImpl implements LocationMasterService {


    private static final Logger log = LoggerFactory.getLogger(LocationMasterServiceImpl.class);

//	@Autowired
//	private MessageSender messageSender;
    @Autowired
    private LocationMasterRepository locationMasterRepository;
    @Autowired
    private UpdateDiffFinder updateDiffFinder;

    @Override
    public LocationMaster saveLocationMaster(LocationMaster locationmaster, Integer mvnoId) {

        try {
            if (!ValidateCrudTransactionData.validateIntegerTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            }
            LocationMaster locationVo = new LocationMaster(locationmaster, mvnoId);
            //validateLocationMasterDetail(locationMaster, false);

            //checkForUniqueLocationMaster(locationMaster.getName(), mvnoId, locationMaster.getLocationMasterId(), false);
            LocationMaster locationMasterVo = locationMasterRepository.save(locationVo);
            return locationMasterVo;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void addWifiLocation(LocationMessage locationMessage,Boolean isUpdate,Boolean isDelete,HttpServletRequest request) {
        int mvno = Integer.parseInt(locationMessage.getLocationMasterData().get("mvnoId").toString());
        LocationMaster location = new LocationMaster();
        location.setLocationMasterId(Long.parseLong(locationMessage.getLocationMasterData().get("locationMasterId").toString()));
        location.setName(locationMessage.getLocationMasterData().get("name").toString());
        location.setCheckItem(locationMessage.getLocationMasterData().get("checkItem").toString());
        location.setStatus(locationMessage.getLocationMasterData().get("status").toString());
        location.setMvnoId(mvno);
        location.setLocationIdentifyAttribute(locationMessage.getLocationMasterData().get("locationIdentifyAttribute").toString());
        LocationMaster vo = locationMasterRepository.save(location);
        if(!isUpdate && !isDelete) {
            LocationMaster locationMasterVo = saveLocationMaster(location,mvno);
        }else if(isUpdate) {
            LocationMaster locationMasterVo = updateLocation(location,mvno,request);
        }else if(isDelete) {
            deleteLocationById(location.getLocationMasterId(),mvno);
        }
    }

    private void checkForUniqueLocationMaster(String name, Integer mvnoId, Long locationMasterId, boolean isUpdate) {
        try {
            String message = "Location exist with the same name : '" + name + "'";
            QLocationMaster qLocatonMaster = QLocationMaster.locationMaster;
            BooleanExpression boolExp = qLocatonMaster.isNotNull();
            if (isUpdate) {
                boolExp = boolExp.and(qLocatonMaster.locationMasterId.ne(locationMasterId));
            }

            if (mvnoId == 1) {
                boolExp = boolExp.and(qLocatonMaster.name.eq(name));
                List<LocationMaster> locationList = (List<LocationMaster>) locationMasterRepository.findAll(boolExp);
                if (!locationList.isEmpty()) {
                    throw new IllegalArgumentException(message);
                }
            } else {
                boolExp = boolExp.and(qLocatonMaster.name.eq(name))
                        .and((qLocatonMaster.mvnoId.eq(mvnoId)).or(qLocatonMaster.mvnoId.eq(1)));
                Optional<LocationMaster> optionalLocation = locationMasterRepository.findOne(boolExp);
                if (optionalLocation.isPresent()) {
                    throw new IllegalArgumentException(message);
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private void validateLocationMasterDetail(LocationMaster locationMaster, boolean isUpdate) {
        try {
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(locationMaster.getName()))
                throw new RuntimeException("LocationMaster name is mandatory. Please enter valid LocationMaster name");
            else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(locationMaster.getStatus())
                    || (!locationMaster.getStatus().equals(RadiusConstants.ACTIVE)
                    && !locationMaster.getStatus().equals(RadiusConstants.IN_ACTIVE))) {
                throw new RuntimeException("Status is mandatory. Please enter valid status. It should be '"
                        + RadiusConstants.ACTIVE + "' or '" + RadiusConstants.IN_ACTIVE + "'");
            } else if (locationMaster.getCheckItem() != null
                    && locationMaster.getCheckItem().equalsIgnoreCase(RadiusConstants.BLANK_STRING)) {
                locationMaster.setCheckItem(null);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<LocationMaster> findAllLocationMaster(Integer mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateIntegerTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            } else {
                QLocationMaster qLocationMaster = QLocationMaster.locationMaster;
                BooleanExpression exp = qLocationMaster.isNotNull();
                if (mvnoId == 1) {
                    return locationMasterRepository.findAll();
                } else {
                    exp = exp.and(qLocationMaster.mvnoId.eq(mvnoId).or(qLocationMaster.mvnoId.eq(1)));
                    return (List<LocationMaster>) locationMasterRepository.findAll(exp);
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public LocationMaster findlocationMasterById(Long locationMasterId, Integer mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateIntegerTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            } else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(locationMasterId)) {
                throw new IllegalArgumentException(
                        RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid locationMaster id.");
            } else {
                Optional<LocationMaster> optionalLocation = locationMasterRepository
                        .findByLocationMasterIdAndMvnoId(locationMasterId, mvnoId);
                if (optionalLocation.isPresent()) {
                    return optionalLocation.get();
                } else {
                    throw new RuntimeException(
                            "No record found for location with the given location id :'" + locationMasterId
                                    + "' and mvno id : '" + mvnoId + "', Please enter valid location id and mvno id");
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public LocationMaster updateLocation(LocationMaster locationDto, Integer mvnoId, HttpServletRequest request) {
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
        try {
            if (!ValidateCrudTransactionData.validateIntegerTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            }
            LocationMaster locationM = new LocationMaster(locationDto, mvnoId);
            QLocationMaster qLocation = QLocationMaster.locationMaster;
            BooleanExpression exp = qLocation.isNotNull();
            exp = exp.and(
                    qLocation.mvnoId.eq(mvnoId).and(qLocation.locationMasterId.eq(locationDto.getLocationMasterId())));
            Optional<LocationMaster> location = locationMasterRepository.findOne(exp);

            if (!location.isPresent()) {
                LocationMaster locationMasterVo = saveLocationMaster(locationDto,mvnoId);
                //throw new RuntimeException("Records not found to udpate.");
            }

            String updatedValues = updateDiffFinder.getUpdatedDiff(location.get(), locationM);
            LocationMaster locationEntity = location.get();
            locationM.setName(locationEntity.getName());
            //validateLocationMasterDetail(locationM, true);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Location master has been Updated successfully updated data,"+updatedValues + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            //checkForUniqueLocationMaster(locationM.getName(), mvnoId, locationM.getLocationMasterId(), true);
            LocationMaster locationMasterVo = locationMasterRepository.save(locationM);
            return locationMasterVo;
        } catch (Throwable e) {
            //	log.error("Error while update location: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }

    @Override
    public void deleteLocationById(Long locationMasterId, Integer mvnoId) {
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        try {
            if (!ValidateCrudTransactionData.validateIntegerTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            }
            Optional<LocationMaster> optionalLocation = locationMasterRepository
                    .findByLocationMasterIdAndMvnoId(locationMasterId, mvnoId);
            if (optionalLocation.isPresent()) {
                log.info("Location has been deleted successfully: " + optionalLocation.get().getName() + " by "
                        + MDC.get(RadiusConstants.USER_NAME));
                locationMasterRepository.delete(optionalLocation.get());
            } else {
                throw new IllegalArgumentException("No record found to delete.");
            }
        } catch (Throwable e) {
            log.error("Error while delete location: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<LocationMaster> findLocation(String name, Integer mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateIntegerTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            }
            if (name == null || name.equals("null")) {
                name = "";
            }

            QLocationMaster qLocationMaster = QLocationMaster.locationMaster;
            BooleanExpression boolExp = qLocationMaster.isNotNull();
            if ((!ValidateCrudTransactionData.validateStringTypeFieldValue(name) || name.equalsIgnoreCase("null"))) {
                if (mvnoId == 1) {
                    return locationMasterRepository.findAll();
                } else {
                    boolExp = boolExp.and(qLocationMaster.mvnoId.eq(mvnoId)).or(qLocationMaster.mvnoId.eq(1));
                    return (List<LocationMaster>) locationMasterRepository.findAll(boolExp);
                }
            } else {
                if (!name.isEmpty()) {
                    if (mvnoId == 1) {
                        boolExp = boolExp.and(qLocationMaster.name.contains(name));
                    } else {
                        boolExp = boolExp.and(qLocationMaster.name.contains(name))
                                .and(qLocationMaster.mvnoId.eq(mvnoId).or(qLocationMaster.mvnoId.eq(1)));
                    }
                } else if (!name.isEmpty()) {
                    if (mvnoId == 1) {
                        boolExp = boolExp.and(qLocationMaster.name.contains(name));
                    } else {
                        boolExp = boolExp.and(qLocationMaster.name.contains(name))
                                .and(qLocationMaster.mvnoId.eq(mvnoId).or(qLocationMaster.mvnoId.eq(1)));
                    }
                }

                return (List<LocationMaster>) locationMasterRepository.findAll(boolExp);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String updateLocationStatus(String name, String status, Integer mvnoId,HttpServletRequest request) {
        try {

            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(name)) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG + "LocationName is mandatory. Please enter valid location name.");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(status)) {
                throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG + "Location status is mandatory. Please enter valid location status.");
            } else if (!status.equals(RadiusConstants.ACTIVE) && !status.equals(RadiusConstants.IN_ACTIVE)) {
                throw new IllegalArgumentException("Please enter valid location status. It should be '" + RadiusConstants.ACTIVE + "' or '" + RadiusConstants.IN_ACTIVE + "'");
            }

            LocationMaster locationMaster = validateLocationForUpdateOrDelete(name, ValidateCrudTransactionData.validateMvnoId(mvnoId));
            String oldtatud=locationMaster.getStatus();
            locationMaster.setStatus(status);
            locationMasterRepository.save(locationMaster);
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Location master has been updated successfully,from "+oldtatud+" updated to" +status+ LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
            String msg = "";
            if (status.equals(RadiusConstants.ACTIVE)) {
                msg = "LocationMaster '" + locationMaster.getName() + "' has been activated successfully.";
            } else {
                msg = "LocationMaster '" + locationMaster.getName() + "' has been inactivated successfully.";
            }
            return msg;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private LocationMaster validateLocationForUpdateOrDelete(String name, Integer mvnoId) {
        try {

            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(name))
                throw new IllegalArgumentException("Please enter valid Location name.");
            QLocationMaster qLocationMaster = QLocationMaster.locationMaster;
            BooleanExpression boolExp = qLocationMaster.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qLocationMaster.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
            boolExp = boolExp.and(qLocationMaster.name.eq(name));

            Optional<LocationMaster> optionalLocation = locationMasterRepository.findOne(boolExp);
            if (!optionalLocation.isPresent()) {
                throw new IllegalArgumentException("You do not have access/No records found to update or delete this record.");
            }
            return optionalLocation.get();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
