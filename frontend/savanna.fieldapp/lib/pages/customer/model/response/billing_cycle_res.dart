import 'package:savbill/webservices/base_response.dart';

class BillingCycleRes extends BaseResponse{
  List<BillingCycleList>? billingCycleList;
  BillingCycleRes({this.billingCycleList});

  BillingCycleRes.fromJson(Map<String, dynamic> json) {
    if (json['dataList'] != null) {
      billingCycleList = <BillingCycleList>[];
      json['dataList'].forEach((v) {
        billingCycleList!.add(new BillingCycleList.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.billingCycleList != null) {
      data['dataList'] =
          this.billingCycleList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}


class BillingCycleList {
  int? id;
  String? text;
  String? value;
  String? type;
  String? status;
  dynamic subTypeList;
  int? displayId;
  String? displayName;
  bool? hasMandatory;
  int? mvnoId;


  BillingCycleList(
      {this.id,
        this.text,
        this.value,
        this.type,
        this.status,
        this.subTypeList,
        this.displayId,
        this.displayName,
        this.hasMandatory,
        this.mvnoId});

  BillingCycleList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    text = json['text'];
    value = json['value'];
    type = json['type'];
    status = json['status'];
    subTypeList = json['subTypeList'];
    displayId = json['displayId'];
    displayName = json['displayName'];
    hasMandatory = json['hasMandatory'];
    mvnoId = json['mvnoId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['text'] = this.text;
    data['value'] = this.value;
    data['type'] = this.type;
    data['status'] = this.status;
    data['subTypeList'] = this.subTypeList;
    data['displayId'] = this.displayId;
    data['displayName'] = this.displayName;
    data['mvnoId'] = this.mvnoId;
    data['hasMandatory'] = this.hasMandatory;
    return data;
  }
}



