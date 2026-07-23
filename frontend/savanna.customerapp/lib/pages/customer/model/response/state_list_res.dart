import 'package:savbill/webservices/base_response.dart';

class StateListRes extends BaseResponse {
  List<StateDetail>? stateList;

  StateListRes({this.stateList, timestamp, status, error});

  StateListRes.fromJson(Map<String, dynamic> json) {
    if (json['stateList'] != null) {
      stateList = <StateDetail>[];
      json['stateList'].forEach((v) {
        stateList!.add(new StateDetail.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
    error = json['error'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.stateList != null) {
      data['stateList'] = this.stateList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    data['error'] = this.error;
    return data;
  }
}

class StateDetail {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? status;
  String? countryName;
  bool? isDeleted;
  int? mvnoId;

  StateDetail(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.name,
      this.status,
      this.countryName,
      this.isDeleted,
      this.mvnoId});

  StateDetail.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    status = json['status'];
    countryName = json['countryName'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
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
    data['status'] = this.status;
    data['countryName'] = this.countryName;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}
