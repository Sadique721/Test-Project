import 'package:savbill/pages/inventory/module/response/staff_service_area_res.dart';
import 'package:savbill/webservices/base_response.dart';

class WareHouseDetailRes extends BaseResponse {
  WareHouseData? data;

  WareHouseDetailRes({responseCode, responseMessage, this.data});

  WareHouseDetailRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data =
        json['data'] != null ? new WareHouseData.fromJson(json['data']) : null;
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

class WareHouseData {
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
  List<StaffServiceAreaDetail>? serviceAreaNameList;
  String? warehouseType;
  int? identityKey;
  List<StaffServiceAreaDetail>? parentServiceAreaNameList;
  int? branchId;
  List<int>? teamsIdsList;
  dynamic warehouseCode;

  WareHouseData(
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
      this.serviceAreaNameList,
      this.parentServiceAreaNameList,
      this.warehouseType,
      this.identityKey,
      this.branchId,
        this.teamsIdsList,
      this.warehouseCode
      });

  WareHouseData.fromJson(Map<String, dynamic> json) {
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
    if (json['serviceAreaNameList'] != null) {
      serviceAreaNameList = <StaffServiceAreaDetail>[];
      json['serviceAreaNameList'].forEach((v) {
        serviceAreaNameList!.add(new StaffServiceAreaDetail.fromJson(v));
      });
    }
    warehouseType = json['warehouseType'];
    identityKey = json['identityKey'];
    branchId = json['branchId'];
    teamsIdsList = json['teamsIdsList'].cast<int>();
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
    if (this.serviceAreaNameList != null) {
      data['serviceAreaNameList'] =
          this.serviceAreaNameList!.map((v) => v.toJson()).toList();
    }
    data['warehouseType'] = this.warehouseType;
    data['identityKey'] = this.identityKey;
    data['branchId'] = this.branchId;
    data['teamsIdsList'] = this.teamsIdsList;
    data['warehouseCode'] = this.warehouseCode;
    return data;
  }
}
