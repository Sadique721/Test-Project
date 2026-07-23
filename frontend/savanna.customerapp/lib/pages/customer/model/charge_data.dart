import 'package:savbill/pages/customer/model/individual_plan_data.dart';
import 'package:savbill/pages/customer/model/response/charge_list_res.dart';
import 'package:savbill/pages/customer/model/response/postpaid_planlist_res.dart';

class ChargeData {
  ChargeDetail? chargeDetail;
  IndividualPlanData? chargePlan;

  //String? validity;
  String? price;
  int? recMonth;

  // String? chargeDate;
  String? chargeType;

  PostpaidPlanDetail? planDetail;

  ChargeData(
      {this.chargeDetail,
      this.chargeType,
      this.recMonth,
      this.price,
      //this.actualPrice,
      this.chargePlan,
      this.planDetail});
}
