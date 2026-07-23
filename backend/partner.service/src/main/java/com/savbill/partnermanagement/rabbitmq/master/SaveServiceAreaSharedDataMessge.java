package com.savbill.partnermanagement.rabbitmq.master;

import com.savbill.partnermanagement.modules.MasterManagement.Pincode.Pincode;
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
}
