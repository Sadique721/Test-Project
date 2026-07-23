import 'package:savbill/pages/dashboard/model/response/ticket_follow_up_find_all_response.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_re_schedule/model/get_ticket_follow_up_remark_res.dart';
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

class TicketRemarkFollowUpController extends GetxController {
  bool isLoading = false;
  List<TicketFollowUpRemarkDataList>? remarkFollowUpDataList = [];
  TicketFollowUpRemarkDataList? selectedFollowUpRemark;
  TextEditingController remarkController = TextEditingController();
  int? followUpId;

  TicketFollowUpFindAllDataList? followUpListData;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.FOLLOW_UP_ID] != null) {
        followUpId = arguments[Constant.FOLLOW_UP_ID];
      }

      if (arguments[Constant.FOLLOW_UP_DATA] != null) {
        followUpListData = arguments[Constant.FOLLOW_UP_DATA];
      }

      getTicketFollowUpRemark(followUpId);
    }
    update();
  }


  getTicketFollowUpRemark(int? followUpId) {
    remarkFollowUpDataList!.clear();
    isLoading = true;
    update();
    TicketSystemProvider().getTicketFollowUpRemark(
      followUpId: followUpId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetTicketFollowUpRemarkRes responseData =
              GetTicketFollowUpRemarkRes.fromJson(map);
              if ((responseData.responseCode != null && responseData.responseCode == 200)|| (responseData.status != null && responseData.status == 200)) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  remarkFollowUpDataList?.addAll(responseData.dataList!);
                }
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
        _handleApiError(error);
      },
    );
  }


  addRemarkFollowUp(String? remarks) {
    isLoading = true;
    update();
    TicketSystemProvider().saveTicketFollowUpRemark(
      ticketFollowUpId: followUpId,
      mvNoId: followUpListData!.mvnoId,
      followUpId: followUpId,
      followUpRemark: remarks,
      onSuccess: (ResponseModel responseModel) {
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if ((responseData.responseCode != null && responseData.responseCode == 200)|| (responseData.status != null && responseData.status == 200)) {
                Utils.showSnackbar(
                    Strings.SUCCESS,
                    responseData.responseMessage,
                    AppTheme.colorWhite,
                    AppTheme.colorGreen);
                remarkController.clear();
                getTicketFollowUpRemark(followUpId);
              } else {
                Utils.showSnackbar(Strings.ERROR, responseData.message,
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