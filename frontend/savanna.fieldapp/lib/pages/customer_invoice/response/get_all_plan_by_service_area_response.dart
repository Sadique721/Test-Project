
import 'package:savbill/webservices/base_response.dart';

class GetAllPlansByServiceAreaResponse extends BaseResponse {
  String? msg;
  List<AllPlansByServiceAreaDataList>? planList;
  String? timestamp;
  int? status;

  GetAllPlansByServiceAreaResponse(
      {this.msg, this.planList, this.timestamp, this.status});

  GetAllPlansByServiceAreaResponse.fromJson(Map<String, dynamic> json) {
    msg = json['msg'];
    if (json['planList'] != null) {
      planList = <AllPlansByServiceAreaDataList>[];
      json['planList'].forEach((v) {
        planList!.add(new AllPlansByServiceAreaDataList.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['msg'] = this.msg;
    if (this.planList != null) {
      data['planList'] = this.planList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class AllPlansByServiceAreaDataList {
  int? id;
  String? serviceName;
  Null? serviceArea;
  List<int>? serviceAreaIds;
  String? name;
  double? offerprice;
  String? unitsOfValidity;
  double? validity;
  int? mvnoId;
  String? plantype;
  String? planGroupType;

  AllPlansByServiceAreaDataList(
      {this.id,
        this.serviceName,
        this.serviceArea,
        this.serviceAreaIds,
        this.name,
        this.offerprice,
        this.unitsOfValidity,
        this.validity,
        this.mvnoId,
        this.plantype,
        this.planGroupType});

  AllPlansByServiceAreaDataList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    serviceName = json['serviceName'];
    serviceArea = json['serviceArea'];
    serviceAreaIds = json['serviceAreaIds'].cast<int>();
    name = json['name'];
    offerprice = json['offerprice'];
    unitsOfValidity = json['unitsOfValidity'];
    validity = json['validity'];
    mvnoId = json['mvnoId'];
    plantype = json['plantype'];
    planGroupType = json['planGroupType'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['serviceName'] = this.serviceName;
    data['serviceArea'] = this.serviceArea;
    data['serviceAreaIds'] = this.serviceAreaIds;
    data['name'] = this.name;
    data['offerprice'] = this.offerprice;
    data['unitsOfValidity'] = this.unitsOfValidity;
    data['validity'] = this.validity;
    data['mvnoId'] = this.mvnoId;
    data['plantype'] = this.plantype;
    data['planGroupType'] = this.planGroupType;
    return data;
  }
}
