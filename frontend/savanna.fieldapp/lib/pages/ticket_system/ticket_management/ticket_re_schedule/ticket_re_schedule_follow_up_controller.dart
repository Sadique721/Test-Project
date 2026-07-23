import 'dart:convert';
import 'package:savbill/pages/dashboard/savbill_care_provider.dart';
import 'package:savbill/pages/dashboard/model/response/generate_ticket_follow_up_res.dart';
import 'package:savbill/pages/dashboard/model/response/ticket_follow_up_find_all_response.dart';
import 'package:savbill/pages/lead_management/lead_system_provider.dart';
import 'package:savbill/pages/lead_management/model/reschedule_follow_up_remark_list_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_re_schedule/model/ticket_re_schedule_follow_up_remark.dart';
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
import 'package:intl/intl.dart';

class TicketRescheduleFollowUpController extends GetxController {
  bool isLoading = false;
  // CustomerDetail? customerDetail;
  int? ticketMasterId;
  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;
  bool? approveBtnDisable = false,
        rejectBtnDisable = false,
        assignShiftLocation = false;
  String newFormatDate = "", pickBtnDisableFlag = "";
  // List<ReassignWorkflowList>? reassignWorkFlowList = [];
  int? entityId;
  bool isShowLoadMore = false;
  String? generatedNameFollowUp;

  TextEditingController remarkController = TextEditingController();
  TextEditingController followupNameController = TextEditingController();
  TextEditingController followupDateTimeController = TextEditingController();

  DateTime? selectedFollowUpDate;
  DateFormat dateFormat =
  DateFormat("${Constant.DATE_FORMAT} ${Constant.APP_TIME_FORMAT}");
  DateFormat apiDateTimeFormat = DateFormat(Constant.API_DATE_TIME_FORMAT);
  String? followUpScheduleDateTime;
  String? rescheduleType = "";

  List<String>? rescheduleFollowupRemarkList = [];
  String? selectedRescheduleFollowUpRemark;

  TicketFollowUpFindAllDataList? followUpListData;

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
      if (arguments[Constant.FOLLOW_UP_ID] != null) {
        ticketMasterId = arguments[Constant.FOLLOW_UP_ID];
        getGenerateLeadNameData();
      }
      if (arguments[Constant.FOLLOW_UP_DATA] != null) {
        followUpListData = arguments[Constant.FOLLOW_UP_DATA];
        // ticketMasterId =followUpListData!.id;
        reScheduleFollowUpListData();

      }
      if (arguments[Constant.SCHEDULE_TYPE] != null) {
        rescheduleType = arguments[Constant.SCHEDULE_TYPE];
      }

    }
    update();
  }

  getGenerateLeadNameData() {
    isLoading = true;
    update();
    SavbillCareProvider().getGenerateNameOfTheTicketFollowUp(
      id: ticketMasterId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GenerateTicketFollowUpRes responseData = GenerateTicketFollowUpRes.fromJson(map);
              // LeadGenerateNameFollowUpRes response =
              // LeadGenerateNameFollowUpRes.fromJson(map);
              if ((responseData.responseCode != null &&
                  responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                followupNameController.text = responseData.data ?? "";
              } else {
                Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
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

  reScheduleFollowUpListData() {
    isLoading = true;
    update();
    LeadSystemProvider().reScheduleFollowUpRemarks(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              RescheduleFollowupRemarkListRes response =
              RescheduleFollowupRemarkListRes.fromJson(map);
              if ((response.responseCode != null &&
                  response.responseCode == 200) ||
                  (response.status != null && response.status == 200)) {
                  rescheduleFollowupRemarkList!.addAll(response.rescheduleFollowupRemarkList![0].split(","));
                // rescheduleType = type;
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
        getGenerateLeadNameData();
      },
      onError: (ResponseModel error) {
        getGenerateLeadNameData();
        _handleApiError(error);
      },
    );
  }

  // saveLeadFollowUp({int? followUpId, String? remark}) {
  //   isLoading = true;
  //   update();
  //   LeadSystemProvider().leadFollowUpSave(
  //     followUpId: followUpId,
  //     leadFollowUpName: '',
  //     followUpDatetime: '',
  //     remarks: '',
  //     isMissedCall: null,
  //     leadMasterId: null,
  //     onSuccess: (ResponseModel responseModel) {
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           try {
  //             Map<String, dynamic> map = responseModel.result;
  //             BaseResponse response = BaseResponse.fromJson(map);
  //             if (response.responseCode == 200) {
  //               Utils.showSnackbar(Strings.SUCCESS, response.responseMessage,
  //                   AppTheme.colorWhite, AppTheme.colorGreen);
  //               Get.back(result: true);
  //             } else {
  //               Utils.showSnackbar(Strings.INFO, response.responseMessage,
  //                   AppTheme.colorWhite, AppTheme.colorBlueRView);
  //             }
  //           } on Exception catch (e) {
  //             print(e.toString());
  //           }
  //         }
  //       } else {
  //         if (responseModel.message!.isNotEmpty) {
  //           Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
  //               AppTheme.colorWhite, AppTheme.colorRed);
  //         }
  //       }
  //       isLoading = false;
  //       update();
  //     },
  //     onError: (ResponseModel error) {
  //       _handleApiError(error);
  //     },
  //   );
  // }

  scheduleLeadFollowUpCreate() {
    isLoading = true;
    update();
    LeadSystemProvider().ticketReScheduleFollowUpData(
      mvNoId: followUpListData!.mvnoId,
      ticketFollowUpId: followUpListData!.id,
      caseNumber: followUpListData!.caseNumber,
      status: followUpListData!.status,
      selectedReasonReschedule: selectedRescheduleFollowUpRemark,
      isSend: followUpListData!.isSend,
      caseID: followUpListData!.caseId,
      staffUserId: followUpListData!.staffUserId,
      leadFollowUpName: followupNameController.text,
      followUpDatetime: followUpScheduleDateTime,
      remarks: remarkController.text,
      isMissedCall: false,
      onSuccess: (ResponseModel responseModel) {
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TicketReScheduleFollowUpDataRes responseData = TicketReScheduleFollowUpDataRes.fromJson(map);
              if ((responseData.responseCode != null && responseData.responseCode == 200) || (responseData.status != null && responseData.status == 200) ) {
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

  reScheduleLeadFollowUpCreate() {
    isLoading = true;
    update();
    LeadSystemProvider().leadReScheduleFollowUpData(
      followUpId: followUpListData!.id,
      selectedReasonReschedule: selectedRescheduleFollowUpRemark,
      leadFollowUpName: followupNameController.text,
      followUpDatetime: followUpScheduleDateTime,
      remarks: remarkController.text,
      isMissedCall: false,
      leadMasterId: ticketMasterId,
      onSuccess: (ResponseModel responseModel) {
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if ((responseData.responseCode != null && responseData.responseCode == 200) || (responseData.status != null && responseData.status == 200) ) {
                Get.back(result: true);
                Utils.showSnackbar(
                    Strings.SUCCESS,
                    responseData.message,
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