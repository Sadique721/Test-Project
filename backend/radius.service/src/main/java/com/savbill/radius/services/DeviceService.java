package com.savbill.radius.services;

import com.savbill.radius.entity.Device;
import com.savbill.radius.helper.DeviceDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface DeviceService {
    List<Device> findAll(Integer mvnoId);

    Device findById(Long id, Integer mvnoId);

    List<Device> findByName(String name, Integer mvnoId);

    Device add(DeviceDto deviceDto, Integer mvnoId);

    Device update(DeviceDto deviceDto, Integer mvnoId, HttpServletRequest request);

    void delete(String name, Integer mvnoId);

    String changeDeviceStatus(String deviceProfileName, String status, Integer mvnoId, HttpServletRequest request);

    Map<String, Object> getDeviceData(Map<String, String> payload, Integer mvnoId, boolean isFromGetDeviceData);

    Map<String, Object> getDeviceDataForLogin(Map<String, String> payload, Integer mvnoId, boolean isFromGetDeviceData);

    Integer countByCoaDmProfileId(Long coaDmProfileId);

    int generateSNMP(Integer mvnoId, String username, String userIP, boolean isMacProvision, boolean isLogout);
}
