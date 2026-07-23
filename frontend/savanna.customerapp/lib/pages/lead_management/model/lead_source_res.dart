import 'package:savbill/webservices/base_response.dart';

class LeadSourceRes extends BaseResponse{
  List<LeadSourceList>? leadSourceList;
  String? timestamp;
  int? status;

  LeadSourceRes({this.leadSourceList, this.timestamp, this.status});

  LeadSourceRes.fromJson(Map<String, dynamic> json) {
    if (json['leadSourceList'] != null) {
      leadSourceList = <LeadSourceList>[];
      json['leadSourceList'].forEach((v) {
        leadSourceList!.add(new LeadSourceList.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.leadSourceList != null) {
      data['leadSourceList'] =
          this.leadSourceList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class LeadSourceList {
  int? id;
  String? leadSourceName;
  String? status;
  bool? view;
  List<LeadSubSourceDtoList>? leadSubSourceDtoList;
  dynamic leadSubSourceDeletedIds;
  dynamic mvnoId;
  dynamic buId;

  LeadSourceList(
      {this.id,
        this.leadSourceName,
        this.status,
        this.view,
        this.leadSubSourceDtoList,
        this.leadSubSourceDeletedIds,
        this.mvnoId,
        this.buId});

  LeadSourceList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    leadSourceName = json['leadSourceName'];
    status = json['status'];
    view = json['view'];
    if (json['leadSubSourceDtoList'] != null) {
      leadSubSourceDtoList = <LeadSubSourceDtoList>[];
      json['leadSubSourceDtoList'].forEach((v) {
        leadSubSourceDtoList!.add(new LeadSubSourceDtoList.fromJson(v));
      });
    }
    leadSubSourceDeletedIds = json['leadSubSourceDeletedIds'];
    mvnoId = json['mvnoId'];
    buId = json['buId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['leadSourceName'] = this.leadSourceName;
    data['status'] = this.status;
    data['view'] = this.view;
    if (this.leadSubSourceDtoList != null) {
      data['leadSubSourceDtoList'] =
          this.leadSubSourceDtoList!.map((v) => v.toJson()).toList();
    }
    data['leadSubSourceDeletedIds'] = this.leadSubSourceDeletedIds;
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    return data;
  }
}

class LeadSubSourceDtoList {
  int? id;
  String? name;
  int? leadSourceId;

  LeadSubSourceDtoList({this.id, this.name, this.leadSourceId});

  LeadSubSourceDtoList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    leadSourceId = json['leadSourceId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['leadSourceId'] = this.leadSourceId;
    return data;
  }
}
