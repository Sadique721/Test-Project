import 'package:savbill/webservices/base_response.dart';

class APPartnerRes extends BaseResponse {
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  List<APPartner>? dataList;

  APPartnerRes(
      {responseCode,
      responseMessage,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages,
      this.dataList});

  APPartnerRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    if (json['dataList'] != null) {
      dataList = <APPartner>[];
      json['dataList'].forEach((v) {
        dataList!.add(new APPartner.fromJson(v));
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

class APPartner {
  int? id;
  String? transcategory;
  String? paymentmode;
  num? amount;
  String? paymentdate;
  int? nextStaff;
  String? status;
  bool? isDeleted;
  String? partnerName;
  bool? deleteFlag;
  int? primaryKey;

  APPartner(
      {this.id,
      this.transcategory,
      this.paymentmode,
      this.amount,
      this.paymentdate,
      this.nextStaff,
      this.status,
      this.isDeleted,
      this.partnerName,
      this.deleteFlag,
      this.primaryKey});

  APPartner.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    transcategory = json['transcategory'];
    paymentmode = json['paymentmode'];
    amount = json['amount'];
    paymentdate = json['paymentdate'];
    nextStaff = json['nextStaff'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    partnerName = json['partnerName'];
    deleteFlag = json['deleteFlag'];
    primaryKey = json['primaryKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['transcategory'] = this.transcategory;
    data['paymentmode'] = this.paymentmode;
    data['amount'] = this.amount;
    data['paymentdate'] = this.paymentdate;
    data['nextStaff'] = this.nextStaff;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['partnerName'] = this.partnerName;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}
