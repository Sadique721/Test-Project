import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/pages/pending_approvals/model/response/inventory_approval_res.dart';
import 'package:savbill/pages/pending_approvals/pending_approvals_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class InventoryPendingApprovalController extends GetxController {
  bool isLoading = false;
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;
  GetStorage getStorage = GetStorage();
  List<InventoryApprovalDataList>? inventoryApprovalList = [];
  InventoryApprovalRes? inventoryApprovalRes;
  UserDetail? userDetail;
  // List<DocApproveRejectAssignStaffDataList>? docApproveRejectAssignStaffList = [];

  @override
  void onInit() {
    super.onInit();
    initPlatformState();

    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (inventoryApprovalRes != null && inventoryApprovalRes!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          getInventoryApprovalApi();
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
    getInventoryApprovalApi();
  }

  getInventoryApprovalApi() {
    PageRequest request = PageRequest(page: page, pageSize: 5);
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    PendingApprovalsProvider().getInventoryApproval(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              InventoryApprovalRes responseData = InventoryApprovalRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                inventoryApprovalRes = responseData;
                if (page == 1) {
                  inventoryApprovalList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {

                  inventoryApprovalList?.addAll(responseData.dataList!);

                }
              } else {
                if (page == 1) {
                  inventoryApprovalList?.clear();
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
            inventoryApprovalList?.clear();
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
          inventoryApprovalList?.clear();
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
        InventoryApprovalDataList? inventoryApprovalDataList}) {
    // CustomerDocApproveRejectReq request = CustomerDocApproveRejectReq(
    //   nextStaffId: "",
    //   flag: "approved",
    //   remark: "",
    //   staffId: userDetail!.userId.toString(),
    // );
    isLoading = true;
    update();
    // CustomerProvider().getCustomerDocumentApproveRejected(
    //   documentId: int.parse(custDocApprovalDataList!.docId.toString()),
    //   remarks: remark,
    //   isApproveRequest: isApprovedRequest,
    //   request: request,
    //   onSuccess: (ResponseModel responseModel) {
    //     customerDocList?.clear();
    //     if (responseModel.statusCode == 200) {
    //       if (responseModel.result != null) {
    //         try {
    //           Map<String, dynamic> map = responseModel.result;
    //           DocApproveRejectAssignStaffRes responseData =
    //           DocApproveRejectAssignStaffRes.fromJson(map);
    //           if (responseData.responseCode == 200) {
    //             if (responseData.dataList != null &&
    //                 responseData.dataList!.isNotEmpty) {
    //               docApproveRejectAssignStaffList?.clear();
    //               docApproveRejectAssignStaffList
    //                   ?.addAll(responseData.dataList!);
    //               showAssignStaffDialog(
    //                   responseData.dataList!, status, context!);
    //             } else {
    //               if (status!.equalsIgnoreCase(Strings.reject)) {
    //                 Utils.showSnackbar(Strings.SUCCESS, "Reject Successfully.",
    //                     AppTheme.colorWhite, AppTheme.colorGreen);
    //               } else {
    //                 Utils.showSnackbar(
    //                     Strings.SUCCESS,
    //                     "Approved Successfully.",
    //                     AppTheme.colorWhite,
    //                     AppTheme.colorGreen);
    //               }
    //               // Get.back(result: true);
    //             }
    //           } else {
    //             if (responseData.responseMessage!.isNotEmpty) {
    //               Utils.showSnackbar(
    //                   Strings.ERROR,
    //                   responseData.responseMessage,
    //                   AppTheme.colorWhite,
    //                   AppTheme.colorRed);
    //             }
    //           }
    //         } on Exception catch (e) {
    //           print(e.toString());
    //         }
    //       }
    //     } else {
    //       if (responseModel.message!.isNotEmpty) {
    //         Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
    //             AppTheme.colorWhite, AppTheme.colorRed);
    //       }
    //     }
    //     isLoading = false;
    //     update();
    //   },
    //   onError: (ResponseModel error) {
    //     _handleApiError(error);
    //   },
    // );
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
