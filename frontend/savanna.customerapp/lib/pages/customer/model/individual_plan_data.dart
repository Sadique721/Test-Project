import 'package:savbill/pages/customer/model/response/get_all_services_by_service_area_res.dart';
import 'package:savbill/pages/customer/model/response/plan_services_res.dart';
import 'package:savbill/pages/customer/model/response/postpaid_planlist_res.dart';
import 'package:savbill/pages/customer/model/response/service_area_plan_mode_res.dart';

class IndividualPlanData {
  // PlanServiceDetail? planService;
  ServicesByServiceAreaDataList? planService;
  // PostpaidPlanDetail? planDetail;
  // ServiceAreaPlanPostpaidplanList? planDetail;
  ServiceAreaPlanPostpaidplanList? planDetail;
  String? discount;
  String? discountType;
  bool? trialPlan = false;
  String? newOfferPrice;
  String? planOfferPrice;
  int? type; // 1 for customer bill (trial plan) , 2 for SUBISU (new offer price)

  IndividualPlanData(
      {this.planService,
      this.planDetail,
      this.discount,
      this.discountType,
      this.trialPlan,
      this.newOfferPrice,
      this.planOfferPrice,
      this.type});
}
