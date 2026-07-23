class CustAddressDetail {
  int? id;
  String? addressType;
  String? address1;
  String? address2;
  dynamic landmark;
  int? areaId;
  int? pincodeId;
  int? cityId;
  int? stateId;
  int? countryId;
  int? customerId;
  int? building_mgmt_id;
  String? fullAddress;
  bool? isDelete;

  dynamic name;
  dynamic countryName;
  dynamic stateName;
  dynamic subarea;
  dynamic buildingName;
  dynamic buildingNumber;
  dynamic cityName;
  dynamic code;

  CustAddressDetail({
    this.id,
    this.addressType,
    this.address1,
    this.address2,
    this.landmark,
    this.areaId,
    this.pincodeId,
    this.cityId,
    this.stateId,
    this.countryId,
    this.customerId,
    this.building_mgmt_id,
    this.fullAddress,
    this.isDelete,
    this.name,
    this.countryName,
    this.stateName,
    this.subarea,
    this.buildingName,
    this.buildingNumber,
    this.cityName,
    this.code,
  });

  CustAddressDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    addressType = json['addressType'];
    address1 = json['address1'];
    address2 = json['address2'];
    landmark = json['landmark'];
    areaId = json['areaId'];
    pincodeId = json['pincodeId'];
    cityId = json['cityId'];
    stateId = json['stateId'];
    countryId = json['countryId'];
    customerId = json['customerId'];
    building_mgmt_id = json['building_mgmt_id'];
    fullAddress = json['fullAddress'];
    isDelete = json['isDelete'];
    name = json['name'];
    countryName = json['countryName'];
    stateName = json['stateName'];
    subarea = json['subarea'];
    buildingName = json['buildingName'];
    buildingNumber = json['buildingNumber'];
    cityName = json['cityName'];
    code = json['code'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['addressType'] = this.addressType;
    data['address1'] = this.address1;
    data['address2'] = this.address2;
    data['landmark'] = this.landmark;
    data['areaId'] = this.areaId;
    data['pincodeId'] = this.pincodeId;
    data['cityId'] = this.cityId;
    data['stateId'] = this.stateId;
    data['countryId'] = this.countryId;
    data['customerId'] = this.customerId;
    data['building_mgmt_id'] = this.building_mgmt_id;
    data['fullAddress'] = this.fullAddress;
    data['isDelete'] = this.isDelete;
    data['name'] = this.name;
    data['countryName'] = this.countryName;
    data['stateName'] = this.stateName;
    data['subarea'] = this.subarea;
    data['buildingName'] = this.buildingName;
    data['buildingNumber'] = this.buildingNumber;
    data['cityName'] = this.cityName;
    data['code'] = this.code;
    return data;
  }
}



/*
class CustAddressDetail {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? addressType;
  Null? address1;
  Null? address2;
  String? landmark;
  String? landmark1;
  AreaList? area;
  int? areaId;
  PincodeList? pincode;
  int? pincodeId;
  City? city;
  int? cityId;
  State? state;
  int? stateId;
  Country? country;
  int? countryId;
  String? fullAddress;
  Null? nextTeamHierarchyMappingId;
  int? nextStaff;
  String? status;
  String? version;
  int? shiftId;
  Null? shiftedPartnerId;
  Null? shitedServiceAreaId;
  Null? requestedByName;
  Null? requestedDate;
  bool? isDelete;

  CustAddressDetail(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.addressType,
        this.address1,
        this.address2,
        this.landmark,
        this.landmark1,
        this.area,
        this.areaId,
        this.pincode,
        this.pincodeId,
        this.city,
        this.cityId,
        this.state,
        this.stateId,
        this.country,
        this.countryId,
        this.fullAddress,
        this.nextTeamHierarchyMappingId,
        this.nextStaff,
        this.status,
        this.version,
        this.shiftId,
        this.shiftedPartnerId,
        this.shitedServiceAreaId,
        this.requestedByName,
        this.requestedDate,
        this.isDelete});

  CustAddressDetail.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    addressType = json['addressType'];
    address1 = json['address1'];
    address2 = json['address2'];
    landmark = json['landmark'];
    landmark1 = json['landmark1'];
    area = json['area'] != null ? new AreaList.fromJson(json['area']) : null;
    areaId = json['areaId'];
    pincode = json['pincode'] != null
        ? new PincodeList.fromJson(json['pincode'])
        : null;
    pincodeId = json['pincodeId'];
    city = json['city'] != null ? new City.fromJson(json['city']) : null;
    cityId = json['cityId'];
    state = json['state'] != null ? new State.fromJson(json['state']) : null;
    stateId = json['stateId'];
    country =
    json['country'] != null ? new Country.fromJson(json['country']) : null;
    countryId = json['countryId'];
    fullAddress = json['fullAddress'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
    nextStaff = json['nextStaff'];
    status = json['status'];
    version = json['version'];
    shiftId = json['shiftId'];
    shiftedPartnerId = json['shiftedPartnerId'];
    shitedServiceAreaId = json['shitedServiceAreaId'];
    requestedByName = json['requestedByName'];
    requestedDate = json['requestedDate'];
    isDelete = json['isDelete'];
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
    data['addressType'] = this.addressType;
    data['address1'] = this.address1;
    data['address2'] = this.address2;
    data['landmark'] = this.landmark;
    data['landmark1'] = this.landmark1;
    if (this.area != null) {
      data['area'] = this.area!.toJson();
    }
    data['areaId'] = this.areaId;
    if (this.pincode != null) {
      data['pincode'] = this.pincode!.toJson();
    }
    data['pincodeId'] = this.pincodeId;
    if (this.city != null) {
      data['city'] = this.city!.toJson();
    }
    data['cityId'] = this.cityId;
    if (this.state != null) {
      data['state'] = this.state!.toJson();
    }
    data['stateId'] = this.stateId;
    if (this.country != null) {
      data['country'] = this.country!.toJson();
    }
    data['countryId'] = this.countryId;
    data['fullAddress'] = this.fullAddress;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    data['nextStaff'] = this.nextStaff;
    data['status'] = this.status;
    data['version'] = this.version;
    data['shiftId'] = this.shiftId;
    data['shiftedPartnerId'] = this.shiftedPartnerId;
    data['shitedServiceAreaId'] = this.shitedServiceAreaId;
    data['requestedByName'] = this.requestedByName;
    data['requestedDate'] = this.requestedDate;
    data['isDelete'] = this.isDelete;
    return data;
  }
}

class PincodeList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
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
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? status;
  bool? isDeleted;
  int? countryId;
  int? cityId;
  int? stateId;
  int? mvnoId;
  int? primaryKey;
  bool? deleteFlag;

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
        this.primaryKey,
        this.deleteFlag});

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
    primaryKey = json['primaryKey'];
    deleteFlag = json['deleteFlag'];
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
    data['primaryKey'] = this.primaryKey;
    data['deleteFlag'] = this.deleteFlag;
    return data;
  }
}

class City {
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
  bool? isDelete;
  int? mvnoId;

  City(
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
        this.isDelete,
        this.mvnoId});

  City.fromJson(Map<String, dynamic> json) {
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
    data['isDelete'] = this.isDelete;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}

class State {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? status;
  // List<CityList>? cityList;
  bool? isDeleted;
  int? mvnoId;

  State(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.status,
        // this.cityList,
        this.isDeleted,
        this.mvnoId});

  State.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    status = json['status'];
    // if (json['cityList'] != null) {
    //   cityList = <CityList>[];
    //   json['cityList'].forEach((v) {
    //     cityList!.add(new CityList.fromJson(v));
    //   });
    // }
    isDeleted = json['isDeleted'];
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
    // if (this.cityList != null) {
    //   data['cityList'] = this.cityList!.map((v) => v.toJson()).toList();
    // }
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}

class Country {
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
  // List<StateList>? stateList;
  bool? isDelete;

  Country(
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
        // this.stateList,
        this.isDelete});

  Country.fromJson(Map<String, dynamic> json) {
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
    // if (json['stateList'] != null) {
    //   stateList = <StateList>[];
    //   json['stateList'].forEach((v) {
    //     stateList!.add(new StateList.fromJson(v));
    //   });
    // }
    isDelete = json['isDelete'];
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
    // if (this.stateList != null) {
    //   data['stateList'] = this.stateList!.map((v) => v.toJson()).toList();
    // }
    data['isDelete'] = this.isDelete;
    return data;
  }
}*/
