import 'package:savbill/webservices/base_response.dart';

class CustomerQuotaListResponse extends BaseResponse {
  List<CustQuotaDettail>? custQuotaList;

  CustomerQuotaListResponse({this.custQuotaList, timestamp, status});

  CustomerQuotaListResponse.fromJson(Map<String, dynamic> json) {
    if (json['custQuotaList'] != null) {
      custQuotaList = <CustQuotaDettail>[];
      json['custQuotaList'].forEach((v) {
        custQuotaList!.add(new CustQuotaDettail.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.custQuotaList != null) {
      data['custQuotaList'] =
          this.custQuotaList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class CustQuotaDettail {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  int? planId;
  String? quotaType;
  num? totalQuota;
  num? usedQuota;
  String? quotaUnit;
  num? timeTotalQuota;
  num? timeQuotaUsed;
  String? timeQuotaUnit;
  bool? isDelete;
  num? totalQuotaKB;
  num? usedQuotaKB;

  /*Null? timeUsedQuotaSec;
  Null? timeTotalQuotaSec;
  int? didtotalquota;
  int? didusedquota;
  int? intercomtotalquota;
  int? intercomusedquota;
  String? didQuotaUnit;
  String? intercomQuotaUnit;*/
  String? planName;

  CustQuotaDettail(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.planId,
      this.quotaType,
      this.totalQuota,
      this.usedQuota,
      this.quotaUnit,
      this.timeTotalQuota,
      this.timeQuotaUsed,
      this.timeQuotaUnit,
      this.isDelete,
      this.totalQuotaKB,
      this.usedQuotaKB,
      /*this.timeUsedQuotaSec,
        this.timeTotalQuotaSec,
        this.didtotalquota,
        this.didusedquota,
        this.intercomtotalquota,
        this.intercomusedquota,
        this.didQuotaUnit,
        this.intercomQuotaUnit,*/
      this.planName});

  CustQuotaDettail.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    planId = json['planId'];
    quotaType = json['quotaType'];
    totalQuota = json['totalQuota'];
    usedQuota = json['usedQuota'];
    quotaUnit = json['quotaUnit'];
    timeTotalQuota = json['timeTotalQuota'];
    timeQuotaUsed = json['timeQuotaUsed'];
    timeQuotaUnit = json['timeQuotaUnit'];
    isDelete = json['isDelete'];
    totalQuotaKB = json['totalQuotaKB'];
    usedQuotaKB = json['usedQuotaKB'];
    /* timeUsedQuotaSec = json['timeUsedQuotaSec'];
    timeTotalQuotaSec = json['timeTotalQuotaSec'];
    didtotalquota = json['didtotalquota'];
    didusedquota = json['didusedquota'];
    intercomtotalquota = json['intercomtotalquota'];
    intercomusedquota = json['intercomusedquota'];
    didQuotaUnit = json['didQuotaUnit'];
    intercomQuotaUnit = json['intercomQuotaUnit'];*/
    planName = json['planName'];
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
    data['planId'] = this.planId;
    data['quotaType'] = this.quotaType;
    data['totalQuota'] = this.totalQuota;
    data['usedQuota'] = this.usedQuota;
    data['quotaUnit'] = this.quotaUnit;
    data['timeTotalQuota'] = this.timeTotalQuota;
    data['timeQuotaUsed'] = this.timeQuotaUsed;
    data['timeQuotaUnit'] = this.timeQuotaUnit;
    data['isDelete'] = this.isDelete;
    data['totalQuotaKB'] = this.totalQuotaKB;
    data['usedQuotaKB'] = this.usedQuotaKB;
    /*data['timeUsedQuotaSec'] = this.timeUsedQuotaSec;
    data['timeTotalQuotaSec'] = this.timeTotalQuotaSec;
    data['didtotalquota'] = this.didtotalquota;
    data['didusedquota'] = this.didusedquota;
    data['intercomtotalquota'] = this.intercomtotalquota;
    data['intercomusedquota'] = this.intercomusedquota;
    data['didQuotaUnit'] = this.didQuotaUnit;
    data['intercomQuotaUnit'] = this.intercomQuotaUnit;*/
    data['planName'] = this.planName;
    return data;
  }
}
