import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/pages/task_management/model/response/task_workflow_audit_res.dart';
import 'package:savbill/pages/task_management/task_system_provider.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_staff_details_dialog.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class TaskWorkflowAuditController extends GetxController {
  bool isLoading = false, isShowLoadMore = false;
  GetStorage getStorage = GetStorage();
  ScrollController? controller;
  int page = 1;
  int? taskId;
  TaskWorkflowAuditRes? taskWorkflowAuditRes;
  List<TaskWorkflowAuditDetail>? taskWorkflowAuditList = [];

  List<String> staffListById = [];

  @override
  void onInit() {
    super.onInit();
    controller = ScrollController();
    getArgumentData();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (taskWorkflowAuditRes != null && taskWorkflowAuditRes?.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          getWorkflowAuditList();
        }
      }
    });
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.TASK_ID] != null) {
        taskId = arguments[Constant.TASK_ID];
      }
    }
    update();
    if (taskId != null) {
      getWorkflowAuditList();
    }
  }

  getWorkflowAuditList() {
    PageRequest normalRequest = PageRequest(page: page, pageSize: 10);
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    TaskSystemProvider().taskWorkflowDetail(
      id: taskId!,
      request: normalRequest,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TaskWorkflowAuditRes responseData = TaskWorkflowAuditRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                taskWorkflowAuditRes = responseData;
                if (page == 1) {
                  taskWorkflowAuditList!.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  taskWorkflowAuditList!.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  taskWorkflowAuditList!.clear();
                }
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
          if (page == 1) {
            taskWorkflowAuditList!.clear();
          }
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        if (page == 1) {
          taskWorkflowAuditList!.clear();
        }
        _handleApiError(error);
      },
    );
  }

  getAllTeamNameByStaffId(int? staffId) {
    isLoading = true;
    update();
    TaskSystemProvider().getAllTaskTeamNameByStaffId(
      staffId: staffId,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              List<String> staffList = List<String>.from(responseModel.result);
              staffListById = staffList;
              if(staffListById.isNotEmpty) {
                TicketStaffDetailsDialog(Get.context!, staffListById);
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          staffListById.clear();
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        staffListById.clear();
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