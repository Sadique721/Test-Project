package com.diameter.service;

import java.util.List;

import javax.xml.bind.ValidationException;

import com.diameter.model.Attribute;
import com.diameter.model.Vendor;

public interface AttributeService {

	Attribute createOrUpdateAttribute(Attribute attribute) throws ValidationException;

	Attribute updateAttribute(Attribute attribute) throws ValidationException;

    void deleteAttribute(String id);

    List<Attribute> getAllAttributes();

    Attribute getAttributeById(String id);

    Attribute getAttributeByName(String name);

	List<Vendor> getAllActiveAttributes(String status);

    List<Attribute> getAttributeByAttributeId(String attributeId);
}

