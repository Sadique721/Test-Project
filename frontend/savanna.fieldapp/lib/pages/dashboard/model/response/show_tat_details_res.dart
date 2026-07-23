class ShowTATDetailsRes {
  int? responseCode;
  String? responseMessage;
  ShowTATDetailsData? data;
  Null? dataList;
  Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  ShowTATDetailsRes(
      {this.responseCode,
      this.responseMessage,
      this.data,
      this.dataList,
      this.excelDataList,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages});

  ShowTATDetailsRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null ? ShowTATDetailsData.fromJson(json['data']) : null;
    dataList = json['dataList'];
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
    if (this.data != null) {
      data['data'] = this.data!.toJson();
    }
    data['dataList'] = this.dataList;
    data['excelDataList'] = this.excelDataList;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class ShowTATDetailsData {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? status;
  int? slaTimep1;
  int? slaTimep2;
  int? slaTime3;
  String? sunitp1;
  String? sunitp2;
  String? sunitp3;
  List<TatMatrixMappings>? tatMatrixMappings;
  int? buId;
  int? mvnoId;
  int? rtime;
  String? runit;
  bool? isDeleted;
  int? primaryKey;
  bool? deleteFlag;

  ShowTATDetailsData(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.name,
      this.status,
      this.slaTimep1,
      this.slaTimep2,
      this.slaTime3,
      this.sunitp1,
      this.sunitp2,
      this.sunitp3,
      this.tatMatrixMappings,
      this.buId,
      this.mvnoId,
      this.rtime,
      this.runit,
      this.isDeleted,
      this.primaryKey,
      this.deleteFlag});

  ShowTATDetailsData.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    status = json['status'];
    slaTimep1 = json['slaTimep1'];
    slaTimep2 = json['slaTimep2'];
    slaTime3 = json['slaTime3'];
    sunitp1 = json['sunitp1'];
    sunitp2 = json['sunitp2'];
    sunitp3 = json['sunitp3'];
    if (json['tatMatrixMappings'] != null) {
      tatMatrixMappings = <TatMatrixMappings>[];
      json['tatMatrixMappings'].forEach((v) {
        tatMatrixMappings!.add(new TatMatrixMappings.fromJson(v));
      });
    }
    buId = json['buId'];
    mvnoId = json['mvnoId'];
    rtime = json['rtime'];
    runit = json['runit'];
    isDeleted = json['isDeleted'];
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
    data['slaTimep1'] = this.slaTimep1;
    data['slaTimep2'] = this.slaTimep2;
    data['slaTime3'] = this.slaTime3;
    data['sunitp1'] = this.sunitp1;
    data['sunitp2'] = this.sunitp2;
    data['sunitp3'] = this.sunitp3;
    if (this.tatMatrixMappings != null) {
      data['tatMatrixMappings'] =
          this.tatMatrixMappings!.map((v) => v.toJson()).toList();
    }
    data['buId'] = this.buId;
    data['mvnoId'] = this.mvnoId;
    data['rtime'] = this.rtime;
    data['runit'] = this.runit;
    data['isDeleted'] = this.isDeleted;
    data['primaryKey'] = this.primaryKey;
    data['deleteFlag'] = this.deleteFlag;
    return data;
  }
}

class TatMatrixMappings {
  int? id;
  int? orderNo;
  String? level;
  num? mtime1;
  num? mtime2;
  num? mtime3;
  String? munit;
  String? action;
  int? tatMappingtId;
  bool? isDeleted;

  TatMatrixMappings(
      {this.id,
      this.orderNo,
      this.level,
      this.mtime1,
      this.mtime2,
      this.mtime3,
      this.munit,
      this.action,
      this.tatMappingtId,
      this.isDeleted});

  TatMatrixMappings.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    orderNo = json['orderNo'];
    level = json['level'];
    mtime1 = json['mtime1'];
    mtime2 = json['mtime2'];
    mtime3 = json['mtime3'];
    munit = json['munit'];
    action = json['action'];
    tatMappingtId = json['tatMappingtId'];
    isDeleted = json['isDeleted'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['orderNo'] = this.orderNo;
    data['level'] = this.level;
    data['mtime1'] = this.mtime1;
    data['mtime2'] = this.mtime2;
    data['mtime3'] = this.mtime3;
    data['munit'] = this.munit;
    data['action'] = this.action;
    data['tatMappingtId'] = this.tatMappingtId;
    data['isDeleted'] = this.isDeleted;
    return data;
  }
}
