package com.savbill.cpm.modules.subscriber.model;

import lombok.Data;

import java.util.List;

import com.savbill.cpm.modules.CommonList.model.CommonListDTO;
import com.savbill.cpm.modules.NetworkDevices.model.NetworkDeviceDTO;
import com.savbill.cpm.modules.NetworkDevices.model.SloatModel.OLTPortDTO;
import com.savbill.cpm.modules.NetworkDevices.model.SloatModel.OLTSlotDetailDTO;
import com.savbill.cpm.modules.ServiceArea.model.ServiceAreaDTO;
import com.savbill.cpm.modules.ippool.model.IPPoolDTO;

@Data
public class NetworkDetailsModel {
    private List<CommonListDTO> networkType;
    private List<CommonListDTO> serviceType;
    private List<IPPoolDTO> defaultPool;
    private List<ServiceAreaDTO> serviceArea;
    private IPPoolDTO selectedDefaultIpPool;
    private ServiceAreaDTO selectedServiceArea;
    private NetworkDeviceDTO selectedNetworkDeviceDTO;
    private OLTSlotDetailDTO selectedOltSlotDetailDTO;
    private OLTPortDTO selectedOltPortDetailsDTO;
    private String selectedNetworkType;
    private String selectedOnuId;
    private String selectedConnectionType;
    private String selectedServiceType;
    private String remarks;
}
