import 'package:savbill/webservices/base_response.dart';

class OutwardInwardListRes extends BaseResponse {
  List<OutwardInwardDetail>? dataList;

  OutwardInwardListRes({responseCode, responseMessage, this.dataList});

  OutwardInwardListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <OutwardInwardDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new OutwardInwardDetail.fromJson(v));
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

class OutwardInwardDetail {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? inwardNumber;
  num? qty;
  num? usedQty;
  num? unusedQty;
  String? inwardDateTime;
  String? type;
  String? status;
  int? mvnoId;
  bool? isDeleted;
  String? sourceType;
  int? sourceId;
  String? destinationType;
  int? destinationId;
  num? inTransitQty;
  String? serviceArea;

  // int? outwardId;
  bool? deleteFlag;
  int? primaryKey;

/*  Null? productId;
  Null? outTransitQty;
  Null? rejectedQty;
  Null? approvalStatus;
  Null? categoryType;
  Null? rmsInwardId;
  Null? navInwardId;
  Null? totalMacSerial;
  Null? approvalRemark;*/

  OutwardInwardDetail(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.inwardNumber,
      this.qty,
      this.usedQty,
      this.unusedQty,
      this.inwardDateTime,
      this.type,
      this.status,
      this.mvnoId,
      this.isDeleted,
      this.sourceType,
      this.sourceId,
      this.destinationType,
      this.destinationId,
      this.inTransitQty,
      this.serviceArea,
      // this.outwardId,
      this.deleteFlag,
      this.primaryKey});

  OutwardInwardDetail.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    inwardNumber = json['inwardNumber'];
    qty = json['qty'];
    usedQty = json['usedQty'];
    unusedQty = json['unusedQty'];
    inwardDateTime = json['inwardDateTime'];
    type = json['type'];
    status = json['status'];
    mvnoId = json['mvnoId'];
    isDeleted = json['isDeleted'];
    sourceType = json['sourceType'];
    sourceId = json['sourceId'];
    destinationType = json['destinationType'];
    destinationId = json['destinationId'];
    inTransitQty = json['inTransitQty'];
    serviceArea = json['serviceArea'];
    //outwardId = json['outwardId'];
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
    data['inwardNumber'] = this.inwardNumber;
    data['qty'] = this.qty;
    data['usedQty'] = this.usedQty;
    data['unusedQty'] = this.unusedQty;
    data['inwardDateTime'] = this.inwardDateTime;
    data['type'] = this.type;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    data['isDeleted'] = this.isDeleted;
    data['sourceType'] = this.sourceType;
    data['sourceId'] = this.sourceId;
    data['destinationType'] = this.destinationType;
    data['destinationId'] = this.destinationId;
    data['inTransitQty'] = this.inTransitQty;
    data['serviceArea'] = this.serviceArea;
    // data['outwardId'] = this.outwardId;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}
