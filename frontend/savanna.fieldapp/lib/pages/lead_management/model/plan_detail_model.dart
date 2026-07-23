import 'package:savbill/pages/customer/model/response/service_area_plan_mode_res.dart';
import 'package:savbill/webservices/base_response.dart';

class PlanDetailsModel  extends BaseResponse{
  ServiceAreaPlanPostpaidplanList? postPaidPlan;
  String? timestamp;
  int? status;

  PlanDetailsModel({this.postPaidPlan, this.timestamp, this.status});

  PlanDetailsModel.fromJson(Map<String, dynamic> json) {
    postPaidPlan = json['postPaidPlan'] != null
        ? new ServiceAreaPlanPostpaidplanList.fromJson(json['postPaidPlan'])
        : null;
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.postPaidPlan != null) {
      data['postPaidPlan'] = this.postPaidPlan!.toJson();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

