import 'package:savbill/webservices/base_response.dart';

class LeadGenerateNameFollowUpRes extends BaseResponse{
  String? generatedNameOfTheFollowUp;
  String? timestamp;
  int? status;

  LeadGenerateNameFollowUpRes(
      {this.generatedNameOfTheFollowUp, this.timestamp, this.status});

  LeadGenerateNameFollowUpRes.fromJson(Map<String, dynamic> json) {
    generatedNameOfTheFollowUp = json['generatedNameOfTheFollowUp'];
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['generatedNameOfTheFollowUp'] = this.generatedNameOfTheFollowUp;
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}
