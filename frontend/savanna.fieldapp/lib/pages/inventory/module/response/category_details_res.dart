import 'package:savbill/webservices/base_response.dart';

class CategoryDetailsRes extends BaseResponse {
  String? responseMessage;
  CategoryDetailsData? data;
  dynamic dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  CategoryDetailsRes(
      {
        // this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  CategoryDetailsRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null ? new CategoryDetailsData.fromJson(json['data']) : null;
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

class CategoryDetailsData {
  int? id;
  String? name;
  String? unit;
  String? type;
  String? status;
  int? mvnoId;
  bool? isDeleted;
  bool? hasMac;
  bool? hasSerial;
  String? productId;
  bool? hasTrackable;
  bool? hasPort;
  bool? hasCas;
  dynamic dtvCategory;
  dynamic deviceType;
  bool? isUpgradeWithExistingProductItem;

  CategoryDetailsData(
      {this.id,
        this.name,
        this.unit,
        this.type,
        this.status,
        this.mvnoId,
        this.isDeleted,
        this.hasMac,
        this.hasSerial,
        this.productId,
        this.hasTrackable,
        this.hasPort,
        this.hasCas,
        this.dtvCategory,
        this.deviceType,
        this.isUpgradeWithExistingProductItem});

  CategoryDetailsData.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    unit = json['unit'];
    type = json['type'];
    status = json['status'];
    mvnoId = json['mvnoId'];
    isDeleted = json['isDeleted'];
    hasMac = json['hasMac'];
    hasSerial = json['hasSerial'];
    productId = json['productId'];
    hasTrackable = json['hasTrackable'];
    hasPort = json['hasPort'];
    hasCas = json['hasCas'];
    dtvCategory = json['dtvCategory'];
    deviceType = json['deviceType'];
    isUpgradeWithExistingProductItem = json['isUpgradeWithExistingProductItem'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['unit'] = this.unit;
    data['type'] = this.type;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    data['isDeleted'] = this.isDeleted;
    data['hasMac'] = this.hasMac;
    data['hasSerial'] = this.hasSerial;
    data['productId'] = this.productId;
    data['hasTrackable'] = this.hasTrackable;
    data['hasPort'] = this.hasPort;
    data['hasCas'] = this.hasCas;
    data['dtvCategory'] = this.dtvCategory;
    data['deviceType'] = this.deviceType;
    data['isUpgradeWithExistingProductItem'] =
        this.isUpgradeWithExistingProductItem;
    return data;
  }
}
