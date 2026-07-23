package com.savbill.salescrmsbss.service.Impl;

import java.util.List;
import java.util.stream.Collectors;

import com.savbill.salescrmsbss.exceptions.CustomValidationException;
import com.savbill.salescrmsbss.rabbitMq.message.SaveClientServMessge;
import com.savbill.salescrmsbss.rabbitMq.message.UpdateClientServMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.salescrmsbss.entity.ClientService;
import com.savbill.salescrmsbss.repository.ClientServiceRepository;
import com.savbill.salescrmsbss.service.ClientServiceSrv;

@Service
public class ClientServiceSrvImpl implements ClientServiceSrv {

	@Autowired
	private ClientServiceRepository clientServiceRepository;

//	@Override
//	public ClientService searchByName(String name) {
//		return this.clientServiceRepository.findByName(name);
//	}

	@Override
	public List<ClientService> getAllEntity() {
		return this.clientServiceRepository.findAll();
	}

	@Override
	public List<ClientService> saveAllEntity(List<ClientService> list) {
		return this.clientServiceRepository.saveAll(list);
	}
	private static Log log = LogFactory.getLog(ClientServiceSrv.class);
	@Override
	public ClientService save(ClientService clientService) {
		return this.clientServiceRepository.save(clientService);
	}

//	@Override
//	public ClientService getByName(String name) {
//		return this.clientServiceRepository.findByName(name);
//	}


	@Override
	public ClientService getByNameAndMvnoId(String name, Long mvnoId) {
		return this.clientServiceRepository.findByNameAndAndMvnoId(name, mvnoId);
	}
	@Override
	 public List<ClientService> getClientSrvByName(String name) {
	        return this.clientServiceRepository.findAll().stream().filter(data -> data.getName().
	                equalsIgnoreCase(name)).collect(Collectors.toList());
	    }

	@Override
	public ClientService update(ClientService clientService) {
		ClientService existingClientService = this.clientServiceRepository.getByNameAndMvnoId(clientService.getName() , clientService.getMvnoId());
		if(existingClientService != null) {
			existingClientService.setName(clientService.getName());
			existingClientService.setValue(clientService.getValue());
			existingClientService.setMvnoId(clientService.getMvnoId());
			return this.clientServiceRepository.save(existingClientService);
		}
		return null;
	}

	// Shared Data From Common APIGW to CMS
	@Override
	public ClientService saveSharedClientService(SaveClientServMessge message) {
		ClientService clientService = new ClientService();
		try {
			clientService.setId(message.getId());
			clientService.setName(message.getName());
			clientService.setValue(message.getValue());
			clientService.setMvnoId(Long.valueOf(message.getMvnoId()));
			clientService = clientServiceRepository.save(clientService);
			log.info("Client Service created successfully with name " + message.getName());
		} catch (CustomValidationException e) {
			log.error("Unable to create client service with name " + message.getName());
		}
		return clientService;
	}

	@Override
	public ClientService updateSharedClientService(UpdateClientServMessage message) {
		ClientService clientService = new ClientService();
		try {
			clientService = clientServiceRepository.getByNameAndMvnoId(message.getName(),Long.valueOf(message.getMvnoId()));
			if (clientService != null) {
				//clientService.setId(clientServiceRepository);
				clientService.setName(message.getName());
				clientService.setValue(message.getValue());
				clientService.setMvnoId(Long.valueOf(message.getMvnoId()));
				clientService = clientServiceRepository.save(clientService);
				log.info("Client service updated successfully with name " + message.getName());
			} else {
				clientService = saveUpdateClientService(message);
			}
		} catch (CustomValidationException e) {
			log.error("Unable to update client service with name " + message.getName());
		}
		return clientService;
	}

	public ClientService saveUpdateClientService(UpdateClientServMessage message) {
		ClientService clientService1 = new ClientService();
		clientService1.setId(clientServiceRepository.findlast()+1);
		clientService1.setName(message.getName());
		clientService1.setValue(message.getValue());
		clientService1.setMvnoId(Long.valueOf(message.getMvnoId()));
		clientServiceRepository.save(clientService1);
		log.info("Client service updated successfully with name " + message.getName());
		return clientService1;
	}

}
