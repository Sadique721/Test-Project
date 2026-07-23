import 'package:savbill/webservices/base_response.dart';

class CityListRes extends BaseResponse {
  List<CityDetail>? cityList;

  CityListRes({this.cityList, timestamp, status, error});

  CityListRes.fromJson(Map<String, dynamic> json) {
    if (json['cityList'] != null) {
      cityList = <CityDetail>[];
      json['cityList'].forEach((v) {
        cityList!.add(new CityDetail.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
    error = json['error'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.cityList != null) {
      data['cityList'] = this.cityList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    data['error'] = this.error;
    return data;
  }
}

class CityDetail {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? status;
  int? countryId;
  String? stateName;
  String? countryName;
  bool? isDelete;
  int? mvnoId;

  CityDetail(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.name,
      this.status,
      this.countryId,
      this.stateName,
      this.countryName,
      this.isDelete,
      this.mvnoId});

  CityDetail.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    status = json['status'];
    countryId = json['countryId'];
    stateName = json['stateName'];
    countryName = json['countryName'];
    isDelete = json['isDelete'];
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
    data['countryId'] = this.countryId;
    data['stateName'] = this.stateName;
    data['countryName'] = this.countryName;
    data['isDelete'] = this.isDelete;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}
