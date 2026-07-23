import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer/model/response/plan_service_by_customer_res.dart';
import 'package:savbill/pages/customer_caf/caf_service_management/caf_service_termination_dialog.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/service_management/request/cust_service_hold_request.dart';
import 'package:savbill/pages/service_management/request/cust_stop_service_in_bulk_req.dart';
import 'package:savbill/pages/service_management/response/service_nick_name_update_res.dart';
import 'package:savbill/pages/service_management/service_management_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:intl/intl.dart';
import '../../customer/model/response/customer_status_list_res.dart';

class CafServiceManagementController extends GetxController
    implements CafServiceTerminationStatusBtnAction {
  bool isLoading = false;
  CustomerDetail? customerDetail;
  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;
  bool? approveBtnDisable = false,
      rejectBtnDisable = false,
      assignShiftLocation = false;
  String newFormatDate = "", pickBtnDisableFlag = "";
  int? entityId;

  DateFormat dateFormat = DateFormat(Constant.DATE_NEW_TIME_FORMAT);
  List<CustomerPlanServiceDetail>? customerServiceList = [];
  TextEditingController nickNameController = TextEditingController();
  CustomerPlanServiceDetail? selectedPlanService;
  ServiceNickName? serviceNickName;
  bool? isServiceTermination = false,
      isPickService = false,
      isApproveRejectService = false,
      isReassignService = false,
      isPauseStartService = false,
      isServiceAudit = false,
      isWorkFlowStatusDetail = false,
      isStopService = false;

  bool? serviceStopBulkFlag = false;
  String? customerType;

  List<CustomerStatusDetail>? deActiveReasonServiceList = [];
  CustomerStatusDetail? selectedDeActiveServiceReason;
  List<DeactivatePlanReqModels>? serviceHoldRequestList = [];

  @override
  void onInit() {
    super.onInit();
    initPlatformState();
  }

  Future<void> initPlatformState() async {
    String strUserData = "";
    if (getStorage.hasData(Constant.USER_DATA)) {
      strUserData = await getStorage.read(Constant.USER_DATA);
    }
    if (!strUserData.isNullOrEmpty()) {
      userDetail = UserDetail.fromJson(jsonDecode(strUserData));

      update();
    }
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_DETAIL] != null) {
        customerDetail = arguments[Constant.CUSTOMER_DETAIL];
      }

      if (arguments[Constant.CUSTOMER_TYPE] != null) {
        customerType = arguments[Constant.CUSTOMER_TYPE];
      }
    }
    getPlanServiceData(customerDetail!.id!);
    update();
  }

  getPlanServiceData(int customerId) {
    customerServiceList!.clear();
    isLoading = true;
    update();
    CustomerProvider().getCustomerCafServiceManagement(
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
                  customerServiceList?.addAll(responseData.dataList!);
                }
              } else if (responseData.responseCode == 417) {
                Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorBlueRView);
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
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  showServiceTerminationDialog(
    String? serviceStatus,
    BuildContext context,
    CafServiceManagementController? controller,
    CustomerPlanServiceDetail? customerPlanServiceDetail,
    String? subTypeServiceStatus,
  ) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return CafServiceTerminationDialog(
              serviceTerminationStatusBtnAction: this,
              deActiveReasonList: deActiveReasonServiceList,
              controller: controller,
              customerPlanServiceDetail: customerPlanServiceDetail,
              from: serviceStatus!,
              subTypeService: subTypeServiceStatus);
        });
  }

  getServiceNickNameUpdate(int serviceMappingId, String? nickName) {
    customerServiceList!.clear();
    isLoading = true;
    update();
    ServiceManagementProvider().serviceNickNameUpdate(
      serviceMappingId: serviceMappingId,
      nickName: nickName,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ServiceNickNameUpdateRes responseData =
                  ServiceNickNameUpdateRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.nickName != null &&
                    responseData.nickName != null) {
                  serviceNickName = responseData.nickName;
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
        getPlanServiceData(customerDetail!.id!);
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  getDeActiveReasonService(
      BuildContext context,
      CafServiceManagementController controller,
      CustomerPlanServiceDetail? customerPlanServiceDetail,
      String? statusValue,
      String? subTypeStatus) {
    isLoading = true;
    deActiveReasonServiceList?.clear();
    update();
    ServiceManagementProvider().getDeActiveReasonService(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerStatusListRes responseData =
                  CustomerStatusListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  deActiveReasonServiceList?.addAll(responseData.dataList!);
                  if (statusValue!
                      .equalsIgnoreCase(Strings.cust_service_termination)) {
                    showServiceTerminationDialog(
                        Strings.cust_service_termination,
                        context,
                        controller,
                        customerPlanServiceDetail,
                        subTypeStatus);
                  } else if (statusValue
                      .equalsIgnoreCase(Strings.cust_service_stop)) {
                    showServiceTerminationDialog(
                        Strings.cust_service_stop,
                        context,
                        controller,
                        customerPlanServiceDetail,
                        subTypeStatus);
                  } else {
                    showServiceTerminationDialog(
                        Strings.cust_service_hold,
                        context,
                        controller,
                        customerPlanServiceDetail,
                        subTypeStatus);
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
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
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

  customerServiceTerminationCall(CustomerStatusDetail? customerStatusDetail,
      CustomerPlanServiceDetail? customerPlanServiceDetail, String? remark) {
    isLoading = true;
    update();
    ServiceManagementProvider().customerServiceTermination(
      serviceId: customerPlanServiceDetail!.planId,
      customerId: customerPlanServiceDetail.custId,
      planMappingId: customerPlanServiceDetail.planmapid,
      reasonId: int.parse(customerStatusDetail!.value!),
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse baseResponse = BaseResponse.fromJson(map);
              if (baseResponse.status == 200) {
                getPlanServiceData(customerDetail!.id!);
              } else {
                if (baseResponse.responseMessage != null &&
                    baseResponse.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      baseResponse.responseMessage,
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
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  customerServiceHoldCall(CustomerStatusDetail? customerStatusDetail,
      CustomerPlanServiceDetail? customerPlanServiceDetail, String? remark) {
    isLoading = true;
    update();
    serviceHoldRequestList!.add(DeactivatePlanReqModels(
        custServiceMappingId:
            customerPlanServiceDetail!.customerServiceMappingId,
        reasonId: customerStatusDetail!.value,
        remarks: remark));
    CustomerServiceHoldReq customerServiceHoldReq = CustomerServiceHoldReq(
        custId: customerPlanServiceDetail.custId,
        deactivatePlanReqModels: serviceHoldRequestList);

    log("CustomerServiceHoldReqRespnse===>>${jsonEncode(customerServiceHoldReq)}");

    ServiceManagementProvider().customerServiceHold(
      serviceHoldReq: customerServiceHoldReq,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse baseResponse = BaseResponse.fromJson(map);
              if (baseResponse.status == 200) {
                getPlanServiceData(customerDetail!.id!);
              } else {
                if (baseResponse.responseMessage != null &&
                    baseResponse.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      baseResponse.responseMessage,
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
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  customerServiceStopInBulkCall(CustomerStatusDetail? customerStatusDetail,
      CustomerPlanServiceDetail? customerPlanServiceDetail, String? remark) {
    isLoading = true;
    update();
    serviceHoldRequestList!.add(DeactivatePlanReqModels(
        custServiceMappingId:
            customerPlanServiceDetail!.customerServiceMappingId,
        reasonId: customerStatusDetail!.value,
        remarks: remark));

    CustStopServiceInBulkReq request = CustStopServiceInBulkReq(
        custId: customerPlanServiceDetail.custId,
        serviceStopBulkFlag: false,
        deactivatePlanReqModels: serviceHoldRequestList);

    log("CustomerServiceStopReqRespnse===>>${jsonEncode(request)}");

    ServiceManagementProvider().customerServiceStopInBulk(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse baseResponse = BaseResponse.fromJson(map);
              if (baseResponse.status == 200) {
                getPlanServiceData(customerDetail!.id!);
              } else {
                if (baseResponse.responseMessage != null &&
                    baseResponse.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      baseResponse.responseMessage,
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
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  customerServiceHoldWithStartCall(CustomerStatusDetail? customerStatusDetail,
      CustomerPlanServiceDetail? customerPlanServiceDetail, String? remark) {
    isLoading = true;
    update();
    serviceHoldRequestList!.add(DeactivatePlanReqModels(
        custServiceMappingId:
            customerPlanServiceDetail!.customerServiceMappingId,
        remarks: remark));
    CustomerServiceHoldReq customerServiceHoldReq = CustomerServiceHoldReq(
        custId: customerPlanServiceDetail.custId,
        deactivatePlanReqModels: serviceHoldRequestList);

    log("CustomerServiceHoldStartReqRespnse===>>${jsonEncode(customerServiceHoldReq)}");

    ServiceManagementProvider().customerServiceWithStart(
      serviceHoldReq: customerServiceHoldReq,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse baseResponse = BaseResponse.fromJson(map);
              if (baseResponse.status == 200) {
                getPlanServiceData(customerDetail!.id!);
              } else {
                if (baseResponse.responseMessage != null &&
                    baseResponse.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      baseResponse.responseMessage,
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
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  void openPauseServiceModel(
      CustomerPlanServiceDetail? data,
      String? subTypeStatus,
      BuildContext context,
      CafServiceManagementController? controller) {
    getDeActiveReasonService(
        context, controller!, data, Strings.cust_service_hold, subTypeStatus);
  }

  @override
  void serviceTerminationBtnAction(
      {String? identifier,
      CustomerStatusDetail? customerStatusDetail,
      String? remark,
      CustomerPlanServiceDetail? customerPlanServiceDetail,
      String? subServiceType}) {
    Get.back();

    if (identifier!.equalsIgnoreCase(Strings.cust_service_termination)) {
      customerServiceTerminationCall(
          customerStatusDetail, customerPlanServiceDetail, remark);
    } else if (identifier.equalsIgnoreCase(Strings.cust_service_hold)) {
      if (subServiceType!.equalsIgnoreCase("Pause")) {
        customerServiceHoldCall(
            customerStatusDetail, customerPlanServiceDetail, remark);
      } else {
        customerServiceHoldWithStartCall(
            customerStatusDetail, customerPlanServiceDetail, remark);
      }
    } else if (identifier.equalsIgnoreCase(Strings.cust_service_stop)) {
      customerServiceStopInBulkCall(
          customerStatusDetail, customerPlanServiceDetail, remark);
    }
  }

  @override
  void cafServiceTerminationBtnAction(
      {String? identifier,
      CustomerStatusDetail? customerStatusDetail,
      String? remark,
      CustomerPlanServiceDetail? customerPlanServiceDetail,
      String? subServiceType}) {
    Get.back();

    if (identifier!.equalsIgnoreCase(Strings.cust_service_termination)) {
      customerServiceTerminationCall(
          customerStatusDetail, customerPlanServiceDetail, remark);
    } else if (identifier.equalsIgnoreCase(Strings.cust_service_hold)) {
      if (subServiceType!.equalsIgnoreCase("Pause")) {
        customerServiceHoldCall(
            customerStatusDetail, customerPlanServiceDetail, remark);
      } else {
        customerServiceHoldWithStartCall(
            customerStatusDetail, customerPlanServiceDetail, remark);
      }
    } else if (identifier.equalsIgnoreCase(Strings.cust_service_stop)) {
      customerServiceStopInBulkCall(
          customerStatusDetail, customerPlanServiceDetail, remark);
    }
  }
}
