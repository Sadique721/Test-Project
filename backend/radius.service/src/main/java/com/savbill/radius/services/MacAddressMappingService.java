package com.savbill.radius.services;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.savbill.radius.entity.MacAddressMapping;
import com.savbill.radius.entity.MacAddressMappingDto;

public interface MacAddressMappingService {
	List<MacAddressMapping> findMacAddressMappingByCustomerId(Long customerId);
    List<MacAddressMapping> findAllMacAddressMapping();
    void deleteMacAddressMappingById(Long id);
    MacAddressMapping saveMacAddressMapping(MacAddressMappingDto macAddressMappingDto);
    List<MacAddressMapping> updateMacAddressMapping(List<MacAddressMapping> macAddressMapping);
    List<HashMap<String, Object>> findMacAddressMappingByUserName(String userName, int mvnoId);

    String deleteMacAddressByUserNameAndMac(Set<Long> mac);
}
