package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.service;

import com.savbill.commonGateway.common.domain.ClientService;
import com.savbill.commonGateway.common.repository.ClientServiceRepository;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.core.service.ExBaseAbstractService;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.PolyGone;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.ServiceArea;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model.PolyGoneDTO;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model.ServiceAreaPolyGoneDTO;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.repository.ServiceAreaRepository;
import com.savbill.commonGateway.security.dto.LoggedInUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PolygoneService extends ExBaseAbstractService<PolyGoneDTO, PolyGone, Long> {


    @Autowired
    ServiceAreaService serviceAreaService;


    @Autowired
    ServiceAreaRepository serviceAreaRepository;

    @Autowired
    private ClientServiceRepository entityRepository;

    public PolygoneService(JpaRepository<PolyGone, Long> repository, IBaseMapper<PolyGoneDTO, PolyGone> mapper) {
        super(repository, mapper);
    }



    @Override
    public String getModuleNameForLog() {
        return null;
    }

    public int getLoggedInUserId() {
        int loggedInUserId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUserId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
            }
        } catch (Exception e) {
            loggedInUserId = -1;
        }
        return loggedInUserId;
    }




    public PolyGoneDTO domainToDTO(PolyGone polyGone){
        PolyGoneDTO polyGoneDTO = new PolyGoneDTO();
        polyGoneDTO.setId(polyGone.getId());
        polyGoneDTO.setPolyOrder(polyGone.getPolyOrder());
        polyGoneDTO.setLat(polyGone.getLat());
        polyGoneDTO.setLng(polyGone.getLng());
        polyGoneDTO.setServiceAreaId(polyGone.getServiceAreaId());
        polyGoneDTO.setMvnoId(polyGone.getMvnoid());
        polyGoneDTO.setPolygoneName(polyGone.getPolygoneName());
        polyGoneDTO.setServiceAreaName(serviceAreaRepository.getNameByServieAreaId(polyGone.getServiceAreaId()));

        return  polyGoneDTO;

    }



    public List<ServiceAreaPolyGoneDTO.ServiceAreaData> getAllPolyGone (List<PolyGone> polyGoneList){
        List <PolyGoneDTO> polyGoneDTOList = new ArrayList<>();
        Map<Long,List<PolyGoneDTO>> serviceAreaMap =  new HashMap<>();


        long serviceAreaId =0L;

        for (PolyGone polyGone: polyGoneList) {
            polyGoneDTOList.add(domainToDTO(polyGone));
        }
        for(PolyGoneDTO polyGoneDTO : polyGoneDTOList){
            serviceAreaId = polyGoneDTO.getServiceAreaId();
            if(!serviceAreaMap.containsKey(serviceAreaId)){
                serviceAreaMap.put(serviceAreaId,new ArrayList<>());
            }
            serviceAreaMap.get(serviceAreaId).add(polyGoneDTO);
        }

        List<ServiceAreaPolyGoneDTO.ServiceAreaData>serviceAreaDataList = new ArrayList<>();
        for (Map.Entry<Long, List<PolyGoneDTO>> entry : serviceAreaMap.entrySet()) {
            ClientService clientService = getByName(CommonConstants.IS_USED_UNDERDEVELOPMENT_SERVICEAREA);
            if ((null != clientService) && (null != clientService.getName()) && (clientService.getValue().equals("1"))) {
                ServiceArea serviceArea = serviceAreaRepository.findByIdAndStatusIn(entry.getKey(),Arrays.asList(CommonConstants.UNDERDEVELOPMENT_STATUS, CommonConstants.ACTIVE_STATUS));
                if(serviceArea != null){
                    ServiceAreaPolyGoneDTO.ServiceAreaData serviceAreaData = new ServiceAreaPolyGoneDTO.ServiceAreaData();
                    serviceAreaData.setServiceAreaId(Math.toIntExact(entry.getKey()));
                    serviceAreaData.setMvnoIds(serviceArea.getMvnoLists());
                    serviceAreaData.setMvnoId(serviceArea.getMvnoId());
                    serviceAreaData.setServiceAreaType(serviceArea.getServiceAreaType());
                    serviceAreaData.setPolyGoneDTOList(entry.getValue());
                    serviceAreaData.setServiceAreaName(serviceArea.getName());
                    serviceAreaData.setPolygonGroups(getGroupedPolygonsByName(polyGoneDTOList,serviceArea.getId()));
                    List<Integer> serviceAreaIdList = serviceAreaRepository.findServiceAreaIdsFromSiteName(serviceArea.getSiteName()).stream().map(Long::intValue).collect(Collectors.toList());
                    serviceAreaData.setSiteNameServiceAreaIdList(serviceAreaIdList);
                    serviceAreaDataList.add(serviceAreaData);
                }
            } else {
                ServiceArea serviceArea = serviceAreaRepository.findByIdAndStatusIn(entry.getKey(),Arrays.asList(CommonConstants.ACTIVE_STATUS));
                if(serviceArea != null){
                    ServiceAreaPolyGoneDTO.ServiceAreaData serviceAreaData = new ServiceAreaPolyGoneDTO.ServiceAreaData();
                    serviceAreaData.setServiceAreaId(Math.toIntExact(entry.getKey()));
                    serviceAreaData.setMvnoId(serviceArea.getMvnoId());
                    serviceAreaData.setMvnoIds(serviceArea.getMvnoLists());
                    serviceAreaData.setServiceAreaType(serviceArea.getServiceAreaType());
                    serviceAreaData.setPolyGoneDTOList(entry.getValue());
                    serviceAreaData.setServiceAreaName(serviceArea.getName());
                    serviceAreaData.setPolygonGroups(getGroupedPolygonsByName(polyGoneDTOList,serviceArea.getId()));
                    List<Integer> serviceAreaIdList = serviceAreaRepository.findServiceAreaIdsFromSiteName(serviceArea.getSiteName()).stream().map(Long::intValue).collect(Collectors.toList());
                    serviceAreaData.setSiteNameServiceAreaIdList(serviceAreaIdList);
                    serviceAreaDataList.add(serviceAreaData);
                }
            }
        }


        return serviceAreaDataList;
    }

    public ClientService getByName(String name) {
        Integer mvnoId = getMvnoIdFromCurrentStaff();
        return entityRepository.findByNameContainingIgnoreCaseAndMvnoIdEquals(name, mvnoId);
    }

    public Map<String, List<Map<String, Object>>> getGroupedPolygonsByName(List<PolyGoneDTO> polyGoneDTOList, Long serviceAreaId) {
        // Step 1: Create a map to group polygons by their polygon name
        Map<String, List<PolyGoneDTO>> polygonGroups = new HashMap<>();

        // Step 2: Group the PolyGoneDTO objects by polygonName
        for (PolyGoneDTO polygonDTO : polyGoneDTOList) {
            if(serviceAreaId==polygonDTO.getServiceAreaId().longValue()){
                String polygonName = polygonDTO.getPolygoneName();  // Get polygon name

                // Check if the polygonName is not null
                if (polygonName != null) {
                    polygonGroups.putIfAbsent(polygonName, new ArrayList<>());
                    polygonGroups.get(polygonName).add(polygonDTO);
                }
            }

        }

        // Step 3: Build the final response format
        Map<String, List<Map<String, Object>>> response = new HashMap<>();

        for (Map.Entry<String, List<PolyGoneDTO>> entry : polygonGroups.entrySet()) {
            List<Map<String, Object>> polygonList = new ArrayList<>();
            for (PolyGoneDTO polygonDTO : entry.getValue()) {
                Map<String, Object> polygonData = new HashMap<>();

                // Add all values of the PolyGoneDTO (you can customize which fields to add)
                polygonData.put("id", polygonDTO.getId());
                polygonData.put("serviceAreaId", polygonDTO.getServiceAreaId());
                polygonData.put("serviceAreaName", polygonDTO.getServiceAreaName());
                polygonData.put("mvnoId", polygonDTO.getMvnoId());
                polygonData.put("polygoneName", polygonDTO.getPolygoneName());
                //polygonData.put("identityKey", polygonDTO.getIdentityKey());
                polygonData.put("lat", polygonDTO.getLat());
                polygonData.put("lng", polygonDTO.getLng());
                polygonData.put("polyOrder", polygonDTO.getPolyOrder());
                //polygonData.put("createdate", polygonDTO.getCreatedate());
                //polygonData.put("updatedate", polygonDTO.getUpdatedate());
               //polygonData.put("createdByName", polygonDTO.getCreatedByName());
                //polygonData.put("lastModifiedByName", polygonDTO.getLastModifiedByName());
                //polygonData.put("createdById", polygonDTO.getCreatedById());
                //put("lastModifiedById", polygonDTO.getLastModifiedById());

                // Add the map to the polygon list
                polygonList.add(polygonData);
            }
            response.put(entry.getKey(), polygonList);  // Add the list of polygons for the current polygon name
        }

        return response;
    }

}
