import 'package:savbill/webservices/base_response.dart';

class InventoryRequestListRes  extends  BaseResponse{
  dynamic data;
  List<InventroyRequestDataList>? dataList;
  Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  InventoryRequestListRes(
      { responseCode,
        responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  InventoryRequestListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <InventroyRequestDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(InventroyRequestDataList.fromJson(v));
      });
    }
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
    data['data'] = this.data;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    data['excelDataList'] = this.excelDataList;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class InventroyRequestDataList {
  int? id;
  String? requestInventoryName;
  int? requestNameId;
  int? requestToWarehouseId;
  String? status;
  String? inventoryRequestStatus;
  String? reason;
  String? requesterName;
  String? requestToName;
  int? mvnoId;
  int? identityKey;
  String? onBehalfOf;

  InventroyRequestDataList(
      {this.id,
        this.requestInventoryName,
        this.requestNameId,
        this.requestToWarehouseId,
        this.status,
        this.inventoryRequestStatus,
        this.reason,
        this.requesterName,
        this.requestToName,
        this.mvnoId,
        this.identityKey,
        this.onBehalfOf});

  InventroyRequestDataList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    requestInventoryName = json['requestInventoryName'];
    requestNameId = json['requestNameId'];
    requestToWarehouseId = json['requestToWarehouseId'];
    status = json['status'];
    inventoryRequestStatus = json['inventoryRequestStatus'];
    reason = json['reason'];
    requesterName = json['requesterName'];
    requestToName = json['requestToName'];
    mvnoId = json['mvnoId'];
    identityKey = json['identityKey'];
    onBehalfOf = json['onBehalfOf'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['requestInventoryName'] = this.requestInventoryName;
    data['requestNameId'] = this.requestNameId;
    data['requestToWarehouseId'] = this.requestToWarehouseId;
    data['status'] = this.status;
    data['inventoryRequestStatus'] = this.inventoryRequestStatus;
    data['reason'] = this.reason;
    data['requesterName'] = this.requesterName;
    data['requestToName'] = this.requestToName;
    data['mvnoId'] = this.mvnoId;
    data['identityKey'] = this.identityKey;
    data['onBehalfOf'] = this.onBehalfOf;
    return data;
  }
}
