import 'dart:convert';

import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/pages/pending_approvals/model/request/payment_approve_reject_req.dart';
import 'package:savbill/pages/pending_approvals/model/response/approval_pending_payment_res.dart';
import 'package:savbill/pages/pending_approvals/model/response/payment_change_status_res.dart';
import 'package:savbill/pages/pending_approvals/model/response/ticket_assign_staff_res.dart';
import 'package:savbill/pages/pending_approvals/pending_approvals_provider.dart';
import 'package:savbill/pages/pending_approvals/tickets/ticket_assign_dialog.dart';
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

class PAPaymentController extends GetxController implements TicketAssignAction {
  bool isLoading = false;
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;
  GetStorage getStorage = GetStorage();
  List<ApprovalPendingPayment>? paymentList = [];
  ApprovalPendingPaymentRes? paymentListRes;
  UserDetail? userDetail;
  int? paymentId;

  @override
  void onInit() {
    super.onInit();
    initPlatformState();
    getPAPaymentList();
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (paymentListRes != null && paymentListRes!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          getPAPaymentList();
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

  getPAPaymentList() {
    PageRequest request = PageRequest(page: page, pageSize: 10);
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    PendingApprovalsProvider().getPaymentPendingApprovals(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ApprovalPendingPaymentRes responseData =
                  ApprovalPendingPaymentRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                paymentListRes = responseData;
                if (page == 1) {
                  paymentList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  paymentList?.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  paymentList?.clear();
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
            paymentList?.clear();
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
          paymentList?.clear();
        }
        _handleApiError(error);
      },
    );
  }

  approveRejectPayment(String status, PaymentApproveRejectReq request) {
    isLoading = true;
    update();
    PendingApprovalsProvider().approveRejectPayment(
      status: status.toLowerCase(),
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PaymentChangeStatusRes responseData =
                  PaymentChangeStatusRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200)) {
                if (status.equalsIgnoreCase(Strings.reject)) {
                  if (responseData.payment != null &&
                      responseData.payment!.dataList != null &&
                      responseData.payment!.dataList!.isNotEmpty) {
                    paymentId = request.idlist;
                    update();
                    showAssignStaffDialog(responseData.payment!.dataList!);
                  } else {
                    page = 1;
                    update();
                    Utils.showSnackbar(Strings.SUCCESS, "Reject Successfully.",
                        AppTheme.colorWhite, AppTheme.colorGreen);
                    getPAPaymentList();
                  }
                } else {
                  page = 1;
                  update();
                  Utils.showSnackbar(Strings.SUCCESS, "Approved Successfully.",
                      AppTheme.colorWhite, AppTheme.colorGreen);
                  getPAPaymentList();
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

  showAssignStaffDialog(List<TicketAssignStaff> item) {
    showDialog(
        context: Get.context!,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return TicketAssignDialog(
              ticketAssignAction: this, itemsOrgLst: item);
        });
  }

  @override
  // void ticketAssignBtnAction({TicketAssignStaff? selectedItem}) {
  void ticketAssignBtnAction({TicketAssignStaff? selectedItem,bool? isStaffSelected,String? approveRejectStatus}) {
    Get.back();
    if (selectedItem != null && paymentId != null) {
      assignTicket(selectedItem.id!,isStaffSelected, approveRejectStatus);
    }
  }

  assignTicket(int staffId, bool? approveRejectFlag,String? approveRejectStatus ) {
    isLoading = true;
    update();
    PendingApprovalsProvider().approveRejectTicket(
      entityId: paymentId!,
      eventName: "PAYMENT",
      approveReject: approveRejectFlag!,
      assignId: staffId,
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
                paymentId = null;
                update();
                if (responseData.message != null &&
                    responseData.message!.isNotEmpty) {
                  Utils.showSnackbar(Strings.SUCCESS, responseData.message,
                      AppTheme.colorWhite, AppTheme.colorGreen);
                }
                getPAPaymentList();
              } else {
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
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
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
}
