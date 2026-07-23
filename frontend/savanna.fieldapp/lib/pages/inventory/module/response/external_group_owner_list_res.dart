import 'package:savbill/webservices/base_response.dart';

class ExternalOwnerListRes extends BaseResponse{
  dynamic responseCode;
  String? responseMessage;
  List<ExternalOwnerDataList>? dataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  ExternalOwnerListRes(
      {this.responseCode,
        this.responseMessage,
        this.dataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  ExternalOwnerListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <ExternalOwnerDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(ExternalOwnerDataList.fromJson(v));
      });
    }
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class ExternalOwnerDataList {
  String? createdate;
  String? updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? title;
  String? username;
  String? firstname;
  String? lastname;
  String? password;
  // Servicearea? servicearea;
  String? status;
  int? mvnoId;
  String? fullName;
  dynamic buId;
  int? popid;
  bool? isDeleted;
  dynamic oltid;
  dynamic ezyBillCustomersId;
  dynamic latitude;
  dynamic longitude;
  String? custname;
  dynamic ezyBillAccountNumber;
  dynamic parentExperience;
  String? custtype;
  dynamic networkDeviceId;
  dynamic parentCustomersId;
  int? partnerId;
  int? parentCustId;
  String? nasPort;
  String? ipPoolNameBind;
  String? framedIp;
  String? framedIpBind;
  dynamic masterdbid;
  dynamic splitterid;
  dynamic oltslotid;
  dynamic oltportid;
  int? identityKey;

  ExternalOwnerDataList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.title,
        this.username,
        this.firstname,
        this.lastname,
        this.password,
        // this.servicearea,
        this.status,
        this.mvnoId,
        this.fullName,
        this.buId,
        this.popid,
        this.isDeleted,
        this.oltid,
        this.ezyBillCustomersId,
        this.latitude,
        this.longitude,
        this.custname,
        this.ezyBillAccountNumber,
        this.parentExperience,
        this.custtype,
        this.networkDeviceId,
        this.parentCustomersId,
        this.partnerId,
        this.parentCustId,
        this.nasPort,
        this.ipPoolNameBind,
        this.framedIp,
        this.framedIpBind,
        this.masterdbid,
        this.splitterid,
        this.oltslotid,
        this.oltportid,
        this.identityKey});

  ExternalOwnerDataList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    title = json['title'];
    username = json['username'];
    firstname = json['firstname'];
    lastname = json['lastname'];
    password = json['password'];
    // servicearea = json['servicearea'] != null
    //     ? new Servicearea.fromJson(json['servicearea'])
    //     : null;
    status = json['status'];
    mvnoId = json['mvnoId'];
    fullName = json['fullName'];
    buId = json['buId'];
    popid = json['popid'];
    isDeleted = json['isDeleted'];
    oltid = json['oltid'];
    ezyBillCustomersId = json['ezyBillCustomersId'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    custname = json['custname'];
    ezyBillAccountNumber = json['ezyBillAccountNumber'];
    parentExperience = json['parentExperience'];
    custtype = json['custtype'];
    networkDeviceId = json['networkDeviceId'];
    parentCustomersId = json['parentCustomersId'];
    partnerId = json['partnerId'];
    parentCustId = json['parentCustId'];
    nasPort = json['nasPort'];
    ipPoolNameBind = json['ipPoolNameBind'];
    framedIp = json['framedIp'];
    framedIpBind = json['framedIpBind'];
    masterdbid = json['masterdbid'];
    splitterid = json['splitterid'];
    oltslotid = json['oltslotid'];
    oltportid = json['oltportid'];
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
    data['title'] = this.title;
    data['username'] = this.username;
    data['firstname'] = this.firstname;
    data['lastname'] = this.lastname;
    data['password'] = this.password;
    // if (this.servicearea != null) {
    //   data['servicearea'] = this.servicearea!.toJson();
    // }
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    data['fullName'] = this.fullName;
    data['buId'] = this.buId;
    data['popid'] = this.popid;
    data['isDeleted'] = this.isDeleted;
    data['oltid'] = this.oltid;
    data['ezyBillCustomersId'] = this.ezyBillCustomersId;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['custname'] = this.custname;
    data['ezyBillAccountNumber'] = this.ezyBillAccountNumber;
    data['parentExperience'] = this.parentExperience;
    data['custtype'] = this.custtype;
    data['networkDeviceId'] = this.networkDeviceId;
    data['parentCustomersId'] = this.parentCustomersId;
    data['partnerId'] = this.partnerId;
    data['parentCustId'] = this.parentCustId;
    data['nasPort'] = this.nasPort;
    data['ipPoolNameBind'] = this.ipPoolNameBind;
    data['framedIp'] = this.framedIp;
    data['framedIpBind'] = this.framedIpBind;
    data['masterdbid'] = this.masterdbid;
    data['splitterid'] = this.splitterid;
    data['oltslotid'] = this.oltslotid;
    data['oltportid'] = this.oltportid;
    data['identityKey'] = this.identityKey;
    return data;
  }
}

class Servicearea {
  int? id;
  String? createdate;
  String? updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  int? createdById;
  dynamic lastModifiedById;
  String? name;
  String? status;
  bool? isDeleted;
  int? mvnoId;
  String? latitude;
  String? longitude;
  dynamic areaId;
  List<PincodeList>? pincodeList;
  int? cityid;

  Servicearea(
      {this.id,
        this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.name,
        this.status,
        this.isDeleted,
        this.mvnoId,
        this.latitude,
        this.longitude,
        this.areaId,
        this.pincodeList,
        this.cityid});

  Servicearea.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    name = json['name'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    areaId = json['areaId'];
    if (json['pincodeList'] != null) {
      pincodeList = <PincodeList>[];
      json['pincodeList'].forEach((v) {
        pincodeList!.add(PincodeList.fromJson(v));
      });
    }
    cityid = json['cityid'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['name'] = this.name;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['areaId'] = this.areaId;
    if (this.pincodeList != null) {
      data['pincodeList'] = this.pincodeList!.map((v) => v.toJson()).toList();
    }
    data['cityid'] = this.cityid;
    return data;
  }
}

class PincodeList {
  String? createdate;
  String? updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? pincode;
  String? status;
  bool? isDeleted;
  int? countryId;
  int? cityId;
  int? stateId;
  List<AreaList>? areaList;
  int? mvnoId;

  PincodeList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.pincode,
        this.status,
        this.isDeleted,
        this.countryId,
        this.cityId,
        this.stateId,
        this.areaList,
        this.mvnoId});

  PincodeList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    pincode = json['pincode'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    countryId = json['countryId'];
    cityId = json['cityId'];
    stateId = json['stateId'];
    if (json['areaList'] != null) {
      areaList = <AreaList>[];
      json['areaList'].forEach((v) {
        areaList!.add(new AreaList.fromJson(v));
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
    data['id'] = this.id;
    data['pincode'] = this.pincode;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['countryId'] = this.countryId;
    data['cityId'] = this.cityId;
    data['stateId'] = this.stateId;
    if (this.areaList != null) {
      data['areaList'] = this.areaList!.map((v) => v.toJson()).toList();
    }
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}

class AreaList {
  String? createdate;
  String? updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? status;
  bool? isDeleted;
  int? countryId;
  dynamic cityId;
  int? stateId;
  int? mvnoId;
  bool? deleteFlag;
  int? primaryKey;

  AreaList(
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
        this.cityId,
        this.stateId,
        this.mvnoId,
        this.deleteFlag,
        this.primaryKey});

  AreaList.fromJson(Map<String, dynamic> json) {
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
    cityId = json['cityId'];
    stateId = json['stateId'];
    mvnoId = json['mvnoId'];
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
    data['isDeleted'] = this.isDeleted;
    data['countryId'] = this.countryId;
    data['cityId'] = this.cityId;
    data['stateId'] = this.stateId;
    data['mvnoId'] = this.mvnoId;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}
