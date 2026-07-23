import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';

class CustomerTicketRes {
  int? responseCode;
  String? responseMessage;
  List<TicketDetail>? dataList;

  CustomerTicketRes({this.responseCode, this.responseMessage, this.dataList});

  CustomerTicketRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <TicketDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new TicketDetail.fromJson(v));
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
