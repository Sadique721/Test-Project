package com.savbill.radius.services.impl;

import com.savbill.radius.aaa.packet.RadiusPacket;
import com.savbill.radius.aaa.util.ValidateExpression;
import com.savbill.radius.entity.*;
import com.savbill.radius.entity.*;
import com.savbill.radius.helper.ClientDto;
import com.savbill.radius.repository.ClientRepository;
import com.savbill.radius.repository.ClientServiceRepository;
import com.savbill.radius.repository.SNMPClientProfileRepository;
import com.savbill.radius.services.ClientGroupService;
import com.savbill.radius.services.ClientService;
import com.savbill.radius.utils.LogConstants;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.UpdateDiffFinder;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private ClientGroupService clientGroupService;
	@Autowired
	private UpdateDiffFinder updateDiffFinder;

	@Autowired
	private ClientServiceRepository entityRepository;
	@Autowired
	private SNMPClientProfileRepository snmpClientProfileRepository;

	private static final Logger log = LoggerFactory.getLogger(ClientServiceImpl.class);

	@Override
	public List<Client> findClientByIpAddress(String ipAddress, Integer mvnoId) {

		try {

			QClient qClient = QClient.client;
			BooleanExpression boolExp = qClient.isNotNull();
			if(mvnoId == null || mvnoId != 1)
				boolExp = boolExp.and(qClient.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
			if (StringUtils.isBlank(ipAddress) || ipAddress.equalsIgnoreCase("null")) {
				return (List<Client>) clientRepository.findAll(boolExp);
			} else {
				boolExp = boolExp.and(qClient.clientIpAddress.containsIgnoreCase(ipAddress));
				List<Client> clientList =  (List<Client>) clientRepository.findAll(boolExp);

//				if(clientList.isEmpty())
//				{
//					throw new IllegalArgumentException(
//							"No record found by with client Ip adddress: "+ipAddress+" Please enter valid ipaddress");
//				}
				return clientList;
			}

		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public Client updateRadiusClientData(Client client, RadiusPacket request) {

		try {
			if (request != null && (client.getClientGroupData() == null)) {
				// for multiple client group scenario we need to clone client group
				client = (Client) client.clone();
				ValidateExpression validate = new ValidateExpression();
				List<ClientGroupMapping> clientGroupMappings = client.getClientGroupMappings();
				for (ClientGroupMapping clientGroupMapping : clientGroupMappings) {
					String checkItem = clientGroupMapping.getCheckItem();
					boolean evaluate = validate.checkExpression(checkItem, request, null);
					if (evaluate) {
						if(clientGroupMapping.getClientGroupData() != null) {
							client.setClientGroupData(clientGroupMapping.getClientGroupData());
						} else {
							ClientGroup clientGroup = clientGroupService.findClientGroupById(clientGroupMapping.getClientGroupId(), client.getMvnoId());
							client.setClientGroupData(clientGroup);
						}
						log.debug("Selected client group for user: " + request.getAttribute("User-Name") != null ? request.getAttributeValue("User-Name") : " " + "is " + clientGroupMapping.getClientGroupData().getName());
						break;
					}
				}

			}
		} catch (Exception ex) {
			log.error("Exception to update Client: "+ex.getMessage());
		}
	return client;
	}

		@Override
		public Client findClientById(Long id, Integer mvnoId, HttpServletRequest request) {

			try {
				if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id))
					throw new IllegalArgumentException(RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid client id.");
				QClient qClient = QClient.client;
				BooleanExpression boolExp = qClient.isNotNull();
				if(mvnoId == null || mvnoId != 1)
					boolExp = boolExp.and(qClient.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
				boolExp = boolExp.and(qClient.clientId.eq(id));

				Optional<Client> client = clientRepository.findOne(boolExp);
				if (!client.isPresent()) {
					throw new IllegalArgumentException(
							"No record found with client id " + id + " . Please enter valid client id.");
				}

				return client.get();

			} catch (RuntimeException e) {
				throw new RuntimeException(e.getMessage());
			}
		}

		@Override
		public List<Client> findAllClients(Integer mvnoId) {

			try {
				QClient qClient = QClient.client;
				BooleanExpression exp = qClient.isNotNull();
				if(mvnoId != null && mvnoId == 1)
					return clientRepository.findAll();
				else {
					exp = exp.and(qClient.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
					return (List<Client>) clientRepository.findAll(exp);
				}
			} catch (RuntimeException e) {
				throw new RuntimeException(e.getMessage());
			}
		}

		@Override
		public void deleteClientById(Long id, Integer mvnoId) {
			MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
			try {
				Client client = getClientById(id, mvnoId);
				clientRepository.deleteById(id);
				//	log.info("Client deleted successfully: "+client.getClientIpAddress());
			} catch (RuntimeException e) {
				//	log.error("Error while deleting client: " + e.getMessage());
				throw new RuntimeException(e.getMessage());
			} finally {
				MDC.remove(RadiusConstants.TYPE);
			}
		}

		@Override
		public Client saveClient(ClientDto clientDto, Integer mvnoId) {
			try {
				Client clientVo = new Client(clientDto);
				clientVo.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
				validateClientDetail(clientVo, false);
				clientVo.setCreatedOn(new Timestamp(new Date().getTime()));
				clientVo.setLastModifiedOn(new Timestamp(new Date().getTime()));
				if(clientVo.isSnmpEnable() && clientVo.getSnmpClientProfile() != null) {
					SNMPClientProfile snmpClientProfile = saveSnmpProfile(clientVo);
					clientVo.setSnmpClientProfile(snmpClientProfile);
				}
				return clientRepository.save(clientVo);
			} catch (RuntimeException e) {
				throw new RuntimeException(e.getMessage());
			}
		}

		private void validateClientDetail(Client client, boolean isUpdate) {
			try {
//			validateClientGroupId(client);
				if (client.getClientIpAddress() == null || client.getClientIpAddress().isEmpty()
						|| client.getClientIpAddress().equalsIgnoreCase(RadiusConstants.BLANK_STRING)) {
					throw new IllegalArgumentException("Please enter valid client ip address.");
				} else if (client.getIpType() == null || client.getIpType().isEmpty()
						|| client.getIpType().equalsIgnoreCase(RadiusConstants.BLANK_STRING)) {
					throw new IllegalArgumentException("Please enter valid client ip type.");
				} else if (client.getSharedKey() == null || client.getSharedKey().isEmpty()
						|| client.getSharedKey().equalsIgnoreCase(RadiusConstants.BLANK_STRING)) {
					throw new IllegalArgumentException("Please enter valid shared key.");
				} else if (client.getTimeOut() == null || client.getTimeOut().isEmpty()
						|| client.getTimeOut().equalsIgnoreCase(RadiusConstants.BLANK_STRING)) {
					throw new IllegalArgumentException("Please enter valid timeout.");
				} else if (isUpdate) {
					validateClientId(client);
				} else if (client.getClientIpAddress() != null && !isUpdate) {
					validateUniqueClientIp(client.getClientIpAddress(),null, client.getMvnoId(), false);
				}
			} catch (RuntimeException e) {
				throw new RuntimeException(e.getMessage());
			}
		}

		private void validateClientGroupId(Client client) {
			try {
				if (client.getClientGroupId() == null || client.getClientGroupId() == 0) {
					throw new IllegalArgumentException("Please enter valid client group id.");
				} else if (client.getClientGroupId() != null && client.getClientGroupId() != 0) {
					clientGroupService.validateGroupById(client.getClientGroupId(), client.getMvnoId());
				}
			} catch (RuntimeException e) {
				throw new RuntimeException(e.getMessage());
			}
		}

		public Client getClientById(Long id, Integer mvnoId) {

			try {
				if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id))
					throw new IllegalArgumentException(RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid client id.");
				QClient qClient = QClient.client;
				BooleanExpression boolExp = qClient.isNotNull();
				if(mvnoId == null || mvnoId != 1)
					boolExp = boolExp.and(qClient.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
				boolExp = boolExp.and(qClient.clientId.eq(id));

				Optional<Client> client = clientRepository.findOne(boolExp);
				if (!client.isPresent()) {
					throw new IllegalArgumentException(
							"You do not have access to update/delete this record or no record found with client id " + id + " . Please enter valid client id.");
				}

				return client.get();

			} catch (RuntimeException e) {
				throw new RuntimeException(e.getMessage());
			}
		}

		private void validateClientId(Client client) {

			try {

				if (client.getClientId() == null || client.getClientId() == 0) {
					throw new IllegalArgumentException("Please enter valid client id.");
				} else {
					Client clientVo = getClientById(client.getClientId(), client.getMvnoId());
					client.setCreatedOn(clientVo.getCreatedOn());
					validateUniqueClientIp(client.getClientIpAddress(),client.getClientId(), client.getMvnoId(), true);
				}
			} catch (RuntimeException e) {
				throw new RuntimeException(e.getMessage());
			}
		}

		@Override
		public Client updateClient(Client client, Integer mvnoId, HttpServletRequest request) {
			MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
			try {
				Client oldClient = findClientById(client.getClientId(), mvnoId,request);
				client.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
				validateClientDetail(client, true);
				String updated = updateDiffFinder.getUpdatedDiff(oldClient, client);
				client.setLastModifiedOn(new Timestamp(new Date().getTime()));
				client.setDeviceId(oldClient.getDeviceId());
				Long oldSNMPClientProfile = null;
				log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Client has been updated successfully. updated data,"+updated  + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
				//	log.info("Client updated succefully: "+client.getClientIpAddress()+" ,updated values "+ updated);
				if(client.isSnmpEnable() && client.getSnmpClientProfile() != null) {
					SNMPClientProfile snmpClientProfile = saveSnmpProfile(client);
					client.setSnmpClientProfile(snmpClientProfile);
				} else if(!client.isSnmpEnable() && oldClient.getSnmpClientProfile() != null) {
					client.setSnmpClientProfile(null);
					oldSNMPClientProfile = oldClient.getSnmpClientProfile().getSnmpClientId();
				} else if(!client.isSnmpEnable()) {
					client.setSnmpClientProfile(null);
				}
				client = clientRepository.save(client);
				if(oldSNMPClientProfile != null) {
					snmpClientProfileRepository.deleteById(oldSNMPClientProfile);
				}
				return client;
			} catch (Throwable e) {
				//		log.error("Error while updating client: " + e.getMessage());
				throw new RuntimeException(e.getMessage());
			} finally {
				MDC.remove(RadiusConstants.TYPE);
			}
		}

		private SNMPClientProfile saveSnmpProfile(Client client) {
			SNMPClientProfile snmpClientProfile = client.getSnmpClientProfile();
			snmpClientProfile = snmpClientProfileRepository.save(snmpClientProfile);
			return snmpClientProfile;
		}

		private void validateUniqueClientIp(String clientIp,Long clientId, Integer mvnoId, Boolean isUpdate) {

			try {

				BooleanExpression boolExp =   this.checkForUniqueClientIp(clientIp, clientId, mvnoId, isUpdate);
				List<Client> clientList =  (List<Client>) clientRepository.findAll(boolExp);
				//List<Object[]> list = clientRepository.checkForUniqueClientIp(clientIp);

				if(clientList.size() > 0) {
					throw new IllegalArgumentException("Client IP Address : '"+clientIp+"' is already exist in the system, Please enter unique ip address.");
				}
			} catch (RuntimeException e) {
				throw new RuntimeException(e.getMessage());
			}
		}

//	private void validateUniqueClientIpOnUpdate(Long clientId,String clientIp) {
//		
//		try {
//			
//			BooleanExpression boolExp =   this.checkForUniqueClientIp(clientIp,clientId);
//			List<Client> clientList =  (List<Client>) clientRepository.findAll(boolExp);
//			//List<Object[]> list = clientRepository.checkForUniqueClientIpOnUpdate(clientId, clientIp);
//			
//			if(clientList.size() > 0) {
//				throw new IllegalArgumentException("Client IP Address : '"+clientIp+"' is already exist in the system, Please enter unique ip address.");
//			}
//		}
//		catch (RuntimeException e) {
//			throw new RuntimeException(e.getMessage());
//		}
//	}

		private BooleanExpression checkForUniqueClientIp(String clientIp,Long clientId, Integer mvnoId, Boolean isUpdate) {

			String errMessage = "Client already exist with ip '"+clientIp+"'. Please enter unique ip";
			QClient qClient = QClient.client;
			BooleanExpression boolExp = qClient.isNotNull();

			if (isUpdate) {
				boolExp = boolExp.and(qClient.clientId.ne(clientId));
			}
			if(mvnoId == 1) {
				boolExp = boolExp.and(qClient.clientIpAddress.eq(clientIp));
				List<Client> clientList = (List<Client>) clientRepository.findAll(boolExp);
				if(!clientList.isEmpty()) {
					throw new IllegalArgumentException(errMessage);
				}
			} else {
				boolExp = boolExp.and(qClient.clientIpAddress.eq(clientIp)).and((qClient.mvnoId.eq(mvnoId)).or(qClient.mvnoId.eq(1)));
				Optional<Client> optionalClient = clientRepository.findOne(boolExp);
				if(optionalClient.isPresent()) {
					throw new IllegalArgumentException(errMessage);
				}
			}


			return boolExp;
		}

		public List<ClientServiceEntity> getClientSrvByName(String name, Integer mvnoId) {
			List<Integer> mvnoIds = new ArrayList<>();
			mvnoIds.add(1);
			mvnoIds.add((mvnoId));
			return entityRepository.findAll().stream()
					.filter(data -> data.getName().equalsIgnoreCase(name))
					.filter(data -> data.getMvnoId() == 1 ||
							data.getMvnoId().equals(mvnoIds.get(1)) ||
							mvnoIds.get(0) == 1)
					.collect(Collectors.toList());
		}

		public ClientServiceEntity getByName(String name, Integer mvnoId) {

			return entityRepository.getByNameAndMvnoId(name,mvnoId );
		}

	@Override
	public List<ClientDto> findAllClientList(Integer mvnoId) {

		try {
			QClient qClient = QClient.client;
			BooleanExpression exp = qClient.isNotNull();
			if(mvnoId != null && mvnoId == 1)
				return clientRepository.findAllClientList();
			else {
//				exp = exp.and(qClient.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
				List<Integer> mvnoIdList = new ArrayList<>();
				mvnoIdList.add(1);
				mvnoIdList.add(mvnoId);
				return clientRepository.findAllClientListByMvnoId(mvnoIdList);
			}
		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

}
