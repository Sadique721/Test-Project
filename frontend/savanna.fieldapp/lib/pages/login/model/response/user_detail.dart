import 'package:savbill/webservices/base_response.dart';

class UserDetail extends BaseResponse {
  int? userId;
  int? mvnoId;
  int? partnerId;
  String? accessToken;
  String? userRoles;
  dynamic serviceAreaId;
  String? serviceAreaIdList;
  String? fullName;
  bool? partnerFlag;
  String? userName;

  UserDetail(
      {status,
      message,
      timestamp,
      this.userId,
      this.mvnoId,
      this.userRoles,
      this.partnerId,
      this.serviceAreaId,
      this.serviceAreaIdList,
      this.fullName,
      this.partnerFlag,
      this.accessToken,
      this.userName});

  UserDetail.fromJson(Map<String, dynamic> json) {
    status = json['status'];
    message = json['message'];
    timestamp = json['timestamp'];
    userId = json['userId'];
    mvnoId = json['mvnoId'];
      userRoles = json['userRoles'];
    partnerId = json['partnerId'];
    serviceAreaId = json['serviceAreaId'];
    serviceAreaIdList = json['serviceAreaIdList'];
    fullName = json['fullName'];
    partnerFlag = json['partnerFlag'];
    accessToken = json['accessToken'];
    userName = json['userName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['status'] = this.status;
    data['message'] = this.message;
    data['timestamp'] = this.timestamp;
    data['userId'] = this.userId;
    data['mvnoId'] = this.mvnoId;
    data['userRoles'] = this.userRoles;
    data['partnerId'] = this.partnerId;
    data['serviceAreaId'] = this.serviceAreaId;
    data['serviceAreaIdList'] = this.serviceAreaIdList;
    data['fullName'] = this.fullName;
    data['partnerFlag'] = this.partnerFlag;
    data['accessToken'] = this.accessToken;
    data['userName'] = this.userName;
    return data;
  }
}
