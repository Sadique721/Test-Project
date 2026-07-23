class BranchesByServiceAreaRes {
  int? responseCode;
  String? responseMessage;
  dynamic data;
  List<BranchesByServiceAreaDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  BranchesByServiceAreaRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  BranchesByServiceAreaRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <BranchesByServiceAreaDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(BranchesByServiceAreaDataList.fromJson(v));
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

class BranchesByServiceAreaDataList {
  int? id;
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  String? name;
  String? status;
  bool? isDeleted;
  dynamic branchCode;
  int? mvnoId;
  List<int>? serviceAreaIdsList;
  List<String>? serviceAreaNameList;
  bool? revenueSharing;
  dynamic sharingPercentage;
  dynamic dunningDays;
  int? displayId;
  String? displayName;
  // List<BranchServiceMappingEntityList>? branchServiceMappingEntityList;

  BranchesByServiceAreaDataList(
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
        this.branchCode,
        this.mvnoId,
        this.serviceAreaIdsList,
        this.serviceAreaNameList,
        this.revenueSharing,
        this.sharingPercentage,
        this.dunningDays,
        this.displayId,
        this.displayName,
        // this.branchServiceMappingEntityList
      });

  BranchesByServiceAreaDataList.fromJson(Map<String, dynamic> json) {
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
    branchCode = json['branch_code'];
    mvnoId = json['mvnoId'];
    serviceAreaIdsList = json['serviceAreaIdsList'].cast<int>();
    serviceAreaNameList = json['serviceAreaNameList'].cast<String>();
    revenueSharing = json['revenue_sharing'];
    sharingPercentage = json['sharing_percentage'];
    dunningDays = json['dunningDays'];
    displayId = json['displayId'];
    displayName = json['displayName'];
    // if (json['branchServiceMappingEntityList'] != null) {
    //   branchServiceMappingEntityList = <BranchServiceMappingEntityList>[];
    //   json['branchServiceMappingEntityList'].forEach((v) {
    //     branchServiceMappingEntityList!
    //         .add(new BranchServiceMappingEntityList.fromJson(v));
    //   });
    // }
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
    data['branch_code'] = this.branchCode;
    data['mvnoId'] = this.mvnoId;
    data['serviceAreaIdsList'] = this.serviceAreaIdsList;
    data['serviceAreaNameList'] = this.serviceAreaNameList;
    data['revenue_sharing'] = this.revenueSharing;
    data['sharing_percentage'] = this.sharingPercentage;
    data['dunningDays'] = this.dunningDays;
    data['displayId'] = this.displayId;
    data['displayName'] = this.displayName;
    // if (this.branchServiceMappingEntityList != null) {
    //   data['branchServiceMappingEntityList'] =
    //       this.branchServiceMappingEntityList!.map((v) => v.toJson()).toList();
    // }
    return data;
  }
}

class BranchServiceMappingEntityList {
  int? id;
  int? branchId;
  int? serviceId;
  int? revenueShareper;
  bool? isDeleted;

  BranchServiceMappingEntityList(
      {this.id,
        this.branchId,
        this.serviceId,
        this.revenueShareper,
        this.isDeleted});

  BranchServiceMappingEntityList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    branchId = json['branchId'];
    serviceId = json['serviceId'];
    revenueShareper = json['revenueShareper'];
    isDeleted = json['isDeleted'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['branchId'] = this.branchId;
    data['serviceId'] = this.serviceId;
    data['revenueShareper'] = this.revenueShareper;
    data['isDeleted'] = this.isDeleted;
    return data;
  }
}
