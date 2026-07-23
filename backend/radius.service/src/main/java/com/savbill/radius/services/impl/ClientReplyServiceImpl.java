package com.savbill.radius.services.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.radius.entity.ClientReply;
import com.savbill.radius.entity.QClientReply;
import com.savbill.radius.entity.QDBMapping;
import com.savbill.radius.repository.ClientReplyRepository;
import com.savbill.radius.services.ClientGroupService;
import com.savbill.radius.services.ClientReplyService;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service
public class ClientReplyServiceImpl implements ClientReplyService 
{
	@Autowired
	private ClientReplyRepository clientReplyRepository;

	@Autowired
	private ClientGroupService clientGroupService;
	
	private static final Logger log = LoggerFactory.getLogger(ClientReplyServiceImpl.class);
	
	@Override
	public ClientReply findClientReplyById(Long id, Integer mvnoId) {

		try {

			if(id == null || id == 0) {
				throw new IllegalArgumentException("Please enter valid attribute id.");
			}
			QClientReply qClientReply = QClientReply.clientReply;
			BooleanExpression boolExp = qClientReply.isNotNull();
			if(mvnoId == null || mvnoId != 1)
				boolExp = boolExp.and(qClientReply.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
			boolExp = boolExp.and(qClientReply.attributeId.eq(id));

			Optional<ClientReply> clientReply = clientReplyRepository.findOne(boolExp);

			if(!clientReply.isPresent()) {
				throw new IllegalArgumentException("No record found with attribute id : '"+id+"' ,Please enter valid attribute id.");
			}

			return clientReply.get();
		}
		catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private ClientReply validateClientReplyForDeleteOrUpdate(Long id, Integer mvnoId) {

		try {

			if(id == null || id == 0) {
				throw new IllegalArgumentException("Please enter valid attribute id.");
			}
			QClientReply qClientReply = QClientReply.clientReply;
			BooleanExpression boolExp = qClientReply.isNotNull();
			if(mvnoId == null || mvnoId != 1)
				boolExp = boolExp.and(qClientReply.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
			boolExp = boolExp.and(qClientReply.attributeId.eq(id));

			Optional<ClientReply> clientReply = clientReplyRepository.findOne(boolExp);

			if(!clientReply.isPresent()) {
				throw new IllegalArgumentException("You do not have access to update or delete this record.");
			}

			return clientReply.get();
		}
		catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public List<ClientReply> findClientReplyByClientGroupId(Long id, Integer mvnoId) {
		
		try {
			
			if(id == null || id == 0) {
				throw new IllegalArgumentException("Please enter valid client id.");
			}
			QClientReply qClientReply = QClientReply.clientReply;
			BooleanExpression exp = qClientReply.isNotNull();
			exp = exp.and(qClientReply.clientGroupId.eq(id));
			if(mvnoId == null || mvnoId != 1)
				exp = exp.and(qClientReply.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
			return (List<ClientReply>) clientReplyRepository.findAll(exp);
			//return custReplyRepository.findClientReplyByClientId(id);
		}
		catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public List<ClientReply> findAllClientReply(Integer mvnoId)  {
		
		try {
			QClientReply qClientReply = QClientReply.clientReply;
			BooleanExpression exp = qClientReply.isNotNull();
			if(mvnoId != null && mvnoId == 1)
				return clientReplyRepository.findAll();
			else {
				exp = exp.and(qClientReply.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
				return (List<ClientReply>) clientReplyRepository.findAll(exp);
			}
		}
		catch (RuntimeException e)  {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public ClientReply addClientReply(ClientReply clientReply, Integer mvnoId) {
		
		try {
			
			clientReply.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
			validatClientReply(clientReply,false);
			clientReply.setCreatedOn(new Timestamp(new Date().getTime()));
			clientReply.setLastModifiedOn(new Timestamp(new Date().getTime())); 
			return clientReplyRepository.save(clientReply);
		}
		catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private void validatClientReply(ClientReply clientReplyVo,boolean isUpdate) {
		
		try {
			
			if(isUpdate) {
				
				if(clientReplyVo.getAttributeId() == null || clientReplyVo.getAttributeId() == 0) {
					throw new IllegalArgumentException("Please enter valid attribute id to update client reply.");
				}
				ClientReply clientReply = validateClientReplyForDeleteOrUpdate(clientReplyVo.getAttributeId(), clientReplyVo.getMvnoId());
				clientReplyVo.setCreatedOn(clientReply.getCreatedOn());
				if(clientReplyVo.getMvnoId() != null && clientReplyVo.getMvnoId() == 1)
					clientReplyVo.setMvnoId(clientReply.getMvnoId());
			}
			
			Long clientGroupId = Optional.ofNullable(clientReplyVo.getClientGroupId()).filter(custid -> (clientReplyVo.getClientGroupId() !=0 && clientReplyVo.getClientGroupId() != null)).orElseThrow(()-> new RuntimeException("Please enter valid client group id."));
			String custAttribute = Optional.ofNullable(clientReplyVo.getAttribute()).filter(attr -> (clientReplyVo.getAttribute() != null && !clientReplyVo.getAttribute().isEmpty() && !clientReplyVo.getAttribute().equalsIgnoreCase(RadiusConstants.BLANK_STRING))).orElseThrow(()-> new RuntimeException("Please enter valid client reply attribute."));
			String custAttributeValue = Optional.ofNullable(clientReplyVo.getAttributeValue()).filter(attrValue -> (clientReplyVo.getAttributeValue() != null && !clientReplyVo.getAttributeValue().isEmpty() && !clientReplyVo.getAttributeValue().equalsIgnoreCase(RadiusConstants.BLANK_STRING))).orElseThrow(()-> new RuntimeException("Please enter valid client reply attribute value."));
			
			clientGroupService.findClientGroupById(clientReplyVo.getClientGroupId(), clientReplyVo.getMvnoId());
		}
		catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public ClientReply updateClientReply(ClientReply clientReply, Integer mvnoId) {
		
		try {
			clientReply.setMvnoId(ValidateCrudTransactionData.validateMvnoId(mvnoId));
			validatClientReply(clientReply,true);
			clientReply.setLastModifiedOn(new Timestamp(new Date().getTime()));
			MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
		//	log.info("Client reply updated succefully, updated values "+clientReply.getClientGroupId());
			return clientReplyRepository.save(clientReply);
			
		}
		catch (Throwable e) {
			//log.error("Error while updating client reply: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			MDC.remove(RadiusConstants.TYPE);
		}
	}

	@Override
	public void deleteClientReply(Long id, Integer mvnoId) {
		
		try {
			
			if(id == null || id == 0) {
				throw new IllegalArgumentException("Please enter valid attribute id to delete client reply.");
			}
			validateClientReplyForDeleteOrUpdate(id, mvnoId);
			clientReplyRepository.deleteById(id);
			MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
		//	log.info("Client reply deleted succefully: "+id);
		}
		catch (RuntimeException e) {
		//	log.error("Error while deleting client reply: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			MDC.remove(RadiusConstants.TYPE);
		}
	}

	@Override
    public void deleteByClientGroupId(Long clientGroupId, Integer mvnoId) {
	try {
	    QClientReply qClientReply = QClientReply.clientReply;
	    BooleanExpression boolExp = qClientReply.isNotNull();
	    if (mvnoId == null || mvnoId != 1)
		boolExp = boolExp.and(qClientReply.mvnoId.eq(ValidateCrudTransactionData.validateMvnoId(mvnoId)));
	    boolExp = boolExp.and(qClientReply.clientGroupId.eq(clientGroupId));
	    List<ClientReply> clientReplyList = (List<ClientReply>) clientReplyRepository.findAll(boolExp);

	    if (clientReplyList.size() > 0) {
	    	clientReplyRepository.deleteAll(clientReplyList);
	    }

	} catch (Exception e) {
	//    log.error("Error while deleting Client Reply: " + e.getMessage());
	    throw new RuntimeException(e.getMessage());
	}
    }
	
	public  BooleanExpression findClientRepliesByClientGroupId(Long clientGroupId, Integer mvnoId) {
		
		QClientReply qClientReply = QClientReply.clientReply;
		BooleanExpression boolExp =  qClientReply.isNotNull();
		
		if (clientGroupId != null) {
			boolExp = boolExp.and(qClientReply.clientGroupId.eq(clientGroupId));
		}
		
		return boolExp;
	}

}
