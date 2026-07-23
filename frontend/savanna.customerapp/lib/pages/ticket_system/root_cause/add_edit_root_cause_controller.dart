import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/ticket_system/model/request/add_edit_root_cause_req.dart';
import 'package:savbill/pages/ticket_system/model/response/root_cause_list_res.dart';
import 'package:savbill/pages/ticket_system/ticket_system_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

import '../../customer/model/request/custmer_list_request.dart';
import '../../model/page_request.dart';
import '../model/response/root_cause_sub_problem_res.dart';

class AddEditRootCauseController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();
  int page = 1;
  bool isShowLoadMore = false;
  bool isFilterApply = false;
  List<RootCauseSubProblemDataList>? rootCauseList = [];
  RootCauseSubProblemRes? rootCauseSubProblemRes;
  RootCauseSubProblemDataList? selectedRootSubProblem;

  TextEditingController rootCauseNameController = TextEditingController();
  TextEditingController resolutionController = TextEditingController();

  UserDetail? userDetail;

  List<DropdownDetail>? statusList = [];
  DropdownDetail? selectedStatus;

  String from = Strings.add;
  RootCauseDetail? rootCauseDetail;

  List<RootCauseResolutionMapping>? rootCauseResolutionMappings = [];
  List<ResoSubCategoryMappingList>? rootCauseSubProblemDomain = [];

  List<String>? subProblemDomainNew = [];

  List<int> selectedParentServiceArea = [];

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
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.FROM] != null) {
        from = arguments[Constant.FROM];
      }
      if (arguments[Constant.ROOT_CAUSE_DETAIL] != null) {
        rootCauseDetail = arguments[Constant.ROOT_CAUSE_DETAIL];
      }

      if (rootCauseDetail != null) {
        rootCauseNameController.text = rootCauseDetail!.name!;

        for (DropdownDetail element in statusList!) {
          if (element.id!.equalsIgnoreCase(rootCauseDetail!.status!)) {
            selectedStatus = element;
            break;
          }
        }

        if (rootCauseDetail!.rootCauseResolutionMappingList != null &&
            rootCauseDetail!.rootCauseResolutionMappingList!.isNotEmpty) {
          rootCauseResolutionMappings!
              .addAll(rootCauseDetail!.rootCauseResolutionMappingList!);
        }

        if (rootCauseDetail!.resoSubCategoryMappingList != null &&
            rootCauseDetail!.resoSubCategoryMappingList!.isNotEmpty) {
          rootCauseSubProblemDomain!
              .addAll(rootCauseDetail!.resoSubCategoryMappingList!);
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
    viewSubProblemDomainData();
  }

  void addEditRootCauseApiCall() {
    isLoading = true;
    update();

    AddEditRootCauseReq request = AddEditRootCauseReq(
        name: rootCauseNameController.text,
        status: selectedStatus != null ? selectedStatus!.text : "",
        id: rootCauseDetail != null ? rootCauseDetail!.id : null,
        rootCauseResolutionMappingList: rootCauseResolutionMappings,
        rootCauseSubProblemList: rootCauseSubProblemDomain);

    TicketSystemProvider().addEditRootCause(
      isAdd: rootCauseDetail != null ? false : true,
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

  //Sub Problem Domain
  viewSubProblemDomainData() {
    PageRequest normalRequest = PageRequest(page: page, pageSize: 10);
    CustomerListRequest searchReq = CustomerListRequest();
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    TicketSystemProvider().viewSubProblemDomainList(
      isSearch: isFilterApply,
      requestNormal: normalRequest,
      requestSearch: searchReq,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              RootCauseSubProblemRes responseData =
                  RootCauseSubProblemRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                rootCauseSubProblemRes = responseData;
                if (page == 1) {
                  rootCauseList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  rootCauseList?.addAll(rootCauseSubProblemRes!.dataList!);

                  if (rootCauseDetail != null &&
                      rootCauseDetail!.resoSubCategoryMappingList != null &&
                      rootCauseDetail!.resoSubCategoryMappingList!.isNotEmpty) {
                    String serviceAreaName = "";
                    for (RootCauseSubProblemDataList element
                        in rootCauseList!) {
                      for (ResoSubCategoryMappingList value
                          in rootCauseDetail!.resoSubCategoryMappingList!) {
                        if (value.subcateId == element.id) {
                          selectedParentServiceArea.add(element.id!);
                          value.subCateName = element.subCategoryName;
                          // serviceAreaName = "$serviceAreaName${element.subCategoryName!}, ";
                          // value.subCateName = serviceAreaName;
                          element.selected = true;
                        }
                      }
                    }
                    // if (!serviceAreaName.isNullOrEmpty() &&
                    //     serviceAreaName.contains(",") &&
                    //     serviceAreaName.length >= 2) {
                    //   serviceAreaName = serviceAreaName.substring(
                    //       0, serviceAreaName.length - 2);
                    // }
                    // parentServicesAreaController.text = serviceAreaName;
                  }
                }
              } else {
                if (page == 1) {
                  rootCauseList?.clear();
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
            rootCauseList?.clear();
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
          rootCauseList?.clear();
        }
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
