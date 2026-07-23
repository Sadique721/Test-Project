package com.savbill.cpm.MicroSeviceDataShare.SharedMessages;

import com.savbill.cpm.modules.Pincode.domain.Pincode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SaveServiceAreaSharedDataMessge {



    private Long id;

    private String name;

    private String status;


    private Boolean isDeleted = false;


   // private List<NetworkDevices> networkDevicesList = new ArrayList<>();


    private Integer mvnoId;


    private String latitude;


    private String longitude;


    private Long areaId;


    private List<Pincode> pincodeList = new ArrayList<>();


    private Long cityid;

    private Integer createdById;

    private Integer updatedById;
    private Boolean staffSAMap = false;
    private String createdByName;
    private String lastModifiedByName;
    private String siteName;
    private List<Long> locationIdList;
}
