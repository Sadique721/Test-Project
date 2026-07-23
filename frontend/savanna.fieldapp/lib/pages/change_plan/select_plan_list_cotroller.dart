import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/change_plan/change_plan_provicer.dart';
import 'package:savbill/pages/change_plan/request/cust_get_plan_filter_req.dart';
import 'package:savbill/pages/change_plan/response/cust_get_plan_by_filter_res.dart';
import 'package:savbill/pages/customer/model/response/postpaid_planlist_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:get/get.dart';
import '../customer/customer_provider.dart';
import '../customer/model/response/plan_service_by_customer_res.dart';

class SelectPlanListController extends GetxController {
  bool isLoading = false;

  int planGroupId = 0;
  int customerId = 0;
  int serviceAreaId = 0;
  String? planType;
  // List<PostpaidPlanDetail>? planList = [];
  // PostpaidPlanDetail? selectPlanGroupp;
  List<CustomerPlanServiceDetail>? planServiceList = [];

  CustGetPlansByFiltersRes? newPlanData;
  CustomerPlanServiceDetail? selectedPlanService;
  bool disablePlanGroup = false, selectPlan = false;
  String? selectedPlanGroupItem = "";
  List<String> selectPlanItem = [];

  List<PostpaidPlanDetail> selectPlanItemList = [];
  PostpaidPlanDetail? planItemData;

  List<PostpaidPlanDetail>? premierePlanAllData = [];
  List<PostpaidPlanDetail>? selectedPlanAllData = [];

  var connectionNumber;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.PLAN_GROUP_ID] != null) {
        planGroupId = arguments[Constant.PLAN_GROUP_ID];
      }

      if (arguments[Constant.SERVICE_AREA_ID] != null) {
        serviceAreaId = arguments[Constant.SERVICE_AREA_ID];
      }

      if (arguments[Constant.CUSTOMER_ID] != null) {
        customerId = arguments[Constant.CUSTOMER_ID];
      }

      if (arguments[Constant.SELECT_PLAN_TYPE] != null) {
        planType = arguments[Constant.SELECT_PLAN_TYPE];
      }
    }
    update();

    // planGroupToPlan();
    getPlanServiceData();
  }

  getCustomerPremierePlan() {
    premierePlanAllData!.clear();
    isLoading = true;
    update();
    ChangePlanProvider().getPremierePlan(
      serviceAreaId: serviceAreaId,
      customerId: customerId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PostpaidPlanListRes responseData =
              PostpaidPlanListRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.postpaidplanList != null &&
                    responseData.postpaidplanList!.isNotEmpty) {
                  premierePlanAllData!.addAll(responseData.postpaidplanList!);
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

        /// planGroupMappings with normal
        // getCustomerPlan();
        // getPlanGroupDetail();
        // getPlanGroupNormalDetail();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        // getCustomerPlan();
        // getPlanGroupDetail();
        // getPlanGroupNormalDetail();
      },
    );
  }

  filterPlanList(List<PostpaidPlanDetail> planList,
      CustomerPlanServiceDetail listMapping) {
    for (var element in planList) {
      if (element.serviceId == listMapping.serviceId) {
        if (element.planGroup!.equalsIgnoreCase("Registration and Renewal") ||
            element.planGroup!.equalsIgnoreCase("Registration")) {
          selectedPlanAllData!.add(element);
        }
      }
    }
  }

  getPlanServiceData() {
    planServiceList!.clear();
    isLoading = true;
    update();
    CustomerProvider().getCustomerService(
      customerId: customerId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PlanServiceByCustomerRes responseData =
              PlanServiceByCustomerRes.fromJson(map);
              if (responseData.responseCode == 200 ||
                  responseData.responseCode == 0) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  planServiceList?.addAll(responseData.dataList!);
                }
              } else {
                if (responseData.responseMessage != null &&
                    responseData.responseMessage!.isNotEmpty) {
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
        isLoading = false;
        update();
        // planGroupToPlan(planServiceList!);
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        // planGroupToPlan(planServiceList!);
      },
    );
  }

  planGroupToPlan(
      {bool? isSelectedPlan,
        CustomerPlanServiceDetail? planServiceList,
        int? index,
        bool? isChildPlan,
        int? childIndex}) {
    // planList!.clear();
    isLoading = true;
    CustGetPlanByFiltersReq payLoadRequest = CustGetPlanByFiltersReq(
      changePlanType: planType,
      custId: customerId,
      serviceId: planServiceList!.serviceId,
      customerServiceMappingID: planServiceList.customerServiceMappingId,
    );

    log("CustGetPlanByFiltersReq===>${jsonEncode(payLoadRequest)}");
    update();

    ChangePlanProvider().getPlanByFilters(
      request: payLoadRequest,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              List<CustGetPlansByFiltersRes> users =
              (responseModel.result as List)
                  .map((data) => CustGetPlansByFiltersRes.fromJson(data))
                  .toList();

              // newPlanData[data['connection_no']] = response
              //     .where((item) => item['plantype'] == planType)
              //     .toList();
            } on Exception catch (e) {
              log("ExceptionCAtch==>>>$e");
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
        // getCustomerPremierePlan();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        // getCustomerPremierePlan();
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
