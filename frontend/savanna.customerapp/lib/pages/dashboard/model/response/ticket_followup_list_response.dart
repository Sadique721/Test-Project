import 'package:savbill/webservices/base_response.dart';

class TicketFollowupListResponse extends BaseResponse {
  List<FollowUpDetail>? dataList;

  TicketFollowupListResponse({responseCode, responseMessage, this.dataList});

  TicketFollowupListResponse.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <FollowUpDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new FollowUpDetail.fromJson(v));
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

class FollowUpDetail {
  int? id;
  String? remark;
  bool? isDelete;
  int? caseId;
  int? staffId;
  int? custId;
  String? remarkDate;
  String? caseTitle;
  String? staffUserName;
  String? customersName;
  bool? deleteFlag;
  int? primaryKey;

  FollowUpDetail(
      {this.id,
      this.remark,
      this.isDelete,
      this.caseId,
      this.staffId,
      this.custId,
      this.remarkDate,
      this.caseTitle,
      this.staffUserName,
      this.customersName,
      this.deleteFlag,
      this.primaryKey});

  FollowUpDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    remark = json['remark'];
    isDelete = json['isDelete'];
    caseId = json['caseId'];
    staffId = json['staffId'];
    custId = json['custId'];
    remarkDate = json['remarkDate'];
    caseTitle = json['caseTitle'];
    staffUserName = json['staffUserName'];
    customersName = json['customersName'];
    deleteFlag = json['deleteFlag'];
    primaryKey = json['primaryKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['remark'] = this.remark;
    data['isDelete'] = this.isDelete;
    data['caseId'] = this.caseId;
    data['staffId'] = this.staffId;
    data['custId'] = this.custId;
    data['remarkDate'] = this.remarkDate;
    data['caseTitle'] = this.caseTitle;
    data['staffUserName'] = this.staffUserName;
    data['customersName'] = this.customersName;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}

