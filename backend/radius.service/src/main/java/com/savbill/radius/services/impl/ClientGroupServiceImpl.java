package com.savbill.radius.services.impl;

import com.savbill.radius.entity.*;
import com.savbill.radius.entity.*;
import com.savbill.radius.helper.ClientGroupDto;
import com.savbill.radius.repository.*;
import com.savbill.radius.repository.*;
import com.savbill.radius.services.ClientGroupService;
import com.savbill.radius.services.ClientReplyService;
import com.savbill.radius.services.CoaDMProfileService;
import com.savbill.radius.utils.LogConstants;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientGroupServiceImpl implements ClientGroupService {

    @Autowired
    private ClientGroupRepository clientGroupRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientReplyService clientReplyService;

    @Autowired
    private CoaDMProfileService coaDMProfileService;

    @Autowired
    private CoaDmProfileMappingRepository coaDmProfileMappingRepository;

    @Autowired
    private DynamicAttributeMappingRepository dynamicAttributeMappingRepository;
    @Autowired
    private SuspendedProfileMappingRepository suspendedProfileMappingRepository;
    @Autowired
    private ClearCacheMappingRepository clearCacheMappingRepository;

    private static final String DM = "DM";
    private static final String COA = "CoA";
    private static final String NONE = "None";
    private static final String BOTH = "Both";


    private static final Logger log = LoggerFactory.getLogger(ClientGroupServiceImpl.class);

    @Override
    public List<ClientGroup> findClientGroupByName(String groupName, Integer mvnoId) {

        try {

            QClientGroup qClientGroup = QClientGroup.clientGroup;
            BooleanExpression boolExp = qClientGroup.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qClientGroup.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
            if (StringUtils.isBlank(groupName) || groupName.equalsIgnoreCase("null")) {
                return (List<ClientGroup>) clientGroupRepository.findAll(boolExp);
            } else {
                boolExp = boolExp.and(qClientGroup.name.containsIgnoreCase(groupName));
                List clientGroupList = (List<ClientGroup>) clientGroupRepository.findAll(boolExp);
//				if(clientGroupList.isEmpty())
//				{
//					throw new IllegalArgumentException(
//							"No record found by with client group name: "+groupName+" Please enter valid group name");
//				}
                return clientGroupList;
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ClientGroup findClientGroupById(Long id, Integer mvnoId) {

        try {

            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id)) {
                throw new IllegalArgumentException("Please enter valid client group id.");
            }
            QClientGroup qClientGroup = QClientGroup.clientGroup;
            BooleanExpression boolExp = qClientGroup.isNotNull();

            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qClientGroup.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
            boolExp = boolExp.and(qClientGroup.clientGroupId.eq(id));


            Optional<ClientGroup> clientGroup = clientGroupRepository.findOne(boolExp);
            List<CoaDmProfileMapping> coaDmProfileMappingList = clientGroup.get().getCoaDmProfileMappings();
            if (!coaDmProfileMappingList.isEmpty()) {
                coaDmProfileMappingList = coaDmProfileMappingList.stream().peek(coaDmProfileMapping -> {
                    if (coaDmProfileMapping.getCoaProfileId() != null && coaDmProfileMapping.getDmProfileId() == null) {
                        coaDmProfileMapping.setCoaDmSelection("CoA");
                    }
                    if (coaDmProfileMapping.getCoaProfileId() == null && coaDmProfileMapping.getDmProfileId() != null) {
                        coaDmProfileMapping.setCoaDmSelection("DM");
                    }
                    if (coaDmProfileMapping.getCoaProfileId() != null && coaDmProfileMapping.getDmProfileId() != null) {
                        coaDmProfileMapping.setCoaDmSelection("Both");
                    }
                    if (coaDmProfileMapping.getCoaDmSelection() == null && coaDmProfileMapping.getCoaProfileId() == null && coaDmProfileMapping.getDmProfileId() == null) {
                        coaDmProfileMapping.setCoaDmSelection("None");
                    }
                }).collect(Collectors.toList());
            }
            clientGroup.get().setCoaDmProfileMappings(coaDmProfileMappingList);
//			List<DynamicAttributeMapping> dynamicAttributeMappings = dynamicAttributeMappingRepository.findAllByClientGroupId(clientGroup.get().getClientGroupId());
//			if(!CollectionUtils.isEmpty(dynamicAttributeMappings)) {
//				clientGroup.get().set
//			}
            if (!clientGroup.isPresent()) {
                throw new IllegalArgumentException("No record found for client group with client group id : '" + id
                        + "'. Please enter valid client group id.");
            }

            return clientGroup.get();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public ClientGroup validateGroupById(Long id, Integer mvnoId) {

        try {

            if (id == null || id == 0) {
                throw new IllegalArgumentException("Please enter valid client group id.");
            }
            QClientGroup qClientGroup = QClientGroup.clientGroup;
            BooleanExpression boolExp = qClientGroup.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qClientGroup.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
            boolExp = boolExp.and(qClientGroup.clientGroupId.eq(id));

            Optional<ClientGroup> clientGroup = clientGroupRepository.findOne(boolExp);

            if (!clientGroup.isPresent()) {
                throw new IllegalArgumentException(
                        "You do not have access to update/delete this record or no record found for client group with client group id : '"
                                + id + "'. Please enter valid client group id.");
            }

            return clientGroup.get();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<ClientGroup> findAllClientGroups(Integer mvnoId) {

        try {
            QClientGroup qClientGroup = QClientGroup.clientGroup;
            BooleanExpression boolExp = qClientGroup.isNotNull();
            if (mvnoId != null && mvnoId == 1)
                return clientGroupRepository.findAll();
            else {
                boolExp = boolExp.and(qClientGroup.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
                return (List<ClientGroup>) clientGroupRepository.findAll(boolExp);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteClientGroupById(Long id, Integer mvnoId) {

        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
        try {
            ClientGroup clientGroup = validateGroupById(id, mvnoId);
            List<Object[]> clientVo = clientRepository.checkForClientGroupIp(id);
            if (!clientVo.isEmpty()) {
                throw new IllegalArgumentException(
                        "This operation will not allow as this Client Group is used for Radius Client.");
            }
            clientReplyService.deleteByClientGroupId(id, mvnoId);
            clientGroupRepository.deleteById(id);
            log.info("Client group deleted succefully: " + clientGroup.getName());
        } catch (RuntimeException e) {
            log.error("Error while deleting client group: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }

    @Override
    public ClientGroup saveClientGroup(ClientGroupDto clientGroupDto, Integer mvnoId) {

        try {
            CoaDMProfile coaDMProfile = validateCoaDMProfile(clientGroupDto.getCoaDMProfile(), ValidateCrudTransactionData.validateMvnoId(mvnoId));
            CoaDMProfile dmProfile = validateCoaDMProfile(clientGroupDto.getDMProfile(), ValidateCrudTransactionData.validateMvnoId(mvnoId));
            ClientGroup clientGroup = new ClientGroup(clientGroupDto);
            clientGroup.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
            validateClientGroupData(clientGroup, false);
            clientGroup.setCreatedOn(new Timestamp(new Date().getTime()));
            clientGroup.setLastModifiedOn(new Timestamp(new Date().getTime()));
            if (clientGroupDto.getClientReplyList() != null) {
                for (ClientReply clientReply : clientGroupDto.getClientReplyList()) {
                    validateClientAttributData(clientReply, false);
                }
            }


            ClientGroup clientGroupVo = clientGroupRepository.save(clientGroup);
            if (clientGroupDto.getCoaDMProfileMappings() != null && !clientGroupDto.getCoaDMProfileMappings().isEmpty()) {
                List<CoaDmProfileMapping> coaDmProfileMappingList = clientGroupDto.getCoaDMProfileMappings();
                coaDmProfileMappingList = coaDmProfileMappingList.stream().peek(coaDmProfileMapping -> coaDmProfileMapping.setClientGroupId(clientGroupVo.getClientGroupId())).collect(Collectors.toList());
                coaDmProfileMappingRepository.saveAll(coaDmProfileMappingList);
            }
            if (clientGroupDto.getClientReplyList() != null) {
                for (ClientReply clientReply : clientGroupDto.getClientReplyList()) {
                    clientReply.setClientGroupId(clientGroupVo.getClientGroupId());
                    clientReplyService.addClientReply(clientReply, ValidateCrudTransactionData.validateMvnoId(mvnoId));
                }
            }


            return clientGroupVo;

        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validateClientAttributData(ClientReply clientReply, boolean isUpdate) {
        if (!ValidateCrudTransactionData.validateStringTypeFieldValue(clientReply.getAttribute())) {
            throw new IllegalArgumentException(
                    "Client Radius Attribute is mandatory. Please enter valid Customer Radius Attribute.");
        } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(clientReply.getAttributeValue())) {
            throw new IllegalArgumentException(
                    "Profile Attribute is mandatory. Please enter valid Client Profile Attribute.");
        }
    }

    @Transactional
    @Override
    public ClientGroup updateClientGroup(ClientGroupDto clientGroupDto, Integer mvnoId) {
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);

        try {
            CoaDMProfile coaDMProfile = validateCoaDMProfile(clientGroupDto.getCoaDMProfile(), ValidateCrudTransactionData.validateMvnoId(mvnoId));
            CoaDMProfile dmProfile = validateCoaDMProfile(clientGroupDto.getDMProfile(), ValidateCrudTransactionData.validateMvnoId(mvnoId));
            List<ClientGroup> clientGroupList = findClientGroupByName(clientGroupDto.getName(), mvnoId);
            ClientGroup clientGroup;
            if (clientGroupList != null || !clientGroupList.isEmpty()) {
                clientGroup = clientGroupList.get(0);
            } else {
                clientGroup = null;
            }
            ClientGroup oldClientGroup = validateGroupById(clientGroup.getClientGroupId(), mvnoId);
            ClientGroup clientGroupVo = new ClientGroup(clientGroupDto);
            clientGroupVo.setClientGroupId(clientGroup.getClientGroupId());
            clientGroupVo.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
            if (clientGroupVo.getClientReplyList() != null) {
                for (ClientReply clientReply : clientGroupVo.getClientReplyList()) {
                    clientReply.setMvnoId(mvnoId);
                }
            }
            if (clientGroupDto.getCoaDMProfileMappings() != null && clientGroupDto.getCoaDMProfileMappings().isEmpty()) {
                List<CoaDmProfileMapping> newCoaDmProfileMappingList = clientGroupDto.getCoaDMProfileMappings();
                newCoaDmProfileMappingList = newCoaDmProfileMappingList.stream().peek(coaDmProfileMapping -> coaDmProfileMapping.setClientGroupId(clientGroupVo.getClientGroupId())).collect(Collectors.toList());
                List<CoaDmProfileMapping> oldCoaDmProfileMappingList = coaDmProfileMappingRepository.findAllByClientGroupId(clientGroupVo.getClientGroupId());
                coaDmProfileMappingRepository.deleteInBatch(oldCoaDmProfileMappingList);
                coaDmProfileMappingRepository.saveAll(newCoaDmProfileMappingList);
            }
            validateClientGroupData(clientGroupVo, true);
            clientGroupVo.setCreatedOn(oldClientGroup.getCreatedOn());
            clientGroupVo.setLastModifiedOn(new Timestamp(new Date().getTime()));
            if (!CollectionUtils.isEmpty(clientGroupDto.getDynamicAttributeMappings())) {
                dynamicAttributeMappingRepository.deleteAllByClientGroupId(clientGroup.getClientGroupId());
//				List<DynamicAttributeMapping> dynamicAttributeMappings = clientGroupDto.getDynamicAttributeMappings().stream().map(a -> new DynamicAttributeMapping(a, clientGroupVo.getClientGroupId())).collect(Collectors.toList());
//				dynamicAttributeMappingRepository.saveAll(dynamicAttributeMappings);
            }
            return clientGroupRepository.save(clientGroupVo);
        } catch (RuntimeException e) {
            //log.error("Error while updating Client Group: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(RadiusConstants.TYPE);
        }
    }

    private void validateClientGroupData(ClientGroup clientGroup, boolean isUpdate) {

        if (clientGroup.getName() == null || clientGroup.getName().isEmpty()
                || clientGroup.getName().equalsIgnoreCase(RadiusConstants.BLANK_STRING)) {
            throw new IllegalArgumentException("Client group name is mandatory. Please enter valid group name.");
        } else if (clientGroup.getCgStatus() == null || clientGroup.getCgStatus().isEmpty()
                || clientGroup.getCgStatus().equalsIgnoreCase(RadiusConstants.BLANK_STRING)) {
            throw new IllegalArgumentException("Client group status is mandatory. Please enter valid group status.");
        } else if (!clientGroup.getCgStatus().equals(RadiusConstants.ACTIVE)
                && !clientGroup.getCgStatus().equals(RadiusConstants.IN_ACTIVE)) {
            throw new IllegalArgumentException("Please enter valid client group status. It should be '"
                    + RadiusConstants.ACTIVE + "' or '" + RadiusConstants.IN_ACTIVE + "'");
        } else if (clientGroup.getName() != null && !isUpdate) {
            validateUniqueClientGroup(clientGroup.getName(), null, clientGroup.getMvnoId(), false);
        } else if (isUpdate) {
            validateClientGroupId(clientGroup);
        }

    }

    private void validateClientGroupId(ClientGroup clientGroup) {

        try {

            if (clientGroup.getClientGroupId() == null || clientGroup.getClientGroupId() == 0) {
                throw new IllegalArgumentException("Please enter valid client group id.");
            } else {
                ClientGroup clientGroupVo = validateGroupById(clientGroup.getClientGroupId(), clientGroup.getMvnoId());
                clientGroup.setMvnoId(clientGroupVo.getMvnoId());
                clientGroup.setCreatedOn(clientGroupVo.getCreatedOn());
                validateUniqueClientGroup(clientGroup.getName(), clientGroup.getClientGroupId(),
                        clientGroup.getMvnoId(), true);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String updateClientGroupStatus(Long clientGroupId, String status, Integer mvnoId, HttpServletRequest request) {

        try {
            ClientGroup optionalClientGroup = validateGroupById(clientGroupId, mvnoId);

            if (status == null || status.isEmpty() || status.equalsIgnoreCase(RadiusConstants.BLANK_STRING)) {
                throw new IllegalArgumentException(
                        "Client group status is mandatory. Please enter valid group status.");
            } else if (!status.equals(RadiusConstants.ACTIVE) && !status.equals(RadiusConstants.IN_ACTIVE)) {
                throw new IllegalArgumentException("Please enter valid client group status. It should be '"
                        + RadiusConstants.ACTIVE + "' or '" + RadiusConstants.IN_ACTIVE + "'");
            }
            String oldStatus = optionalClientGroup.getCgStatus();
            optionalClientGroup.setCgStatus(status);
            optionalClientGroup.setLastModifiedOn(new Timestamp(new Date().getTime()));
            clientGroupRepository.save(optionalClientGroup);
            String msg = "";

            if (status.equals(RadiusConstants.ACTIVE)) {
                msg = "Client group '" + optionalClientGroup.getName() + "' has been activated successfully.";
            } else {
                msg = "Client group '" + optionalClientGroup.getName() + "' has been inactivated successfully.";
            }
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Updating Client group Status :," + oldStatus + " is updated to" + status + LogConstants.REQUEST_BY + MDC.get(RadiusConstants.USER_NAME) + "," + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
            return msg;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validateUniqueClientGroup(String name, Long clientGroupId, Integer mvnoId, Boolean isUpdate) {
        try {
            // List<Object[]> list = clientGroupRepository.checkForUniqueClientGroup(name);
            BooleanExpression boolExp = this.validateUniqueClientGroupByNameAndClientId(name, clientGroupId, mvnoId,
                    isUpdate);
            List<ClientGroup> clientGroupList = (List<ClientGroup>) clientGroupRepository.findAll(boolExp);

            if (clientGroupList.size() > 0) {
                throw new IllegalArgumentException("Client group with name : '" + name
                        + "' is already exist in the system, Please enter unique group name.");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

//	private void validateUniqueClientGroupOnUpdate(Long clientGroupId,String name) {
//		
//		try {
//			List<Object[]> list = clientGroupRepository.checkForUniqueClientGroupOnUpdate(clientGroupId, name);
//			
//			if(!list.isEmpty()) {
//				throw new IllegalArgumentException("Client group with name : '"+name+"' is already exist in the system, Please enter unique group name.");
//			}
//		}
//		catch (RuntimeException e) {
//			throw new RuntimeException(e.getMessage());
//		}
//	}

    private BooleanExpression validateUniqueClientGroupByNameAndClientId(String name, Long clientGroupId,
                                                                         Integer mvnoId, Boolean isUpdate) {
        QClientGroup qClientGroup = QClientGroup.clientGroup;
        BooleanExpression boolExp = qClientGroup.isNotNull();
        if (isUpdate) {
            boolExp = boolExp.and(qClientGroup.clientGroupId.ne(clientGroupId));
        }
        if (mvnoId == 1) {
            boolExp = boolExp.and(qClientGroup.name.eq(name));
            List<ClientGroup> clientGroupList = (List<ClientGroup>) clientGroupRepository.findAll(boolExp);
            if (!clientGroupList.isEmpty()) {
                throw new IllegalArgumentException(
                        "Client group already exist with name '" + name + "'. Please enter unique name");
            }
        } else {
            boolExp = boolExp.and(qClientGroup.name.eq(name))
                    .and((qClientGroup.mvnoId.eq(mvnoId)).or(qClientGroup.mvnoId.eq(1)));
            Optional<ClientGroup> optionalClientGroup = clientGroupRepository.findOne(boolExp);
            if (optionalClientGroup.isPresent()) {
                throw new IllegalArgumentException(
                        "Client group already exist with name '" + name + "'. Please enter unique name");
            }
        }

        return boolExp;
    }

    @Override
    public List<ClientGroup> getRadiusGroups(Integer mvnoId) {
        try {
            QClientGroup qClientGroup = QClientGroup.clientGroup;
            BooleanExpression exp = qClientGroup.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                exp = exp.and(qClientGroup.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
            exp = exp.and(qClientGroup.cgStatus.eq(RadiusConstants.ACTIVE));
            return (List<ClientGroup>) clientGroupRepository.findAll(exp);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<ClientGroup> getCltGroupByIdsAndConcurrencyAndSessionLogout(List<Long> cltGrps, boolean checkConcurrency, boolean checkSession) {
        QClientGroup qClientGroup = QClientGroup.clientGroup;
        BooleanExpression exp = qClientGroup.isNotNull();
        exp = exp.and(qClientGroup.clientGroupId.in(cltGrps));
        exp = exp.and(qClientGroup.checkConcurrency.eq(true)).and(qClientGroup.logoutOldSessionOnNew.eq(true));
        return (List<ClientGroup>) clientGroupRepository.findAll(exp);
    }

    private CoaDMProfile validateCoaDMProfile(String name, Integer mvnoId) {
        try {
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(name))
                return coaDMProfileService.validateCoaDMProfileByName(name, ValidateCrudTransactionData.validateMvnoId(mvnoId));
            return null;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
