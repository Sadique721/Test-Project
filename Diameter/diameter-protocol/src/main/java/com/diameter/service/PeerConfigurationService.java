package com.diameter.service;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.ValidationException;

import com.diameter.commons.PeerData;
import com.diameter.model.PeerConfiguration;
import com.diameter.model.PeerConfiguration.Status;

public interface PeerConfigurationService {
	PeerConfiguration create(PeerConfiguration dto) throws ValidationException;
    Optional<PeerConfiguration> getById(long id);
    List<PeerConfiguration> getAll(Long id, String name, String remoteIpAddress, String realm);
    PeerConfiguration update(long id, PeerConfiguration dto) throws ValidationException;
    void delete(long id);
	List<PeerConfiguration> getPeerConfigByStatus(Status active);
}
