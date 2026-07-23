import 'package:savbill/pages/change_plan/request/change_plan_req.dart';
import 'package:savbill/pages/change_plan/request/charge_override_req.dart';
import 'package:savbill/pages/change_plan/request/cust_get_plan_filter_req.dart';
import 'package:savbill/pages/change_plan/request/deactivate_plan_req.dart';
import 'package:savbill/pages/change_plan/request/plan_start_end_date_req.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/webservices/api_request.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';

class ChangePlanProvider {
  void getCustomerPlanType({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.generic_request}planPurchaseType",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // check customer is prime
  void chkCustomerPrime({
    required int custId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.check_customer_prime}$custId",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // check customer plan
  void getServiceAreaToPlan({
    required int serviceAreaId,
    required int customerId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url:
          "${UrlConstants.service_area_to_plan}serviceArea?planCategory=Normal&serviceAreaId=$serviceAreaId&planmode=NORMAL&custId=$customerId",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // check customer plan
  void getPremierePlan({
    required int serviceAreaId,
    required int customerId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url:
          "${UrlConstants.premiere_plan}all?custId=${customerId}&isPremiere=true&serviceAreaId=${serviceAreaId}",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // de-active plan (change plan type)
  void deactivatePlan({
    DeactivatePlanReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.deactivate_plan, data: request!).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get plan start-end date
  void planStartEndDateReq({
    PlanStartEndDateReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.get_plan_start_end_date, data: request!)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  //  Get Plans By Filters
  void getPlanByFilters({
    required CustGetPlanByFiltersReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.get_plan_by_filter,
      data: request,
    ).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  //  plan group to plan
  void planGroupToPlan({
    required int planGroupId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.plan_group_to_plan}$planGroupId",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // special plan request
  void getSpecialPlanGroup({
    required int customerId,
    required int serviceAreaId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url:
          "${UrlConstants.change_special_plan}$customerId&planmode=SPECIAL&serviceAreaId=$serviceAreaId",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // customer change plan request
  void changePlan({
    ChangePlanReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.cust_change_plan,
      data: request,
    ).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // Customer Charge Override
  void customerOverrideCharge({
    ChargeOverrideReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.cust_charge_override,
      data: request,
    ).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // customer change plan request
  void getChangePlanGroup({
    Function()? beforeSend,
    required String? changePlanType,
    required int? customerId,
    required int? planGroupId,
    required int? custServiceMappingID,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    Map<String, dynamic> request = {
      "changePlanType": changePlanType,
      "custId": customerId,
      "planGroupId": planGroupId,
      "customerServiceMappingID": custServiceMappingID,
    };
    ApiRequest(
      url: UrlConstants.plan_group_by_filters,
      data: request,
    ).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // changePlanDate
  void customerChangePlanDate({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.change_plan_date,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // deactivatePlanInBulk
  void customerDeActivePlanInBluk(
      {required dynamic finalData,
      Function()? beforeSend,
      Function(ResponseModel responseModel)? onSuccess,
      Function(ResponseModel error)? onError,
      required}) {
    ApiRequest(url: UrlConstants.deActivePlanBulk, data: finalData).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void getCustomerChangePlanDueAmount(
      {required dynamic finalData,
      Function()? beforeSend,
      Function(ResponseModel responseModel)? onSuccess,
      Function(ResponseModel error)? onError,
      required}) {
    ApiRequest(url: UrlConstants.get_customer_change_plan_dueAmount, data: finalData).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// changePlanDate
  void childCustomersChangePlan({
    required PageRequest pageRequest,
    required int? customerId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url:
          "${UrlConstants.get_all_child_customer}?customerId=$customerId&invoiceType=Group",
      data: pageRequest,
    ).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void getCustomerDiscountList({
    required int? id,
    required String? discountType,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    bool isDiscount = false;
    if (discountType!.equalsIgnoreCase("changeDiscount")) {
      isDiscount = true;
    } else {
      isDiscount = false;
    }
    ApiRequest(
            url:
                "${UrlConstants.fetch_customer_discount_detail}/$id?isExpiredRequired=$isDiscount")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// deactivatePlanInBulk
  void customerAddOnPlan(
      {required dynamic finalAddonData,
      Function()? beforeSend,
      Function(ResponseModel responseModel)? onSuccess,
      Function(ResponseModel error)? onError,
      required}) {
    ApiRequest(url: UrlConstants.changeplan_add_on_plan, data: finalAddonData)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get Customer charge by Id and type
  void customerChargeByIdAndType({
    required String type,
    required int? serviceId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String? url;
    if (serviceId != null) {
      url =
          "${UrlConstants.charge_by_type}${type.toString()}?serviceId=$serviceId";
    } else {
      url = "${UrlConstants.charge_by_type}${type.toString()}";
    }
    ApiRequest(url: url).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }
}
