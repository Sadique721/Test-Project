import 'package:savbill/webservices/base_response.dart';

// class GetDtvCategoryDataRes extends BaseResponse {
//   int? responseCode;
//   String? responseMessage;
//   // dynamic data;
//   List<DtvCategoryDataList>? dataList;
//   // dynamic excelDataList;
//   int? totalRecords;
//   int? pageRecords;
//   int? currentPageNumber;
//   int? totalPages;
//
//   GetDtvCategoryDataRes(
//       {this.responseCode,
//         this.responseMessage,
//         // this.data,
//         this.dataList,
//         // this.excelDataList,
//         this.totalRecords,
//         this.pageRecords,
//         this.currentPageNumber,
//         this.totalPages});
//
//   GetDtvCategoryDataRes.fromJson(Map<String, dynamic> json) {
//     responseCode = json['responseCode'];
//     responseMessage = json['responseMessage'];
//     // data = json['data'];
//     if (json['dataList'] != null) {
//       dataList = <DtvCategoryDataList>[];
//       json['dataList'].forEach((v) {
//         dataList!.add(new DtvCategoryDataList.fromJson(v));
//       });
//     }
//     // excelDataList = json['excelDataList'];
//     totalRecords = json['totalRecords'];
//     pageRecords = json['pageRecords'];
//     currentPageNumber = json['currentPageNumber'];
//     totalPages = json['totalPages'];
//   }
//
//   Map<String, dynamic> toJson() {
//     final Map<String, dynamic> data = new Map<String, dynamic>();
//     data['responseCode'] = this.responseCode;
//     data['responseMessage'] = this.responseMessage;
//     // data['data'] = this.data;
//     if (this.dataList != null) {
//       data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
//     }
//     // data['excelDataList'] = this.excelDataList;
//     data['totalRecords'] = this.totalRecords;
//     data['pageRecords'] = this.pageRecords;
//     data['currentPageNumber'] = this.currentPageNumber;
//     data['totalPages'] = this.totalPages;
//     return data;
//   }
// }
//
// class DtvCategoryDataList {
//   int? id;
//   String? text;
//   String? value;
//   String? type;
//   String? status;
//   // dynamic subTypeList;
//   int? displayId;
//   String? displayName;
//   int? mvnoId;
//
//   DtvCategoryDataList(
//       {this.id,
//         this.text,
//         this.value,
//         this.type,
//         this.status,
//         // this.subTypeList,
//         this.displayId,
//         this.displayName,
//         this.mvnoId});
//
//   DtvCategoryDataList.fromJson(Map<String, dynamic> json) {
//     id = json['id'];
//     text = json['text'];
//     value = json['value'];
//     type = json['type'];
//     status = json['status'];
//     // subTypeList = json['subTypeList'];
//     displayId = json['displayId'];
//     displayName = json['displayName'];
//     mvnoId = json['mvnoId'];
//   }
//
//   Map<String, dynamic> toJson() {
//     final Map<String, dynamic> data = new Map<String, dynamic>();
//     data['id'] = this.id;
//     data['text'] = this.text;
//     data['value'] = this.value;
//     data['type'] = this.type;
//     data['status'] = this.status;
//     // data['subTypeList'] = this.subTypeList;
//     data['displayId'] = this.displayId;
//     data['displayName'] = this.displayName;
//     data['mvnoId'] = this.mvnoId;
//     return data;
//   }
// }

class GetDtvCategoryDataRes extends BaseResponse {
  // dynamic responseCode;
  // String? responseMessage;
  // Null? data;
  List<DtvCategoryDataList>? dataList;

  // Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  GetDtvCategoryDataRes(
      {
      // this.responseCode,
      // this.responseMessage,
      // this.data,
      this.dataList,
      // this.excelDataList,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages});

  GetDtvCategoryDataRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    // data = json['data'];
    if (json['dataList'] != null) {
      dataList = <DtvCategoryDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(DtvCategoryDataList.fromJson(v));
      });
    }
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
    // data['data'] = this.data;
    if (this.dataList != null) {
      data['dataList'] = dataList!.map((v) => v.toJson()).toList();
    }
    // data['excelDataList'] = this.excelDataList;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class DtvCategoryDataList {
  int? id;
  String? text;
  String? value;
  String? type;
  String? status;
  // Null? subTypeList;
  int? displayId;
  String? displayName;
  int? mvnoId;

  DtvCategoryDataList(
      {this.id,
      this.text,
      this.value,
      this.type,
      this.status,
      // this.subTypeList,
      this.displayId,
      this.displayName,
      this.mvnoId});

  DtvCategoryDataList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    text = json['text'];
    value = json['value'];
    type = json['type'];
    status = json['status'];
    // subTypeList = json['subTypeList'];
    displayId = json['displayId'];
    displayName = json['displayName'];
    mvnoId = json['mvnoId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['text'] = this.text;
    data['value'] = this.value;
    data['type'] = this.type;
    data['status'] = this.status;
    // data['subTypeList'] = this.subTypeList;
    data['displayId'] = this.displayId;
    data['displayName'] = this.displayName;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}
