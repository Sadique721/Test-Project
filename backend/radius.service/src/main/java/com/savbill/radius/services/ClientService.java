package com.savbill.radius.services;

import com.savbill.radius.aaa.packet.RadiusPacket;
import com.savbill.radius.entity.Client;
import com.savbill.radius.entity.ClientServiceEntity;
import com.savbill.radius.helper.ClientDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ClientService 
{
	List<Client> findClientByIpAddress(String ipAddress, Integer mvnoId);
//	Client findClientById(Long id,Integer mvnoId);

	Client findClientById(Long id, Integer mvnoId, HttpServletRequest request);

	List<Client> findAllClients(Integer mvnoId);
	void deleteClientById(Long id, Integer mvnoId);
	Client saveClient(ClientDto client, Integer mvnoId);
	Client updateClient(Client client, Integer mvnoId,HttpServletRequest request);
	List<ClientServiceEntity> getClientSrvByName(String name, Integer mvnoId);
	ClientServiceEntity getByName(String name, Integer mvnoId);

	Client updateRadiusClientData(Client client, RadiusPacket request);

	List<ClientDto> findAllClientList(Integer mvnoId);

}
