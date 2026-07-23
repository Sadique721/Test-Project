import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/task_management/model/request/add_edit_category_request.dart';
import 'package:savbill/pages/task_management/model/response/task_add_edit_category_res.dart';
import 'package:savbill/pages/task_management/model/response/task_cat_search_by_status_res.dart';
import 'package:savbill/pages/task_management/model/response/task_category_management_list_res.dart';
import 'package:savbill/pages/task_management/task_system_provider.dart';
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

class AddEditTaskCategoryMgmtController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();

  TextEditingController categoryNameController = TextEditingController();

  UserDetail? userDetail;

  List<DropdownDetail>? statusList = [];
  DropdownDetail? selectedStatus;
  String from = Strings.add;
  TaskCategoryDataListDetails? taskCategoryDataDetail;
  TaskCategoryMgmtDataList? taskCategoryDataListDetails;
  List<CaseCategoryTatMappingList>? caseCategoryTatMappingList = [];

  List<TaskCatSearchByDataList>? taskCatSearchByDataList = [];

  List<TaskCatSearchByDataList>? taskCatSearchDataList = [];
  TaskCatSearchByDataList? selectTaskCatSearchByData;

  int orderId = 1;
  bool chkDepartment = false, chkService = false;

  @override
  void onInit() {
    super.onInit();
    statusList!.add(DropdownDetail(
        id: Strings.active.toUpperCase(),
        text: Strings.active,
        type: Strings.status));
    statusList!.add(DropdownDetail(
        id: Strings.in_active.toUpperCase(),
        text: Strings.in_active,
        type: Strings.status));
    getArgumentData();
    initPlatformState();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.FROM] != null) {
        from = arguments[Constant.FROM];
      }
      if (arguments[Constant.TCM_DETAIL] != null) {
        taskCategoryDataListDetails = arguments[Constant.TCM_DETAIL];
      }
      if (from.equalsIgnoreCase(Strings.edit)) {
        if (taskCategoryDataListDetails != null) {
          categoryNameController.text =
          taskCategoryDataListDetails!.categoryName!;

          for (DropdownDetail element in statusList!) {
            if (element.id!
                .equalsIgnoreCase(taskCategoryDataListDetails!.status!)) {
              selectedStatus = element;
              break;
            }
          }

          getCaseCategoryById(taskCategoryDataListDetails!.categoryId);
        }
      }else{
        getSearchByStatusApiCall();
      }
    }

    update();
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

  addTicketMapping() {
    taskCatSearchDataList!.add(TaskCatSearchByDataList(
        id: selectTaskCatSearchByData?.id,
        name: selectTaskCatSearchByData?.name,
        orderId: orderId,
        tatForTicketID: selectTaskCatSearchByData!.tatForTicketID));

    orderId = orderId + 1;
    update();
  }

  getSearchByStatusApiCall() {
    isLoading = true;
    update();
    TaskSystemProvider().getSearchByStatusCall(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TaskCatSearchByStatusRes responseData =
              TaskCatSearchByStatusRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  taskCatSearchByDataList!.addAll(responseData.dataList!);
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
  }

  getCaseCategoryById(int? caseCategoryId) {
    isLoading = true;
    update();
    TaskSystemProvider().getTaskCaseCategoryById(
      caseCategoryId: caseCategoryId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TaskAddEditCategoryRes responseData =
              TaskAddEditCategoryRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.data != null &&
                    responseData.data!.caseCategoryTatMappingList!.isNotEmpty) {
                  taskCategoryDataDetail = responseData.data;
                  caseCategoryTatMappingList!.addAll(responseData.data!.caseCategoryTatMappingList!);
                  for (var element in caseCategoryTatMappingList!) {
                    if (caseCategoryTatMappingList!.isNotEmpty) {

                      taskCatSearchByDataList!.add(TaskCatSearchByDataList(
                          id: element.id,
                          caseCategoryId: element.caseCategoryId,
                          name: element.ticketTatMatrix!.name,
                          status: element.ticketTatMatrix!.status,
                          tatMatrixMappings: element.ticketTatMatrix!.tatMatrixMappings,
                          isDeleted: element.isDeleted,
                          deleteFlag: element.deleteFlag,
                          orderId: element.orderid,
                          primaryKey: element.primaryKey,
                          tatForTicketID: element.ticketTatMatrix!.id
                      ));

                      taskCatSearchDataList!.add(TaskCatSearchByDataList(
                          id: element.id,
                          name: element.ticketTatMatrix!.name,
                          orderId: element.orderid,
                          caseCategoryId: element.caseCategoryId!,
                          status: element.ticketTatMatrix!.status,
                          tatMatrixMappings:
                          element.ticketTatMatrix!.tatMatrixMappings,
                          tatForTicketID: element.ticketTatMatrix!.id));
                    }
                  }
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
  }

  addEditCategoryMgmtApiCall() {
    isLoading = true;
    update();
    TaskTicketTatMatrix? ticketTatMatrix;
    for (var element in taskCatSearchDataList!) {
      ticketTatMatrix = TaskTicketTatMatrix(id: element.tatForTicketID);
    }
    AddEditCategoryReq request = AddEditCategoryReq(
        categoryName: categoryNameController.text,
        status: selectedStatus != null ? selectedStatus!.text : "",
        isDeleted: false,
        categoryId: taskCategoryDataDetail != null ? taskCategoryDataDetail!.categoryId : "",
        mvnoId: userDetail!.mvnoId,
        isDefaultCaseCategory: taskCategoryDataDetail != null ? taskCategoryDataDetail!.isDefaultCaseCategory : false,
        caseCategoryTatMappingList: [
          CategoryTatMappingList(
              orderid: taskCatSearchDataList!.isNotEmpty
                  ? taskCatSearchDataList![0].orderId
                  : null,
              id: taskCategoryDataDetail != null  && taskCatSearchDataList!.isNotEmpty
                  ? taskCatSearchDataList![0].id
                  : "",
              ticketTatMatrix: ticketTatMatrix)
        ]);

    log("AddEditCategoryReq===>>>${jsonEncode(request)}");

    TaskSystemProvider().addEditCategoryMgmt(
      isAdd: taskCategoryDataListDetails != null ? false : true,
      request: request,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Map<String, dynamic> map = responseModel.result;
            BaseResponse responseData = BaseResponse.fromJson(map);
            if (responseData.responseCode == 200) {
              Get.back(result: true);
            } else {
              if (responseData.responseMessage!.isNotEmpty) {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
              }
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
