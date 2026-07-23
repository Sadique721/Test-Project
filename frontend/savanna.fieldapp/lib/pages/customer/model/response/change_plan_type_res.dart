import 'package:savbill/webservices/base_response.dart';

class ChangePlanTypeRes extends BaseResponse{
  List<ChangePlanTypeList>? changePlanTypeList;
  ChangePlanTypeRes({this.changePlanTypeList});

  ChangePlanTypeRes.fromJson(Map<String, dynamic> json) {
    if (json['dataList'] != null) {
      changePlanTypeList = <ChangePlanTypeList>[];
      json['dataList'].forEach((v) {
        changePlanTypeList!.add(new ChangePlanTypeList.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.changePlanTypeList != null) {
      data['dataList'] =
          this.changePlanTypeList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class ChangePlanTypeList {
  int? id;
  String? text;
  String? value;
  String? type;
  String? status;
  int? displayId;
  String? displayName;


  ChangePlanTypeList(
      {this.id,
        this.text,
        this.value,
        this.type,
        this.status,
        this.displayId,
        this.displayName
      });

  ChangePlanTypeList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    text = json['text'];
    value = json['value'];
    type = json['type'];
    status = json['status'];
    displayId = json['displayId'];
    displayName = json['displayName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['text'] = this.text;
    data['value'] = this.value;
    data['type'] = this.type;
    data['status'] = this.status;
    data['displayId'] = this.displayId;
    data['displayName'] = this.displayName;
    return data;
  }
}







