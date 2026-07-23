class WarehouseDetailsRes {
  int? responseCode;
  String? responseMessage;
  WarehouseDetailData? data;
  // Null? dataList;
  // Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  WarehouseDetailsRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        // this.dataList,
        // this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  WarehouseDetailsRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null ? new WarehouseDetailData.fromJson(json['data']) : null;
    // dataList = json['dataList'];
    // excelDataList = json['excelDataList'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    if (this.data != null) {
      data['data'] = this.data!.toJson();
    }
    // data['dataList'] = this.dataList;
    // data['excelDataList'] = this.excelDataList;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class WarehouseDetailData {
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
  List<ServiceAreaNameList>? serviceAreaNameList;
  List<ParenetServiceAreaNameList>? parenetServiceAreaNameList;
  String? warehouseType;
  Null? rmsWarehouseId;
  Null? navWarehouseId;
  String? barnchName;
  List<TeamsList>? teamsList;
  String? warehouseCode;
  int? identityKey;

  WarehouseDetailData(
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
        this.parenetServiceAreaNameList,
        this.warehouseType,
        this.rmsWarehouseId,
        this.navWarehouseId,
        this.barnchName,
        this.teamsList,
        this.warehouseCode,
        this.identityKey});

  WarehouseDetailData.fromJson(Map<String, dynamic> json) {
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
      serviceAreaNameList = <ServiceAreaNameList>[];
      json['serviceAreaNameList'].forEach((v) {
        serviceAreaNameList!.add(new ServiceAreaNameList.fromJson(v));
      });
    }
    if (json['parenetServiceAreaNameList'] != null) {
      parenetServiceAreaNameList = <ParenetServiceAreaNameList>[];
      json['parenetServiceAreaNameList'].forEach((v) {
        parenetServiceAreaNameList!
            .add(new ParenetServiceAreaNameList.fromJson(v));
      });
    }
    warehouseType = json['warehouseType'];
    rmsWarehouseId = json['rmsWarehouseId'];
    navWarehouseId = json['navWarehouseId'];
    barnchName = json['barnchName'];
    if (json['teamsList'] != null) {
      teamsList = <TeamsList>[];
      json['teamsList'].forEach((v) {
        teamsList!.add(new TeamsList.fromJson(v));
      });
    }
    warehouseCode = json['warehouseCode'];
    identityKey = json['identityKey'];
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
    if (this.parenetServiceAreaNameList != null) {
      data['parenetServiceAreaNameList'] =
          this.parenetServiceAreaNameList!.map((v) => v.toJson()).toList();
    }
    data['warehouseType'] = this.warehouseType;
    data['rmsWarehouseId'] = this.rmsWarehouseId;
    data['navWarehouseId'] = this.navWarehouseId;
    data['barnchName'] = this.barnchName;
    if (this.teamsList != null) {
      data['teamsList'] = this.teamsList!.map((v) => v.toJson()).toList();
    }
    data['warehouseCode'] = this.warehouseCode;
    data['identityKey'] = this.identityKey;
    return data;
  }
}

class ServiceAreaNameList {
  int? id;
  String? name;

  ServiceAreaNameList({this.id, this.name});

  ServiceAreaNameList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    return data;
  }
}

class TeamsList {
  int? id;
  String? name;

  TeamsList({this.id, this.name});

  TeamsList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    return data;
  }
}


class ParenetServiceAreaNameList {
  int? id;
  String? name;

  ParenetServiceAreaNameList({this.id, this.name});

  ParenetServiceAreaNameList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    return data;
  }
}
