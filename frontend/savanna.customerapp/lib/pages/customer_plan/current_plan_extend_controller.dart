import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/customer_plan/customer_plan_provider.dart';
import 'package:savbill/pages/customer_plan/model/request/extend_current_plan_validity_req.dart';
import 'package:savbill/pages/dashboard/model/response/cust_plan_detail_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/cupertino.dart';
import 'package:get/get.dart';
import 'package:intl/intl.dart';
import '../../util/constant.dart';

class CurrentPlanExtendController extends GetxController {
  bool isLoading = false;
  TextEditingController downTimeStartDateController = TextEditingController();
  TextEditingController downTimeEndDateController = TextEditingController();
  TextEditingController remarksController = TextEditingController();

  DateFormat dateFormat = DateFormat(Constant.API_DATE_FORMAT);
  String? selectedDownTimeStartDate = "", selectedDownTimeEndDate = "";
  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);
  DateTime? selectedExtendFromDate, selectedExtendToDate;
  String? currentTime;
  // PlanDetail? planDetailData;
  CustPlanDataList? planDetailData;

  int customerId = 0;
  String customerName = "";
  String startDateTime = "",endDateTime="";
  DateFormat apiDateTimeFormat = DateFormat(Constant.DATE_TIME_FORMAT_API_US);
  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.EXTEND_VALIDITY] != null) {
        planDetailData = arguments[Constant.EXTEND_VALIDITY];
      }
    }
    update();
  }




  extendCurrentPlanValidity() {
    isLoading = true;
    update();
    bool isPlanGroup = false;
    int? planGroupId = 0;
    ExtendCurrentPlanValidityReq request;
    List<ExtendPlanValidity>? extendPlanValidityList = [];

    if (planDetailData != null && planDetailData!.plangroupid != null) {
      if (planDetailData!.plangroupid!.isNotEmpty ||
          planDetailData!.plangroupid != null) {
        isPlanGroup = true;
        planGroupId = planDetailData!.plangroupid;
      } else {
        isPlanGroup = false;
        planGroupId = null;
      }
    }

    extendPlanValidityList.add(ExtendPlanValidity(
        custPlanMapppingId: planDetailData!.custPlanMapppingId,
        extentionforChild: false,
        // downStartDate: selectedDownTimeStartDate,
        downStartDate: startDateTime,
        // downEndDate: selectedDownTimeEndDate,
        downEndDate: endDateTime,
        extendValidityRemarks: remarksController.text,
        planGroupId: planGroupId,
        planGroup: isPlanGroup));

    request = ExtendCurrentPlanValidityReq(
        extendPlanValidity: extendPlanValidityList);
    log("extendPlanValidityList=>>${jsonEncode(request)}");

    CustomerPlanProvider().extendCurrentPlanValidity(
      extendCurrentPlanValidityReq: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData =
              BaseResponse.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                Get.back(result: true);
                Utils.showSnackbar(Strings.SUCCESS, Strings.successfully,
                    AppTheme.colorWhite, AppTheme.colorGreen);

              } else if (responseData.responseCode == 417) {
                Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorBlueRView);
              } /*else {
                if (responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorRed);
                }
              }*/
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
      },
      onError: (ResponseModel error) {
        update();
        _handleApiError(error);
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
