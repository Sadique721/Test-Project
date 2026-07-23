import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/customer/model/request/custmer_list_request.dart';
import 'package:savbill/pages/customer/model/request/filters.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/pages/task_management/model/response/view_task_detail_response.dart';
import 'package:savbill/pages/task_management/model/response/view_task_response.dart';
import 'package:savbill/pages/task_management/task_system_provider.dart';
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

class LinkTaskController extends GetxController {
  bool isLoading = false, isShowLoadMore = false;
  TaskDetail? taskDetail;
  int page = 1;

  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;

  List<ViewTaskDataList>? taskList = [];
  ViewTaskResponse? viewTicketResponse;
  // List<TaskDetail>? taskList = [];
  // ViewTaskDetailResponse? viewTaskResponse;

  ScrollController? controller;
  String? castTitle = "";

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (viewTicketResponse != null &&
            viewTicketResponse!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          viewLinkTicketList();
        }
      }
    });
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.TASK_DETAIL] != null) {
        taskDetail = arguments[Constant.TASK_DETAIL];
        log("caseId==>${taskDetail!.caseId}");
        if (taskDetail != null &&
            taskDetail!.caseTitle != null &&
            taskDetail!.caseTitle!.isNotEmpty) {
          castTitle = taskDetail!.caseTitle!;
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
    viewLinkTicketList();
  }

  viewLinkTicketList() {
    CustomerListRequest searchReq = CustomerListRequest();

    List<Filters>? filters = [];
    if (taskDetail!.customersId != null) {
      filters.add(Filters(
          filterColumn: "customerId",
          filterCondition: "",
          filterDataType: "",
          filterOperator: "",
          filterValue: taskDetail!.customersId!.toString()));
    }
    if (taskDetail!.tatMappingId != null) {
      filters.add(Filters(
          filterColumn: "ticketReasonCategoryId",
          filterCondition: "",
          filterDataType: "",
          filterOperator: "",
          filterValue: taskDetail!.tatMappingId.toString()));
    }

    if (taskDetail!.caseId != null) {
      filters.add(Filters(
          filterColumn: "ticketIdToLink",
          filterCondition: "",
          filterDataType: "",
          filterOperator: "",
          filterValue: taskDetail!.caseId.toString()));
    }

    searchReq.filters = filters;
    searchReq.page = page;
    searchReq.pageSize = 10;
    searchReq.sortBy = "createdate";
    searchReq.sortOrder = 0;

    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    TaskSystemProvider().viewTaskListDate(
      isSearch: true,
      requestNormal: PageRequest(page: page, pageSize: 10),
      requestSearch: searchReq,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ViewTaskResponse responseData =
              ViewTaskResponse.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                viewTicketResponse = responseData;
                // log("list==>${jsonEncode(responseData.dataList)}");
                if (page == 1) {
                  taskList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  taskList?.addAll(responseData.dataList!);
                  log("list==>${jsonEncode(taskList)}");

                }
              } else {
                if (page == 1) {
                  taskList?.clear();
                }
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
          if (page == 1) {
            taskList?.clear();
          }
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        update();
      },
      onError: (ResponseModel error) {
        if (page == 1) {
          taskList?.clear();
        }
        handleApiError(error);
      },
    );
  }

  linkTicketApiCall(List<int> linkTicketIds) {
    isLoading = true;
    update();
    TaskSystemProvider().linkTask(
      linkTicketIds: linkTicketIds,
      caseId: taskDetail!.caseId!,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if ((responseData.responseCode != null &&
                  responseData.responseCode == 200) || (responseData.responseCode != null &&
                  responseData.responseCode == 0)) {
                Get.back(result: true);
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
