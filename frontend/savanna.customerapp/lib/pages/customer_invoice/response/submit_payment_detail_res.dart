
import '../../../webservices/base_response.dart';

class SubmitPaymentDetailRes extends BaseResponse {
  String? paytmRedirectUrl;
  String? paytmRedirectUrl2;
  String? timestamp;
  int? status;

  SubmitPaymentDetailRes(
      {this.paytmRedirectUrl,
        this.paytmRedirectUrl2,
        this.timestamp,
        this.status});

  SubmitPaymentDetailRes.fromJson(Map<String, dynamic> json) {
    paytmRedirectUrl = json['paytmRedirectUrl'];
    paytmRedirectUrl2 = json['paytmRedirectUrl2'];
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['paytmRedirectUrl'] = this.paytmRedirectUrl;
    data['paytmRedirectUrl2'] = this.paytmRedirectUrl2;
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}
