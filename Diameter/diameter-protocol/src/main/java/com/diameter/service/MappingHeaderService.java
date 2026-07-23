package com.diameter.service;

import java.util.List;

import javax.xml.bind.ValidationException;

import com.diameter.model.MappingHeader;

public interface MappingHeaderService {

    MappingHeader createOrUpdateMapping(MappingHeader mappingHeader) throws ValidationException;

    MappingHeader updateMapping(MappingHeader mappingHeader) throws ValidationException;

    void deleteMapping(String id);

    List<MappingHeader> getAllMappings();

    MappingHeader getMappingById(String id);

    MappingHeader getMappingByCommandName(String commandName);

    List<MappingHeader> getMappingsByRequestAndResponseType(String requestType, String responseType, String application, Integer vendorId, String ccRequestType);
}