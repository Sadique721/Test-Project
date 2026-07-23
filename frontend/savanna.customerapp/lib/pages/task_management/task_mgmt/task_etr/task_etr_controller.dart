import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/task_management/model/request/task_etr_customer_req.dart';
import 'package:savbill/pages/task_management/model/response/view_task_detail_response.dart';
import 'package:savbill/pages/task_management/task_mgmt/task_etr/task_etr_provider.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_etr/model/ticket_etr_customer_request.dart';
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

class TaskETRController extends GetxController {
  bool isLoading = false;

  List<DropdownDetail>? messageModeList = [];
  DropdownDetail? selectMessageMode;
  TextEditingController dateController = TextEditingController();
  TextEditingController timeController = TextEditingController();
  TextEditingController remarkController = TextEditingController();
  DateTime? selectEtrDate;
  TimeOfDay? selectErtTime;

  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);

  bool smsNotification = false, eMailNotification = false, showRemark = false;

  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;
  int? serviceAreaId;

  TaskDetail? taskDetail;

  @override
  void onInit() {
    super.onInit();
    messageModeList!.add(DropdownDetail(
        id: Strings.dynamic.toUpperCase(),
        text: Strings.dynamic,
        type: Strings.messageMode));
    messageModeList!.add(DropdownDetail(
        id: Strings.static.toUpperCase(),
        text: Strings.static,
        type: Strings.messageMode));
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.TASK_DETAIL] != null) {
        taskDetail = arguments[Constant.TASK_DETAIL];
        if (taskDetail != null && taskDetail!.serviceAreaId != null) {
          serviceAreaId = taskDetail!.serviceAreaId!;
        }

        log("taskDetail===>>${jsonEncode(taskDetail)}");
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

  void clearTaskETRData() {
    selectMessageMode = null;
    dateController.clear();
    timeController.clear();
    remarkController.clear();
    smsNotification = false;
    eMailNotification = false;
    update();
  }

  sendTaskETRCustomerCall() {
    isLoading = true;
    update();
    TaskETRCustomerReq taskETRCustomerReq = TaskETRCustomerReq(
      taskOwnerStaffId: taskDetail!.createdById,
      mvnoId: taskDetail!.mvnoId,
      notificationDate: dateController.text,
      notificationTime: timeController.text,
      remark: selectMessageMode!.text!.equalsIgnoreCase(Strings.dynamic)
          ? remarkController.text
          : "",
      selectedNotificationType: SelectedNotificationType(
          sms: smsNotification, email: eMailNotification),
      staffId: taskDetail!.currentAssigneeId,
      templateContent: "",
      ticketId: taskDetail!.caseId,
      ticketNumber: taskDetail!.caseNumber,
      isTemplateDynamic: showRemark,
      status: taskDetail!.caseStatus,
      sender: "Organization",
    );

    TaskETRCustomerProvider().sendTaskETRCustomer(
      request: taskETRCustomerReq,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                Get.back(result: true);
              } else if (responseData.responseCode == 406){
                Get.back(result: true);
              } else{
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