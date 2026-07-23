import 'dart:convert';

import 'package:savbill/pages/customer/model/request/filters.dart';
import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/request/category_search_req.dart';
import 'package:savbill/pages/inventory/module/response/assigned_inv_approve_status_res.dart';
import 'package:savbill/pages/inventory/module/response/assigned_inventory_request_list_res.dart';
import 'package:savbill/pages/inventory/module/response/inventory_request_list_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

import '../../../webservices/response_model.dart';
import '../module/response/filter_data.dart';

class RequestInventoryController extends GetxController{
  bool isLoading = false;
  int tabIndex = 0;
  bool isShowLoadMore = false;
  bool isFilterApply = false;
  FilterData? filterData;
  int page = 1;
  ScrollController? controller;
  ScrollController? assignedInvController;
  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;

  TextEditingController searchController = TextEditingController();
  InventoryRequestListRes? requestInventoryListRes;
  AssignedInventoryRequestListRes? assignedInventoryListRes;
  List<InventroyRequestDataList>? requestInventoryList = [];
  List<AssignedInventoryDataList>? assignedInventoryReqList = [];

  @override
  void onInit(){
    super.onInit();
    initPlatformState();
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (requestInventoryListRes != null &&
            requestInventoryListRes?.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          viewRequestInventroyList();
        }
      }
    });


    assignedInvController?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (assignedInventoryListRes != null &&
            assignedInventoryListRes?.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          assignedRequestInventoryRequestList();
        }
      }
    });
  }

  Future<void> initPlatformState() async {
    String strUserData = "";
    if (getStorage.hasData(Constant.USER_DATA)) {
      strUserData = await getStorage.read(Constant.USER_DATA);
    }
    if (!strUserData.isNullOrEmpty()) {
      userDetail = UserDetail.fromJson(jsonDecode(strUserData));
      update();
      viewRequestInventroyList();

    }
  }

  applyFilter() {
    if (searchController.text.isNullOrEmpty()) {
      isFilterApply = false;
      update();
      Utils.showSnackbar(Strings.ERROR, "Please enter filter option.",
          AppTheme.colorWhite, AppTheme.colorRed);
      return;
    }
    isFilterApply = true;
    page = 1;
    update();
  }

  clearFilter() {
    searchController.clear();
    page = 1;
    isFilterApply = false;
    update();
  }

  viewRequestInventroyList() {
    PageRequest normalRequest = PageRequest(page: page, pageSize: 10);
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    InventoryManagementProvider().viewRequestInventoryList(
      requestNormal: normalRequest,
      pageNumber: page,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (page == 1) {
            requestInventoryList?.clear();
            // assignedCustomerListOrg?.clear();
          }
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              InventoryRequestListRes responseData =
              InventoryRequestListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                requestInventoryListRes = responseData;
                if (responseData.dataList != null && responseData.dataList!.isNotEmpty) {
                  requestInventoryList?.addAll(responseData.dataList!);
                }
                assignedRequestInventoryRequestList();
              } else {
                if (page == 1) {
                  requestInventoryList?.clear();
                  // assignedCustomerListOrg?.clear();
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
        }
        else {
          if (page == 1) {
            requestInventoryList?.clear();
            // assignedCustomerListOrg?.clear();
          }
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
        // viewAssignedInventoryList();
      },
      onError: (ResponseModel error) {
        if (page == 1) {
          requestInventoryList?.clear();
          // assignedCustomerListOrg?.clear();
        }
        _handleApiError(error);
        // viewAssignedInventoryList();
      },
    );
  }

  assignedRequestInventoryRequestList() {
    PageRequest normalRequest = PageRequest(page: page, pageSize: 10);
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    InventoryManagementProvider().assignedInventoryRequestList(
      requestNormal: normalRequest,
      pageNumber: page,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (page == 1) {
            assignedInventoryReqList?.clear();
            // assignedCustomerListOrg?.clear();
          }
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              AssignedInventoryRequestListRes responseData =
              AssignedInventoryRequestListRes.fromJson(map);
              if (responseData.responseCode == 200||responseData.responseCode == 0) {
                assignedInventoryListRes = responseData;
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  assignedInventoryReqList?.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  assignedInventoryReqList?.clear();
                  // assignedCustomerListOrg?.clear();
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
        }
        else {
          if (page == 1) {
            assignedInventoryReqList?.clear();
            // assignedCustomerListOrg?.clear();
          }
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
        // viewAssignedInventoryList();
      },
      onError: (ResponseModel error) {
        if (page == 1) {
          assignedInventoryReqList?.clear();
          // assignedCustomerListOrg?.clear();
        }
        _handleApiError(error);
        // viewAssignedInventoryList();
      },
    );
  }

  assignedInvReqApproveStatus(int? id, String? statusId,String? remarkController) {
    isLoading = true;
    update();
    InventoryManagementProvider().getAssignedApproveStatusReq(
      assignedId: id,
      status: statusId,
      remarks: remarkController,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Utils.showSnackbar(Strings.successfully, responseModel.message!,
                AppTheme.colorWhite, AppTheme.colorGreen);
            viewRequestInventroyList();
          }
        }
        else {
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

  deleteRequestInventroyItem(int? deleteRequestId) {
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    InventoryManagementProvider().deleteRequestInventory(
      id: deleteRequestId,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                viewRequestInventroyList();
                // Get.back(result: true);
              } else {
                if (responseData.responseMessage != null &&
                    responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorRed);
                }

                if (responseData.ERROR != null &&
                    responseData.ERROR!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.ERROR,
                      AppTheme.colorWhite, AppTheme.colorRed);
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