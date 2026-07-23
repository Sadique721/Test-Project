import 'package:savbill/webservices/base_response.dart';

class ServiceAreaPlanModeRes extends BaseResponse{
  List<ServiceAreaPlanPostpaidplanList>? postpaidplanList;
  String? timestamp;
  int? status;

  ServiceAreaPlanModeRes({this.postpaidplanList, this.timestamp, this.status});

  ServiceAreaPlanModeRes.fromJson(Map<String, dynamic> json) {
    if (json['postpaidplanList'] != null) {
      postpaidplanList = <ServiceAreaPlanPostpaidplanList>[];
      json['postpaidplanList'].forEach((v) {
        postpaidplanList!.add(new ServiceAreaPlanPostpaidplanList.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.postpaidplanList != null) {
      data['postpaidplanList'] =
          this.postpaidplanList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class ServiceAreaPlanPostpaidplanList {
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
  String? invoiceType;
  String? startDate;
  String? endDate;
  dynamic uploadQOS;
  dynamic downloadQOS;
  dynamic uploadTs;
  dynamic downloadTs;
  bool? allowOverUsage;
  String? quotaUnit;
  dynamic quota;
  String? planStatus;
  dynamic childQuota;
  dynamic childQuotaUnit;
  dynamic slice;
  dynamic sliceUnit;
  dynamic attachedToAllHotSpots;
  dynamic param1;
  dynamic param2;
  dynamic param3;
  dynamic mvnoId;
  String? status;
  dynamic taxId;
  dynamic serviceId;
  dynamic serviceName;
  dynamic timebasepolicyId;
  dynamic timebasepolicyName;
  String? plantype;
  dynamic maxChild;
  List<ChargeList>? chargeList;
  double? dbr;
  String? planGroup;
  dynamic validity;
  String? saccode;
  String? maxconcurrentsession;
  String? quotaunittime;
  dynamic quotatime;
  String? quotatype;
  double? offerprice;
  dynamic qospolicyid;
  String? qospolicyName;
  bool? isDelete;
  String? createDateString;
  String? updateDateString;
  dynamic quotadid;
  dynamic quotaintercom;
  dynamic quotaunitdid;
  dynamic quotaunitintercom;
  dynamic dataCategory;
  double? taxamount;
  List<ServiceAreaNameList>? serviceAreaNameList;
  String? quotaResetInterval;
  String? mode;
  String? unitsOfValidity;
  dynamic buId;
  dynamic nextStaff;
  double? newOfferPrice;
  dynamic nextTeamHierarchyMapping;
  bool? allowdiscount;
  dynamic productCategory;
  dynamic productType;
  dynamic productId;
  dynamic discount;
  dynamic ownershipType;
  bool? invoiceToOrg;
  bool? requiredApproval;
  List<PlanCasMappingList>? planCasMappingList;
  String? bandwidth;
  dynamic linkType;
  String? connectionType;
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
  String? displayPostpaidName;
  dynamic planQosMappingEntityList;
  dynamic viewplanQosMappingEntityList;
  String? businessType;
  bool? basePlan;
  String? accessibility;

  ServiceAreaPlanPostpaidplanList(
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
        this.invoiceType,
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
        this.isDelete,
        this.createDateString,
        this.updateDateString,
        this.quotadid,
        this.quotaintercom,
        this.quotaunitdid,
        this.quotaunitintercom,
        this.dataCategory,
        this.taxamount,
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
        this.invoiceToOrg,
        this.requiredApproval,
        this.planCasMappingList,
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
        this.accessibility});

  ServiceAreaPlanPostpaidplanList.fromJson(Map<String, dynamic> json) {
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
    invoiceType = json['invoiceType'];
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

    isDelete = json['isDelete'];
    createDateString = json['createDateString'];
    updateDateString = json['updateDateString'];
    quotadid = json['quotadid'];
    quotaintercom = json['quotaintercom'];
    quotaunitdid = json['quotaunitdid'];
    quotaunitintercom = json['quotaunitintercom'];
    dataCategory = json['dataCategory'];
    taxamount = json['taxamount'];

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
    invoiceToOrg = json['invoiceToOrg'];
    requiredApproval = json['requiredApproval'];
    if (json['planCasMappingList'] != null) {
      planCasMappingList = <PlanCasMappingList>[];
      json['planCasMappingList'].forEach((v) {
        planCasMappingList!.add(new PlanCasMappingList.fromJson(v));
      });
    }
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
    data['invoiceType'] = this.invoiceType;
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

    data['isDelete'] = this.isDelete;
    data['createDateString'] = this.createDateString;
    data['updateDateString'] = this.updateDateString;
    data['quotadid'] = this.quotadid;
    data['quotaintercom'] = this.quotaintercom;
    data['quotaunitdid'] = this.quotaunitdid;
    data['quotaunitintercom'] = this.quotaunitintercom;
    data['dataCategory'] = this.dataCategory;
    data['taxamount'] = this.taxamount;

    if (this.serviceAreaNameList != null) {
      data['serviceAreaNameList'] =
          this.serviceAreaNameList!.map((v) => v.toJson()).toList();
    }
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
    data['invoiceToOrg'] = this.invoiceToOrg;
    data['requiredApproval'] = this.requiredApproval;
    if (this.planCasMappingList != null) {
      data['planCasMappingList'] =
          this.planCasMappingList!.map((v) => v.toJson()).toList();
    }
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

  ChargeList(
      {this.id,
        this.charge,
        this.billingCycle,
        this.createdate,
        this.chargeprice});

  ChargeList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    charge =
    json['charge'] != null ? new Charge.fromJson(json['charge']) : null;
    billingCycle = json['billingCycle'];
    createdate = json['createdate'];
    chargeprice = json['chargeprice'];
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
  dynamic taxid;
  String? taxName;
  Null? discountid;
  dynamic dbr;
  dynamic actualprice;
  bool? isDelete;
  String? chargecategory;
  String? saccode;
  Null? taxamount;
  dynamic mvnoId;
  dynamic buId;
  String? status;
  String? ledgerId;
  bool? royaltyPayable;
  Null? serviceid;
  Null? services;
  dynamic displayId;
  String? displayName;
  String? businessType;
  String? pushableLedgerId;

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
        this.services,
        this.displayId,
        this.displayName,
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
    services = json['services'];
    displayId = json['displayId'];
    displayName = json['displayName'];
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
    data['services'] = this.services;
    data['displayId'] = this.displayId;
    data['displayName'] = this.displayName;
    data['businessType'] = this.businessType;
    data['pushableLedgerId'] = this.pushableLedgerId;
    return data;
  }
}

class ServiceAreaNameList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? name;
  String? status;
  bool? isDeleted;
  String? latitude;
  String? longitude;
  Null? areaid;
  dynamic mvnoId;
  Null? pincodes;
  dynamic cityid;
  Null? displayId;
  Null? displayName;

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

class PlanCasMappingList {
  dynamic id;
  dynamic planId;
  dynamic casId;
  dynamic packageId;

  PlanCasMappingList({this.id, this.planId, this.casId, this.packageId});

  PlanCasMappingList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    planId = json['planId'];
    casId = json['casId'];
    packageId = json['packageId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['planId'] = this.planId;
    data['casId'] = this.casId;
    data['packageId'] = this.packageId;
    return data;
  }
}







