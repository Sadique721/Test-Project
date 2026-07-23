import 'dart:convert';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/task_management/model/request/task_remark_req.dart';
import 'package:savbill/pages/task_management/model/response/view_task_detail_response.dart';
import 'package:savbill/pages/task_management/task_mgmt/task_remark/task_remark_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:intl/intl.dart';

class TaskRemarkController extends GetxController {
  bool isLoading = false;
  List<DropdownDetail>? remarkTypeList = [];
  DropdownDetail? selectRemarkType;

  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;
  int? serviceAreaId;
  TaskDetail? taskDetail;
  TextEditingController remarksController = TextEditingController();
  String? currentDate = "";

  @override
  void onInit() {
    super.onInit();

    currentDate =DateFormat(Constant.API_DATE_TIME_FORMAT).format(DateTime.now());
    remarkTypeList!.add(DropdownDetail(
        id: Strings.internalRemark.toUpperCase(),
        text: Strings.internalRemark,
        type: Strings.remark_type));

    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.TASK_DETAIL] != null) {
        taskDetail = arguments[Constant.TASK_DETAIL];

        if (taskDetail!.caseOrigin!.equalsIgnoreCase("Email")) {
          remarkTypeList!.add(DropdownDetail(
              id: Strings.externalRemark.toUpperCase(),
              text: Strings.externalRemark,
              type: Strings.remark_type));
        }

        if (taskDetail != null && taskDetail!.serviceAreaId != null) {
          serviceAreaId = taskDetail!.serviceAreaId!;
        }
      }
    }
    update();
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
  }

  saveTaskFollowupCall() {
    isLoading = true;
    update();
    TaskRemarkReq taskRemarkReq = TaskRemarkReq(
      custId: taskDetail!.customersId,
      remarkType: selectRemarkType!.text,
      isFromCustomer: false,
      remark: remarksController.text,
      caseId: taskDetail!.caseId,
      remarkDate: currentDate,
      staffId: taskDetail!.currentAssigneeId,
    );

    TaskRemarkProvider().saveTaskFollowupDetails(
      request: taskRemarkReq,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.responseCode == 0) {
                Get.back(result: true);
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
        handleApiError(error);
      },
    );
  }

  handleApiError(ResponseModel error) {
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
