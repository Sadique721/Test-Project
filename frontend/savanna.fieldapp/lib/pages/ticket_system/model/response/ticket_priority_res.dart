import 'package:savbill/webservices/base_response.dart';

class TicketPriorityRes extends BaseResponse {
  List<TicketPriority>? dataList;

  TicketPriorityRes({responseCode, responseMessage, this.dataList});

  TicketPriorityRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <TicketPriority>[];
      json['dataList'].forEach((v) {
        dataList!.add(new TicketPriority.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class TicketPriority {
  int? id;
  String? text;
  String? value;
  String? type;
  String? status;
  int? mvnoId;

  TicketPriority(
      {this.id, this.text, this.value, this.type, this.status, this.mvnoId});

  TicketPriority.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    text = json['text'];
    value = json['value'];
    type = json['type'];
    status = json['status'];
    mvnoId = json['mvnoId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['text'] = this.text;
    data['value'] = this.value;
    data['type'] = this.type;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}
