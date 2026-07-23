package com.savbill.salescrmsbss.service;

import java.util.List;

import com.savbill.salescrmsbss.entity.ClientService;
import com.savbill.salescrmsbss.rabbitMq.message.SaveClientServMessge;
import com.savbill.salescrmsbss.rabbitMq.message.UpdateClientServMessage;

public interface ClientServiceSrv {



	List<ClientService> getAllEntity();

	List<ClientService> saveAllEntity(List<ClientService> list);
	
	ClientService save(ClientService clientService);
	
	ClientService update(ClientService clientService);




	ClientService getByNameAndMvnoId(String name, Long mvnoId);

	List<ClientService> getClientSrvByName(String name);

	ClientService saveSharedClientService(SaveClientServMessge messge);

	ClientService updateSharedClientService(UpdateClientServMessage message);
}
