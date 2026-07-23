import 'package:savbill/webservices/base_response.dart';

class CountryListRes extends BaseResponse {
  List<CountryDetail>? countryList;

  CountryListRes({this.countryList, timestamp, status, error});

  CountryListRes.fromJson(Map<String, dynamic> json) {
    if (json['countryList'] != null) {
      countryList = <CountryDetail>[];
      json['countryList'].forEach((v) {
        countryList!.add(new CountryDetail.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
    error = json['error'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.countryList != null) {
      data['countryList'] = this.countryList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    data['error'] = this.error;
    return data;
  }
}

class CountryDetail {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? mvnoId;
  int? id;
  String? name;
  String? status;
  bool? isDelete;
  bool? delete;

  CountryDetail(
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
      this.delete});

  CountryDetail.fromJson(Map<String, dynamic> json) {
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
    data['delete'] = this.delete;
    return data;
  }
}
