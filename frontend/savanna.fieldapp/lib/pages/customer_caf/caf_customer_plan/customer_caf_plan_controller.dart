import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer_plan/change_subscribe_trial_plan_dialog.dart';
import 'package:savbill/pages/customer_plan/customer_plan_provider.dart';
import 'package:savbill/pages/customer_plan/model/request/cancel_trial_plan_req.dart';
import 'package:savbill/pages/customer_plan/model/response/cancel_trial_plan_res.dart';
import 'package:savbill/pages/dashboard/model/response/cust_plan_detail_res.dart';
import 'package:savbill/pages/login/model/response/role_operation_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class CustomerCafPlanController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();
  // List<CustPlanDataList>? trialPlanList = [];
  List<CustPlanDataList>? activePlanList = [];

  List<CustPlanDataList>? futurePlanList = [];
  List<CustPlanDataList>? expiredPlanList = [];

  TrialPlanResponse? cancelTrialPlanRes;

  // UserDetail? userDetail;
  bool isCallAllApi = true;
  int tabIndex = 0;

  int customerId = 0;
  String customerName = "";

  bool checkBtnClickEvent = false;
  UserDetail? userData;
  RoleOperationDataList? roleOperationData;
  CustomerDetail? customerDetail;

  @override
  void onInit() {
    super.onInit();

    initPlatformState();
  }

  Future<void> initPlatformState() async {
    String strUserData = "";
    String roleOperation = "";
    if (getStorage.hasData(Constant.USER_DATA)) {
      strUserData = await getStorage.read(Constant.USER_DATA);
    }

    if (getStorage.hasData(Constant.ROLE_OPRATION)) {
      roleOperation = await getStorage.read(Constant.ROLE_OPRATION);
    }

    // log("roleOperation====>>>>${jsonDecode(roleOperation)}");
    // if (!roleOperation.isNullOrEmpty()) {
    //   roleOperationData = RoleOperationDataList.fromJson(jsonDecode(roleOperation));
    //   update();
    // }
    if (!strUserData.isNullOrEmpty()) {
      userData = UserDetail.fromJson(jsonDecode(strUserData));
      update();
    }

    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_NAME] != null) {
        customerName = arguments[Constant.CUSTOMER_NAME];
      }

      if (arguments[Constant.CUSTOMER_PLAN] != null) {
        customerDetail = arguments[Constant.CUSTOMER_PLAN];
      }
      if (arguments[Constant.CUSTOMER_ID] != null) {
        customerId = arguments[Constant.CUSTOMER_ID];
        // getActivePlanListData();
        getActivePlanListData(true);
      }
    }
    update();
  }

  /*deleteInwardsData(InwardsDetail item, int index) {
    isLoading = true;
    update();
    InventoryManagementProvider().deleteInwards(
      request: item,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                inwardsList!.removeAt(index);
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
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }*/


  getActivePlanListData(bool isChangePlan) {
    String apiUrl = UrlConstants.get_active_plan;
    apiUrl = "${apiUrl}/${customerId.toString()}?isNotChangePlan=$isChangePlan";
    isLoading = true;
    update();
    CustomerPlanProvider().getActivePlanList(
      url: apiUrl,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustPlanDetailResponse responseData =
                  CustPlanDetailResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  activePlanList?.clear();
                  // activePlanList?.addAll(responseData.dataList!);
                  activePlanList = responseData.dataList!
                      .where((element) => !element.custPlanStatus!
                          .toLowerCase()
                          .equalsIgnoreCase("newactivation"))
                      .toList();
                }
              }
              else if (responseData.responseCode == 417) {
                Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorBlueRView);
              } else {
                if (responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorBlueRView);
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
        if (isCallAllApi) {
          getFuturePlanListData();
        }
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        if (isCallAllApi) {
          getFuturePlanListData();
        }
      },
    );
  }

  getFuturePlanListData() {
    isLoading = true;
    update();
    CustomerPlanProvider().getFuturePlanList(
      id: customerId.toString(),
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustPlanDetailResponse responseData =
                  CustPlanDetailResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  futurePlanList?.clear();
                  futurePlanList?.addAll(responseData.dataList!);
                }
              } else if (responseData.responseCode == 417) {
                Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorBlueRView);
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
        isLoading = false;
        update();
        if (isCallAllApi) {
          getExpiredPlanListData();
        }
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        if (isCallAllApi) {
          getExpiredPlanListData();
        }
      },
    );
  }

  getExpiredPlanListData() {
    isLoading = true;
    update();
    CustomerPlanProvider().getExpiredPlanList(
      id: customerId.toString(),
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustPlanDetailResponse responseData =
                  CustPlanDetailResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  expiredPlanList?.clear();
                  expiredPlanList?.addAll(responseData.dataList!);
                }
              } else if (responseData.responseCode == 417) {
                Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorBlueRView);
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
        isLoading = false;
        isCallAllApi = false;
        update();
      },
      onError: (ResponseModel error) {
        isCallAllApi = false;
        update();
        _handleApiError(error);
      },
    );
  }

  cancelTrialPlanData(CustPlanDataList item, int index) {
    isLoading = true;
    update();
    CancelTrailPlanReq request = CancelTrailPlanReq(
      billingStartFrom: item.dbStartDate,
      cprId: item.planmapid,
      custId: item.custId,
      extendDays: "",
      planGroupId: item.plangroupid,
      planId: item.planId,
    );
    CustomerPlanProvider().cancelTrialPlan(
      cancelTrailPlanReq: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CancelTrailPlanRes responseData =
                  CancelTrailPlanRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                Get.back(result: true);
                Utils.showSnackbar(
                    Strings.SUCCESS,
                    "${Strings.delete} ${Strings.successfully}",
                    AppTheme.colorWhite,
                    AppTheme.colorGreen);

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
      },
      onError: (ResponseModel error) {
        isCallAllApi = false;
        update();
        _handleApiError(error);
      },
    );
  }

  extendDaysTrialPlanData(CustPlanDataList? item, String? extendsDays) {
    isLoading = true;
    update();
    CancelTrailPlanReq request = CancelTrailPlanReq(
      billingStartFrom: item!.dbStartDate,
      cprId: item.planmapid,
      custId: item.custId,
      extendDays: extendsDays,
      planGroupId: item.plangroupid,
      planId: item.planId,
    );
    CustomerPlanProvider().extendDaysTrialPlan(
      cancelTrailPlanReq: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CancelTrailPlanRes responseData =
                  CancelTrailPlanRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                Get.back(result: true);
                Utils.showSnackbar(Strings.SUCCESS, Strings.successfully,
                    AppTheme.colorWhite, AppTheme.colorGreen);

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
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  subscribeTrialPlanData(
      {CustPlanDataList? item,
      String? remark,
      DropdownDetail? selectedBillingStartFrom}) {
    isLoading = true;
    update();
    String? billingStartFrom = "";
    if (selectedBillingStartFrom!.text!.equalsIgnoreCase(Strings.from_today)) {
      billingStartFrom = "CURRENTDATE";
    } else {
      billingStartFrom = "INCLUDINGTRIALPERIOD";
    }
    CancelTrailPlanReq request = CancelTrailPlanReq(
        billingStartFrom: billingStartFrom,
        cprId: item!.planmapid,
        custId: item.custId,
        extendDays: "",
        planGroupId: item.plangroupid,
        planId: item.planId,
        remarks: remark);

    CustomerPlanProvider().subscribeTrialPlan(
      cancelTrailPlanReq: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CancelTrailPlanRes responseData =
                  CancelTrailPlanRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                Get.back(result: true);
                Utils.showSnackbar(Strings.SUCCESS, Strings.successfully,
                    AppTheme.colorWhite, AppTheme.colorGreen);

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
      },
      onError: (ResponseModel error) {
        isCallAllApi = false;
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

  setBtnClickEvent(bool status) {
    checkBtnClickEvent = status;
    update();
  }

  changeSubscribePlanPopup(
      int index,
      ChangeSubScribePlanBtnAction changeSubScribePlanBtnAction,
      CustPlanDataList item) {
    showDialog(
        context: Get.context!,
        barrierDismissible: false,
        builder: (BuildContext context) {
          return ChangeSubscribeTrialPlanDialog(
            changeSubscribePlanBtnAction: changeSubScribePlanBtnAction,
            trialPlanData: item,
          );
        });
  }
}
