import 'package:savbill/pages/customer/model/response/cust_plan_detail.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer/model/response/postpaid_planlist_res.dart';
import 'package:savbill/pages/customer_charge/charge_management_provider.dart';
import 'package:savbill/pages/customer_charge/response/add_charge_plan_detail.dart';
import 'package:savbill/pages/customer_charge/response/customer_charge_list_res.dart';
import 'package:savbill/pages/dashboard/model/response/payment_configuration_res.dart';
import 'package:savbill/pages/dashboard/payment_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:get/get.dart';
import 'package:intl/intl.dart';

class ChargeManagementController extends GetxController {
  bool isLoading = false;
  List<PostpaidPlanDetail>? planList = [];
  PostpaidPlanDetail? selectedPlan;
  List<CustChargeOverrideDetail>? chargeOverrideList = [];
  int? custPlanGrpId = 0;
  String customerName = "";
  List<PlanMappingDetail>? planMappingList = [];
  String? currencySymbol;
  CustomerDetail? customerDetail;
  DateFormat apiDateFormat = DateFormat(Constant.DATE_TIME_FORMAT);
  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_NAME] != null) {
        customerName = arguments[Constant.CUSTOMER_NAME];
      }
      if (arguments[Constant.CUSTOMER_PLAN_GRP_ID] != null) {
        custPlanGrpId = arguments[Constant.CUSTOMER_PLAN_GRP_ID];
      }
      if (arguments[Constant.CUSTOMER_PLAN_MAP] != null) {
        planMappingList = arguments[Constant.CUSTOMER_PLAN_MAP];
      }
      if (arguments[Constant.CUSTOMER_DETAIL] != null) {
        customerDetail = arguments[Constant.CUSTOMER_DETAIL];
        getSystemConfigurationData(Strings.currency_payment);
      }
    }
    update();
  }

  /// Currency Symbol
  getSystemConfigurationData(String type) {
    isLoading = true;
    update();
    PaymentProvider().getSystemConfiguration(
      type: type,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PaymentConfigurationRes responseData =
                  PaymentConfigurationRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.data != null) {
                  if (responseData.data!.name!.isNotEmpty &&
                      type.equalsIgnoreCase(Strings.currency_payment)) {
                    currencySymbol = responseData.data!.value;
                  }
                }
              } else {
                if (responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorRed);
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        update();
        getCustomerChargeDetail();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getCustomerChargeDetail();
      },
    );
  }

  /// Charge List
  getCustomerChargeDetail() {
    chargeOverrideList!.clear();
    isLoading = true;
    update();
    ChargeManagementProvider().getCustomerChargeList(
      customerId: customerDetail!.id,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerChargeListRes response =
                  CustomerChargeListRes.fromJson(map);
              if (response.status == 200) {
                if (response.custChargeOverrideList != null &&
                    response.custChargeOverrideList!.isNotEmpty) {
                  chargeOverrideList!.addAll(response.custChargeOverrideList!);
                  for (var element in chargeOverrideList!) {
                    getPlanDetailFromPlanId(element.planid!);
                  }
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  ///charge plan list according to plan id
  Future<void>getPlanDetailFromPlanId(int customerPlanId) async{
    isLoading = true;
    update();
    ChargeManagementProvider().getCustomerPlanDetail(
      planId: customerPlanId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              AddChargePlanDetail responseData =
                  AddChargePlanDetail.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.postPaidPlan != null) {
                  planList?.add(responseData.postPaidPlan!);
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
        return true;
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        return true;
      },
    );
  }

  _handleApiError(ResponseModel error) {
    isLoading = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }
}
