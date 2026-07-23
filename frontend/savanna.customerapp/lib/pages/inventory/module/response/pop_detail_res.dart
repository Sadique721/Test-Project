import 'package:savbill/pages/inventory/module/response/staff_service_area_res.dart';
import 'package:savbill/webservices/base_response.dart';

class PopDetailRes extends BaseResponse {
  PopDetailData? data;

  PopDetailRes({responseCode, responseMessage, this.data});

  PopDetailRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data =
        json['data'] != null ? new PopDetailData.fromJson(json['data']) : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    if (this.data != null) {
      data['data'] = this.data!.toJson();
    }
    return data;
  }
}

class PopDetailData {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? popCode;
  String? latitude;
  String? longitude;
  // List<StaffServiceAreaDetail>? serviceAreaNameList;
  List<int>? serviceAreaIdsList;
  List<String>? serviceAreaNameList;
  String? status;
  bool? isDeleted;
  int? mvnoId;
  int? identityKey;
  String? displayName;
  int? displayId;


  PopDetailData(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.name,
      this.popCode,
      this.latitude,
      this.longitude,
      // this.serviceAreaNameList,
      this.serviceAreaIdsList,
      this.serviceAreaNameList,
      this.status,
      this.isDeleted,
      this.mvnoId,
      this.identityKey});

  PopDetailData.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    popCode = json['popCode'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    // if (json['serviceAreaNameList'] != null) {
    //   serviceAreaNameList = <StaffServiceAreaDetail>[];
    //   json['serviceAreaNameList'].forEach((v) {
    //     serviceAreaNameList!.add(new StaffServiceAreaDetail.fromJson(v));
    //   });
    // }

    serviceAreaIdsList = json['serviceAreaIdsList'].cast<int>();
    serviceAreaNameList = json['serviceAreaNameList'].cast<String>();
    status = json['status'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
    identityKey = json['identityKey'];
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
    data['popCode'] = this.popCode;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['serviceAreaIdsList'] = this.serviceAreaIdsList;
    data['serviceAreaNameList'] = this.serviceAreaNameList;
    // if (this.serviceAreaNameList != null) {
    //   data['serviceAreaNameList'] =
    //       this.serviceAreaNameList!.map((v) => v.toJson()).toList();
    // }
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    data['identityKey'] = this.identityKey;
    return data;
  }
}
