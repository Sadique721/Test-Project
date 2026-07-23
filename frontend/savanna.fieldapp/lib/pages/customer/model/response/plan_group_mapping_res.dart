import 'package:savbill/pages/customer/model/response/postpaid_planlist_res.dart';
import 'package:savbill/webservices/base_response.dart';

class PlanGroupMappingRes extends BaseResponse {
  List<PlanGroupMappingDetail>? planGroupMappingList;

  PlanGroupMappingRes({this.planGroupMappingList, timestamp, status});

  PlanGroupMappingRes.fromJson(Map<String, dynamic> json) {
    if (json['planGroupMappingList'] != null) {
      planGroupMappingList = <PlanGroupMappingDetail>[];
      json['planGroupMappingList'].forEach((v) {
        planGroupMappingList!.add(new PlanGroupMappingDetail.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.planGroupMappingList != null) {
      data['planGroupMappingList'] =
          this.planGroupMappingList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class PlanGroupMappingDetail {
  // String? createdate;
  // String? updatedate;
  // String? createdByName;
  // String? lastModifiedByName;
  // int? createdById;
  // int? lastModifiedById;
  // dynamic planGroupMappingId;
  // PostpaidPlanDetail? plan;

  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic planGroupMappingId;
  PlanGroupMappingPlan? plan;
  String? service;
  bool? isDelete;
  dynamic mvnoId;
  dynamic validity;
  dynamic newofferprice;

  // PlanGroupMappingDetail(
  //     {this.createdate,
  //       this.updatedate,
  //       this.createdByName,
  //       this.lastModifiedByName,
  //       this.createdById,
  //       this.lastModifiedById,
  //       this.planGroupMappingId,
  //       this.plan});

  PlanGroupMappingDetail(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.planGroupMappingId,
      this.plan,
      this.service,
      this.isDelete,
      this.mvnoId,
      this.validity,
      this.newofferprice});

  /*PlanGroupMappingDetail.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    planGroupMappingId = json['planGroupMappingId'];
    plan = json['plan'] != null ? new PostpaidPlanDetail.fromJson(json['plan']) : null;
  }*/

  PlanGroupMappingDetail.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    planGroupMappingId = json['planGroupMappingId'];
    plan = json['plan'] != null
        ? new PlanGroupMappingPlan.fromJson(json['plan'])
        : null;
    service = json['service'];
    isDelete = json['isDelete'];
    mvnoId = json['mvnoId'];
    validity = json['validity'];
    newofferprice = json['newofferprice'];
  }

/*  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['planGroupMappingId'] = this.planGroupMappingId;
    if (this.plan != null) {
      data['plan'] = this.plan!.toJson();
    }
    return data;
  }*/

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['planGroupMappingId'] = this.planGroupMappingId;
    if (this.plan != null) {
      data['plan'] = this.plan!.toJson();
    }
    data['service'] = this.service;
    data['isDelete'] = this.isDelete;
    data['mvnoId'] = this.mvnoId;
    data['validity'] = this.validity;
    data['newofferprice'] = this.newofferprice;
    return data;
  }
}

class PlanGroupMappingPlan {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? name;
  String? displayName;
  String? code;
  String? desc;
  String? category;
  dynamic maxChild;
  String? startDate;
  String? endDate;
  dynamic quota;
  String? quotaUnit;
  dynamic uploadQOS;
  dynamic downloadQOS;
  dynamic uploadTs;
  dynamic downloadTs;
  bool? allowOverUsage;
  String? status;
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
  dynamic taxId;
  dynamic serviceId;
  dynamic timebasepolicyId;
  String? plantype;
  double? dbr;
  List<ChargeList>? chargeList;
  String? planGroup;
  dynamic validity;
  dynamic saccode;
  String? maxconcurrentsession;
  dynamic quotaunittime;
  dynamic quotatime;
  String? quotatype;
  dynamic offerprice;
  dynamic quotadid;
  dynamic quotaintercom;
  dynamic quotaunitdid;
  dynamic quotaunitintercom;
  bool? isDelete;
  dynamic dataCategory;
  dynamic taxamount;
  dynamic serviceName;
  dynamic timebasepolicyName;
  String? quotaResetInterval;
  String? mode;
  String? unitsOfValidity;
  dynamic buId;
  dynamic nextTeamHierarchyMapping;
  dynamic nextStaff;
  dynamic newOfferPrice;
  bool? allowdiscount;
  dynamic productId;
  bool? invoiceToOrg;
  bool? requiredApproval;
  String? bandwidth;
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
  String? businessType;
  bool? basePlan;
  dynamic templateId;
  dynamic planQosMappingEntityList;
  dynamic accessibility;

  PlanGroupMappingPlan(
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
      this.maxChild,
      this.startDate,
      this.endDate,
      this.quota,
      this.quotaUnit,
      this.uploadQOS,
      this.downloadQOS,
      this.uploadTs,
      this.downloadTs,
      this.allowOverUsage,
      this.status,
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
      this.taxId,
      this.serviceId,
      this.timebasepolicyId,
      this.plantype,
      this.dbr,
      this.chargeList,
      this.planGroup,
      this.validity,
      this.saccode,
      this.maxconcurrentsession,
      this.quotaunittime,
      this.quotatime,
      this.quotatype,
      this.offerprice,
      this.quotadid,
      this.quotaintercom,
      this.quotaunitdid,
      this.quotaunitintercom,
      this.isDelete,
      this.dataCategory,
      this.taxamount,
      this.serviceName,
      this.timebasepolicyName,
      this.quotaResetInterval,
      this.mode,
      this.unitsOfValidity,
      this.buId,
      this.nextTeamHierarchyMapping,
      this.nextStaff,
      this.newOfferPrice,
      this.allowdiscount,
      this.productId,
      this.invoiceToOrg,
      this.requiredApproval,
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
      this.businessType,
      this.basePlan,
      this.templateId,
      this.planQosMappingEntityList,
      this.accessibility});

  PlanGroupMappingPlan.fromJson(Map<String, dynamic> json) {
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
    maxChild = json['maxChild'];
    startDate = json['startDate'];
    endDate = json['endDate'];
    quota = json['quota'];
    quotaUnit = json['quotaUnit'];
    uploadQOS = json['uploadQOS'];
    downloadQOS = json['downloadQOS'];
    uploadTs = json['uploadTs'];
    downloadTs = json['downloadTs'];
    allowOverUsage = json['allowOverUsage'];
    status = json['status'];
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
    taxId = json['taxId'];
    serviceId = json['serviceId'];
    timebasepolicyId = json['timebasepolicyId'];
    plantype = json['plantype'];
    dbr = json['dbr'];
    if (json['chargeList'] != null) {
      chargeList = <ChargeList>[];
      json['chargeList'].forEach((v) {
        chargeList!.add(new ChargeList.fromJson(v));
      });
    }
    planGroup = json['planGroup'];
    validity = json['validity'];
    saccode = json['saccode'];
    maxconcurrentsession = json['maxconcurrentsession'];
    quotaunittime = json['quotaunittime'];
    quotatime = json['quotatime'];
    quotatype = json['quotatype'];
    offerprice = json['offerprice'];
    quotadid = json['quotadid'];
    quotaintercom = json['quotaintercom'];
    quotaunitdid = json['quotaunitdid'];
    quotaunitintercom = json['quotaunitintercom'];
    isDelete = json['isDelete'];
    dataCategory = json['dataCategory'];
    taxamount = json['taxamount'];
    serviceName = json['serviceName'];
    timebasepolicyName = json['timebasepolicyName'];
    quotaResetInterval = json['quotaResetInterval'];
    mode = json['mode'];
    unitsOfValidity = json['unitsOfValidity'];
    buId = json['buId'];
    nextTeamHierarchyMapping = json['nextTeamHierarchyMapping'];
    nextStaff = json['nextStaff'];
    newOfferPrice = json['newOfferPrice'];
    allowdiscount = json['allowdiscount'];
    productId = json['productId'];
    invoiceToOrg = json['invoiceToOrg'];
    requiredApproval = json['requiredApproval'];
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
    businessType = json['businessType'];
    basePlan = json['basePlan'];
    templateId = json['templateId'];
    planQosMappingEntityList = json['planQosMappingEntityList'];
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
    data['maxChild'] = this.maxChild;
    data['startDate'] = this.startDate;
    data['endDate'] = this.endDate;
    data['quota'] = this.quota;
    data['quotaUnit'] = this.quotaUnit;
    data['uploadQOS'] = this.uploadQOS;
    data['downloadQOS'] = this.downloadQOS;
    data['uploadTs'] = this.uploadTs;
    data['downloadTs'] = this.downloadTs;
    data['allowOverUsage'] = this.allowOverUsage;
    data['status'] = this.status;
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
    data['taxId'] = this.taxId;
    data['serviceId'] = this.serviceId;
    data['timebasepolicyId'] = this.timebasepolicyId;
    data['plantype'] = this.plantype;
    data['dbr'] = this.dbr;
    if (this.chargeList != null) {
      data['chargeList'] = this.chargeList!.map((v) => v.toJson()).toList();
    }
    data['planGroup'] = this.planGroup;
    data['validity'] = this.validity;
    data['saccode'] = this.saccode;
    data['maxconcurrentsession'] = this.maxconcurrentsession;
    data['quotaunittime'] = this.quotaunittime;
    data['quotatime'] = this.quotatime;
    data['quotatype'] = this.quotatype;
    data['offerprice'] = this.offerprice;
    data['quotadid'] = this.quotadid;
    data['quotaintercom'] = this.quotaintercom;
    data['quotaunitdid'] = this.quotaunitdid;
    data['quotaunitintercom'] = this.quotaunitintercom;
    data['isDelete'] = this.isDelete;
    data['dataCategory'] = this.dataCategory;
    data['taxamount'] = this.taxamount;
    data['serviceName'] = this.serviceName;
    data['timebasepolicyName'] = this.timebasepolicyName;
    data['quotaResetInterval'] = this.quotaResetInterval;
    data['mode'] = this.mode;
    data['unitsOfValidity'] = this.unitsOfValidity;
    data['buId'] = this.buId;
    data['nextTeamHierarchyMapping'] = this.nextTeamHierarchyMapping;
    data['nextStaff'] = this.nextStaff;
    data['newOfferPrice'] = this.newOfferPrice;
    data['allowdiscount'] = this.allowdiscount;
    data['productId'] = this.productId;
    data['invoiceToOrg'] = this.invoiceToOrg;
    data['requiredApproval'] = this.requiredApproval;
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
    data['businessType'] = this.businessType;
    data['basePlan'] = this.basePlan;
    data['templateId'] = this.templateId;
    data['planQosMappingEntityList'] = this.planQosMappingEntityList;
    data['accessibility'] = this.accessibility;
    return data;
  }
}

class ChargeList {
  dynamic id;
  Charge? charge;
  dynamic billingCycle;
  String? createdate;
  dynamic chargeprice;
  String? chargeName;

  ChargeList(
      {this.id,
      this.charge,
      this.billingCycle,
      this.createdate,
      this.chargeprice,
      this.chargeName});

  ChargeList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    charge =
        json['charge'] != null ? new Charge.fromJson(json['charge']) : null;
    billingCycle = json['billingCycle'];
    createdate = json['createdate'];
    chargeprice = json['chargeprice'];
    chargeName = json['chargeName'];
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
    data['chargeName'] = this.chargeName;
    return data;
  }
}

class Charge {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? name;
  String? desc;
  String? chargetype;
  dynamic price;
  dynamic actualprice;
  Tax? tax;
  Null? discountid;
  dynamic dbr;
  bool? isDelete;
  String? chargecategory;
  Null? saccode;
  List<ServiceList>? serviceList;
  dynamic mvnoId;
  dynamic buId;
  Null? service;
  String? status;
  Null? ledgerId;
  bool? royaltyPayable;
  Null? taxamount;
  String? businessType;
  Null? pushableLedgerId;

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
      this.actualprice,
      this.tax,
      this.discountid,
      this.dbr,
      this.isDelete,
      this.chargecategory,
      this.saccode,
      this.serviceList,
      this.mvnoId,
      this.buId,
      this.service,
      this.status,
      this.ledgerId,
      this.royaltyPayable,
      this.taxamount,
      this.businessType,
      this.pushableLedgerId});

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
    actualprice = json['actualprice'];
    tax = json['tax'] != null ? new Tax.fromJson(json['tax']) : null;
    discountid = json['discountid'];
    dbr = json['dbr'];
    isDelete = json['isDelete'];
    chargecategory = json['chargecategory'];
    saccode = json['saccode'];
    if (json['serviceList'] != null) {
      serviceList = <ServiceList>[];
      json['serviceList'].forEach((v) {
        serviceList!.add(new ServiceList.fromJson(v));
      });
    }
    mvnoId = json['mvnoId'];
    buId = json['buId'];
    service = json['service'];
    status = json['status'];
    ledgerId = json['ledgerId'];
    royaltyPayable = json['royalty_payable'];
    taxamount = json['taxamount'];
    businessType = json['businessType'];
    pushableLedgerId = json['pushableLedgerId'];
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
    data['actualprice'] = this.actualprice;
    if (this.tax != null) {
      data['tax'] = this.tax!.toJson();
    }
    data['discountid'] = this.discountid;
    data['dbr'] = this.dbr;
    data['isDelete'] = this.isDelete;
    data['chargecategory'] = this.chargecategory;
    data['saccode'] = this.saccode;
    if (this.serviceList != null) {
      data['serviceList'] = this.serviceList!.map((v) => v.toJson()).toList();
    }
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    data['service'] = this.service;
    data['status'] = this.status;
    data['ledgerId'] = this.ledgerId;
    data['royalty_payable'] = this.royaltyPayable;
    data['taxamount'] = this.taxamount;
    data['businessType'] = this.businessType;
    data['pushableLedgerId'] = this.pushableLedgerId;
    return data;
  }
}

class Tax {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? name;
  String? desc;
  String? taxtype;
  String? status;
  dynamic mvnoId;
  dynamic buId;
  List<TieredList>? tieredList;
  bool? isDelete;

  Tax(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.name,
      this.desc,
      this.taxtype,
      this.status,
      this.mvnoId,
      this.buId,
      this.tieredList,
      this.isDelete});

  Tax.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    desc = json['desc'];
    taxtype = json['taxtype'];
    status = json['status'];
    mvnoId = json['mvnoId'];
    buId = json['buId'];
    if (json['tieredList'] != null) {
      tieredList = <TieredList>[];
      json['tieredList'].forEach((v) {
        tieredList!.add(new TieredList.fromJson(v));
      });
    }
    isDelete = json['isDelete'];
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
    data['taxtype'] = this.taxtype;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    if (this.tieredList != null) {
      data['tieredList'] = this.tieredList!.map((v) => v.toJson()).toList();
    }
    data['isDelete'] = this.isDelete;
    return data;
  }
}

class TieredList {
  dynamic id;
  String? name;
  String? taxGroup;
  dynamic rate;
  bool? isDelete;
  bool? beforeDiscount;
  String? taxLedgerId;

  TieredList(
      {this.id,
      this.name,
      this.taxGroup,
      this.rate,
      this.isDelete,
      this.beforeDiscount,
      this.taxLedgerId});

  TieredList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    taxGroup = json['taxGroup'];
    rate = json['rate'];
    isDelete = json['isDelete'];
    beforeDiscount = json['beforeDiscount'];
    taxLedgerId = json['taxLedgerId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['taxGroup'] = this.taxGroup;
    data['rate'] = this.rate;
    data['isDelete'] = this.isDelete;
    data['beforeDiscount'] = this.beforeDiscount;
    data['taxLedgerId'] = this.taxLedgerId;
    return data;
  }
}

class ServiceList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? serviceName;
  dynamic mvnoId;

  ServiceList(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.serviceName,
      this.mvnoId});

  ServiceList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    serviceName = json['serviceName'];
    mvnoId = json['mvnoId'];
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
    data['serviceName'] = this.serviceName;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}
