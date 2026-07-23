import 'dart:convert';

import 'package:savbill/pages/dashboard/savbill_care_provider.dart';
import 'package:savbill/pages/dashboard/model/request/case_assign_req.dart';
import 'package:savbill/pages/dashboard/model/response/case_status_response.dart';
import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/ticket_system/ticket_management/get_staff_user_service_res.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_remark/model/ticket_remark_req.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_remark/ticket_remark_provider.dart';
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
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:dio/dio.dart' as dia;
import 'package:intl/intl.dart';

import '../../../model/dropdown_detail.dart';

class TicketRemarkController extends GetxController {
  bool isLoading = false;
  List<DropdownDetail>? remarkTypeList = [];
  DropdownDetail? selectRemarkType;
  DateTime now = DateTime.now();

  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;
  int? serviceAreaId;
  TicketDetail? ticketDetail;
  TextEditingController remarksController = TextEditingController();
  DateFormat apiDateTimeFormat = DateFormat(Constant.API_DATE_TIME_FORMAT);
  String remarkDate = "";

  @override
  void onInit() {
    super.onInit();
    remarkDate = apiDateTimeFormat.format(now);

    remarkTypeList!.add(DropdownDetail(
        id: Strings.internalRemark.toUpperCase(),
        text: Strings.internalRemark,
        type: Strings.remark_type));

    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.TICKET_DETAIL] != null) {
        ticketDetail = arguments[Constant.TICKET_DETAIL];

        if (ticketDetail!.caseOrigin!.equalsIgnoreCase("Email")) {
          remarkTypeList!.add(DropdownDetail(
              id: Strings.externalRemark.toUpperCase(),
              text: Strings.externalRemark,
              type: Strings.remark_type));
        }

        if (ticketDetail != null && ticketDetail!.serviceAreaId != null) {
          serviceAreaId = ticketDetail!.serviceAreaId!;
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

  saveTicketFollowupCall() {
    isLoading = true;
    update();
    TicketRemarkReq ticketRemarkReq = TicketRemarkReq(
      custId: ticketDetail!.customersId,
      remarkType: selectRemarkType!.text,
      isFromCustomer: false,
      remark: remarksController.text,
      caseId: ticketDetail!.caseId,
      staffId: ticketDetail!.currentAssigneeId,
  remarkDate: remarkDate,
    );

    TicketRemarkProvider().saveTicketFollowupDetails(
      request: ticketRemarkReq,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.responseCode == 0|| responseData.responseCode ==200) {
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
