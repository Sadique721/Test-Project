import 'package:savbill/webservices/base_response.dart';

class CategoryListRes extends BaseResponse {
  List<CategoryDetail>? dataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  CategoryListRes(
      {responseCode,
      responseMessage,
      this.dataList,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages});

  CategoryListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <CategoryDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new CategoryDetail.fromJson(v));
      });
    }
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
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class CategoryDetail {
  int? id;
  String? name;
  String? unit;
  String? type;
  String? status;
  int? mvnoId;
  bool? isDeleted;
  bool? hasMac;
  bool? hasSerial;
  bool? hasTrackable;
  bool? hasPort;
  bool? hasCas;
  String? expiryTime;
  String? expiryTimeUnit;
  String? productId;
  String? dtvCategory;
  List<SpecificationParametersDTOList>? specificationParametersDTOList;


  CategoryDetail(
      {this.id,
      this.name,
      this.unit,
      this.type,
      this.status,
      this.mvnoId,
      this.isDeleted,
      this.hasMac,
      this.hasSerial,
      this.hasTrackable,
      this.hasPort,
      this.hasCas,
      this.expiryTime,
      this.expiryTimeUnit,
        this.productId,this.dtvCategory,
        this.specificationParametersDTOList,});

  CategoryDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    unit = json['unit'];
    type = json['type'];
    status = json['status'];
    mvnoId = json['mvnoId'];
    isDeleted = json['isDeleted'];
    hasMac = json['hasMac'];
    hasSerial = json['hasSerial'];
    hasTrackable = json['hasTrackable'];
    hasPort = json['hasPort'];
    hasCas = json['hasCas'];
    expiryTime = json['expiryTime'];
    expiryTimeUnit = json['expiryTimeUnit'];
    productId = json['productId'];
    dtvCategory = json['dtvCategory'];
    if (json['specificationParametersDTOList'] != null) {
      specificationParametersDTOList = <SpecificationParametersDTOList>[];
      json['specificationParametersDTOList'].forEach((v) {
        specificationParametersDTOList!
            .add(new SpecificationParametersDTOList.fromJson(v));
      });
    }
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
    data['hasTrackable'] = this.hasTrackable;
    data['hasPort'] = this.hasPort;
    data['hasCas'] = this.hasCas;
    data['expiryTime'] = this.expiryTime;
    data['expiryTimeUnit'] = this.expiryTimeUnit;
    data['productId'] = this.productId;
    data['dtvCategory'] = this.dtvCategory;
    if (this.specificationParametersDTOList != null) {
      data['specificationParametersDTOList'] =
          this.specificationParametersDTOList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class SpecificationParametersDTOList {
  int? id;
  Null? pcid;
  String? paramName;
  Null? paramValue;
  bool? isMandatory;
  int? mvnoId;
  int? identityKey;

  SpecificationParametersDTOList(
      {this.id,
        this.pcid,
        this.paramName,
        this.paramValue,
        this.isMandatory,
        this.mvnoId,
        this.identityKey});

  SpecificationParametersDTOList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    pcid = json['pcid'];
    paramName = json['paramName'];
    paramValue = json['paramValue'];
    isMandatory = json['isMandatory'];
    mvnoId = json['mvnoId'];
    identityKey = json['identityKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['pcid'] = this.pcid;
    data['paramName'] = this.paramName;
    data['paramValue'] = this.paramValue;
    data['isMandatory'] = this.isMandatory;
    data['mvnoId'] = this.mvnoId;
    data['identityKey'] = this.identityKey;
    return data;
  }
}
