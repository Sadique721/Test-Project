import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/webservices/base_response.dart';

class TicketDetailResponse extends BaseResponse {
  TicketDetail? data;

  TicketDetailResponse({responseCode, responseMessage, this.data});

  TicketDetailResponse.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data =
        json['data'] != null ? new TicketDetail.fromJson(json['data']) : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    if (this.data != null) {
      data['data'] = this.data!.toJson();
    }
    return data;
  }
}
