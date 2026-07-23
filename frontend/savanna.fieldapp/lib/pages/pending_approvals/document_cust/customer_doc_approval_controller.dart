import 'dart:convert';

import 'package:savbill/pages/credit_note/credit_note_provider.dart';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/document_assign_dialog.dart';
import 'package:savbill/pages/customer/model/request/customer_doc_approve_reject_req.dart';
import 'package:savbill/pages/customer/model/response/doc_approve_reject_staff_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/pages/pending_approvals/model/response/ap_invoice_res.dart';
import 'package:savbill/pages/pending_approvals/model/response/customer_doc_approval_res.dart';
import 'package:savbill/pages/pending_approvals/pending_approvals_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class CustomerDocApprovalController extends GetxController implements DocumentAssignAction {
  bool isLoading = false;
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;
  GetStorage getStorage = GetStorage();
  List<CustDocApprovalDataList>? customerDocList = [];
  CustomerDocApprovalRes? customerDocApprovalRes;
  UserDetail? userDetail;
  int? entityId;
  List<DocApproveRejectAssignStaffDataList>? docApproveRejectAssignStaffList = [];

  @override
  void onInit() {
    super.onInit();
    initPlatformState();

    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (customerDocApprovalRes != null && customerDocApprovalRes!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          getCustomerDocApprovalApi();
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
    getCustomerDocApprovalApi();
  }

  getCustomerDocApprovalApi() {
    PageRequest request = PageRequest(page: page, pageSize: 10);
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    PendingApprovalsProvider().getCustomerDocApproval(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerDocApprovalRes responseData = CustomerDocApprovalRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                customerDocApprovalRes = responseData;
                if (page == 1) {
                  customerDocList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  customerDocList?.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  customerDocList?.clear();
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
            customerDocList?.clear();
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
          customerDocList?.clear();
        }
        _handleApiError(error);
      },
    );
  }


  getCustomerDocumentApproveRejectData(
      {String? status,
        bool? isApprovedRequest,
        String? remark,
        BuildContext? context,
        CustDocApprovalDataList? custDocApprovalDataList}) {
    CustomerDocApproveRejectReq request = CustomerDocApproveRejectReq(
      nextStaffId: "",
      flag: "approved",
      remark: "",
      staffId: userDetail!.userId.toString(),
    );
    isLoading = true;
    update();
    CustomerProvider().getCustomerDocumentApproveRejected(
      documentId: int.parse(custDocApprovalDataList!.docId.toString()),
      remarks: remark,
      isApproveRequest: isApprovedRequest,
      request: request,
      onSuccess: (ResponseModel responseModel) {
        customerDocList?.clear();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              DocApproveRejectAssignStaffRes responseData =
              DocApproveRejectAssignStaffRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  docApproveRejectAssignStaffList?.clear();
                  docApproveRejectAssignStaffList
                      ?.addAll(responseData.dataList!);
                  showAssignStaffDialog(
                      responseData.dataList!, status, context!);
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


  showAssignStaffDialog(List<DocApproveRejectAssignStaffDataList> item,
      String? staffStatus, BuildContext context) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return DocumentAssignDialog(
              documentAssignAction: this,
              itemsOrgLst: item,
              staffStatus: staffStatus);
        });
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


  assignStaffCustDocument(
      int? entityId, int? nextAssignStaff, bool? isApproveRequest) {
    String apiUrl =
        "${UrlConstants.assignFromStaffCreditNoteList}?entityId=$entityId&eventName=DOCUMENT_VERIFICATION&nextAssignStaff=$nextAssignStaff&isApproveRequest=$isApproveRequest";
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
            // getCreditNoteListData();
            getCustomerDocApprovalApi();
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

  assignEveryStaffCustDocument({int? entityId, bool? isApprovedRequest}) {
    String apiUrl =
        "${UrlConstants.creditNote_assign_every_staff}?entityId=$entityId&eventName=DOCUMENT_VERIFICATION&isApproveRequest=$isApprovedRequest";
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
            // getCreditNoteListData();
            getCustomerDocApprovalApi();
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

  @override
  void documentAssignBtnAction({DocApproveRejectAssignStaffDataList? selectedItem, bool? isStaffSelected, String? approveRejectStatus}) {
    Get.back();
    if (isStaffSelected == true) {
      if (approveRejectStatus!.equalsIgnoreCase(Strings.approve)) {
        assignStaffCustDocument(entityId, selectedItem!.id, true);
      } else if (approveRejectStatus.equalsIgnoreCase(Strings.reject)) {
        assignStaffCustDocument(entityId, selectedItem!.id, false);
      }
    } else {
      if (approveRejectStatus!.equalsIgnoreCase(Strings.approve)) {
        assignEveryStaffCustDocument(
            entityId: entityId, isApprovedRequest: true);
      } else if (approveRejectStatus.equalsIgnoreCase(Strings.reject)) {
        assignEveryStaffCustDocument(
            entityId: entityId, isApprovedRequest: false);
      }
    }
  }
}
