import 'package:savbill/webservices/base_response.dart';

class LeadAllRejectedReasonRes extends BaseResponse {
  List<RejectReasonList>? rejectReasonList;
  String? timestamp;
  int? status;

  LeadAllRejectedReasonRes(
      {this.rejectReasonList, this.timestamp, this.status});

  LeadAllRejectedReasonRes.fromJson(Map<String, dynamic> json) {
    if (json['rejectReasonList'] != null) {
      rejectReasonList = <RejectReasonList>[];
      json['rejectReasonList'].forEach((v) {
        rejectReasonList!.add(new RejectReasonList.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.rejectReasonList != null) {
      data['rejectReasonList'] =
          this.rejectReasonList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class RejectReasonList {
  int? id;
  String? name;
  String? status;
  List<RejectSubReasonList>? rejectSubReasonList;
  bool? isDelete;
  int? mvnoId;
  Null? buId;

  RejectReasonList(
      {this.id,
        this.name,
        this.status,
        this.rejectSubReasonList,
        this.isDelete,
        this.mvnoId,
        this.buId});

  RejectReasonList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    status = json['status'];
    if (json['rejectSubReasonList'] != null) {
      rejectSubReasonList = <RejectSubReasonList>[];
      json['rejectSubReasonList'].forEach((v) {
        rejectSubReasonList!.add(new RejectSubReasonList.fromJson(v));
      });
    }
    isDelete = json['isDelete'];
    mvnoId = json['mvnoId'];
    buId = json['buId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['status'] = this.status;
    if (this.rejectSubReasonList != null) {
      data['rejectSubReasonList'] =
          this.rejectSubReasonList!.map((v) => v.toJson()).toList();
    }
    data['isDelete'] = this.isDelete;
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    return data;
  }
}

class RejectSubReasonList {
  int? id;
  String? name;
  bool? isDelete;

  RejectSubReasonList({this.id, this.name, this.isDelete});

  RejectSubReasonList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    isDelete = json['isDelete'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['isDelete'] = this.isDelete;
    return data;
  }
}
