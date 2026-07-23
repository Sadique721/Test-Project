import 'dart:convert';
import 'package:savbill/pages/credit_note/response/reassign_workflow_get_staff_res.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer_caf/followup/caf_follow_up/caf_follow_up_provider.dart';
import 'package:savbill/pages/customer_caf/followup/caf_follow_up/model/customer_caf_follow_up_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/cupertino.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class CafFollowUpController extends GetxController {
  bool isLoading = false;
  CustomerDetail? customerDetail;

  List<CafFollowUpDataList> cafFollowUpDataList = [];
  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;
  bool? approveBtnDisable = false,
      rejectBtnDisable = false,
      assignShiftLocation = false;
  String newFormatDate = "", pickBtnDisableFlag = "";
  List<ReassignWorkflowList>? reassignWorkFlowList = [];
  int? entityId;
  bool isShowLoadMore = false;

  TextEditingController remarkController = TextEditingController();
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
        getCustomerCafFollowUPData();
      }
    }
    update();
  }

  getCustomerCafFollowUPData() {
    isLoading = true;
    update();
    CafFollowUpProvider().getCustomerCafFollowUp(
      customerId: customerDetail!.id!,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerCafFollowUpRes response =
                  CustomerCafFollowUpRes.fromJson(map);
              if (response.responseCode == 200) {
                if (response.dataList != null ||
                    !response.dataList!.isNullOrEmpty()) {
                  cafFollowUpDataList.addAll(response.dataList!);
                }
              } else {
                Utils.showSnackbar(Strings.INFO, response.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorBlueRView);
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


  getCloseFollowUp({int? followUpId,String? remark}) {
    isLoading = true;
    update();
    CafFollowUpProvider().getCloseFollowUp(
      followUpId: followUpId,
      remark: remark,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse response = BaseResponse.fromJson(map);
              if (response.responseCode == 200) {
                Utils.showSnackbar(Strings.SUCCESS, response.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorGreen);
                getCustomerCafFollowUPData();
              } else {
                Utils.showSnackbar(Strings.INFO, response.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorBlueRView);
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


}
