import 'package:savbill/webservices/base_response.dart';

class CheckReassignTicketRes extends BaseResponse {
  String? data;

  CheckReassignTicketRes({responseCode, responseMessage, this.data});

  CheckReassignTicketRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    data['data'] = this.data;
    return data;
  }
}
