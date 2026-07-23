import 'package:savbill/webservices/base_response.dart';

class PincodeListRes extends BaseResponse {
  List<PincodeDetail>? dataList;

  PincodeListRes({responseCode, responseMessage, this.dataList});

  PincodeListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <PincodeDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new PincodeDetail.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class PincodeDetail {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? pincodeid;
  int? id;
  String? pincode;
  String? status;
  bool? isDeleted;
  int? countryId;
  int? stateId;
  int? cityId;
  String? cityName;
  String? stateName;
  String? countryName;
  String? areas;
  List<PincodeAreaDetail>? areaList;
  int? mvnoId;

  PincodeDetail(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.pincodeid,
      this.id,
      this.pincode,
      this.status,
      this.isDeleted,
      this.countryId,
      this.stateId,
      this.cityId,
      this.cityName,
      this.stateName,
      this.countryName,
      this.areas,
      this.areaList,
      this.mvnoId});

  PincodeDetail.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    pincodeid = json['pincodeid'];
    id = json['id'];
    pincode = json['pincode'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    countryId = json['countryId'];
    stateId = json['stateId'];
    cityId = json['cityId'];
    cityName = json['cityName'];
    stateName = json['stateName'];
    countryName = json['countryName'];
    areas = json['areas'];
    if (json['areaList'] != null) {
      areaList = <PincodeAreaDetail>[];
      json['areaList'].forEach((v) {
        areaList!.add(new PincodeAreaDetail.fromJson(v));
      });
    }
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
    data['pincodeid'] = this.pincodeid;
    data['id'] = this.id;
    data['pincode'] = this.pincode;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['countryId'] = this.countryId;
    data['stateId'] = this.stateId;
    data['cityId'] = this.cityId;
    data['cityName'] = this.cityName;
    data['stateName'] = this.stateName;
    data['countryName'] = this.countryName;
    data['areas'] = this.areas;
    if (this.areaList != null) {
      data['areaList'] = this.areaList!.map((v) => v.toJson()).toList();
    }
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}

class PincodeAreaDetail {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? status;
  bool? isDeleted;
  int? countryId;
  int? stateId;
  int? cityId;
  String? countryName;
  String? stateName;
  String? cityName;
  int? pincodeId;
  String? code;
  int? mvnoId;

  PincodeAreaDetail(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.name,
      this.status,
      this.isDeleted,
      this.countryId,
      this.stateId,
      this.cityId,
      this.countryName,
      this.stateName,
      this.cityName,
      this.pincodeId,
      this.code,
      this.mvnoId});

  PincodeAreaDetail.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    countryId = json['countryId'];
    stateId = json['stateId'];
    cityId = json['cityId'];
    countryName = json['countryName'];
    stateName = json['stateName'];
    cityName = json['cityName'];
    pincodeId = json['pincodeId'];
    code = json['code'];
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
    data['isDeleted'] = this.isDeleted;
    data['countryId'] = this.countryId;
    data['stateId'] = this.stateId;
    data['cityId'] = this.cityId;
    data['countryName'] = this.countryName;
    data['stateName'] = this.stateName;
    data['cityName'] = this.cityName;
    data['pincodeId'] = this.pincodeId;
    data['code'] = this.code;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}
