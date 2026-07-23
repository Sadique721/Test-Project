import 'package:savbill/pages/ticket_system/model/response/condition_res.dart';
import 'package:savbill/pages/ticket_system/model/response/problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/model/response/tat_ticket_list_res.dart';
import 'package:savbill/webservices/base_response.dart';

class SubProblemDomainListRes extends BaseResponse {
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  List<SubProblemDomainDetail>? dataList;

  SubProblemDomainListRes(
      {responseCode,
      responseMessage,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages,
      this.dataList});

  SubProblemDomainListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    if (json['dataList'] != null) {
      dataList = <SubProblemDomainDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new SubProblemDomainDetail.fromJson(v));
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

class SubProblemDomainDetail {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? subCategoryName;
  ProblemDomainDetail? parentCategory;
  List<TicketSubCategoryGroupReasonMappingList>?
      ticketSubCategoryGroupReasonMappingList;
  List<TicketSubCategoryTatMappingList>? ticketSubCategoryTatMappingList;

  List<TicketSubCategoryReasonCategoryMappingList>?ticketSubCategoryReasonCategoryMappingList;

  int? mvnoId;
  bool? isDeleted;
  String? status;
  int? buId;
  int? identityKey;

  SubProblemDomainDetail(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.subCategoryName,
      this.parentCategory,
      this.ticketSubCategoryGroupReasonMappingList,
      this.ticketSubCategoryTatMappingList,
        this.ticketSubCategoryReasonCategoryMappingList,
      this.mvnoId,
      this.isDeleted,
      this.status,
      this.buId,
      this.identityKey});

  SubProblemDomainDetail.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    subCategoryName = json['subCategoryName'];
    parentCategory = json['parentCategory'] != null
        ? new ProblemDomainDetail.fromJson(json['parentCategory'])
        : null;
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
    if (json['ticketSubCategoryReasonCategoryMappingList'] != null) {
      ticketSubCategoryReasonCategoryMappingList = <TicketSubCategoryReasonCategoryMappingList>[];
      json['ticketSubCategoryReasonCategoryMappingList'].forEach((v) {
        ticketSubCategoryReasonCategoryMappingList!
            .add(TicketSubCategoryReasonCategoryMappingList.fromJson(v));
      });
    }
    mvnoId = json['mvnoId'];
    isDeleted = json['isDeleted'];
    status = json['status'];
    buId = json['buId'];
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
    if (this.parentCategory != null) {
      data['parentCategory'] = this.parentCategory!.toJson();
    }
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
    if (this.ticketSubCategoryReasonCategoryMappingList != null) {
      data['ticketSubCategoryReasonCategoryMappingList'] =
          this.ticketSubCategoryReasonCategoryMappingList!.map((v) => v.toJson()).toList();
    }
    data['mvnoId'] = this.mvnoId;
    data['isDeleted'] = this.isDeleted;
    data['status'] = this.status;
    data['buId'] = this.buId;
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
    data['ticket—'] = this.ticketReasonSubCategoryId;
    return data;
  }
}

class TicketSubCategoryTatMappingList {
  int? id;
  int? ticketReasonSubCategoryId;
  TatTicketDetail? ticketTatMatrix;
  List<TatQueryFieldMappingList>? tatQueryFieldMappingList;
  bool? isDeleted;
  int? orderid;
  bool? deleteFlag;
  int? primaryKey;
  String? txtCondition;

  TicketSubCategoryTatMappingList(
      {this.id,
      this.ticketReasonSubCategoryId,
      this.ticketTatMatrix,
      this.tatQueryFieldMappingList,
      this.isDeleted,
      this.orderid,
      this.deleteFlag,
      this.primaryKey,
      this.txtCondition});

  TicketSubCategoryTatMappingList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    ticketReasonSubCategoryId = json['ticketReasonSubCategoryId'];
    ticketTatMatrix = json['ticketTatMatrix'] != null
        ? new TatTicketDetail.fromJson(json['ticketTatMatrix'])
        : null;
    if (json['tatQueryFieldMappingList'] != null) {
      tatQueryFieldMappingList = <TatQueryFieldMappingList>[];
      json['tatQueryFieldMappingList'].forEach((v) {
        tatQueryFieldMappingList!.add(new TatQueryFieldMappingList.fromJson(v));
      });
    }
    isDeleted = json['isDeleted'];
    orderid = json['orderid'];
    deleteFlag = json['deleteFlag'];
    primaryKey = json['primaryKey'];
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
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
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

  int? uId;
  ConditionDetail? selectedField;
  String? selectedOperator;
  String? selectedCondition;
  bool? enableCondition=false;


  TatQueryFieldMappingList({
    this.id,
    this.queryField,
    this.queryOperator,
    this.queryValue,
    this.queryCondition,
    this.tatMappingId,
    this.isDeleted,

    this.uId,
    this.selectedField,
    this.selectedOperator,
    this.selectedCondition,
    this.enableCondition
  });

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
