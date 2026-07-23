package com.savbill.radius.services.impl;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import com.savbill.radius.entity.*;
import com.savbill.radius.utils.*;
import com.savbill.radius.entity.ProxyServer;
import com.savbill.radius.entity.RadiusProfile;
import com.savbill.radius.utils.LogConstants;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.UpdateDiffFinder;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.savbill.radius.helper.ProxyServerDto;
import com.savbill.radius.repository.ProxyServerRepository;
import com.savbill.radius.repository.RadiusProfileRepository;
import com.savbill.radius.services.ProxyServerService;
import com.savbill.radius.services.RadiusProfileService;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;

@Service
public class ProxyServerServiceImpl implements ProxyServerService {



	@Autowired
	private ProxyServerRepository proxyServerRepo;
	
	@Autowired
	private RadiusProfileRepository radiusProfileRepository;

	@Autowired
	private RadiusProfileService radiusProfileService;
	@Autowired
	private UpdateDiffFinder updateDiffFinder;

	private static final Logger log = LoggerFactory.getLogger(ProxyServerServiceImpl.class);
    @Override
    public List<ProxyServer> getByName(String name, Integer mvnoId) {
        
        if(StringUtils.isBlank(name) || name.equalsIgnoreCase("null"))
        {
            name = "";
        }

		QProxyServer qProxyServer = QProxyServer.proxyServer;
		BooleanExpression boolExp;
		boolExp = qProxyServer.name.containsIgnoreCase(name);
//		boolExp = qProxyServer.name.like("%"+ name +"%");
		if(mvnoId == null || mvnoId != 1)
			boolExp = boolExp.and(qProxyServer.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
		List<ProxyServer> list = (List<ProxyServer>) proxyServerRepo.findAll(boolExp);
		return list;
    }

	@Override
	public ProxyServer getById(Long id, Integer mvnoId) {
		if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id))
			throw new IllegalArgumentException("Please enter valid proxy server id.");
		QProxyServer qProxyServer = QProxyServer.proxyServer;
		BooleanExpression boolExp = qProxyServer.isNotNull();
		if(mvnoId == null || mvnoId != 1)
			boolExp = boolExp.and(qProxyServer.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
		boolExp = boolExp.and(qProxyServer.id.eq(id));

		Optional<ProxyServer> proxyServer = proxyServerRepo.findOne(boolExp);
		if (!proxyServer.isPresent()) {
			throw new IllegalArgumentException(
					"No record found with proxy server id " + id + " . Please enter valid proxy server id.");
		}
		return proxyServer.get();
	}

	public ProxyServer validateProxyServerForUpdateAndDelete(Long id, Integer mvnoId) {
		if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id))
			throw new IllegalArgumentException("Please enter valid proxy server id.");
		QProxyServer qProxyServer = QProxyServer.proxyServer;
		BooleanExpression boolExp = qProxyServer.isNotNull();
		if(mvnoId == null || mvnoId != 1)
			boolExp = boolExp.and(qProxyServer.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
		boolExp = boolExp.and(qProxyServer.id.eq(id));

		Optional<ProxyServer> proxyServer = proxyServerRepo.findOne(boolExp);
		if (!proxyServer.isPresent()) {
			throw new IllegalArgumentException(
					"No record found with proxy server id " + id + ". OR You might not have access to update/delete this record.");
		}
		return proxyServer.get();
	}

	private void checkDuplicateUser(String userName, Integer mvnoId, Long proxyServerId) {

		try {
			String errMessage = "Proxy Server already exist with name '"+userName+"'. Please enter unique name";
			QProxyServer qProxyServer = QProxyServer.proxyServer;
			BooleanExpression boolExp = qProxyServer.isNotNull();
			boolExp = boolExp.and(qProxyServer.id.ne(proxyServerId));

			if(mvnoId == 1)
			{
				boolExp = boolExp.and(qProxyServer.name.eq(userName));
				List<ProxyServer> proxyServerList = (List<ProxyServer>) proxyServerRepo.findAll(boolExp);
				if(!proxyServerList.isEmpty())
				{
					throw new IllegalArgumentException(errMessage);
				}
			}
			else
			{
				boolExp = boolExp.and(qProxyServer.name.eq(userName)).and((qProxyServer.mvnoId.eq(mvnoId)).or(qProxyServer.mvnoId.eq(1)));
				Optional<ProxyServer> optionalProxyServer = proxyServerRepo.findOne(boolExp);
				if(optionalProxyServer.isPresent())
				{
					throw new IllegalArgumentException(errMessage);
				}
			}
		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}


	@Override
	public List<ProxyServer> getAll(Integer mvnoId) {
		QProxyServer qProxyServer = QProxyServer.proxyServer;
		BooleanExpression boolExp = qProxyServer.isNotNull();
		if(mvnoId != null && mvnoId == 1)
			return proxyServerRepo.findAll();
		else {
			boolExp = boolExp.and(qProxyServer.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
			return (List<ProxyServer>) proxyServerRepo.findAll(boolExp);
		}
	}

	@Override
	public void delete(Long id, Integer mvnoId) {
		List<RadiusProfile> radiusProfileList = radiusProfileService.findByProxyServerId(id, mvnoId);
		if(!radiusProfileList.isEmpty())
		{
			throw new IllegalArgumentException("This operation will not allow as this Proxy Server is used for Radius Profile.");
		}
		Optional.ofNullable(id).map(longId -> {
			ProxyServer proxyServer = validateProxyServerForUpdateAndDelete(id, ValidateCrudTransactionData.validateMvnoId(mvnoId));
			proxyServerRepo.deleteById(id);
			MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
			//log.info("Proxy Server deleted successfully: "+proxyServer.getName());
			return true;
		}).orElseThrow(() -> new IllegalArgumentException("Proxy Server not found for Id " +id));

	}

	@Override
	public ProxyServer save(ProxyServerDto proxyServerDto, Integer mvnoId)
	{
		validateProxyServerData(proxyServerDto);

		QProxyServer proxyServer = QProxyServer.proxyServer;
		BooleanExpression boolExp = proxyServer.name.eq(proxyServerDto.getName());
		if(mvnoId == null || mvnoId != 1)
			boolExp = boolExp.and(proxyServer.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId),1));

		List<ProxyServer> proxyServers = IterableUtils.toList(proxyServerRepo.findAll(boolExp));
		if (!CollectionUtils.isEmpty(proxyServers)) {
			throw new RuntimeException("Server exist with same name '"+proxyServerDto.getName()+"'. Please enter unique name");
		}

		ProxyServer server = new ProxyServer(proxyServerDto);
		server.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
		server.setCreatedon(ZonedDateTime.now());
		return proxyServerRepo.save(server);
	}

	@Override
	public ProxyServer update(Long id, ProxyServerDto proxyServerDto, Integer mvnoId, HttpServletRequest request)
	{
		validateProxyServerData(proxyServerDto);
		/*return proxyServerRepo.findById(id).map(proxyServer -> {
			ProxyServer server = new ProxyServer(proxyServerDto);
			server.setId(proxyServer.getId());
			server.setLastmodifiedon(ZonedDateTime.now());
			return proxyServerRepo.save(server);
		}).orElseThrow(() -> new RuntimeException("Server not found "));*/
		validateProxyServerForUpdateAndDelete(id, ValidateCrudTransactionData.validateMvnoId(mvnoId));
		ProxyServer oldProxyServer = getById(id, ValidateCrudTransactionData.validateMvnoId(mvnoId));
		checkDuplicateUser(proxyServerDto.getName(), mvnoId, oldProxyServer.getId());
		ProxyServer proxyServer = new ProxyServer(proxyServerDto);
		proxyServer.setId(id);
		if(mvnoId != null && mvnoId == 1)
			proxyServer.setMvnoId(oldProxyServer.getMvnoId());
		else
			proxyServer.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
		proxyServer.setLastmodifiedon(ZonedDateTime.now());
		String updates = updateDiffFinder.getUpdatedDiff(oldProxyServer, proxyServer);
	    MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
		log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Proxy server has been updated successfully updated values"+updates + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
		return proxyServerRepo.save(proxyServer);
	}

	private void validateProxyServerData(ProxyServerDto proxyServerDto)
	{
		try
		{
			if(!ValidateCrudTransactionData.validateStringTypeFieldValue(proxyServerDto.getAcctport()))
			{
				throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG+"Please enter valid acct port");
			}
			else if(!ValidateCrudTransactionData.validateStringTypeFieldValue(proxyServerDto.getAuthport()))
			{
				throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG+"Please enter valid auth port");
			}
			else if(!ValidateCrudTransactionData.validateStringTypeFieldValue(proxyServerDto.getIp()))
			{
				throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG+"Please enter valid IP address");
			}
			else if(!ValidateCrudTransactionData.validateStringTypeFieldValue(proxyServerDto.getName()))
			{
				throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG+"Please enter valid proxy server name");
			}
			else if(!ValidateCrudTransactionData.validateStringTypeFieldValue(proxyServerDto.getSecretkey()))
			{
				throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG+"Please enter valid secret key");
			}
			else if (!proxyServerDto.getStatus().equalsIgnoreCase(RadiusConstants.ACTIVE) && !proxyServerDto.getStatus().equalsIgnoreCase(RadiusConstants.IN_ACTIVE))
			{
				throw new IllegalArgumentException("Please enter valid proxy server status. It should be '"+ RadiusConstants.ACTIVE + "' or '" + RadiusConstants.IN_ACTIVE + "'");
			}
		}
		catch(Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}
	
	@Override
	public void updateStatus(Long id, String status, Integer mvnoId,HttpServletRequest request)
	{
		if (!ValidateCrudTransactionData.validateStringTypeFieldValue(status)) 
		{
			throw new IllegalArgumentException(RadiusConstants.BASIC_STRING_MSG+ "Proxy server status is mandatory. Please enter valid status.");
		}
		else if (!status.equals(RadiusConstants.ACTIVE) && !status.equals(RadiusConstants.IN_ACTIVE)) 
		{
			throw new IllegalArgumentException("Please enter valid proxy server status. It should be '"+ RadiusConstants.ACTIVE + "' or '" + RadiusConstants.IN_ACTIVE + "'");
		}
		ProxyServer proxyServer = validateProxyServerForUpdateAndDelete(id, ValidateCrudTransactionData.validateMvnoId(mvnoId));
		log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Proxy server has been updated successfully from,"+proxyServer.getStatus()+" updated to "+status + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
		proxyServer.setStatus(status);
		proxyServer.setLastmodifiedon(ZonedDateTime.now());
		proxyServerRepo.save(proxyServer);
	}

}
