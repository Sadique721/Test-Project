import 'package:savbill/webservices/base_response.dart';

class AllInventorySpecByItemIdRes extends BaseResponse {
  String? responseMessage;
  List<AllInventorySpecByItemDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  AllInventorySpecByItemIdRes(
      {
        this.responseMessage,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  AllInventorySpecByItemIdRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <AllInventorySpecByItemDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new AllInventorySpecByItemDataList.fromJson(v));
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

class AllInventorySpecByItemDataList {
  int? id;
  int? paramId;
  String? paramValue;
  int? inwardId;
  int? invenSpecId;
  String? paramName;
  bool? isMandatory;
  bool? isMultiValueParam;
  dynamic paramMultiValues;
  dynamic paramValues;
  dynamic defaultValue;
  dynamic specificationParametersDTO;
  int? identityKey;
  dynamic mvnoId;

  AllInventorySpecByItemDataList(
      {this.id,
        this.paramId,
        this.paramValue,
        this.inwardId,
        this.invenSpecId,
        this.paramName,
        this.isMandatory,
        this.isMultiValueParam,
        this.paramMultiValues,
        this.paramValues,
        this.defaultValue,
        this.specificationParametersDTO,
        this.identityKey,
        this.mvnoId});

  AllInventorySpecByItemDataList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    paramId = json['paramId'];
    paramValue = json['paramValue'];
    inwardId = json['inwardId'];
    invenSpecId = json['invenSpecId'];
    paramName = json['paramName'];
    isMandatory = json['isMandatory'];
    isMultiValueParam = json['isMultiValueParam'];
    paramMultiValues = json['paramMultiValues'];
    paramValues = json['paramValues'];
    defaultValue = json['defaultValue'];
    specificationParametersDTO = json['specificationParametersDTO'];
    identityKey = json['identityKey'];
    mvnoId = json['mvnoId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['paramId'] = this.paramId;
    data['paramValue'] = this.paramValue;
    data['inwardId'] = this.inwardId;
    data['invenSpecId'] = this.invenSpecId;
    data['paramName'] = this.paramName;
    data['isMandatory'] = this.isMandatory;
    data['isMultiValueParam'] = this.isMultiValueParam;
    data['paramMultiValues'] = this.paramMultiValues;
    data['paramValues'] = this.paramValues;
    data['defaultValue'] = this.defaultValue;
    data['specificationParametersDTO'] = this.specificationParametersDTO;
    data['identityKey'] = this.identityKey;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}
