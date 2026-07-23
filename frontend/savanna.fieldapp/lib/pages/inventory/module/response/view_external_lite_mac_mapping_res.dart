import 'package:savbill/webservices/base_response.dart';

class ViewExternalLiteMacMappingRes extends BaseResponse{

  List<ExternalLiteMacMappingDetail>? dataList;

  ViewExternalLiteMacMappingRes(
      {responseCode, responseMessage, this.dataList});

  ViewExternalLiteMacMappingRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <ExternalLiteMacMappingDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new ExternalLiteMacMappingDetail.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class ExternalLiteMacMappingDetail {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  int? externalItemId;
  String? macAddress;
  bool? isDeleted;
  int? custInventoryMappingId;
  String? serialNumber;
  bool? deleteFlag;
  int? primaryKey;

  ExternalLiteMacMappingDetail(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.externalItemId,
      this.macAddress,
      this.isDeleted,
      this.custInventoryMappingId,
      this.serialNumber,
      this.deleteFlag,
      this.primaryKey});

  ExternalLiteMacMappingDetail.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    externalItemId = json['externalItemId'];
    macAddress = json['macAddress'];
    isDeleted = json['isDeleted'];
    custInventoryMappingId = json['custInventoryMappingId'];
    serialNumber = json['serialNumber'];
    deleteFlag = json['deleteFlag'];
    primaryKey = json['primaryKey'];
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
    data['externalItemId'] = this.externalItemId;
    data['macAddress'] = this.macAddress;
    data['isDeleted'] = this.isDeleted;
    data['custInventoryMappingId'] = this.custInventoryMappingId;
    data['serialNumber'] = this.serialNumber;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}
