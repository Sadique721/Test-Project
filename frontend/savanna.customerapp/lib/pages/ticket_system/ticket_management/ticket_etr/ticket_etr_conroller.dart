import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/dashboard/savbill_care_provider.dart';
import 'package:savbill/pages/dashboard/model/request/case_assign_req.dart';
import 'package:savbill/pages/dashboard/model/response/case_status_response.dart';
import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/ticket_system/ticket_management/get_staff_user_service_res.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_etr/model/ticket_etr_customer_request.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_etr/ticket_etr_provider.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_resolution_reasons_res.dart';
import 'package:savbill/pages/ticket_system/ticket_system_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:dio/dio.dart' as dia;
import 'package:intl/intl.dart';

import '../../../model/dropdown_detail.dart';

class TicketETRController extends GetxController {
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

  TicketDetail? ticketDetail;

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
      if (arguments[Constant.TICKET_DETAIL] != null) {
        ticketDetail = arguments[Constant.TICKET_DETAIL];
        if (ticketDetail != null && ticketDetail!.serviceAreaId != null) {
          serviceAreaId = ticketDetail!.serviceAreaId!;
        }

        log("ticketDetail===>>${jsonEncode(ticketDetail)}");
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

  void clearTicketETRData() {
    selectMessageMode = null;
    dateController.clear();
    timeController.clear();
    remarkController.clear();
    smsNotification = false;
    eMailNotification = false;
    update();
  }

  sendTicketETRCustomerCall() {
    isLoading = true;
    update();
    TicketETRCustomerReq ticketETRCustomerReq = TicketETRCustomerReq(
      custId: ticketDetail!.customersId,
      customerEmailId: ticketDetail!.email,
      customerMobileNo: ticketDetail!.mobile,
      mvnoId: ticketDetail!.mvnoId,
      notificationDate: dateController.text,
      notificationTime: timeController.text,
      remark: selectMessageMode!.text!.equalsIgnoreCase(Strings.dynamic)
          ? remarkController.text
          : "",
      selectedNotificationType: SelectedNotificationType(
          sms: smsNotification, email: eMailNotification),
      staffId: ticketDetail!.currentAssigneeId,
      templateContent: "",
      ticketId: ticketDetail!.caseId,
      ticketNumber: ticketDetail!.caseNumber,
      isTemplateDynamic: showRemark,
      status: ticketDetail!.caseStatus,
      sender: "Organization",
    );

    TicketETRCustomerProvider().sendTicketETRCustomer(
      request: ticketETRCustomerReq,
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
