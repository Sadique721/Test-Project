package com.savbill.integrationsystem.nms.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectivityServicePojo {

    private List<ConnectivityService> connectivityService;

    public List<ConnectivityService> getConnectivityService() {
        return connectivityService;
    }

    public void setConnectivityService(List<ConnectivityService> connectivityService) {
        this.connectivityService = connectivityService;
    }

    public static class ConnectivityService {
        @JsonProperty("connectivity-service")
        private ConnectivityServiceDetail connectivityServiceDetail;

        public ConnectivityServiceDetail getConnectivityServiceDetail() {
            return connectivityServiceDetail;
        }

        public void setConnectivityServiceDetail(ConnectivityServiceDetail connectivityServiceDetail) {
            this.connectivityServiceDetail = connectivityServiceDetail;
        }
    }

    public static class ConnectivityServiceDetail {
        @JsonProperty("operational-state")
        private String operationalState;

        @JsonProperty("lifecycle-state")
        private String lifecycleState;

        @JsonProperty("administrative-state")
        private String administrativeState;

        private String uuid;

        @JsonProperty("additional-information")
        private List<AdditionalInformation> additionalInformation;

        @JsonProperty("end-point")
        private List<EndPoint> endPoint;

        @JsonProperty("layer-protocol-name")
        private String layerProtocolName;

        @JsonProperty("layer-protocol-qualifier")
        private String layerProtocolQualifier;

        @JsonProperty("connectivity-constraint")
        private ConnectivityConstraint connectivityConstraint;

        private String direction;

        public String getOperationalState() {
            return operationalState;
        }

        public void setOperationalState(String operationalState) {
            this.operationalState = operationalState;
        }

        public String getLifecycleState() {
            return lifecycleState;
        }

        public void setLifecycleState(String lifecycleState) {
            this.lifecycleState = lifecycleState;
        }

        public String getAdministrativeState() {
            return administrativeState;
        }

        public void setAdministrativeState(String administrativeState) {
            this.administrativeState = administrativeState;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public List<AdditionalInformation> getAdditionalInformation() {
            return additionalInformation;
        }

        public void setAdditionalInformation(List<AdditionalInformation> additionalInformation) {
            this.additionalInformation = additionalInformation;
        }

        public List<EndPoint> getEndPoint() {
            return endPoint;
        }

        public void setEndPoint(List<EndPoint> endPoint) {
            this.endPoint = endPoint;
        }

        public String getLayerProtocolName() {
            return layerProtocolName;
        }

        public void setLayerProtocolName(String layerProtocolName) {
            this.layerProtocolName = layerProtocolName;
        }

        public String getLayerProtocolQualifier() {
            return layerProtocolQualifier;
        }

        public void setLayerProtocolQualifier(String layerProtocolQualifier) {
            this.layerProtocolQualifier = layerProtocolQualifier;
        }

        public ConnectivityConstraint getConnectivityConstraint() {
            return connectivityConstraint;
        }

        public void setConnectivityConstraint(ConnectivityConstraint connectivityConstraint) {
            this.connectivityConstraint = connectivityConstraint;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }
    }

    public static class AdditionalInformation {
        @JsonProperty("value-name")
        private String valueName;

        private String value;

        public String getValueName() {
            return valueName;
        }

        public void setValueName(String valueName) {
            this.valueName = valueName;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class EndPoint {
        @JsonProperty("nrp-carrier-eth-connectivity-end-point-resource")
        private CarrierEthConnectivityEndPointResource endPointResource;

        @JsonProperty("lifecycle-state")
        private String lifecycleState;

        private String uuid;

        @JsonProperty("name")
        private List<Name> name;

        @JsonProperty("service-interface-point")
        private ServiceInterfacePoint serviceInterfacePoint;

        @JsonProperty("layer-protocol-name")
        private String layerProtocolName;

        @JsonProperty("layer-protocol-qualifier")
        private String layerProtocolQualifier;

        @JsonProperty("connection-end-point")
        private List<ConnectionEndPoint> connectionEndPoint;

        private String direction;

        @JsonProperty("additional-information")
        private List<AdditionalInformation> additionalInformation;

        @JsonProperty("downstream-bandwidth-profile")
        private BandwidthProfile downstreamBandwidthProfile;

        @JsonProperty("upstream-bandwidth-profile")
        private BandwidthProfile upstreamBandwidthProfile;

        public CarrierEthConnectivityEndPointResource getEndPointResource() {
            return endPointResource;
        }

        public void setEndPointResource(CarrierEthConnectivityEndPointResource endPointResource) {
            this.endPointResource = endPointResource;
        }

        public String getLifecycleState() {
            return lifecycleState;
        }

        public void setLifecycleState(String lifecycleState) {
            this.lifecycleState = lifecycleState;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public List<Name> getName() {
            return name;
        }

        public void setName(List<Name> name) {
            this.name = name;
        }

        public ServiceInterfacePoint getServiceInterfacePoint() {
            return serviceInterfacePoint;
        }

        public void setServiceInterfacePoint(ServiceInterfacePoint serviceInterfacePoint) {
            this.serviceInterfacePoint = serviceInterfacePoint;
        }

        public String getLayerProtocolName() {
            return layerProtocolName;
        }

        public void setLayerProtocolName(String layerProtocolName) {
            this.layerProtocolName = layerProtocolName;
        }

        public String getLayerProtocolQualifier() {
            return layerProtocolQualifier;
        }

        public void setLayerProtocolQualifier(String layerProtocolQualifier) {
            this.layerProtocolQualifier = layerProtocolQualifier;
        }

        public List<ConnectionEndPoint> getConnectionEndPoint() {
            return connectionEndPoint;
        }

        public void setConnectionEndPoint(List<ConnectionEndPoint> connectionEndPoint) {
            this.connectionEndPoint = connectionEndPoint;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }

        public List<AdditionalInformation> getAdditionalInformation() {
            return additionalInformation;
        }

        public void setAdditionalInformation(List<AdditionalInformation> additionalInformation) {
            this.additionalInformation = additionalInformation;
        }

        public BandwidthProfile getDownstreamBandwidthProfile() {
            return downstreamBandwidthProfile;
        }

        public void setDownstreamBandwidthProfile(BandwidthProfile downstreamBandwidthProfile) {
            this.downstreamBandwidthProfile = downstreamBandwidthProfile;
        }

        public BandwidthProfile getUpstreamBandwidthProfile() {
            return upstreamBandwidthProfile;
        }

        public void setUpstreamBandwidthProfile(BandwidthProfile upstreamBandwidthProfile) {
            this.upstreamBandwidthProfile = upstreamBandwidthProfile;
        }
    }

    public static class ConnectivityConstraint {
        @JsonProperty("service-type")
        private String serviceType;

        public String getServiceType() {
            return serviceType;
        }

        public void setServiceType(String serviceType) {
            this.serviceType = serviceType;
        }
    }

    public static class Name {
        @JsonProperty("value-name")
        private String valueName;

        private String value;

        public String getValueName() {
            return valueName;
        }

        public void setValueName(String valueName) {
            this.valueName = valueName;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class ServiceInterfacePoint {
        @JsonProperty("service-interface-point-uuid")
        private String serviceInterfacePointUuid;

        public String getServiceInterfacePointUuid() {
            return serviceInterfacePointUuid;
        }

        public void setServiceInterfacePointUuid(String serviceInterfacePointUuid) {
            this.serviceInterfacePointUuid = serviceInterfacePointUuid;
        }
    }

    public static class ConnectionEndPoint {
        @JsonProperty("topology-uuid")
        private String topologyUuid;

        @JsonProperty("node-uuid")
        private String nodeUuid;

        @JsonProperty("node-edge-point-uuid")
        private String nodeEdgePointUuid;

        @JsonProperty("connection-end-point-uuid")
        private String connectionEndPointUuid;

        public String getTopologyUuid() {
            return topologyUuid;
        }

        public void setTopologyUuid(String topologyUuid) {
            this.topologyUuid = topologyUuid;
        }

        public String getNodeUuid() {
            return nodeUuid;
        }

        public void setNodeUuid(String nodeUuid) {
            this.nodeUuid = nodeUuid;
        }

        public String getNodeEdgePointUuid() {
            return nodeEdgePointUuid;
        }

        public void setNodeEdgePointUuid(String nodeEdgePointUuid) {
            this.nodeEdgePointUuid = nodeEdgePointUuid;
        }

        public String getConnectionEndPointUuid() {
            return connectionEndPointUuid;
        }

        public void setConnectionEndPointUuid(String connectionEndPointUuid) {
            this.connectionEndPointUuid = connectionEndPointUuid;
        }
    }

    public static class CarrierEthConnectivityEndPointResource {
        @JsonProperty("ce-vlan-id-list-and-untag")
        private CeVlanIdListAndUntag ceVlanIdListAndUntag;

        @JsonProperty("lifecycle-state")
        private String lifecycleState;

        @JsonProperty("name")
        private List<Name> name;

        @JsonProperty("service-interface-point")
        private ServiceInterfacePoint serviceInterfacePoint;

        @JsonProperty("layer-protocol-name")
        private String layerProtocolName;

        @JsonProperty("layer-protocol-qualifier")
        private String layerProtocolQualifier;

        @JsonProperty("connection-end-point")
        private List<ConnectionEndPoint> connectionEndPoint;

        private String direction;

        @JsonProperty("additional-information")
        private List<AdditionalInformation> additionalInformation;

        @JsonProperty("uuid")
        private String uuid;

        @JsonProperty("downstream-bandwidth-profile")
        private BandwidthProfile downstreamBandwidthProfile;

        public CeVlanIdListAndUntag getCeVlanIdListAndUntag() {
            return ceVlanIdListAndUntag;
        }

        public void setCeVlanIdListAndUntag(CeVlanIdListAndUntag ceVlanIdListAndUntag) {
            this.ceVlanIdListAndUntag = ceVlanIdListAndUntag;
        }

        public String getLifecycleState() {
            return lifecycleState;
        }

        public void setLifecycleState(String lifecycleState) {
            this.lifecycleState = lifecycleState;
        }

        public List<Name> getName() {
            return name;
        }

        public void setName(List<Name> name) {
            this.name = name;
        }

        public ServiceInterfacePoint getServiceInterfacePoint() {
            return serviceInterfacePoint;
        }

        public void setServiceInterfacePoint(ServiceInterfacePoint serviceInterfacePoint) {
            this.serviceInterfacePoint = serviceInterfacePoint;
        }

        public String getLayerProtocolName() {
            return layerProtocolName;
        }

        public void setLayerProtocolName(String layerProtocolName) {
            this.layerProtocolName = layerProtocolName;
        }

        public String getLayerProtocolQualifier() {
            return layerProtocolQualifier;
        }

        public void setLayerProtocolQualifier(String layerProtocolQualifier) {
            this.layerProtocolQualifier = layerProtocolQualifier;
        }

        public List<ConnectionEndPoint> getConnectionEndPoint() {
            return connectionEndPoint;
        }

        public void setConnectionEndPoint(List<ConnectionEndPoint> connectionEndPoint) {
            this.connectionEndPoint = connectionEndPoint;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }

        public List<AdditionalInformation> getAdditionalInformation() {
            return additionalInformation;
        }

        public void setAdditionalInformation(List<AdditionalInformation> additionalInformation) {
            this.additionalInformation = additionalInformation;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public BandwidthProfile getDownstreamBandwidthProfile() {
            return downstreamBandwidthProfile;
        }

        public void setDownstreamBandwidthProfile(BandwidthProfile downstreamBandwidthProfile) {
            this.downstreamBandwidthProfile = downstreamBandwidthProfile;
        }
    }

    public static class CeVlanIdListAndUntag {
        @JsonProperty("vlan-id")
        private List<VlanId> vlanIdList;

        @JsonProperty("vlan-id-mapping-type")
        private String vlanIdMappingType;

        @JsonProperty("untagged-and-prio-tagged-included")
        private boolean untaggedAndPrioTaggedIncluded;

        public List<VlanId> getVlanIdList() {
            return vlanIdList;
        }

        public void setVlanIdList(List<VlanId> vlanIdList) {
            this.vlanIdList = vlanIdList;
        }

        public String getVlanIdMappingType() {
            return vlanIdMappingType;
        }

        public void setVlanIdMappingType(String vlanIdMappingType) {
            this.vlanIdMappingType = vlanIdMappingType;
        }

        public boolean isUntaggedAndPrioTaggedIncluded() {
            return untaggedAndPrioTaggedIncluded;
        }

        public void setUntaggedAndPrioTaggedIncluded(boolean untaggedAndPrioTaggedIncluded) {
            this.untaggedAndPrioTaggedIncluded = untaggedAndPrioTaggedIncluded;
        }
    }

    public static class VlanId {
        @JsonProperty("vlan-id")
        private String vlanId;

        public String getVlanId() {
            return vlanId;
        }

        public void setVlanId(String vlanId) {
            this.vlanId = vlanId;
        }
    }

    public static class BandwidthProfile {
        @JsonProperty("downstream-bandwidth-profile-uuid")
        private String downstreamBandwidthProfileUuid;

        @JsonProperty("upstream-bandwidth-profile-uuid")
        private String upstreamBandwidthProfileUuid;

        public String getDownstreamBandwidthProfileUuid() {
            return downstreamBandwidthProfileUuid;
        }

        public void setDownstreamBandwidthProfileUuid(String downstreamBandwidthProfileUuid) {
            this.downstreamBandwidthProfileUuid = downstreamBandwidthProfileUuid;
        }

        public String getUpstreamBandwidthProfileUuid() {
            return upstreamBandwidthProfileUuid;
        }

        public void setUpstreamBandwidthProfileUuid(String upstreamBandwidthProfileUuid) {
            this.upstreamBandwidthProfileUuid = upstreamBandwidthProfileUuid;
        }
    }
}
