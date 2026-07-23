import 'package:savbill/webservices/base_response.dart';

// class ServicesAreaRes extends BaseResponse {
//   List<ServicesAreaDetail>? dataList;
//
//   ServicesAreaRes({responseCode, responseMessage, this.dataList});
//
//   ServicesAreaRes.fromJson(Map<String, dynamic> json) {
//     responseCode = json['responseCode'];
//     responseMessage = json['responseMessage'];
//     if (json['dataList'] != null) {
//       dataList = <ServicesAreaDetail>[];
//       json['dataList'].forEach((v) {
//         dataList!.add(new ServicesAreaDetail.fromJson(v));
//       });
//     }
//   }
//
//   Map<String, dynamic> toJson() {
//     final Map<String, dynamic> data = new Map<String, dynamic>();
//     data['responseCode'] = this.responseCode;
//     data['responseMessage'] = this.responseMessage;
//     if (this.dataList != null) {
//       data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
//     }
//     return data;
//   }
// }
//
// class ServicesAreaDetail {
//   String? createdate;
//   String? updatedate;
//   String? createdByName;
//   String? lastModifiedByName;
//   int? createdById;
//   int? lastModifiedById;
//   int? id;
//   String? name;
//   String? status;
//   bool? isDeleted;
//   String? latitude;
//   String? longitude;
//   int? areaid;
//   String? areaName;
//   int? mvnoId;
//   bool? selected;
//
//
//   ServicesAreaDetail(
//       {this.createdate,
//       this.updatedate,
//       this.createdByName,
//       this.lastModifiedByName,
//       this.createdById,
//       this.lastModifiedById,
//       this.id,
//       this.name,
//       this.status,
//       this.isDeleted,
//       this.latitude,
//       this.longitude,
//       this.areaid,
//       this.areaName,
//       this.mvnoId,
//       this.selected});
//
//   ServicesAreaDetail.fromJson(Map<String, dynamic> json) {
//     createdate = json['createdate'];
//     updatedate = json['updatedate'];
//     createdByName = json['createdByName'];
//     lastModifiedByName = json['lastModifiedByName'];
//     createdById = json['createdById'];
//     lastModifiedById = json['lastModifiedById'];
//     id = json['id'];
//     name = json['name'];
//     status = json['status'];
//     isDeleted = json['isDeleted'];
//     latitude = json['latitude'];
//     longitude = json['longitude'];
//     areaid = json['areaid'];
//     areaName = json['areaName'];
//     mvnoId = json['mvnoId'];
//   }
//
//   Map<String, dynamic> toJson() {
//     final Map<String, dynamic> data = new Map<String, dynamic>();
//     data['createdate'] = this.createdate;
//     data['updatedate'] = this.updatedate;
//     data['createdByName'] = this.createdByName;
//     data['lastModifiedByName'] = this.lastModifiedByName;
//     data['createdById'] = this.createdById;
//     data['lastModifiedById'] = this.lastModifiedById;
//     data['id'] = this.id;
//     data['name'] = this.name;
//     data['status'] = this.status;
//     data['isDeleted'] = this.isDeleted;
//     data['latitude'] = this.latitude;
//     data['longitude'] = this.longitude;
//     data['areaid'] = this.areaid;
//     data['areaName'] = this.areaName;
//     data['mvnoId'] = this.mvnoId;
//     return data;
//   }
// }
class ServicesAreaRes  extends BaseResponse{
  // int? responseCode;
  String? responseMessage;
  dynamic data;
  List<ServicesAreaDetail>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  ServicesAreaRes(
      {
        // this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  ServicesAreaRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <ServicesAreaDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new ServicesAreaDetail.fromJson(v));
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

class ServicesAreaDetail {
  int? id;
  String? name;
  int? createdById;
  dynamic pincodes;
  bool? selected;

  ServicesAreaDetail({this.id, this.name, this.createdById, this.pincodes,this.selected});

  ServicesAreaDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    createdById = json['createdById'];
    pincodes = json['pincodes'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['createdById'] = this.createdById;
    data['pincodes'] = this.pincodes;
    return data;
  }
}
