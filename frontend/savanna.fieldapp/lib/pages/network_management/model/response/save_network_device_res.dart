class SaveNetworkDeviceRes {
  int? responseCode;
  dynamic responseMessage;
  Data? data;
  dynamic dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  SaveNetworkDeviceRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  SaveNetworkDeviceRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null ? new Data.fromJson(json['data']) : null;
    dataList = json['dataList'];
    excelDataList = json['excelDataList'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    if (this.data != null) {
      data['data'] = this.data!.toJson();
    }
    data['dataList'] = this.dataList;
    data['excelDataList'] = this.excelDataList;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class Data {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  int? productId;
  int? inwardId;
  String? devicetype;
  String? status;
  String? latitude;
  String? longitude;
  bool? isDeleted;
  dynamic servicearea;
  dynamic serviceAreaIdsList;
  List<ServiceAreaNameList>? serviceAreaNameList;
  int? mvnoId;
  dynamic availableInPorts;
  dynamic totalInPorts;
  dynamic availableOutPorts;
  dynamic totalOutPorts;
  int? totalPorts;
  int? availablePorts;
  dynamic itemId;
  dynamic custInventoryId;
  dynamic inventorymappingId;
  dynamic productName;

  Data(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.productId,
        this.inwardId,
        this.devicetype,
        this.status,
        this.latitude,
        this.longitude,
        this.isDeleted,
        this.servicearea,
        this.serviceAreaIdsList,
        this.serviceAreaNameList,
        this.mvnoId,
        this.availableInPorts,
        this.totalInPorts,
        this.availableOutPorts,
        this.totalOutPorts,
        this.totalPorts,
        this.availablePorts,
        this.itemId,
        this.custInventoryId,
        this.inventorymappingId,
        this.productName});

  Data.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    productId = json['productId'];
    inwardId = json['inwardId'];
    devicetype = json['devicetype'];
    status = json['status'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    isDeleted = json['isDeleted'];
    servicearea = json['servicearea'];
    serviceAreaIdsList = json['serviceAreaIdsList'];
    if (json['serviceAreaNameList'] != null) {
      serviceAreaNameList = <ServiceAreaNameList>[];
      json['serviceAreaNameList'].forEach((v) {
        serviceAreaNameList!.add(new ServiceAreaNameList.fromJson(v));
      });
    }
    mvnoId = json['mvnoId'];
    availableInPorts = json['availableInPorts'];
    totalInPorts = json['totalInPorts'];
    availableOutPorts = json['availableOutPorts'];
    totalOutPorts = json['totalOutPorts'];
    totalPorts = json['totalPorts'];
    availablePorts = json['availablePorts'];
    itemId = json['itemId'];
    custInventoryId = json['custInventoryId'];
    inventorymappingId = json['inventorymappingId'];
    productName = json['productName'];
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
    data['productId'] = this.productId;
    data['inwardId'] = this.inwardId;
    data['devicetype'] = this.devicetype;
    data['status'] = this.status;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['isDeleted'] = this.isDeleted;
    data['servicearea'] = this.servicearea;
    data['serviceAreaIdsList'] = this.serviceAreaIdsList;
    if (this.serviceAreaNameList != null) {
      data['serviceAreaNameList'] =
          this.serviceAreaNameList!.map((v) => v.toJson()).toList();
    }
    data['mvnoId'] = this.mvnoId;
    data['availableInPorts'] = this.availableInPorts;
    data['totalInPorts'] = this.totalInPorts;
    data['availableOutPorts'] = this.availableOutPorts;
    data['totalOutPorts'] = this.totalOutPorts;
    data['totalPorts'] = this.totalPorts;
    data['availablePorts'] = this.availablePorts;
    data['itemId'] = this.itemId;
    data['custInventoryId'] = this.custInventoryId;
    data['inventorymappingId'] = this.inventorymappingId;
    data['productName'] = this.productName;
    return data;
  }
}

class ServiceAreaNameList {
  dynamic createdate;
  String? updatedate;
  dynamic createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? status;
  bool? isDeleted;
  dynamic latitude;
  dynamic longitude;
  dynamic areaid;
  int? mvnoId;
  dynamic pincodes;
  int? cityid;
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
