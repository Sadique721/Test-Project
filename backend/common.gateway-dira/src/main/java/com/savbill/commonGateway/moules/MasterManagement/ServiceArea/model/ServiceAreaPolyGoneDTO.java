package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ServiceAreaPolyGoneDTO {


    private List<ServiceAreaData> serviceAreaDataList = new ArrayList<>();




    public static class ServiceAreaData{
        private Integer serviceAreaId;


        private String serviceAreaType;

        private String mvnoIds;

        private Integer mvnoId;

        private List<PolyGoneDTO> polyGoneDTOList;

        private String serviceAreaName;

        private List<Integer> siteNameServiceAreaIdList;

        public String getMvnoIds() {
            return mvnoIds;
        }

        public void setMvnoIds(String mvnoIds) {
            this.mvnoIds = mvnoIds;
        }

        public Integer getServiceAreaId() {
            return serviceAreaId;
        }

        public void setServiceAreaId(Integer serviceAreaId) {
            this.serviceAreaId = serviceAreaId;
        }
        public String getServiceAreaType() {
            return serviceAreaType;
        }

        public void setServiceAreaType(String serviceAreaType) {
            this.serviceAreaType = serviceAreaType;
        }



        public List<PolyGoneDTO> getPolyGoneDTOList() {
            return polyGoneDTOList;
        }

        public void setPolyGoneDTOList(List<PolyGoneDTO> polyGoneDTOList) {
            this.polyGoneDTOList = polyGoneDTOList;
        }

        public String getServiceAreaName() {
            return serviceAreaName;
        }

        public void setServiceAreaName(String serviceAreaName) {
            this.serviceAreaName = serviceAreaName;
        }

        public List<Integer> getSiteNameServiceAreaIdList() {
            return siteNameServiceAreaIdList;
        }

        public void setSiteNameServiceAreaIdList(List<Integer> siteNameServiceAreaIdList) {
            this.siteNameServiceAreaIdList = siteNameServiceAreaIdList;
        }

        public Map<String, List<Map<String, Object>>> getPolygonGroups() {
            return polygonGroups;
        }

        public void setPolygonGroups(Map<String, List<Map<String, Object>>> polygonGroups) {
            this.polygonGroups = polygonGroups;
        }

        Map<String, List<Map<String, Object>>> polygonGroups = new HashMap<>();

        public Integer getMvnoId() {
            return mvnoId;
        }

        public void setMvnoId(Integer mvnoId) {
            this.mvnoId = mvnoId;
        }

    }


}
