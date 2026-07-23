import 'package:savbill/webservices/base_response.dart';

class CustometDeparmentListRes extends BaseResponse {
  List<DepartmentListData>? departmentList;
  String? timestamp;
  int? status;

  CustometDeparmentListRes({this.departmentList, this.timestamp, this.status});

  CustometDeparmentListRes.fromJson(Map<String, dynamic> json) {
    if (json['departmentList'] != null) {
      departmentList = <DepartmentListData>[];
      json['departmentList'].forEach((v) {
        departmentList!.add(new DepartmentListData.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.departmentList != null) {
      data['departmentList'] =
          this.departmentList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class DepartmentListData {
  dynamic createdate;
  dynamic updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic mvnoId;
  dynamic id;
  dynamic name;
  dynamic status;
  bool? isDelete;
  dynamic displayId;
  dynamic displayName;
  bool? delete;

  DepartmentListData(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.mvnoId,
        this.id,
        this.name,
        this.status,
        this.isDelete,
        this.displayId,
        this.displayName,
        this.delete});

  DepartmentListData.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    mvnoId = json['mvnoId'];
    id = json['id'];
    name = json['name'];
    status = json['status'];
    isDelete = json['isDelete'];
    displayId = json['displayId'];
    displayName = json['displayName'];
    delete = json['delete'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['mvnoId'] = this.mvnoId;
    data['id'] = this.id;
    data['name'] = this.name;
    data['status'] = this.status;
    data['isDelete'] = this.isDelete;
    data['displayId'] = this.displayId;
    data['displayName'] = this.displayName;
    data['delete'] = this.delete;
    return data;
  }
}
