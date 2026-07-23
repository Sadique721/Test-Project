import 'package:savbill/pages/inventory/module/response/view_inwards_list_res.dart';
import 'package:savbill/webservices/base_response.dart';

class ExternalGroupListRes extends BaseResponse {
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  List<ExternalGroupDetail>? dataList;

  ExternalGroupListRes(
      {responseCode,
      responseMessage,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages,
      this.dataList});

  ExternalGroupListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    if (json['dataList'] != null) {
      dataList = <ExternalGroupDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new ExternalGroupDetail.fromJson(v));
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
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class ExternalGroupDetail {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  int? qty;
  int? usedQty;
  int? unusedQty;
  String? ownershipType;
  String? status;
  int? mvnoId;
  int? ownerId;
  bool? isDeleted;
  int? inTransitQty;
  int? rejectedQty;
  String? approvalStatus;
  String? externalItemGroupNumber;
  int? totalMacSerial;
  String? approvalRemark;
  int? identityKey;
  InwardsProductDetail? productId;
  ServiceAreaId? serviceAreaId;

  ExternalGroupDetail(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.qty,
      this.usedQty,
      this.unusedQty,
      this.ownershipType,
      this.status,
      this.mvnoId,
      this.ownerId,
      this.isDeleted,
      this.inTransitQty,
      this.rejectedQty,
      this.approvalStatus,
      this.externalItemGroupNumber,
      this.totalMacSerial,
      this.approvalRemark,
      this.identityKey,
      this.productId,
      this.serviceAreaId});

  ExternalGroupDetail.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    qty = json['qty'];
    usedQty = json['usedQty'];
    unusedQty = json['unusedQty'];
    ownershipType = json['ownershipType'];
    status = json['status'];
    mvnoId = json['mvnoId'];
    ownerId = json['ownerId'];
    isDeleted = json['isDeleted'];
    inTransitQty = json['inTransitQty'];
    rejectedQty = json['rejectedQty'];
    approvalStatus = json['approvalStatus'];
    externalItemGroupNumber = json['externalItemGroupNumber'];
    totalMacSerial = json['totalMacSerial'];
    approvalRemark = json['approvalRemark'];
    identityKey = json['identityKey'];
    productId = json['productId'] != null
        ? new InwardsProductDetail.fromJson(json['productId'])
        : null;
    try {
      if (json.containsKey('serviceAreaId')) {
        serviceAreaId = json['serviceAreaId'] != null
            ? new ServiceAreaId.fromJson(json['serviceAreaId'])
            : null;
      }
    } catch (e) {}
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
    data['qty'] = this.qty;
    data['usedQty'] = this.usedQty;
    data['unusedQty'] = this.unusedQty;
    data['ownershipType'] = this.ownershipType;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    data['ownerId'] = this.ownerId;
    data['isDeleted'] = this.isDeleted;
    data['inTransitQty'] = this.inTransitQty;
    data['rejectedQty'] = this.rejectedQty;
    data['approvalStatus'] = this.approvalStatus;
    data['externalItemGroupNumber'] = this.externalItemGroupNumber;
    data['totalMacSerial'] = this.totalMacSerial;
    data['approvalRemark'] = this.approvalRemark;
    data['identityKey'] = this.identityKey;
    if (this.productId != null) {
      data['productId'] = this.productId!.toJson();
    }
    if (this.serviceAreaId != null) {
      data['serviceAreaId'] = this.serviceAreaId!.toJson();
    }
    return data;
  }
}

class ServiceAreaId {
  int? id;
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  String? name;
  String? status;
  bool? isDeleted;
  int? mvnoId;
  String? latitude;
  String? longitude;
  int? areaId;
  int? cityid;

  ServiceAreaId(
      {this.id,
      this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.name,
      this.status,
      this.isDeleted,
      this.mvnoId,
      this.latitude,
      this.longitude,
      this.areaId,
      this.cityid});

  ServiceAreaId.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    name = json['name'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    areaId = json['areaId'];
    cityid = json['cityid'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['name'] = this.name;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['areaId'] = this.areaId;
    data['cityid'] = this.cityid;
    return data;
  }
}
