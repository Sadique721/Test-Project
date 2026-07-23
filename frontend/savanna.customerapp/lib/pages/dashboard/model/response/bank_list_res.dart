import 'package:savbill/webservices/base_response.dart';

/*class BankListRes extends BaseResponse {
  List<BankDetail>? dataList;

  BankListRes({responseCode, responseMessage, this.dataList});

  BankListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <BankDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new BankDetail.fromJson(v));
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

class BankDetail {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? bankname;
  String? accountnum;
  String? ifsccode;
  String? bankholdername;
  String? status;
  int? mvnoId;
  bool? isDeleted;
  bool? deleteFlag;

  BankDetail(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.bankname,
      this.accountnum,
      this.ifsccode,
      this.bankholdername,
      this.bankcode,
      this.banktype,
      this.displayId,
      this.displayName,
      this.status,
      this.mvnoId,
      this.isDeleted,
      this.deleteFlag});

  BankDetail.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    bankname = json['bankname'];
    accountnum = json['accountnum'];
    ifsccode = json['ifsccode'];
    bankholdername = json['bankholdername'];
    status = json['status'];
    mvnoId = json['mvnoId'];
    isDeleted = json['isDeleted'];
    deleteFlag = json['deleteFlag'];
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
    data['bankname'] = this.bankname;
    data['accountnum'] = this.accountnum;
    data['ifsccode'] = this.ifsccode;
    data['bankholdername'] = this.bankholdername;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    data['isDeleted'] = this.isDeleted;
    data['deleteFlag'] = this.deleteFlag;
    return data;
  }
}*/


class BankListRes {
  int? responseCode;
  dynamic responseMessage;
  List<BankDetail>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  BankListRes(
      {this.responseCode,
        this.responseMessage,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  BankListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <BankDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new BankDetail.fromJson(v));
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

class BankDetail {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? bankname;
  String? accountnum;
  String? ifsccode;
  String? bankholdername;
  String? status;
  bool? isDeleted;
  String? bankcode;
  int? mvnoId;
  String? banktype;
  int? displayId;
  String? displayName;

  BankDetail(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.bankname,
        this.accountnum,
        this.ifsccode,
        this.bankholdername,
        this.status,
        this.isDeleted,
        this.bankcode,
        this.mvnoId,
        this.banktype,
        this.displayId,
        this.displayName});

  BankDetail.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    bankname = json['bankname'];
    accountnum = json['accountnum'];
    ifsccode = json['ifsccode'];
    bankholdername = json['bankholdername'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    bankcode = json['bankcode'];
    mvnoId = json['mvnoId'];
    banktype = json['banktype'];
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
    data['bankname'] = this.bankname;
    data['accountnum'] = this.accountnum;
    data['ifsccode'] = this.ifsccode;
    data['bankholdername'] = this.bankholdername;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['bankcode'] = this.bankcode;
    data['mvnoId'] = this.mvnoId;
    data['banktype'] = this.banktype;
    data['displayId'] = this.displayId;
    data['displayName'] = this.displayName;
    return data;
  }
}
