import 'package:savbill/webservices/base_response.dart';

class GetPlanByServiceIdRes extends BaseResponse {
  List<PlanByServiceId>? postPaidPlan;

  GetPlanByServiceIdRes({this.postPaidPlan,});

  GetPlanByServiceIdRes.fromJson(Map<String, dynamic> json) {
    if (json['postPaidPlan'] != null) {
      postPaidPlan = <PlanByServiceId>[];
      json['postPaidPlan'].forEach((v) {
        postPaidPlan!.add(new PlanByServiceId.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.postPaidPlan != null) {
      data['postPaidPlan'] = this.postPaidPlan!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class PlanByServiceId {
  dynamic createdate;
  dynamic updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  dynamic name;
  dynamic displayName;
  dynamic code;
  dynamic desc;
  dynamic category;
  dynamic startDate;
  dynamic endDate;
  dynamic uploadQOS;
  dynamic downloadQOS;
  dynamic uploadTs;
  dynamic downloadTs;
  dynamic allowOverUsage;
  dynamic quotaUnit;
  dynamic quota;
  dynamic planStatus;
  dynamic childQuota;
  dynamic childQuotaUnit;
  dynamic slice;
  dynamic sliceUnit;
  dynamic attachedToAllHotSpots;
  dynamic param1;
  dynamic param2;
  dynamic param3;
  dynamic mvnoId;
  dynamic status;
  dynamic taxId;
  dynamic serviceId;
  dynamic serviceName;
  dynamic timebasepolicyId;
  dynamic timebasepolicyName;
  dynamic plantype;
  dynamic maxChild;
  List<ChargeList>? chargeList;
  dynamic dbr;
  dynamic planGroup;
  dynamic validity;
  dynamic saccode;
  dynamic maxconcurrentsession;
  dynamic quotaunittime;
  dynamic quotatime;
  dynamic quotatype;
  dynamic offerprice;
  dynamic qospolicyid;
  dynamic qospolicyName;
  // List<Null>? radiusprofileIds;
  dynamic isDelete;
  dynamic createDateString;
  dynamic updateDateString;
  dynamic quotadid;
  dynamic quotaintercom;
  dynamic quotaunitdid;
  dynamic quotaunitintercom;
  dynamic dataCategory;
  dynamic taxamount;
  List<Null>? serviceAreaIds;
  List<ServiceAreaNameList>? serviceAreaNameList;
  dynamic quotaResetInterval;
  dynamic mode;
  dynamic unitsOfValidity;
  dynamic buId;
  dynamic nextStaff;
  dynamic newOfferPrice;
  dynamic nextTeamHierarchyMapping;
  dynamic allowdiscount;
  dynamic productCategory;
  dynamic productType;
  dynamic productId;
  dynamic discount;
  dynamic ownershipType;
  // List<Null>? productplanmappingList;
  // List<Null>? productplanmappingids;
  dynamic invoiceToOrg;
  dynamic requiredApproval;
  // List<Null>? planCasMappingList;
  dynamic bandwidth;
  dynamic linkType;
  dynamic connectionType;
  dynamic distance;
  dynamic ram;
  dynamic cpu;
  dynamic storage;
  dynamic storageType;
  dynamic autoBackup;
  dynamic cpanel;
  dynamic location;
  dynamic quantity;
  dynamic packageType;
  dynamic numberOfDays;
  dynamic noOfUsers;
  dynamic rackSpace;
  dynamic rackUnit;
  dynamic powerConsumption;
  dynamic networkCard;
  dynamic ipOrIpPool;
  dynamic noOfLicense;
  dynamic noOfEmailUserLicense;
  dynamic noOfServerLicense;
  dynamic noOfUserLicense;
  dynamic noOfNodes;
  dynamic eventPerSecond;
  dynamic noOfAdditionalServer;
  dynamic noOfAdditionalStorage;
  dynamic additionalStorageType;
  dynamic epsLicense;
  dynamic noOfNodesLicense;
  dynamic hardwareResource;
  dynamic manPower;
  dynamic noOfDomains;
  dynamic securityModules;
  dynamic hardwareOrServers;
  dynamic country;
  dynamic noOfVpn;
  dynamic deviceThroughput;
  dynamic retail;
  dynamic displayId;
  dynamic displayPostpaidName;
  dynamic planQosMappingEntityList;
  dynamic viewplanQosMappingEntityList;
  dynamic businessType;
  dynamic basePlan;
  dynamic useQuota;
  dynamic chunk;
  dynamic accessibility;

  PlanByServiceId(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.displayName,
        this.code,
        this.desc,
        this.category,
        this.startDate,
        this.endDate,
        this.uploadQOS,
        this.downloadQOS,
        this.uploadTs,
        this.downloadTs,
        this.allowOverUsage,
        this.quotaUnit,
        this.quota,
        this.planStatus,
        this.childQuota,
        this.childQuotaUnit,
        this.slice,
        this.sliceUnit,
        this.attachedToAllHotSpots,
        this.param1,
        this.param2,
        this.param3,
        this.mvnoId,
        this.status,
        this.taxId,
        this.serviceId,
        this.serviceName,
        this.timebasepolicyId,
        this.timebasepolicyName,
        this.plantype,
        this.maxChild,
        this.chargeList,
        this.dbr,
        this.planGroup,
        this.validity,
        this.saccode,
        this.maxconcurrentsession,
        this.quotaunittime,
        this.quotatime,
        this.quotatype,
        this.offerprice,
        this.qospolicyid,
        this.qospolicyName,
        // this.radiusprofileIds,
        this.isDelete,
        this.createDateString,
        this.updateDateString,
        this.quotadid,
        this.quotaintercom,
        this.quotaunitdid,
        this.quotaunitintercom,
        this.dataCategory,
        this.taxamount,
        this.serviceAreaIds,
        this.serviceAreaNameList,
        this.quotaResetInterval,
        this.mode,
        this.unitsOfValidity,
        this.buId,
        this.nextStaff,
        this.newOfferPrice,
        this.nextTeamHierarchyMapping,
        this.allowdiscount,
        this.productCategory,
        this.productType,
        this.productId,
        this.discount,
        this.ownershipType,
        // this.productplanmappingList,
        // this.productplanmappingids,
        this.invoiceToOrg,
        this.requiredApproval,
        // this.planCasMappingList,
        this.bandwidth,
        this.linkType,
        this.connectionType,
        this.distance,
        this.ram,
        this.cpu,
        this.storage,
        this.storageType,
        this.autoBackup,
        this.cpanel,
        this.location,
        this.quantity,
        this.packageType,
        this.numberOfDays,
        this.noOfUsers,
        this.rackSpace,
        this.rackUnit,
        this.powerConsumption,
        this.networkCard,
        this.ipOrIpPool,
        this.noOfLicense,
        this.noOfEmailUserLicense,
        this.noOfServerLicense,
        this.noOfUserLicense,
        this.noOfNodes,
        this.eventPerSecond,
        this.noOfAdditionalServer,
        this.noOfAdditionalStorage,
        this.additionalStorageType,
        this.epsLicense,
        this.noOfNodesLicense,
        this.hardwareResource,
        this.manPower,
        this.noOfDomains,
        this.securityModules,
        this.hardwareOrServers,
        this.country,
        this.noOfVpn,
        this.deviceThroughput,
        this.retail,
        this.displayId,
        this.displayPostpaidName,
        this.planQosMappingEntityList,
        this.viewplanQosMappingEntityList,
        this.businessType,
        this.basePlan,
        this.useQuota,
        this.chunk,
        this.accessibility});

  PlanByServiceId.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    displayName = json['displayName'];
    code = json['code'];
    desc = json['desc'];
    category = json['category'];
    startDate = json['startDate'];
    endDate = json['endDate'];
    uploadQOS = json['uploadQOS'];
    downloadQOS = json['downloadQOS'];
    uploadTs = json['uploadTs'];
    downloadTs = json['downloadTs'];
    allowOverUsage = json['allowOverUsage'];
    quotaUnit = json['quotaUnit'];
    quota = json['quota'];
    planStatus = json['planStatus'];
    childQuota = json['childQuota'];
    childQuotaUnit = json['childQuotaUnit'];
    slice = json['slice'];
    sliceUnit = json['sliceUnit'];
    attachedToAllHotSpots = json['attachedToAllHotSpots'];
    param1 = json['param1'];
    param2 = json['param2'];
    param3 = json['param3'];
    mvnoId = json['mvnoId'];
    status = json['status'];
    taxId = json['taxId'];
    serviceId = json['serviceId'];
    serviceName = json['serviceName'];
    timebasepolicyId = json['timebasepolicyId'];
    timebasepolicyName = json['timebasepolicyName'];
    plantype = json['plantype'];
    maxChild = json['maxChild'];
    if (json['chargeList'] != null) {
      chargeList = <ChargeList>[];
      json['chargeList'].forEach((v) {
        chargeList!.add(new ChargeList.fromJson(v));
      });
    }
    dbr = json['dbr'];
    planGroup = json['planGroup'];
    validity = json['validity'];
    saccode = json['saccode'];
    maxconcurrentsession = json['maxconcurrentsession'];
    quotaunittime = json['quotaunittime'];
    quotatime = json['quotatime'];
    quotatype = json['quotatype'];
    offerprice = json['offerprice'];
    qospolicyid = json['qospolicyid'];
    qospolicyName = json['qospolicyName'];
    // if (json['radiusprofileIds'] != null) {
    //   radiusprofileIds = <Null>[];
    //   json['radiusprofileIds'].forEach((v) {
    //     radiusprofileIds!.add(new Null.fromJson(v));
    //   });
    // }
    isDelete = json['isDelete'];
    createDateString = json['createDateString'];
    updateDateString = json['updateDateString'];
    quotadid = json['quotadid'];
    quotaintercom = json['quotaintercom'];
    quotaunitdid = json['quotaunitdid'];
    quotaunitintercom = json['quotaunitintercom'];
    dataCategory = json['dataCategory'];
    taxamount = json['taxamount'];
    // if (json['serviceAreaIds'] != null) {
    //   serviceAreaIds = <Null>[];
    //   json['serviceAreaIds'].forEach((v) {
    //     serviceAreaIds!.add(new Null.fromJson(v));
    //   });
    // }
    if (json['serviceAreaNameList'] != null) {
      serviceAreaNameList = <ServiceAreaNameList>[];
      json['serviceAreaNameList'].forEach((v) {
        serviceAreaNameList!.add(new ServiceAreaNameList.fromJson(v));
      });
    }
    quotaResetInterval = json['quotaResetInterval'];
    mode = json['mode'];
    unitsOfValidity = json['unitsOfValidity'];
    buId = json['buId'];
    nextStaff = json['nextStaff'];
    newOfferPrice = json['newOfferPrice'];
    nextTeamHierarchyMapping = json['nextTeamHierarchyMapping'];
    allowdiscount = json['allowdiscount'];
    productCategory = json['product_category'];
    productType = json['product_type'];
    productId = json['productId'];
    discount = json['discount'];
    ownershipType = json['ownershipType'];
    // if (json['productplanmappingList'] != null) {
    //   productplanmappingList = <Null>[];
    //   json['productplanmappingList'].forEach((v) {
    //     productplanmappingList!.add(new Null.fromJson(v));
    //   });
    // }
    // if (json['productplanmappingids'] != null) {
    //   productplanmappingids = <Null>[];
    //   json['productplanmappingids'].forEach((v) {
    //     productplanmappingids!.add(new Null.fromJson(v));
    //   });
    // }
    invoiceToOrg = json['invoiceToOrg'];
    requiredApproval = json['requiredApproval'];
    // if (json['planCasMappingList'] != null) {
    //   planCasMappingList = <Null>[];
    //   json['planCasMappingList'].forEach((v) {
    //     planCasMappingList!.add(new Null.fromJson(v));
    //   });
    // }
    bandwidth = json['bandwidth'];
    linkType = json['link_type'];
    connectionType = json['connection_type'];
    distance = json['distance'];
    ram = json['ram'];
    cpu = json['cpu'];
    storage = json['storage'];
    storageType = json['storage_type'];
    autoBackup = json['auto_backup'];
    cpanel = json['cpanel'];
    location = json['location'];
    quantity = json['quantity'];
    packageType = json['package_type'];
    numberOfDays = json['number_of_days'];
    noOfUsers = json['no_of_users'];
    rackSpace = json['rack_space'];
    rackUnit = json['rack_unit'];
    powerConsumption = json['power_consumption'];
    networkCard = json['network_card'];
    ipOrIpPool = json['ip_or_ip_pool'];
    noOfLicense = json['no_of_license'];
    noOfEmailUserLicense = json['no_of_email_user_license'];
    noOfServerLicense = json['no_of_server_license'];
    noOfUserLicense = json['no_of_user_license'];
    noOfNodes = json['no_of_nodes'];
    eventPerSecond = json['event_per_second'];
    noOfAdditionalServer = json['no_of_additional_server'];
    noOfAdditionalStorage = json['no_of_additional_storage'];
    additionalStorageType = json['additional_storage_type'];
    epsLicense = json['eps_License'];
    noOfNodesLicense = json['no_of_nodes_license'];
    hardwareResource = json['hardware_resource'];
    manPower = json['man_power'];
    noOfDomains = json['no_of_domains'];
    securityModules = json['security_modules'];
    hardwareOrServers = json['hardware_or_servers'];
    country = json['country'];
    noOfVpn = json['no_of_vpn'];
    deviceThroughput = json['device_throughput'];
    retail = json['retail'];
    displayId = json['displayId'];
    displayPostpaidName = json['displayPostpaidName'];
    planQosMappingEntityList = json['planQosMappingEntityList'];
    viewplanQosMappingEntityList = json['viewplanQosMappingEntityList'];
    businessType = json['businessType'];
    basePlan = json['basePlan'];
    useQuota = json['useQuota'];
    chunk = json['chunk'];
    accessibility = json['accessibility'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['id'] = this.id;
    data['name'] = this.name;
    data['displayName'] = this.displayName;
    data['code'] = this.code;
    data['desc'] = this.desc;
    data['category'] = this.category;
    data['startDate'] = this.startDate;
    data['endDate'] = this.endDate;
    data['uploadQOS'] = this.uploadQOS;
    data['downloadQOS'] = this.downloadQOS;
    data['uploadTs'] = this.uploadTs;
    data['downloadTs'] = this.downloadTs;
    data['allowOverUsage'] = this.allowOverUsage;
    data['quotaUnit'] = this.quotaUnit;
    data['quota'] = this.quota;
    data['planStatus'] = this.planStatus;
    data['childQuota'] = this.childQuota;
    data['childQuotaUnit'] = this.childQuotaUnit;
    data['slice'] = this.slice;
    data['sliceUnit'] = this.sliceUnit;
    data['attachedToAllHotSpots'] = this.attachedToAllHotSpots;
    data['param1'] = this.param1;
    data['param2'] = this.param2;
    data['param3'] = this.param3;
    data['mvnoId'] = this.mvnoId;
    data['status'] = this.status;
    data['taxId'] = this.taxId;
    data['serviceId'] = this.serviceId;
    data['serviceName'] = this.serviceName;
    data['timebasepolicyId'] = this.timebasepolicyId;
    data['timebasepolicyName'] = this.timebasepolicyName;
    data['plantype'] = this.plantype;
    data['maxChild'] = this.maxChild;
    if (this.chargeList != null) {
      data['chargeList'] = this.chargeList!.map((v) => v.toJson()).toList();
    }
    data['dbr'] = this.dbr;
    data['planGroup'] = this.planGroup;
    data['validity'] = this.validity;
    data['saccode'] = this.saccode;
    data['maxconcurrentsession'] = this.maxconcurrentsession;
    data['quotaunittime'] = this.quotaunittime;
    data['quotatime'] = this.quotatime;
    data['quotatype'] = this.quotatype;
    data['offerprice'] = this.offerprice;
    data['qospolicyid'] = this.qospolicyid;
    data['qospolicyName'] = this.qospolicyName;
    // if (this.radiusprofileIds != null) {
    //   data['radiusprofileIds'] =
    //       this.radiusprofileIds!.map((v) => v.toJson()).toList();
    // }
    data['isDelete'] = this.isDelete;
    data['createDateString'] = this.createDateString;
    data['updateDateString'] = this.updateDateString;
    data['quotadid'] = this.quotadid;
    data['quotaintercom'] = this.quotaintercom;
    data['quotaunitdid'] = this.quotaunitdid;
    data['quotaunitintercom'] = this.quotaunitintercom;
    data['dataCategory'] = this.dataCategory;
    data['taxamount'] = this.taxamount;
    // if (this.serviceAreaIds != null) {
    //   data['serviceAreaIds'] =
    //       this.serviceAreaIds!.map((v) => v.toJson()).toList();
    // }
    // if (this.serviceAreaNameList != null) {
    //   data['serviceAreaNameList'] =
    //       this.serviceAreaNameList!.map((v) => v.toJson()).toList();
    // }
    data['quotaResetInterval'] = this.quotaResetInterval;
    data['mode'] = this.mode;
    data['unitsOfValidity'] = this.unitsOfValidity;
    data['buId'] = this.buId;
    data['nextStaff'] = this.nextStaff;
    data['newOfferPrice'] = this.newOfferPrice;
    data['nextTeamHierarchyMapping'] = this.nextTeamHierarchyMapping;
    data['allowdiscount'] = this.allowdiscount;
    data['product_category'] = this.productCategory;
    data['product_type'] = this.productType;
    data['productId'] = this.productId;
    data['discount'] = this.discount;
    data['ownershipType'] = this.ownershipType;
    // if (this.productplanmappingList != null) {
    //   data['productplanmappingList'] =
    //       this.productplanmappingList!.map((v) => v.toJson()).toList();
    // }
    // if (this.productplanmappingids != null) {
    //   data['productplanmappingids'] =
    //       this.productplanmappingids!.map((v) => v.toJson()).toList();
    // }
    data['invoiceToOrg'] = this.invoiceToOrg;
    data['requiredApproval'] = this.requiredApproval;
    // if (this.planCasMappingList != null) {
    //   data['planCasMappingList'] =
    //       this.planCasMappingList!.map((v) => v.toJson()).toList();
    // }
    data['bandwidth'] = this.bandwidth;
    data['link_type'] = this.linkType;
    data['connection_type'] = this.connectionType;
    data['distance'] = this.distance;
    data['ram'] = this.ram;
    data['cpu'] = this.cpu;
    data['storage'] = this.storage;
    data['storage_type'] = this.storageType;
    data['auto_backup'] = this.autoBackup;
    data['cpanel'] = this.cpanel;
    data['location'] = this.location;
    data['quantity'] = this.quantity;
    data['package_type'] = this.packageType;
    data['number_of_days'] = this.numberOfDays;
    data['no_of_users'] = this.noOfUsers;
    data['rack_space'] = this.rackSpace;
    data['rack_unit'] = this.rackUnit;
    data['power_consumption'] = this.powerConsumption;
    data['network_card'] = this.networkCard;
    data['ip_or_ip_pool'] = this.ipOrIpPool;
    data['no_of_license'] = this.noOfLicense;
    data['no_of_email_user_license'] = this.noOfEmailUserLicense;
    data['no_of_server_license'] = this.noOfServerLicense;
    data['no_of_user_license'] = this.noOfUserLicense;
    data['no_of_nodes'] = this.noOfNodes;
    data['event_per_second'] = this.eventPerSecond;
    data['no_of_additional_server'] = this.noOfAdditionalServer;
    data['no_of_additional_storage'] = this.noOfAdditionalStorage;
    data['additional_storage_type'] = this.additionalStorageType;
    data['eps_License'] = this.epsLicense;
    data['no_of_nodes_license'] = this.noOfNodesLicense;
    data['hardware_resource'] = this.hardwareResource;
    data['man_power'] = this.manPower;
    data['no_of_domains'] = this.noOfDomains;
    data['security_modules'] = this.securityModules;
    data['hardware_or_servers'] = this.hardwareOrServers;
    data['country'] = this.country;
    data['no_of_vpn'] = this.noOfVpn;
    data['device_throughput'] = this.deviceThroughput;
    data['retail'] = this.retail;
    data['displayId'] = this.displayId;
    data['displayPostpaidName'] = this.displayPostpaidName;
    data['planQosMappingEntityList'] = this.planQosMappingEntityList;
    data['viewplanQosMappingEntityList'] = this.viewplanQosMappingEntityList;
    data['businessType'] = this.businessType;
    data['basePlan'] = this.basePlan;
    data['useQuota'] = this.useQuota;
    data['chunk'] = this.chunk;
    data['accessibility'] = this.accessibility;
    return data;
  }
}

class ChargeList {
  dynamic id;
  Charge? charge;
  dynamic billingCycle;
  dynamic createdate;
  dynamic chargeprice;
  dynamic chargeId;
  dynamic chargeName;
  dynamic planId;

  ChargeList(
      {this.id,
        this.charge,
        this.billingCycle,
        this.createdate,
        this.chargeprice,
        this.chargeId,
        this.chargeName,
        this.planId});

  ChargeList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    charge =
    json['charge'] != null ? new Charge.fromJson(json['charge']) : null;
    billingCycle = json['billingCycle'];
    createdate = json['createdate'];
    chargeprice = json['chargeprice'];
    chargeId = json['chargeId'];
    chargeName = json['chargeName'];
    planId = json['planId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    if (this.charge != null) {
      data['charge'] = this.charge!.toJson();
    }
    data['billingCycle'] = this.billingCycle;
    data['createdate'] = this.createdate;
    data['chargeprice'] = this.chargeprice;
    data['chargeId'] = this.chargeId;
    data['chargeName'] = this.chargeName;
    data['planId'] = this.planId;
    return data;
  }
}

class Charge {
  dynamic createdate;
  dynamic updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  dynamic name;
  dynamic desc;
  dynamic chargetype;
  dynamic price;
  dynamic taxid;
  dynamic taxName;
  dynamic discountid;
  dynamic dbr;
  dynamic actualprice;
  dynamic isDelete;
  dynamic chargecategory;
  dynamic saccode;
  dynamic taxamount;
  dynamic mvnoId;
  dynamic buId;
  dynamic status;
  dynamic ledgerId;
  dynamic royaltyPayable;
  dynamic serviceid;
  // List<Null>? servicesid;
  // List<Null>? serviceNameList;
  dynamic services;
  dynamic displayId;
  dynamic displayName;
  dynamic businessType;
  dynamic pushableLedgerId;
  dynamic isinventorycharge;
  dynamic productId;
  dynamic inventoryChargeType;

  Charge(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.desc,
        this.chargetype,
        this.price,
        this.taxid,
        this.taxName,
        this.discountid,
        this.dbr,
        this.actualprice,
        this.isDelete,
        this.chargecategory,
        this.saccode,
        this.taxamount,
        this.mvnoId,
        this.buId,
        this.status,
        this.ledgerId,
        this.royaltyPayable,
        this.serviceid,
        // this.servicesid,
        // this.serviceNameList,
        this.services,
        this.displayId,
        this.displayName,
        this.businessType,
        this.pushableLedgerId,
        this.isinventorycharge,
        this.productId,
        this.inventoryChargeType});

  Charge.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    desc = json['desc'];
    chargetype = json['chargetype'];
    price = json['price'];
    taxid = json['taxid'];
    taxName = json['taxName'];
    discountid = json['discountid'];
    dbr = json['dbr'];
    actualprice = json['actualprice'];
    isDelete = json['isDelete'];
    chargecategory = json['chargecategory'];
    saccode = json['saccode'];
    taxamount = json['taxamount'];
    mvnoId = json['mvnoId'];
    buId = json['buId'];
    status = json['status'];
    ledgerId = json['ledgerId'];
    royaltyPayable = json['royalty_payable'];
    serviceid = json['serviceid'];
    // if (json['servicesid'] != null) {
    //   servicesid = <Null>[];
    //   json['servicesid'].forEach((v) {
    //     servicesid!.add(new Null.fromJson(v));
    //   });
    // }
    // if (json['serviceNameList'] != null) {
    //   serviceNameList = <Null>[];
    //   json['serviceNameList'].forEach((v) {
    //     serviceNameList!.add(new Null.fromJson(v));
    //   });
    // }
    services = json['services'];
    displayId = json['displayId'];
    displayName = json['displayName'];
    businessType = json['businessType'];
    pushableLedgerId = json['pushableLedgerId'];
    isinventorycharge = json['isinventorycharge'];
    productId = json['productId'];
    inventoryChargeType = json['inventoryChargeType'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['id'] = this.id;
    data['name'] = this.name;
    data['desc'] = this.desc;
    data['chargetype'] = this.chargetype;
    data['price'] = this.price;
    data['taxid'] = this.taxid;
    data['taxName'] = this.taxName;
    data['discountid'] = this.discountid;
    data['dbr'] = this.dbr;
    data['actualprice'] = this.actualprice;
    data['isDelete'] = this.isDelete;
    data['chargecategory'] = this.chargecategory;
    data['saccode'] = this.saccode;
    data['taxamount'] = this.taxamount;
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    data['status'] = this.status;
    data['ledgerId'] = this.ledgerId;
    data['royalty_payable'] = this.royaltyPayable;
    data['serviceid'] = this.serviceid;
    // if (this.servicesid != null) {
    //   data['servicesid'] = this.servicesid!.map((v) => v.toJson()).toList();
    // }
    // if (this.serviceNameList != null) {
    //   data['serviceNameList'] =
    //       this.serviceNameList!.map((v) => v.toJson()).toList();
    // }
    data['services'] = this.services;
    data['displayId'] = this.displayId;
    data['displayName'] = this.displayName;
    data['businessType'] = this.businessType;
    data['pushableLedgerId'] = this.pushableLedgerId;
    data['isinventorycharge'] = this.isinventorycharge;
    data['productId'] = this.productId;
    data['inventoryChargeType'] = this.inventoryChargeType;
    return data;
  }
}

class ServiceAreaNameList {
  dynamic createdate;
  dynamic updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  dynamic name;
  dynamic status;
  dynamic isDeleted;
  dynamic latitude;
  dynamic longitude;
  dynamic areaid;
  dynamic mvnoId;
  dynamic pincodes;
  dynamic cityid;
  dynamic displayId;
  dynamic displayName;

  ServiceAreaNameList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.status,
        this.isDeleted,
        this.latitude,
        this.longitude,
        this.areaid,
        this.mvnoId,
        this.pincodes,
        this.cityid,
        this.displayId,
        this.displayName});

  ServiceAreaNameList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    areaid = json['areaid'];
    mvnoId = json['mvnoId'];
    pincodes = json['pincodes'];
    cityid = json['cityid'];
    displayId = json['displayId'];
    displayName = json['displayName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['id'] = this.id;
    data['name'] = this.name;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['areaid'] = this.areaid;
    data['mvnoId'] = this.mvnoId;
    data['pincodes'] = this.pincodes;
    data['cityid'] = this.cityid;
    data['displayId'] = this.displayId;
    data['displayName'] = this.displayName;
    return data;
  }
}
