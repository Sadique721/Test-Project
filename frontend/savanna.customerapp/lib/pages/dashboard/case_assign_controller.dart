import 'dart:convert';
import 'package:savbill/pages/dashboard/savbill_care_provider.dart';
import 'package:savbill/pages/dashboard/model/request/case_assign_req.dart';
import 'package:savbill/pages/dashboard/model/response/case_assign_staff_lst.dart';
import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:dio/dio.dart' as dia;
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class CaseAssignController extends GetxController {
  bool isLoading = false;
  TextEditingController remarksController = TextEditingController();
  TextEditingController latLonController = TextEditingController();
  TicketDetail? ticketDetail;

  List<CaseStaffDetail>? staffList = [];
  CaseStaffDetail? selectedStaff;

  @override
  void onInit() {
    super.onInit();
    initPlatformState();
  }

  Future<void> initPlatformState() async {
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.TICKET_DETAIL] != null) {
        ticketDetail = arguments[Constant.TICKET_DETAIL];
      }

      if(arguments[Constant.STAFF_DETAIL] != null){
        staffList = arguments[Constant.STAFF_DETAIL];
      }

    }
    update();
    // getStaffListData();
  }

  getStaffListData() {
    isLoading = true;
    staffList!.clear();
    update();
    SavbillCareProvider().getTicketStaffLst(
      ticketId: ticketDetail!.caseId!,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CaseStaffListRes responseData = CaseStaffListRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  staffList!.addAll(responseData.dataList!);
                } else {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      "No staff available to assign..",
                      AppTheme.colorWhite,
                      AppTheme.colorRed);
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

  /*getMemberListData() {
    isLoading = true;
    update();
    AdoptCareProvider().getAllTeamListRequest(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TeamListResponse responseData = TeamListResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  teamList.clear();
                  teamList.addAll(responseData.dataList!);
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
  }*/

  caseAssignRequest() {
    isLoading = true;
    update();
    Map<String, dynamic> map = {};

    CaseAssignReq caseAssignReq = CaseAssignReq(
        assignee: selectedStaff!.id,
        remark: remarksController.text,
        status: ticketDetail!.caseStatus,
        remarkType: "Change Assignee",
        ticketId: ticketDetail?.caseId,);

    map["caseUpdate"] = jsonEncode(caseAssignReq);
    dia.FormData formData = dia.FormData.fromMap(map);
    SavbillCareProvider().caseAssignRequest(
      formData: formData,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.responseCode == 200) {
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
