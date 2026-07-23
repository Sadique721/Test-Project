import 'package:savbill/webservices/base_response.dart';

class PlanDateDetailRes extends BaseResponse {
  PlanDateDetail? data;

  PlanDateDetailRes({responseCode, responseMessage, this.data});

  PlanDateDetailRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data =
        json['data'] != null ? new PlanDateDetail.fromJson(json['data']) : null;
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

class PlanDateDetail {
  String? expiryDate;
  String? endDate;
  String? startDate;

  PlanDateDetail({this.expiryDate, this.endDate, this.startDate});

  PlanDateDetail.fromJson(Map<String, dynamic> json) {
    expiryDate = json['expiryDate'];
    endDate = json['endDate'];
    startDate = json['startDate'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['expiryDate'] = this.expiryDate;
    data['endDate'] = this.endDate;
    data['startDate'] = this.startDate;
    return data;
  }
}
