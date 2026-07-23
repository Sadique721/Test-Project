import 'package:savbill/webservices/base_response.dart';

class LeadSourceServiceAreaCRMRes extends BaseResponse{
  List<ServiceAreaList>? serviceAreaList;
  String? timestamp;
  int? status;

  LeadSourceServiceAreaCRMRes(
      {this.serviceAreaList, this.timestamp, this.status});

  LeadSourceServiceAreaCRMRes.fromJson(Map<String, dynamic> json) {
    if (json['serviceAreaList'] != null) {
      serviceAreaList = <ServiceAreaList>[];
      json['serviceAreaList'].forEach((v) {
        serviceAreaList!.add(new ServiceAreaList.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.serviceAreaList != null) {
      data['serviceAreaList'] =
          this.serviceAreaList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class ServiceAreaList {
  int? id;
  String? name;
  String? status;
  bool? isDeleted;
  int? mvnoId;
  String? latitude;
  String? longitude;
  int? buId;

  ServiceAreaList(
      {this.id,
        this.name,
        this.status,
        this.isDeleted,
        this.mvnoId,
        this.latitude,
        this.longitude,
        this.buId});

  ServiceAreaList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    buId = json['buId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['buId'] = this.buId;
    return data;
  }
}
