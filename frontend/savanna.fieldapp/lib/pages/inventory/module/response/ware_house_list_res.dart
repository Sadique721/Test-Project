import 'package:savbill/webservices/base_response.dart';

class WareHouseListRes extends BaseResponse {
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  List<WareHouseDetail>? dataList;

  WareHouseListRes(
      {responseCode,
      responseMessage,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages,
      this.dataList});

  WareHouseListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    if (json['dataList'] != null) {
      dataList = <WareHouseDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new WareHouseDetail.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class WareHouseDetail {
  int? id;
  String? name;
  String? description;
  String? status;
  String? address1;
  String? address2;
  String? pincode;
  String? city;
  String? state;
  String? country;
  String? longitude;
  String? latitude;
  int? mvnoId;
  String? warehouseType;
  int? identityKey;
  dynamic warehouseCode;


  WareHouseDetail(
      {this.id,
      this.name,
      this.description,
      this.status,
      this.address1,
      this.address2,
      this.pincode,
      this.city,
      this.state,
      this.country,
      this.longitude,
      this.latitude,
      this.mvnoId,
      this.warehouseType,
      this.identityKey,
      this.warehouseCode});

  WareHouseDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    description = json['description'];
    status = json['status'];
    address1 = json['address1'];
    address2 = json['address2'];
    pincode = json['pincode'];
    city = json['city'];
    state = json['state'];
    country = json['country'];
    longitude = json['longitude'];
    latitude = json['latitude'];
    mvnoId = json['mvnoId'];
    warehouseType = json['warehouseType'];
    identityKey = json['identityKey'];
    warehouseCode = json['warehouseCode'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['description'] = this.description;
    data['status'] = this.status;
    data['address1'] = this.address1;
    data['address2'] = this.address2;
    data['pincode'] = this.pincode;
    data['city'] = this.city;
    data['state'] = this.state;
    data['country'] = this.country;
    data['longitude'] = this.longitude;
    data['latitude'] = this.latitude;
    data['mvnoId'] = this.mvnoId;
    data['warehouseType'] = this.warehouseType;
    data['identityKey'] = this.identityKey;
    data['warehouseCode'] = this.warehouseCode;
    return data;
  }
}


