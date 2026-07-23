import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/dashboard/savbill_care_provider.dart';
import 'package:savbill/pages/dashboard/model/request/case_followup_req.dart';
import 'package:savbill/pages/dashboard/model/request/get_all_case_request.dart';
import 'package:savbill/pages/dashboard/model/response/case_status_response.dart';
import 'package:savbill/pages/dashboard/model/response/case_type_response.dart';
import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
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
import 'package:intl/intl.dart';

class SavbillCareTabController extends GetxController {
  bool isLoading = false, isLoadingProgress = false, filterViewOpen = false;
  GetStorage getStorage = GetStorage();
  List<TicketDetail>? viewItemsOrg = [];
  List<TicketDetail>? viewItems = [];
  List<CaseTypeDetail>? caseTypeList = [];
  CaseTypeDetail? selectedCaseType;
  List<CaseStatusDetail>? caseStatusList = [];
  CaseStatusDetail? selectedCaseStatus;
  bool isFilterApply = false;
  UserDetail? userDetail;

  TextEditingController remarksController = TextEditingController();
  double? rating = 0;

  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;
  ViewTicketResponse? viewTicketResponse;

  @override
  void onInit() {
    super.onInit();
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (viewTicketResponse != null && viewTicketResponse?.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          getTicketListData();
        }
      }
    });
  }

  Future<void> initPlatformState() async {
    filterViewOpen = false;
    selectedCaseStatus = null;
    selectedCaseType = null;
    isFilterApply = false;
    update();
    if (userDetail != null && userDetail?.userId != null) {
      if ((caseTypeList == null && caseTypeList!.isEmpty) ||
          (caseStatusList == null && caseStatusList!.isEmpty)) {
        if (caseTypeList == null && caseTypeList!.isEmpty) {
          getCaseTypeListData();
        } else {
          getCaseStatusListData();
        }
      } else {
        getTicketListData();
      }
    } else {
      String strUserData = "";
      if (getStorage.hasData(Constant.USER_DATA)) {
        strUserData = await getStorage.read(Constant.USER_DATA);
      }
      if (!strUserData.isNullOrEmpty()) {
        userDetail = UserDetail.fromJson(jsonDecode(strUserData));

        update();
        if (userDetail != null && userDetail?.userId != null) {
          getCaseTypeListData();
        }
      }
    }
  }

  clearFilter() {
    selectedCaseStatus = null;
    selectedCaseType = null;
    isFilterApply = false;
    filterViewOpen = false;
    List<TicketDetail> filterItem = [];
    filterItem.addAll(viewItemsOrg!);
    viewItems = filterItem;
    update();
    //Get.back();
  }

  applyFilter() {
    List<TicketDetail> filterItem = [];
    if ((selectedCaseType == null) && (selectedCaseStatus == null)) {
      filterItem.addAll(viewItemsOrg!);
    } else if ((selectedCaseType != null && selectedCaseType?.id == 0) &&
        (selectedCaseStatus == null && selectedCaseStatus?.id == 0)) {
      filterItem.addAll(viewItemsOrg!);
    } else {
      if (viewItemsOrg != null && viewItemsOrg!.isNotEmpty) {
        for (TicketDetail ticketItem in viewItemsOrg!) {
          if ((selectedCaseType != null && selectedCaseType?.id != 0) &&
              (selectedCaseStatus != null && selectedCaseStatus?.id != 0)) {
            if (ticketItem.caseType == selectedCaseType?.value &&
                ticketItem.caseStatus == selectedCaseStatus?.value) {
              filterItem.add(ticketItem);
            }
          } else {
            if (selectedCaseType != null && selectedCaseType?.id != 0) {
              if (ticketItem.caseType == selectedCaseType?.value) {
                filterItem.add(ticketItem);
              }
            }
            if (selectedCaseStatus != null && selectedCaseStatus?.id != 0) {
              if (ticketItem.caseStatus == selectedCaseStatus?.value) {
                filterItem.add(ticketItem);
              }
            }
          }
        }
      }
    }
    viewItems = filterItem;
    isFilterApply = true;
    filterViewOpen = false;
    update();
    // Get.back();
  }

  getTicketListData() {
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    // GetAllCaseRequest ticketReq = GetAllCaseRequest(
    //     page: page,
    //     pageSize: Constant.PAGE_LOAD_DATA_LIMIT,
    //     sortBy: "nextFollowupDate",
    //     sortOrder: 0);
    PageRequest pageRequest = PageRequest(page: page, pageSize: Constant.PAGE_LOAD_DATA_LIMIT);
    SavbillCareProvider().getUserTicketsList(
      // getAllCaseRequest: ticketReq,
      pageRequest: pageRequest,
      filterType: "Assigned_to_me",
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ViewTicketResponse responseData = ViewTicketResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                viewTicketResponse = responseData;
                if (page == 1) {
                  viewItems?.clear();
                  viewItemsOrg?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  viewItems?.addAll(responseData.dataList!);
                  viewItemsOrg?.addAll(responseData.dataList!);
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
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        update();
        //getCaseTypeListData();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        //getCaseTypeListData();
      },
    );
  }

  getCaseTypeListData() {
    isLoading = true;
    update();
    SavbillCareProvider().getCaseTypeList(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CaseTypeResponse responseData = CaseTypeResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  caseTypeList?.clear();
                  caseTypeList?.addAll(responseData.dataList!);
                  caseTypeList?.insert(
                      0,
                      CaseTypeDetail(
                          id: 0, text: Strings.select, value: Strings.select));
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
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
        getCaseStatusListData();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getCaseStatusListData();
      },
    );
  }

  getCaseStatusListData() {
    isLoading = true;
    update();
    SavbillCareProvider().getCaseStatusList(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CaseStatusResponse responseData =
                  CaseStatusResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  caseStatusList?.clear();
                  caseStatusList?.addAll(responseData.dataList!);
                  caseStatusList?.insert(
                      0,
                      CaseStatusDetail(
                          id: 0, text: Strings.select, value: Strings.select));
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
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
        getTicketListData();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getTicketListData();
      },
    );
  }

  /*void addCaseRattingApiCall(int caseId, String remarks, int rating) {
    isLoading = true;
    update();
    CaseRatRequest _addCaseRatRequest = CaseRatRequest(
        caseId: caseId, customerFeedback: remarks, rating: rating);
    AdoptCareProvider().addCaseRattingRequest(
      caseRatRequest: _addCaseRatRequest,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Map<String, dynamic> map = responseModel.result;
            BaseResponse responseData = BaseResponse.fromJson(map);
            if (responseData.status == 200) {
              Utils.showSnackbar(Strings.SUCCESS, responseData.message,
                  AppTheme.colorWhite, AppTheme.colorGreen);
              Get.back();
            } else {
              if (responseData.message!.isNotEmpty) {
                Utils.showSnackbar(Strings.ERROR, responseData.message,
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
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }*/

  void caseFollowUpApiCall(TicketDetail? caseDetail, String remarks) {
    isLoadingProgress = true;
    update();
    DateTime now = DateTime.now();
    String currentDate = DateFormat(Constant.API_DATE_TIME_FORMAT).format(now);
    CaseFollowupReq caseFollowupReq = CaseFollowupReq(
        caseId: caseDetail?.caseId,
        remark: remarks,
        remarkDate: currentDate,
        staffId: caseDetail?.currentAssigneeId,
        custId: caseDetail?.customersId);
    SavbillCareProvider().caseFollowupRequest(
      caseFollowupReq: caseFollowupReq,
      onSuccess: (ResponseModel responseModel) {
        isLoadingProgress = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Map<String, dynamic> map = responseModel.result;
            BaseResponse responseData = BaseResponse.fromJson(map);
            if (responseData.responseCode == 200) {
              if (responseData.responseMessage != null &&
                  responseData.responseMessage!.isNotEmpty) {
                Utils.showSnackbar(
                    Strings.SUCCESS,
                    responseData.responseMessage,
                    AppTheme.colorWhite,
                    AppTheme.colorGreen);
              }
              page = 1;
              update();
              getTicketListData();
              //  Get.back();
            } else {
              if (responseData.responseMessage!.isNotEmpty) {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
              }
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  _handleApiError(ResponseModel error) {
    isShowLoadMore = false;
    isLoadingProgress = false;
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
