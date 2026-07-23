import 'package:savbill/webservices/base_response.dart';

class GetAllTypePartnerRes extends BaseResponse{
  String? responseMessage;
  dynamic data;
  List<PartnerTypeDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  GetAllTypePartnerRes(
      {
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  GetAllTypePartnerRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <PartnerTypeDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new PartnerTypeDataList.fromJson(v));
      });
    }
    excelDataList = json['excelDataList'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    data['data'] = this.data;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    data['excelDataList'] = this.excelDataList;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class PartnerTypeDataList {
  String? createdate;
  String? updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  int? id;
  String? name;
  String? status;
  dynamic city;
  dynamic state;
  dynamic country;
  dynamic pincode;
  dynamic partnerType;
  dynamic email;
  // List<Null>? serviceAreaList;
  dynamic parentPartner;
  bool? isDelete;
  int? mvnoId;
  dynamic buId;
  dynamic branch;
  bool? deleteFlag;
  int? primaryKey;

  PartnerTypeDataList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.status,
        this.city,
        this.state,
        this.country,
        this.pincode,
        this.partnerType,
        this.email,
        // this.serviceAreaList,
        this.parentPartner,
        this.isDelete,
        this.mvnoId,
        this.buId,
        this.branch,
        this.deleteFlag,
        this.primaryKey});

  PartnerTypeDataList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    status = json['status'];
    city = json['city'];
    state = json['state'];
    country = json['country'];
    pincode = json['pincode'];
    partnerType = json['partnerType'];
    email = json['email'];
    // if (json['serviceAreaList'] != null) {
    //   serviceAreaList = <Null>[];
    //   json['serviceAreaList'].forEach((v) {
    //     serviceAreaList!.add(new Null.fromJson(v));
    //   });
    // }
    parentPartner = json['parentPartner'];
    isDelete = json['isDelete'];
    mvnoId = json['mvnoId'];
    buId = json['buId'];
    branch = json['branch'];
    deleteFlag = json['deleteFlag'];
    primaryKey = json['primaryKey'];
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
    data['city'] = this.city;
    data['state'] = this.state;
    data['country'] = this.country;
    data['pincode'] = this.pincode;
    data['partnerType'] = this.partnerType;
    data['email'] = this.email;
    // if (this.serviceAreaList != null) {
    //   data['serviceAreaList'] =
    //       this.serviceAreaList!.map((v) => v.toJson()).toList();
    // }
    data['parentPartner'] = this.parentPartner;
    data['isDelete'] = this.isDelete;
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    data['branch'] = this.branch;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}
