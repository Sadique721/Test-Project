import 'dart:convert';

import 'package:savbill/pages/customer_caf/followup/caf_follow_up/model/reschedule_follow_up_req.dart';
import 'package:savbill/pages/dashboard/savbill_care_provider.dart';
import 'package:savbill/pages/dashboard/model/response/generate_ticket_follow_up_res.dart';
import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/ticket_system/ticket_system_provider.dart';
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

class TicketScheduleFollowUpController extends GetxController {
  bool isLoading = false;
  TextEditingController remarkController = TextEditingController();
  TextEditingController followupNameController = TextEditingController();
  TextEditingController followupDateTimeController = TextEditingController();
  DateTime? selectedFollowUpDate;
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

  // CustomerDetail? customerDetail;

  TicketDetail? ticketDetail;
  int? ticketId;

  DateFormat dateFormat =
      DateFormat("${Constant.DATE_FORMAT} ${Constant.APP_TIME_FORMAT}");
  DateFormat apiDateTimeFormat = DateFormat(Constant.API_DATE_TIME_FORMAT);
  String? followUpScheduleDateTime;

  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;
  // int? followUpId;

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
      if (arguments[Constant.TICKET_DETAIL] != null) {
        ticketDetail = arguments[Constant.TICKET_DETAIL];
      }
      if (arguments[Constant.TICKET_ID] != null) {
        ticketId = arguments[Constant.TICKET_ID];
      }
    }
    update();
    if (ticketId != null) {
      getGenerateNameOfTheTicketFollowUpApiCall(ticketId);
    }
  }

  getGenerateNameOfTheTicketFollowUpApiCall(int? ticketID) {
    isLoading = true;
    update();
    SavbillCareProvider().getGenerateNameOfTheTicketFollowUp(
      id: ticketID,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GenerateTicketFollowUpRes responseData =
                  GenerateTicketFollowUpRes.fromJson(map);
              if (responseData.responseCode == 200) {
                followupNameController.text = responseData.data?? "";
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

  scheduleFollowUpSave() {
    isLoading = true;
    update();
    RescheduleFollowUpReq request = RescheduleFollowUpReq(
        // id: "",
        followUpDatetime: followUpScheduleDateTime,
        followUpName: followupNameController.text,
        isMissed: false,
        caseId: ticketId,
        isSend: false,
        mvnoId: ticketDetail?.mvnoId,
        remarks: remarkController.text,
        remarksTemp: remarkController.text,
        staffUserId: userDetail!.userId,
        status: "Pending");
    TicketSystemProvider().scheduleFollowUpSave(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                Get.back(result: true);
                Utils.showSnackbar(
                    Strings.SUCCESS,
                    responseData.responseMessage,
                    AppTheme.colorWhite,
                    AppTheme.colorGreen);
              } else {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
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
        isLoading = false;
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
