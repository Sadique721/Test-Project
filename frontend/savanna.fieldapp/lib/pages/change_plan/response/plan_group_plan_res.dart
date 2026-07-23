import 'package:savbill/pages/customer/model/response/postpaid_planlist_res.dart';
import 'package:savbill/webservices/base_response.dart';

class PlanGroupPlanListRes extends BaseResponse {
  List<PostpaidPlanDetail>? planList;

  PlanGroupPlanListRes({this.planList, timestamp, error, status});

  PlanGroupPlanListRes.fromJson(Map<String, dynamic> json) {
    if (json['planList'] != null) {
      planList = <PostpaidPlanDetail>[];
      json['planList'].forEach((v) {
        planList!.add(new PostpaidPlanDetail.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    error = json['error'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.planList != null) {
      data['planList'] = this.planList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['error'] = this.error;
    data['status'] = this.status;
    return data;
  }
}
