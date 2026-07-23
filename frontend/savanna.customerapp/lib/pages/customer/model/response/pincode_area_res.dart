import 'package:savbill/pages/customer/model/response/pincode_list_res.dart';
import 'package:savbill/webservices/base_response.dart';

class PincodeToAreaRes extends BaseResponse {
  dynamic responseCode;
  String? responseMessage;
  PincodeDetail? data;

  PincodeToAreaRes({this.responseCode, this.responseMessage, this.data});

  PincodeToAreaRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data =
        json['data'] != null ? new PincodeDetail.fromJson(json['data']) : null;
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
