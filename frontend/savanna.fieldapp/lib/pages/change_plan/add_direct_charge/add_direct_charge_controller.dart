import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/change_plan/change_plan_provicer.dart';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/charge_data.dart';
import 'package:savbill/pages/customer/model/response/charge_list_res.dart';
import 'package:savbill/pages/customer/model/response/cust_plan_detail.dart';
import 'package:savbill/pages/customer/model/response/parent_customer_res.dart';
import 'package:savbill/pages/customer/model/response/parent_staff_res.dart';
import 'package:savbill/pages/customer/model/response/postpaid_planlist_res.dart';
import 'package:savbill/pages/customer_charge/charge_management_provider.dart';
import 'package:savbill/pages/customer_charge/request/create_cust_charge_req.dart';
import 'package:savbill/pages/customer_charge/request/serial_number_req.dart';
import 'package:savbill/pages/customer_charge/response/active_plan_list_res.dart';
import 'package:savbill/pages/customer_charge/response/add_charge_plan_detail.dart';
import 'package:savbill/pages/customer_charge/response/charge_by_id_res.dart';
import 'package:savbill/pages/customer_charge/response/plan_group_map_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/shift_location/response/charge_by_type_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:intl/intl.dart';

import '../../../webservices/base_response.dart';
import '../../customer/model/response/customer_detail_response.dart';

class AddDirectChargeController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;
  int? serviceId;

  List<ChargeDetail>? chargeByIdList = [];
  ChargeDetail? selectChargeById;

  List<Chargelist>? chargeList = [];
  Chargelist? selectedChargeList;

  // List<String> chargeTypeLst = [Strings.onetime, Strings.recurring];
  // String? selectedChargeType = Strings.recurring;

  DateTime? selectedExpiryDateTime;
  TextEditingController discountController = TextEditingController();
  TextEditingController actualPriceController = TextEditingController();
  TextEditingController newPriceController = TextEditingController();
  TextEditingController staticIpController = TextEditingController();


  // List<ChargeData>? chargeDataList = [];
  // List<CustChargeDetailsPojoList>? custChargeDetails = [];
  // String from = Strings.create_charge, title = Strings.create_charge;
  // List<PostpaidPlanDetail>? planList = [];
  // PostpaidPlanDetail? selectedPlan;
  // ParentStaffUserlist? selectedParentStaff;
  // ParentStaffUserlist? selectedPaymentOwner;
  // List<PlanMappingDetail>? planMappingList = [];
  // PlanMappingDetail? selectPlanMappingList;
  // String currentDate = "";
  // DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);
  // DateFormat apiDateFormatChange = DateFormat(Constant.DATE_FORMAT);
  // DateFormat apiDateTimeFormat = DateFormat(Constant.DATE_TIME_FORMAT);
  // CustomerDetail? customerDetail;
  // String? customerType = Strings.prepaid;
  // ParentCustomerDetail? selectedParentCustomer;
  // int? billableCustomerId = 0, paymentOwnerId = 0;
  // List<ActivePlanListDataList>? activePlanList = [];
  // ActivePlanListDataList? selectedConnectionNumber;
  // bool? displayCreateChargeDialog = false;
  // // List serviceSerialNumbers = [];
  // ChargebyidData? viewChargeData;
  // List<SerialNumberReq> serviceSerialNumbers = [];
  // SerialNumberReq? selectSerialNumber;
  // var keepGoing = false, staticIPAddress = false;
  // String? expiryDateFormat,dateExpiry;

  String title = Strings.add_direct_charge;

  List<Map<String, dynamic>> plansForCharge = [];

  dynamic selectPlansForCharge;

  String currentDate = "";
  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);
  CustomerDetail? customerDetail;
  ChargebyidData? viewChargeData;
  var keepGoing = false, staticIPAddress = false;
  List<CustChargeDetailsPojoList>? custChargeDetails = [];

  String? chargeDate;


  @override
  void onInit() {
    super.onInit();
    currentDate = apiDateFormat.format(DateTime.now());
    getArgumentData();
    initPlatformState();
    chargeDate = DateFormat("yyyy-MM-dd", "en_US").format(DateTime.parse(DateTime.now().toIso8601String()));

  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_DETAIL] != null) {
        customerDetail = arguments[Constant.CUSTOMER_DETAIL];
      }
      if (arguments[Constant.PLAN_FOR_CHARGE_DATA] != null) {
        plansForCharge = arguments[Constant.PLAN_FOR_CHARGE_DATA];
        
      }

    }
    update();
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
    customerChargeById();
  }


  customerChargeById() {
    chargeList!.clear();
    isLoading = true;
    update();
    ChangePlanProvider().customerChargeByIdAndType(
      type: Constant.CUSTOMER_DIRECT,
      serviceId: null,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ChargeByTypeRes responseData = ChargeByTypeRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.chargelist != null &&
                    responseData.chargelist!.isNotEmpty) {
                  chargeList?.addAll(responseData.chargelist!);
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
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  selectCharge(int? chargeId) {
    isLoading = true;
    selectChargeById = null;
    chargeByIdList!.clear();
    update();
    CustomerProvider().getChargeById(
      chargeID: chargeId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ChargeByIdRes responseData = ChargeByIdRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.chargebyid != null) {
                  viewChargeData = responseData.chargebyid;
                  if (viewChargeData!.chargecategory!.equalsIgnoreCase("IP")) {
                    staticIPAddress = true;
                  } else {
                    staticIPAddress = false;
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

  showApiResponsePopup() {
    showDialog(
      context: Get.context!,
      builder: (BuildContext context) {
        return AlertDialogHelper(
            title: Strings.app_name,
            message: "Create Change Successfully.",
            positiveBtnText: Strings.ok,
            positiveBtnClick: () {
              Get.back(result: true);
              Get.back(result: true);
            },
            negativeBtnClick: () {
              Get.back();
            });
      },
    );
  }

  _handleApiError(ResponseModel error) {
    isLoading = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    }else if (error.statusCode == Constant.CODE_NO_TRY_CATCH || error.statusCode == 500) {
      Utils.showSnackbar(Strings.ERROR, error,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }



}
