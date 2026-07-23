package com.savbill.radius.services;

import java.util.List;

import com.savbill.radius.entity.ClientReply;
import com.querydsl.core.types.dsl.BooleanExpression;

public interface ClientReplyService {

    List<ClientReply> findAllClientReply(Integer mvnoId);
	ClientReply findClientReplyById(Long attributeId, Integer mvnoId);
	ClientReply addClientReply(ClientReply clientReply, Integer mvnoId);
	ClientReply updateClientReply(ClientReply clientReply, Integer mvnoId);
	void deleteClientReply(Long attributeId, Integer mvnoId);
	BooleanExpression findClientRepliesByClientGroupId(Long clientGroupId, Integer MvnoId);
	List<ClientReply> findClientReplyByClientGroupId(Long clientGroupId, Integer mvnoId);
	void deleteByClientGroupId(Long clientGroupId, Integer mvnoId);

}
