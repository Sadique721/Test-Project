class RootCauseSubProblemRes {
  int? responseCode;
  String? responseMessage;
  dynamic data;
  List<RootCauseSubProblemDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  RootCauseSubProblemRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  RootCauseSubProblemRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <RootCauseSubProblemDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new RootCauseSubProblemDataList.fromJson(v));
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

class RootCauseSubProblemDataList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? subCategoryName;
  List<TicketSubCategoryGroupReasonMappingList>?
  ticketSubCategoryGroupReasonMappingList;
  List<TicketSubCategoryTatMappingList>? ticketSubCategoryTatMappingList;
  int? mvnoId;
  bool? isDeleted;
  String? status;
  List<TicketSubCategoryReasonCategoryMappingList>?
  ticketSubCategoryReasonCategoryMappingList;
  int? buId;
  Null? lcoId;
  int? identityKey;
  bool? selected;


  RootCauseSubProblemDataList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.subCategoryName,
        this.ticketSubCategoryGroupReasonMappingList,
        this.ticketSubCategoryTatMappingList,
        this.mvnoId,
        this.isDeleted,
        this.status,
        this.ticketSubCategoryReasonCategoryMappingList,
        this.buId,
        this.lcoId,
        this.identityKey,
      this.selected});

  RootCauseSubProblemDataList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    subCategoryName = json['subCategoryName'];
    if (json['ticketSubCategoryGroupReasonMappingList'] != null) {
      ticketSubCategoryGroupReasonMappingList =
      <TicketSubCategoryGroupReasonMappingList>[];
      json['ticketSubCategoryGroupReasonMappingList'].forEach((v) {
        ticketSubCategoryGroupReasonMappingList!
            .add(new TicketSubCategoryGroupReasonMappingList.fromJson(v));
      });
    }
    if (json['ticketSubCategoryTatMappingList'] != null) {
      ticketSubCategoryTatMappingList = <TicketSubCategoryTatMappingList>[];
      json['ticketSubCategoryTatMappingList'].forEach((v) {
        ticketSubCategoryTatMappingList!
            .add(new TicketSubCategoryTatMappingList.fromJson(v));
      });
    }
    mvnoId = json['mvnoId'];
    isDeleted = json['isDeleted'];
    status = json['status'];
    if (json['ticketSubCategoryReasonCategoryMappingList'] != null) {
      ticketSubCategoryReasonCategoryMappingList =
      <TicketSubCategoryReasonCategoryMappingList>[];
      json['ticketSubCategoryReasonCategoryMappingList'].forEach((v) {
        ticketSubCategoryReasonCategoryMappingList!
            .add(new TicketSubCategoryReasonCategoryMappingList.fromJson(v));
      });
    }
    buId = json['buId'];
    lcoId = json['lcoId'];
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
    data['subCategoryName'] = this.subCategoryName;
    if (this.ticketSubCategoryGroupReasonMappingList != null) {
      data['ticketSubCategoryGroupReasonMappingList'] = this
          .ticketSubCategoryGroupReasonMappingList!
          .map((v) => v.toJson())
          .toList();
    }
    if (this.ticketSubCategoryTatMappingList != null) {
      data['ticketSubCategoryTatMappingList'] =
          this.ticketSubCategoryTatMappingList!.map((v) => v.toJson()).toList();
    }
    data['mvnoId'] = this.mvnoId;
    data['isDeleted'] = this.isDeleted;
    data['status'] = this.status;
    if (this.ticketSubCategoryReasonCategoryMappingList != null) {
      data['ticketSubCategoryReasonCategoryMappingList'] = this
          .ticketSubCategoryReasonCategoryMappingList!
          .map((v) => v.toJson())
          .toList();
    }
    data['buId'] = this.buId;
    data['lcoId'] = this.lcoId;
    data['identityKey'] = this.identityKey;
    return data;
  }
}

class TicketSubCategoryGroupReasonMappingList {
  int? id;
  String? reason;
  int? ticketReasonSubCategoryId;

  TicketSubCategoryGroupReasonMappingList(
      {this.id, this.reason, this.ticketReasonSubCategoryId});

  TicketSubCategoryGroupReasonMappingList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    reason = json['reason'];
    ticketReasonSubCategoryId = json['ticketReasonSubCategoryId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['reason'] = this.reason;
    data['ticketReasonSubCategoryId'] = this.ticketReasonSubCategoryId;
    return data;
  }
}

class TicketSubCategoryTatMappingList {
  int? id;
  int? ticketReasonSubCategoryId;
  TicketTatMatrix? ticketTatMatrix;
  List<TatQueryFieldMappingList>? tatQueryFieldMappingList;
  bool? isDeleted;
  int? orderid;
  int? primaryKey;
  bool? deleteFlag;

  TicketSubCategoryTatMappingList(
      {this.id,
        this.ticketReasonSubCategoryId,
        this.ticketTatMatrix,
        this.tatQueryFieldMappingList,
        this.isDeleted,
        this.orderid,
        this.primaryKey,
        this.deleteFlag});

  TicketSubCategoryTatMappingList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    ticketReasonSubCategoryId = json['ticketReasonSubCategoryId'];
    ticketTatMatrix = json['ticketTatMatrix'] != null
        ? new TicketTatMatrix.fromJson(json['ticketTatMatrix'])
        : null;
    if (json['tatQueryFieldMappingList'] != null) {
      tatQueryFieldMappingList = <TatQueryFieldMappingList>[];
      json['tatQueryFieldMappingList'].forEach((v) {
        tatQueryFieldMappingList!.add(new TatQueryFieldMappingList.fromJson(v));
      });
    }
    isDeleted = json['isDeleted'];
    orderid = json['orderid'];
    primaryKey = json['primaryKey'];
    deleteFlag = json['deleteFlag'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['ticketReasonSubCategoryId'] = this.ticketReasonSubCategoryId;
    if (this.ticketTatMatrix != null) {
      data['ticketTatMatrix'] = this.ticketTatMatrix!.toJson();
    }
    if (this.tatQueryFieldMappingList != null) {
      data['tatQueryFieldMappingList'] =
          this.tatQueryFieldMappingList!.map((v) => v.toJson()).toList();
    }
    data['isDeleted'] = this.isDeleted;
    data['orderid'] = this.orderid;
    data['primaryKey'] = this.primaryKey;
    data['deleteFlag'] = this.deleteFlag;
    return data;
  }
}

class TicketTatMatrix {
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

  TicketTatMatrix(
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

  TicketTatMatrix.fromJson(Map<String, dynamic> json) {
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
  int? mtime1;
  int? mtime2;
  int? mtime3;
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

class TatQueryFieldMappingList {
  int? id;
  String? queryField;
  String? queryOperator;
  String? queryValue;
  String? queryCondition;
  int? tatMappingId;
  bool? isDeleted;

  TatQueryFieldMappingList(
      {this.id,
        this.queryField,
        this.queryOperator,
        this.queryValue,
        this.queryCondition,
        this.tatMappingId,
        this.isDeleted});

  TatQueryFieldMappingList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    queryField = json['queryField'];
    queryOperator = json['queryOperator'];
    queryValue = json['queryValue'];
    queryCondition = json['queryCondition'];
    tatMappingId = json['tatMappingId'];
    isDeleted = json['isDeleted'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['queryField'] = this.queryField;
    data['queryOperator'] = this.queryOperator;
    data['queryValue'] = this.queryValue;
    data['queryCondition'] = this.queryCondition;
    data['tatMappingId'] = this.tatMappingId;
    data['isDeleted'] = this.isDeleted;
    return data;
  }
}

class TicketSubCategoryReasonCategoryMappingList {
  int? id;
  int? ticketReasonCategoryId;
  int? ticketReasonSubCategoryId;

  TicketSubCategoryReasonCategoryMappingList(
      {this.id, this.ticketReasonCategoryId, this.ticketReasonSubCategoryId});

  TicketSubCategoryReasonCategoryMappingList.fromJson(
      Map<String, dynamic> json) {
    id = json['id'];
    ticketReasonCategoryId = json['ticketReasonCategoryId'];
    ticketReasonSubCategoryId = json['ticketReasonSubCategoryId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['ticketReasonCategoryId'] = this.ticketReasonCategoryId;
    data['ticketReasonSubCategoryId'] = this.ticketReasonSubCategoryId;
    return data;
  }
}
