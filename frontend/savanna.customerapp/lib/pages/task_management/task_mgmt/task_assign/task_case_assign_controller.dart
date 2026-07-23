import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/dashboard/savbill_care_provider.dart';
import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/pages/task_management/model/request/task_case_assign_req.dart';
import 'package:savbill/pages/task_management/model/response/get_all_team_list_res.dart';
import 'package:savbill/pages/task_management/model/response/get_team_by_id_res.dart';
import 'package:savbill/pages/task_management/model/response/view_task_detail_response.dart';
import 'package:savbill/pages/task_management/task_system_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:dio/dio.dart' as dia;
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class TaskCaseAssignController extends GetxController {
  bool isLoading = false;
  TextEditingController remarksController = TextEditingController();
  TextEditingController latLonController = TextEditingController();
  TaskDetail? taskDetail;
  int? assignStaffParentId;

  List<AllTeamDataList>? teamList = [];
  AllTeamDataList? selectedTeam;

  List<TeamByIdDataList>?teamByIdList = [];
  TeamByIdDataList? selectedTeamByIdData;
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
      if (arguments[Constant.TASK_DETAIL] != null) {
        taskDetail = arguments[Constant.TASK_DETAIL];
      }

      if(arguments[Constant.TEAM_DETAIL] != null){
        teamList = arguments[Constant.TEAM_DETAIL];
      }
      if(arguments[Constant.ASSIGN_STAFF_PARENT_ID] != null){
        assignStaffParentId = arguments[Constant.ASSIGN_STAFF_PARENT_ID];
      }

    }
    update();
    // getStaffListData();
  }

  getByTeamIds(int teamId) {
    isLoading = true;
    teamByIdList!.clear();
    update();
    SavbillCareProvider().getByTeamId(
      teamId: teamId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetTeamByIdRes responseData = GetTeamByIdRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  teamByIdList!.addAll(responseData.dataList!);
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

  caseAssignRequest() {
    isLoading = true;
    update();
    Map<String, dynamic> map = {};

    TaskCaseAssignReq taskCaseAssignReq = TaskCaseAssignReq(
        assignee: selectedTeamByIdData!.id,
        remark: remarksController.text,
        status: taskDetail!.caseStatus,
        remarkType: "Change Assignee",
        ticketId: taskDetail?.caseId,
        teamId: selectedTeam!.id);

    log("TaskCaseAssignReq===>${jsonEncode(taskCaseAssignReq)}");

    map["caseUpdate"] = jsonEncode(taskCaseAssignReq);

    dia.FormData formData = dia.FormData.fromMap(map);
    TaskSystemProvider().taskCaseAssignRequest(
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
