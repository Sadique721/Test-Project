import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/credit_note/credit_note_provider.dart';
import 'package:savbill/pages/customer_change_status/change_status_provider.dart';
import 'package:savbill/pages/customer_change_status/cust_change_staus_assign_dialog.dart';
import 'package:savbill/pages/customer_change_status/request/cust_terminate_approve_reject_req.dart';
import 'package:savbill/pages/customer_change_status/response/customer_terminate_approve_reject_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/pages/pending_approvals/model/request/customer_approve_reject_req.dart';
import 'package:savbill/pages/pending_approvals/model/request/termination_approve_reject_req.dart';
import 'package:savbill/pages/pending_approvals/model/response/approval_pending_customer_res.dart';
import 'package:savbill/pages/pending_approvals/pending_approvals_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class PACustomerTerminationController extends GetxController implements ChangeStatusAssignAction{

  bool isLoading = false;
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;
  GetStorage getStorage = GetStorage();
  List<ApprovalPendingCustomer>? customersList = [];
  ApprovalPendingCustomerRes? customerListRes;
  UserDetail? userDetail;
  List<CustomerTerminateApproveRejectDataList>? customerChangeStatusDataList = [];

  int? entityId;

  @override
  void onInit() {
    super.onInit();
    initPlatformState();
    getPACustomerTerminationList();
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (customerListRes != null && customerListRes!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          getPACustomerTerminationList();
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
      if (userDetail != null && userDetail?.userId != null) {}
    }
    update();
  }

  getPACustomerTerminationList() {
    PageRequest request = PageRequest(page: page, pageSize: 10);
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    PendingApprovalsProvider().getPACustomerTermination(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ApprovalPendingCustomerRes responseData =
                  ApprovalPendingCustomerRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                customerListRes = responseData;
                if (page == 1) {
                  customersList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  customersList?.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  customersList?.clear();
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
            customersList?.clear();
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
          customersList?.clear();
        }
        _handleApiError(error);
      },
    );
  }

  approveRejectTermination(TerminationApproveRejectReq request) {
    isLoading = true;
    update();
    PendingApprovalsProvider().approveRejectCustomerTermination(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {

                page = 1;
                update();
                if(responseData.message!=null &&responseData.message!.isNotEmpty){
                  Utils.showSnackbar(
                      Strings.SUCCESS,
                      responseData.message,
                      AppTheme.colorWhite,
                      AppTheme.colorGreen);
                }
                getPACustomerTerminationList();
              } else {
                if (responseData.ERROR!=null && responseData.ERROR!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      responseData.ERROR,
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
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }


  getApproveCustomerChangeStatusApproveReject(String? status, String? remarks,
      CustomerTerminateApproveRejectReq? request, BuildContext? context) {
    isLoading = true;
    update();
    ChangeStatusProvider().customerTerminationApproveRejectStatus(
      customerId: entityId,
      status: status,
      remarks: remarks,
      request: request,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerTerminateApproveRejectRes responseData =
              CustomerTerminateApproveRejectRes.fromJson(map);
              if ((responseData.result!.responseCode != null &&
                  responseData.result!.responseCode == 0)) {
                if (responseData.result!.dataList != null &&
                    responseData.result!.dataList!.isNotEmpty) {
                  customerChangeStatusDataList?.clear();
                  customerChangeStatusDataList
                      ?.addAll(responseData.result!.dataList!);
                  showAssignStaffDialog(
                    customerChangeStatusDataList!,
                    status,
                  );
                } else {
                  if (status!.equalsIgnoreCase(Strings.reject)) {
                    Utils.showSnackbar(Strings.SUCCESS, "Reject Successfully.",
                        AppTheme.colorWhite, AppTheme.colorGreen);
                  } else {
                    Utils.showSnackbar(
                        Strings.SUCCESS,
                        "Approved Successfully.",
                        AppTheme.colorWhite,
                        AppTheme.colorGreen);
                  }
                  // Get.back(result: true);
                  // getCreditNoteListData();
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
        _handleApiErrorCustom(error);
      },
    );
  }


  showAssignStaffDialog(
      List<CustomerTerminateApproveRejectDataList> item, String? staffStatus) {
    showDialog(
        context: Get.context!,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return ChangeStatusAssignDialog(
              changeStatusAssignAction: this,
              itemsOrgLst: item,
              staffStatus: staffStatus);
        });
  }

  _handleApiErrorCustom(ResponseModel error) {
    isLoading = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (error.statusCode == Constant.EXPECTION_FAIL_STATUS) {
      Utils.showSnackbar(
          Strings.INFO, error.message,
          AppTheme.colorWhite,
          AppTheme.colorBlueRView);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }

  _handleApiError(ResponseModel error) {
    isLoading = false;
    isShowLoadMore = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }

  @override
  void changeStatusAssignBtnAction({CustomerTerminateApproveRejectDataList? selectedItem, bool? isStaffSelected, String? approveRejectStatus}) {
    Get.back();

    log("approveRejectStatus==>${approveRejectStatus}");

    if (isStaffSelected == true) {
      log("Staff is selected");
      if (approveRejectStatus!.equalsIgnoreCase(Strings.approve)) {
        log("Staff is selected!!!!!!!!=>${Strings.approve}");
        assignStaffShiftLocation(
            entityId: entityId,
            nextAssignStaff: selectedItem!.id,
            isApproveRequest: true,
            eventName: "TERMINATION");
      } else if (approveRejectStatus.equalsIgnoreCase(Strings.reject)) {
        log("Staff is selected!!!!!!!!=>${Strings.reject}");
        assignStaffShiftLocation(
            entityId: entityId,
            nextAssignStaff: selectedItem!.id,
            isApproveRequest: false,
            eventName: "TERMINATION");
      }
    }else{
      Utils.showSnackbar(Strings.INFO, Strings.pelase_select_staff,
          AppTheme.colorWhite, AppTheme.colorBlueRView);
    } /*else {
      if (approveRejectStatus!.equalsIgnoreCase(Strings.approved)) {
        // log("Not Staff is selected!!!!!!!!=>${Strings.rejected}");
        assignEveryStaffShiftLocation(
            entityId: entityId,
            isApprovedRequest: true,
            eventName: "TERMINATION");
      } else if (approveRejectStatus.equalsIgnoreCase(Strings.rejected)) {
        // log("Not Staff is selected!!!!!!!!=>${Strings.rejected}");
        assignEveryStaffShiftLocation(
            entityId: entityId,
            isApprovedRequest: false,
            eventName: "TERMINATION");
      }
    }*/
  }


  assignStaffShiftLocation(
      {int? entityId,
        int? nextAssignStaff,
        bool? isApproveRequest,
        String? eventName}) {
    String apiUrl =
        "${UrlConstants.assignFromStaffCreditNoteList}?entityId=$entityId&eventName=$eventName&nextAssignStaff=$nextAssignStaff&isApproveRequest=$isApproveRequest";
    isLoading = true;
    update();
    ViewCreditNoteProvider().assignCreditNoteEveryStaff(
      url: apiUrl,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          // if (responseModel.result != null) {
          try {
            Utils.showSnackbar(Strings.SUCCESS, Strings.successfully,
                AppTheme.colorWhite, AppTheme.colorGreen);
            // getNewAddressShiftLocation();
            getPACustomerTerminationList();
          } on Exception catch (e) {
            print(e.toString());
          }
          // }
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
  }

  assignEveryStaffShiftLocation(
      {int? entityId, bool? isApprovedRequest, String? eventName}) {
    String apiUrl =
        "${UrlConstants.creditNote_assign_every_staff}?entityId=$entityId&eventName=$eventName&isApproveRequest=$isApprovedRequest";
    isLoading = true;
    update();
    ViewCreditNoteProvider().assignCreditNoteEveryStaff(
      url: apiUrl,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          // if (responseModel.result != null) {
          try {
            Utils.showSnackbar(Strings.SUCCESS, Strings.successfully,
                AppTheme.colorWhite, AppTheme.colorGreen);
            // getNewAddressShiftLocation();
            getPACustomerTerminationList();
          } on Exception catch (e) {
            print(e.toString());
          }
          // }
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
  }




}
