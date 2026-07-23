import 'package:savbill/pages/customer/model/response/services_area_res.dart';
import 'package:savbill/webservices/base_response.dart';

class DeviceListRes extends BaseResponse {
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  DeviceDetail? data;
  List<DeviceDetail>? dataList;

  DeviceListRes(
      {responseCode,
      responseMessage,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages,
      this.dataList,
      this.data});

  DeviceListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    data =
        json['data'] != null ? new DeviceDetail.fromJson(json['data']) : null;
    if (json['dataList'] != null) {
      dataList = <DeviceDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new DeviceDetail.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    if (this.data != null) {
      data['data'] = this.data!.toJson();
    }
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class DeviceDetail {
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
  int? mvnoId;
  int? availableInPorts;
  int? totalInPorts;
  int? availableOutPorts;
  int? totalOutPorts;
  int? itemId;
  int? custInventoryId;
  int? inventorymappingId;
  ServicesAreaDetail? servicearea;
  List<ServicesAreaDetail>? serviceAreaNameList;

  DeviceDetail(
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
      this.mvnoId,
      this.availableInPorts,
      this.totalInPorts,
      this.availableOutPorts,
      this.totalOutPorts,
      this.itemId,
      this.custInventoryId,
      this.inventorymappingId,
      this.servicearea,
      this.serviceAreaNameList});

  DeviceDetail.fromJson(Map<String, dynamic> json) {
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
    mvnoId = json['mvnoId'];
    availableInPorts = json['availableInPorts'];
    totalInPorts = json['totalInPorts'];
    availableOutPorts = json['availableOutPorts'];
    totalOutPorts = json['totalOutPorts'];
    itemId = json['itemId'];
    custInventoryId = json['custInventoryId'];
    inventorymappingId = json['inventorymappingId'];
    servicearea = json['servicearea'] != null
        ? new ServicesAreaDetail.fromJson(json['servicearea'])
        : null;
    if (json['serviceAreaNameList'] != null) {
      serviceAreaNameList = <ServicesAreaDetail>[];
      json['serviceAreaNameList'].forEach((v) {
        serviceAreaNameList!.add(new ServicesAreaDetail.fromJson(v));
      });
    }
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
    data['mvnoId'] = this.mvnoId;
    data['availableInPorts'] = this.availableInPorts;
    data['totalInPorts'] = this.totalInPorts;
    data['availableOutPorts'] = this.availableOutPorts;
    data['totalOutPorts'] = this.totalOutPorts;
    data['itemId'] = this.itemId;
    data['custInventoryId'] = this.custInventoryId;
    data['inventorymappingId'] = this.inventorymappingId;
    if (this.servicearea != null) {
      data['servicearea'] = this.servicearea!.toJson();
    }
    if (this.serviceAreaNameList != null) {
      data['serviceAreaNameList'] =
          this.serviceAreaNameList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}
