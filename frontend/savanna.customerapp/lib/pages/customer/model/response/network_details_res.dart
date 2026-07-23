import 'package:savbill/webservices/base_response.dart';

class NetworkDetailsRes extends BaseResponse {
  String? responseMessage;
  NetworkDetailData? data;
  dynamic dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  NetworkDetailsRes(
      {
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  NetworkDetailsRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null ? new NetworkDetailData.fromJson(json['data']) : null;
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

class NetworkDetailData {
  int? customerid;
  dynamic popid;
  dynamic popName;
  dynamic oltid;
  dynamic oltDeviceName;
  dynamic nasPort;
  dynamic ipPoolNameBind;
  dynamic framedIp;
  dynamic framedIpBind;
  dynamic masterdbid;
  dynamic masterdbDeviceName;
  dynamic splitterid;
  dynamic splitterDerviceName;
  dynamic oltslotid;
  dynamic oltportid;
  dynamic staticOrPooledIP;
  List<dynamic>? macAddress;
  List<dynamic>? onuSerialNumber;
  dynamic externalOnuSerialNumber;

  NetworkDetailData(
      {this.customerid,
        this.popid,
        this.popName,
        this.oltid,
        this.oltDeviceName,
        this.nasPort,
        this.ipPoolNameBind,
        this.framedIp,
        this.framedIpBind,
        this.masterdbid,
        this.masterdbDeviceName,
        this.splitterid,
        this.splitterDerviceName,
        this.oltslotid,
        this.oltportid,
        this.staticOrPooledIP,
        this.macAddress,
        this.onuSerialNumber,
        this.externalOnuSerialNumber});

  NetworkDetailData.fromJson(Map<String, dynamic> json) {
    customerid = json['customerid'];
    popid = json['popid'];
    popName = json['popName'];
    oltid = json['oltid'];
    oltDeviceName = json['oltDeviceName'];
    nasPort = json['nasPort'];
    ipPoolNameBind = json['ipPoolNameBind'];
    framedIp = json['framedIp'];
    framedIpBind = json['framedIpBind'];
    masterdbid = json['masterdbid'];
    masterdbDeviceName = json['masterdbDeviceName'];
    splitterid = json['splitterid'];
    splitterDerviceName = json['splitterDerviceName'];
    oltslotid = json['oltslotid'];
    oltportid = json['oltportid'];
    staticOrPooledIP = json['staticOrPooledIP'];
    // macAddress = json['macAddress'].cast<dynamic>();
    if(json['macAddress'] != null) {
      macAddress = (json['macAddress'] ?? []).cast<dynamic>();
    }
    // onuSerialNumber = json['onuSerialNumber'].cast<dynamic>();
    if(json['onuSerialNumber'] != null) {
      onuSerialNumber = (json['onuSerialNumber'] ?? []).cast<dynamic>();
    }
    // onuSerialNumber = json['onuSerialNumber'].cast<dynamic>();
    externalOnuSerialNumber = json['externalOnuSerialNumber'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['customerid'] = this.customerid;
    data['popid'] = this.popid;
    data['popName'] = this.popName;
    data['oltid'] = this.oltid;
    data['oltDeviceName'] = this.oltDeviceName;
    data['nasPort'] = this.nasPort;
    data['ipPoolNameBind'] = this.ipPoolNameBind;
    data['framedIp'] = this.framedIp;
    data['framedIpBind'] = this.framedIpBind;
    data['masterdbid'] = this.masterdbid;
    data['masterdbDeviceName'] = this.masterdbDeviceName;
    data['splitterid'] = this.splitterid;
    data['splitterDerviceName'] = this.splitterDerviceName;
    data['oltslotid'] = this.oltslotid;
    data['oltportid'] = this.oltportid;
    data['staticOrPooledIP'] = this.staticOrPooledIP;
    data['macAddress'] = this.macAddress;
    data['onuSerialNumber'] = this.onuSerialNumber;
    data['externalOnuSerialNumber'] = this.externalOnuSerialNumber;
    return data;
  }
}
