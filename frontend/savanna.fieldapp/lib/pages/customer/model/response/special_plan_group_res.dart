class SpecialPlanGroupRes {
  List<PostpaidplanList>? postpaidplanList;
  String? timestamp;
  String? errorMsg;
  dynamic status;

  SpecialPlanGroupRes({this.postpaidplanList, this.timestamp, this.status});

  SpecialPlanGroupRes.fromJson(Map<String, dynamic> json) {
    if (json['postpaidplanList'] != null) {
      postpaidplanList = <PostpaidplanList>[];
      json['postpaidplanList'].forEach((v) {
        postpaidplanList!.add(new PostpaidplanList.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    errorMsg = json['ERROR'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.postpaidplanList != null) {
      data['postpaidplanList'] =
          this.postpaidplanList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class PostpaidplanList {
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
  dynamic saccode;
  String? maxconcurrentsession;
  dynamic quotaunittime;
  dynamic quotatime;
  String? quotatype;
  dynamic offerprice;
  dynamic qospolicyid;
  dynamic qospolicyName;
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
  dynamic newOfferPrice;
  dynamic nextTeamHierarchyMapping;
  bool? allowdiscount;
  dynamic productCategory;
  dynamic productType;
  dynamic productId;
  dynamic discount;
  dynamic ownershipType;
  bool? invoiceToOrg;
  bool? requiredApproval;
  dynamic bandwidth;
  dynamic linkType;
  dynamic connectionType;
  dynamic distance;
  dynamic storage;
  dynamic storageType;
  dynamic autoBackup;
  dynamic location;
  dynamic quantity;
  dynamic packageType;
  dynamic numberOfDays;
  dynamic noOfUsers;
  dynamic rackSpace;
  dynamic rackUnit;
  dynamic powerConsumption;
  dynamic networkCard;
  dynamic noOfLicense;
  dynamic noOfEmailUserLicense;
  dynamic noOfServerLicense;
  dynamic noOfUserLicense;
  dynamic noOfNodes;
  dynamic eventPerSecond;
  dynamic noOfAdditionalServer;
  dynamic noOfAdditionalStorage;
  dynamic additionalStorageType;
  dynamic noOfNodesLicense;
  dynamic hardwareResource;
  dynamic manpower;
  dynamic noOfDomains;
  dynamic securityModules;
  dynamic hardwareOrServers;
  dynamic country;
  dynamic noOfVPN;
  dynamic deviceThroughput;
  dynamic retail;
  dynamic installationCharge;
  dynamic supportCharge;
  dynamic feasibility;
  dynamic poc;
  dynamic installation;
  dynamic provisioning;
  dynamic supportchargeteamid;
  dynamic installationchargeteamid;
  dynamic feasibilityteamid;
  dynamic pocteamid;
  dynamic installationteamid;
  dynamic provisioningteamid;
  dynamic priceeditable;
  dynamic accessibility;
  dynamic ram;
  dynamic cpu;
  dynamic cpanel;
  dynamic iporIPPool;
  dynamic epslicense;

  PostpaidplanList(
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
        this.bandwidth,
        this.linkType,
        this.connectionType,
        this.distance,
        this.storage,
        this.storageType,
        this.autoBackup,
        this.location,
        this.quantity,
        this.packageType,
        this.numberOfDays,
        this.noOfUsers,
        this.rackSpace,
        this.rackUnit,
        this.powerConsumption,
        this.networkCard,
        this.noOfLicense,
        this.noOfEmailUserLicense,
        this.noOfServerLicense,
        this.noOfUserLicense,
        this.noOfNodes,
        this.eventPerSecond,
        this.noOfAdditionalServer,
        this.noOfAdditionalStorage,
        this.additionalStorageType,
        this.noOfNodesLicense,
        this.hardwareResource,
        this.manpower,
        this.noOfDomains,
        this.securityModules,
        this.hardwareOrServers,
        this.country,
        this.noOfVPN,
        this.deviceThroughput,
        this.retail,
        this.installationCharge,
        this.supportCharge,
        this.feasibility,
        this.poc,
        this.installation,
        this.provisioning,
        this.supportchargeteamid,
        this.installationchargeteamid,
        this.feasibilityteamid,
        this.pocteamid,
        this.installationteamid,
        this.provisioningteamid,
        this.priceeditable,
        this.accessibility,
        this.ram,
        this.cpu,
        this.cpanel,
        this.iporIPPool,
        this.epslicense});

  PostpaidplanList.fromJson(Map<String, dynamic> json) {
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
        serviceAreaNameList!.add(ServiceAreaNameList.fromJson(v));
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

    bandwidth = json['bandwidth'];
    linkType = json['linkType'];
    connectionType = json['connectionType'];
    distance = json['distance'];
    storage = json['storage'];
    storageType = json['storageType'];
    autoBackup = json['autoBackup'];
    location = json['location'];
    quantity = json['quantity'];
    packageType = json['packageType'];
    numberOfDays = json['numberOfDays'];
    noOfUsers = json['noOfUsers'];
    rackSpace = json['rackSpace'];
    rackUnit = json['rackUnit'];
    powerConsumption = json['powerConsumption'];
    networkCard = json['networkCard'];
    noOfLicense = json['noOfLicense'];
    noOfEmailUserLicense = json['noOfEmailUserLicense'];
    noOfServerLicense = json['noOfServerLicense'];
    noOfUserLicense = json['noOfUserLicense'];
    noOfNodes = json['noOfNodes'];
    eventPerSecond = json['eventPerSecond'];
    noOfAdditionalServer = json['noOfAdditionalServer'];
    noOfAdditionalStorage = json['noOfAdditionalStorage'];
    additionalStorageType = json['additionalStorageType'];
    noOfNodesLicense = json['noOfNodesLicense'];
    hardwareResource = json['hardwareResource'];
    manpower = json['manpower'];
    noOfDomains = json['noOfDomains'];
    securityModules = json['securityModules'];
    hardwareOrServers = json['hardwareOrServers'];
    country = json['country'];
    noOfVPN = json['noOfVPN'];
    deviceThroughput = json['deviceThroughput'];
    retail = json['retail'];
    installationCharge = json['installation_charge'];
    supportCharge = json['support_charge'];
    feasibility = json['feasibility'];
    poc = json['poc'];
    installation = json['installation'];
    provisioning = json['provisioning'];
    supportchargeteamid = json['supportchargeteamid'];
    installationchargeteamid = json['installationchargeteamid'];
    feasibilityteamid = json['feasibilityteamid'];
    pocteamid = json['pocteamid'];
    installationteamid = json['installationteamid'];
    provisioningteamid = json['provisioningteamid'];
    priceeditable = json['priceeditable'];
    accessibility = json['accessibility'];
    ram = json['ram'];
    cpu = json['cpu'];
    cpanel = json['cpanel'];
    iporIPPool = json['iporIPPool'];
    epslicense = json['epslicense'];
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

    data['bandwidth'] = this.bandwidth;
    data['linkType'] = this.linkType;
    data['connectionType'] = this.connectionType;
    data['distance'] = this.distance;
    data['storage'] = this.storage;
    data['storageType'] = this.storageType;
    data['autoBackup'] = this.autoBackup;
    data['location'] = this.location;
    data['quantity'] = this.quantity;
    data['packageType'] = this.packageType;
    data['numberOfDays'] = this.numberOfDays;
    data['noOfUsers'] = this.noOfUsers;
    data['rackSpace'] = this.rackSpace;
    data['rackUnit'] = this.rackUnit;
    data['powerConsumption'] = this.powerConsumption;
    data['networkCard'] = this.networkCard;
    data['noOfLicense'] = this.noOfLicense;
    data['noOfEmailUserLicense'] = this.noOfEmailUserLicense;
    data['noOfServerLicense'] = this.noOfServerLicense;
    data['noOfUserLicense'] = this.noOfUserLicense;
    data['noOfNodes'] = this.noOfNodes;
    data['eventPerSecond'] = this.eventPerSecond;
    data['noOfAdditionalServer'] = this.noOfAdditionalServer;
    data['noOfAdditionalStorage'] = this.noOfAdditionalStorage;
    data['additionalStorageType'] = this.additionalStorageType;
    data['noOfNodesLicense'] = this.noOfNodesLicense;
    data['hardwareResource'] = this.hardwareResource;
    data['manpower'] = this.manpower;
    data['noOfDomains'] = this.noOfDomains;
    data['securityModules'] = this.securityModules;
    data['hardwareOrServers'] = this.hardwareOrServers;
    data['country'] = this.country;
    data['noOfVPN'] = this.noOfVPN;
    data['deviceThroughput'] = this.deviceThroughput;
    data['retail'] = this.retail;
    data['installation_charge'] = this.installationCharge;
    data['support_charge'] = this.supportCharge;
    data['feasibility'] = this.feasibility;
    data['poc'] = this.poc;
    data['installation'] = this.installation;
    data['provisioning'] = this.provisioning;
    data['supportchargeteamid'] = this.supportchargeteamid;
    data['installationchargeteamid'] = this.installationchargeteamid;
    data['feasibilityteamid'] = this.feasibilityteamid;
    data['pocteamid'] = this.pocteamid;
    data['installationteamid'] = this.installationteamid;
    data['provisioningteamid'] = this.provisioningteamid;
    data['priceeditable'] = this.priceeditable;
    data['accessibility'] = this.accessibility;
    data['ram'] = this.ram;
    data['cpu'] = this.cpu;
    data['cpanel'] = this.cpanel;
    data['iporIPPool'] = this.iporIPPool;
    data['epslicense'] = this.epslicense;
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
  dynamic discountid;
  dynamic dbr;
  dynamic actualprice;
  bool? isDelete;
  String? chargecategory;
  String? saccode;
  dynamic taxamount;
  dynamic mvnoId;
  dynamic buId;
  String? status;
  dynamic ledgerId;
  bool? royaltyPayable;
  dynamic serviceid;
  List<Null>? servicesid;
  List<Null>? serviceNameList;
  dynamic services;

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
        this.servicesid,
        this.serviceNameList,
        this.services});

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
  dynamic areaid;
  dynamic mvnoId;
  dynamic pincodes;
  dynamic cityid;

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
        this.cityid});

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
    return data;
  }
}
