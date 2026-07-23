import 'package:savbill/webservices/base_response.dart';

class TicketContactFailedCallRes extends BaseResponse {
  List<String>? contactFailed;
  String? timestamp;
  int? status;

  TicketContactFailedCallRes(
      {this.contactFailed, this.timestamp, this.status});

  TicketContactFailedCallRes.fromJson(Map<String, dynamic> json) {
    contactFailed = json['ContactFailed'].cast<String>();
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['ContactFailed'] = this.contactFailed;
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}
