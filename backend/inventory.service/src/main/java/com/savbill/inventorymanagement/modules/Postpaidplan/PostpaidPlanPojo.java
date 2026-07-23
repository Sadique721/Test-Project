package com.savbill.inventorymanagement.modules.Postpaidplan;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.dto.IBaseDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanMapping.Productplanmappingdto;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class PostpaidPlanPojo extends Auditable implements IBaseDto {

    private Integer id;

    @NotNull(message = "Please enter name")
    private String name;
    private String planStatus;

    private Integer mvnoId;

    @NotNull(message = "Please enter status")
    private String status;

    private Integer serviceId;
    
    private String serviceName;

    @NotNull(message = "Please enter plantype")
    private String plantype;

    private Integer maxChild;

    private String planGroup;

    private Boolean isDelete = false;

    private String createDateString;
    private String updateDateString;

    private List<Long> serviceAreaIds = new ArrayList<>();
    private List<ServiceAreaDTO> serviceAreaNameList = new ArrayList<>();

    private Long product_category;

   private String product_type;

   private Long productId;

    private String discount;
    private String ownershipType;


    private List<Productplanmappingdto> productplanmappingList =  new ArrayList<>();
    private List<Long> productplanmappingids = new ArrayList<>();

    private Boolean invoiceToOrg;

    private Boolean requiredApproval;

    private String bandwidth;
    private String link_type;
    private String connection_type;
    private String distance;
    private String ram;
    private String cpu;
    private String storage;
    private String storage_type;
    private String auto_backup;
    private String cpanel;
    private String location;
    private String quantity;
    private String package_type;
    private String number_of_days;
    private String no_of_users;
    private String rack_space;
    private String rack_unit;
    private String power_consumption;
    private String network_card;
    private String ip_or_ip_pool;
    private String no_of_license;
    private String no_of_email_user_license;
    private String no_of_server_license;
    private String no_of_user_license;
    private String no_of_nodes;
    private String event_per_second;
    private String no_of_additional_server;
    private String no_of_additional_storage;
    private String additional_storage_type;
    private String eps_License;
    private String no_of_nodes_license;
    private String hardware_resource;
    private String man_power;
    private String no_of_domains;
    private String security_modules;
    private String hardware_or_servers;
    private String country;
    private String no_of_vpn;
    private String device_throughput;
    private String retail;

    private Long displayId;
    private String displayPostpaidName;
    private String businessType;

    private  Boolean basePlan = false;

    @Override
    public Long getIdentityKey() {
        return Long.valueOf(id);
    }
}
