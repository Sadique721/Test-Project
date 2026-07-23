import 'package:savbill/webservices/base_response.dart';

class InventoryDocumentListRes extends BaseResponse {

  String? responseMessage;
  dynamic data;
  List<DocumentLis>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  InventoryDocumentListRes(
      {
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  InventoryDocumentListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <DocumentLis>[];
      json['dataList'].forEach((v) {
        dataList!.add(new DocumentLis.fromJson(v));
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

class DocumentLis {
  String? sectionName;
  List<FileDetails>? fileDetails;

  DocumentLis({this.sectionName, this.fileDetails});

  DocumentLis.fromJson(Map<String, dynamic> json) {
    sectionName = json['sectionName'];
    if (json['fileDetails'] != null) {
      fileDetails = <FileDetails>[];
      json['fileDetails'].forEach((v) {
        fileDetails!.add(new FileDetails.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['sectionName'] = this.sectionName;
    if (this.fileDetails != null) {
      data['fileDetails'] = this.fileDetails!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class FileDetails {
  String? fileName;
  String? uniqueName;
  String? latitude;
  String? longitude;
  int? customerInventoryId;
  String? customerCafImageMappingId;
  String? opticalRange;

  FileDetails(
      {this.fileName,
        this.uniqueName,
        this.latitude,
        this.longitude,
        this.customerInventoryId,
        this.customerCafImageMappingId,
        this.opticalRange});

  FileDetails.fromJson(Map<String, dynamic> json) {
    fileName = json['fileName'];
    uniqueName = json['uniqueName'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    customerInventoryId = json['customerInventoryId'];
    customerCafImageMappingId = json['customerCafImageMappingId'];
    opticalRange = json['opticalRange'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['fileName'] = this.fileName;
    data['uniqueName'] = this.uniqueName;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['customerInventoryId'] = this.customerInventoryId;
    data['customerCafImageMappingId'] = this.customerCafImageMappingId;
    data['opticalRange'] = this.opticalRange;
    return data;
  }
}
