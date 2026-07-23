import 'package:savbill/webservices/base_response.dart';

class GetAclEntryRes extends BaseResponse {
  List<GetAclDataList>? dataList;
  String? timestamp;
  int? status;

  GetAclEntryRes({this.dataList, this.timestamp, this.status});

  GetAclEntryRes.fromJson(Map<String, dynamic> json) {
    if (json['dataList'] != null) {
      dataList = <GetAclDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(GetAclDataList.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    if (dataList != null) {
      data['dataList'] = dataList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = timestamp;
    data['status'] = status;
    return data;
  }
}

class GetAclDataList {
  int? id;
  dynamic roleId;
  String? code;
  dynamic product;
  int? menuid;

  GetAclDataList({this.id, this.roleId, this.code, this.product, this.menuid});

  GetAclDataList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    roleId = json['roleId'];
    code = json['code'];
    product = json['product'];
    menuid = json['menuid'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['id'] = id;
    data['roleId'] = roleId;
    data['code'] = code;
    data['product'] = product;
    data['menuid'] = menuid;
    return data;
  }
}
