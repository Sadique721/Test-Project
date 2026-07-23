import 'package:savbill/webservices/base_response.dart';

class ServiceAreaDetailRes extends BaseResponse {
  ServiceAreaDetailData? data;

  ServiceAreaDetailRes({responseCode, responseMessage, this.data});

  ServiceAreaDetailRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null
        ? new ServiceAreaDetailData.fromJson(json['data'])
        : null;
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

class ServiceAreaDetailData {
  dynamic createdate;
  dynamic updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  String? serviceAreaType;
  int? createdById;
  dynamic lastModifiedById;
  int? id;
  String? name;
  String? status;
  bool? isDeleted;
  int? countryId;
  int? stateId;
  int? cityId;
  int? areaId;

  // String? countryName;
  // String? stateName;
  // String? cityName;
  // int? pincodeId;
  // String? code;
  int? mvnoId;
  dynamic blockNo;
  List<int>? pincodes;


  ServiceAreaDetailData(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.serviceAreaType,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.name,
      this.status,
      this.isDeleted,
      this.countryId,
      this.stateId,
      this.cityId,
      // this.countryName,
      // this.stateName,
      // this.cityName,
      // this.pincodeId,
      // this.code,
        this.areaId,
      this.mvnoId,
      this.blockNo,
      this.pincodes,
      });

  ServiceAreaDetailData.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    serviceAreaType = json['serviceAreaType'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    countryId = json['countryId'];
    stateId = json['stateId'];
    cityId = json['cityId'];
    areaId = json['areaId'];
    // countryName = json['countryName'];
    // stateName = json['stateName'];
    // cityName = json['cityName'];
    // pincodeId = json['pincodeId'];
    // code = json['code'];
    mvnoId = json['mvnoId'];
    blockNo = json['blockNo'];
    pincodes = json['pincodes'].cast<int>();
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['serviceAreaType'] = this.serviceAreaType;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['id'] = this.id;
    data['name'] = this.name;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['countryId'] = this.countryId;
    data['stateId'] = this.stateId;
    data['cityId'] = this.cityId;
    data['areaId'] = this.areaId;
    // data['countryName'] = this.countryName;
    // data['stateName'] = this.stateName;
    // data['cityName'] = this.cityName;
    // data['pincodeId'] = this.pincodeId;
    // data['code'] = this.code;
    data['mvnoId'] = this.mvnoId;
    data['blockNo'] = this.blockNo;
    data['pincodes'] = this.pincodes;
    return data;
  }
}
