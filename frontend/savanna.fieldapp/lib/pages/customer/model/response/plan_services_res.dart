import 'package:savbill/webservices/base_response.dart';

class PlanServicesRes extends BaseResponse {
  List<PlanServiceDetail>? serviceList;

  PlanServicesRes({this.serviceList, timestamp, error, status});

  PlanServicesRes.fromJson(Map<String, dynamic> json) {
    if (json['serviceList'] != null) {
      serviceList = <PlanServiceDetail>[];
      json['serviceList'].forEach((v) {
        serviceList!.add(new PlanServiceDetail.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    error = json['error'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.serviceList != null) {
      data['serviceList'] = this.serviceList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['error'] = this.error;
    data['status'] = this.status;
    return data;
  }
}

class PlanServiceDetail {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? icname;
  String? iccode;

  PlanServiceDetail(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.name,
      this.icname,
      this.iccode});

  PlanServiceDetail.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    icname = json['icname'];
    iccode = json['iccode'];
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
    data['name'] = this.name;
    data['icname'] = this.icname;
    data['iccode'] = this.iccode;
    return data;
  }
}
