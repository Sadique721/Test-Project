import 'package:savbill/pages/customer/model/response/postpaid_planlist_res.dart';
import 'package:savbill/webservices/base_response.dart';

class AddChargePlanDetail extends BaseResponse {
  PostpaidPlanDetail? postPaidPlan;

  AddChargePlanDetail({timestamp, status, this.postPaidPlan});

  AddChargePlanDetail.fromJson(Map<String, dynamic> json) {
    timestamp = json['timestamp'];
    status = json['status'];
    postPaidPlan = json['postPaidPlan'] != null
        ? new PostpaidPlanDetail.fromJson(json['postPaidPlan'])
        : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    if (this.postPaidPlan != null) {
      data['postPaidPlan'] = this.postPaidPlan!.toJson();
    }
    return data;
  }
}
