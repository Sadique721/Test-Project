import 'dart:convert';
import 'dart:developer';

import 'package:path/path.dart';
import 'package:savbill/pages/credit_note/credit_note_provider.dart';
import 'package:savbill/pages/customer_caf/caf_customer_staff_assign_dialog.dart';
import 'package:savbill/pages/customer_caf/response/approve_reject_caf_customer_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/pages/pending_approvals/model/request/customer_approve_reject_req.dart';
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
import 'package:http/http.dart' as http;
import 'package:http_parser/http_parser.dart';

import '../../model/file_detail.dart';

class CustomerPendingApprovalController extends GetxController
    implements CafCustomerAssignAction {
  bool isLoading = false;
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;
  GetStorage getStorage = GetStorage();
  List<ApprovalPendingCustomer>? customersList = [];
  ApprovalPendingCustomerRes? customerListRes;
  UserDetail? userDetail;

  List<ApproveRejectCafDataList>? approveRejectCustomerApprovalList = [];

  @override
  void onInit() {
    super.onInit();
    initPlatformState();
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (customerListRes != null && customerListRes!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          getPACustomerList();
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
    getPACustomerList();
  }

  getPACustomerList() {
    PageRequest request = PageRequest(page: page, pageSize: 10);
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    PendingApprovalsProvider().getCustomerPendingApprovals(
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

  approveRejectCustomer(
      CustomerApproveRejectReq? custRequest, BuildContext context,List<FileDetail>? allFiles) async {
    // isLoading = true;
    // update();

    final url = Uri.parse(UrlConstants.approveCustomerCAF);
    final request = http.MultipartRequest('PUT', url);

    String token = "";
    if (getStorage.hasData(Constant.USER_TOKEN)) {
      token = await getStorage.read(Constant.USER_TOKEN);
    }

    request.headers['Authorization'] = token;
    request.headers['x-access-token'] = '';
    request.headers['Content-Type'] = 'multipart/form-data';

// ✅ Populate fields safely
    request.fields['custcafId'] = safeString(custRequest?.custcafId ?? "");
    request.fields['flag'] = safeString(custRequest?.flag ?? "");
    request.fields['remark'] = safeString(custRequest?.remark ?? "");
    request.fields['staffId'] = safeString(custRequest?.staffId ?? "");
    request.fields['nextStaffId'] = "";

    // ✅ Attach files if any
    if (allFiles != null && allFiles.isNotEmpty) {
      for (int i = 0; i < allFiles.length; i++) {
        final file = allFiles[i];
        if (file.filePathLocal != null) {
          debugPrint("📡 filePathLocal: ${file.filePathLocal}");
          request.files.add(await http.MultipartFile.fromPath(
            'files',
            file.filePathLocal!,
            filename: basename(file.fileName ?? ""),
            contentType: MediaType('image', 'jpg'), // or 'png'
          ));
        }
      }
    }
    debugPrint("📡 API URL: ${request.url}");
    debugPrint("📦 Payload (fields): ${request.fields}");
    debugPrint(
        "📎 Attached files: ${request.files.map((f) => f.filename).toList()}");

    try {
      isLoading = true;
      update();

      final streamedResponse = await request.send();
      final response = await http.Response.fromStream(streamedResponse);

      debugPrint("🔄 Response Status: ${response.statusCode}");
      debugPrint("📄 Response Body: ${response.body}");

      final data = jsonDecode(response.body);

      log("response body => ${response.body}"); // ✅ correct
      if (response.statusCode == 200 && data != null) {
        Map<String, dynamic> map = data;
        ApproveRejectCafCustomerRes responseData =
            ApproveRejectCafCustomerRes.fromJson(map);
        if (responseData.result!.dataList != null &&
            responseData.result!.dataList!.isNotEmpty) {
          approveRejectCustomerApprovalList?.clear();
          approveRejectCustomerApprovalList
              ?.addAll(responseData.result!.dataList!);
          showAssignStaffDialog(responseData.result!.dataList!,
              custRequest?.flag, context, custRequest?.custcafId);
        } else {
          if (safeString(custRequest?.flag)
              .equalsIgnoreCase(Strings.rejected.toLowerCase())) {
            Utils.showSnackbar(Strings.SUCCESS, "Reject Successfully.",
                AppTheme.colorWhite, AppTheme.colorGreen);
          } else {
            Utils.showSnackbar(Strings.SUCCESS, "Approved Successfully.",
                AppTheme.colorWhite, AppTheme.colorGreen);
          }
          if (Get.isRegistered<CustomerPendingApprovalController>()) {
            final customerPendingApprovalController =
                Get.find<CustomerPendingApprovalController>();
            customerPendingApprovalController.page = 1;
            customerPendingApprovalController.getPACustomerList();
          }
        }
      } else {
        if (data is Map &&
            data["message"] != null &&
            data["message"].toString().isNotEmpty) {
          Utils.showSnackbar(
            Strings.ERROR,
            data["message"].toString(), // ✅ Show actual message, not isNotEmpty
            AppTheme.colorWhite,
            AppTheme.colorRed,
          );
        } else {
          Utils.showSnackbar(
            Strings.ERROR,
            "Something went wrong. Please try again.",
            AppTheme.colorWhite,
            AppTheme.colorRed,
          );
        }
      }
      isLoading = false;
      update();
    } catch (e) {
      Utils.showSnackbar(
        Strings.ERROR,
        e.toString(),
        AppTheme.colorWhite,
        AppTheme.colorRed,
      );
      debugPrint('❌ Exception during upload: $e');
      // Show error UI
      isLoading = false;
      update();
    }

    // PendingApprovalsProvider().approveRejectCustomer(
    //   request: request,
    //   onSuccess: (ResponseModel responseModel) {
    //     isLoading = false;
    //     update();
    //     if (responseModel.statusCode == 200) {
    //       if (responseModel.result != null) {
    //         try {
    //           Map<String, dynamic> map = responseModel.result;
    //           ApproveRejectCafCustomerRes responseData =
    //           ApproveRejectCafCustomerRes.fromJson(map);
    //           if ((responseData.status != null && responseData.status == 200) ||
    //               (responseData.responseCode != null &&
    //                   responseData.responseCode == 200)) {
    //             if (responseData.result!.dataList != null &&
    //                 responseData.result!.dataList!.isNotEmpty) {
    //               approveRejectCustomerApprovalList?.clear();
    //               approveRejectCustomerApprovalList?.addAll(responseData.result!.dataList!);
    //               showAssignStaffDialog(responseData.result!.dataList!, request.flag, context, request.custcafId);
    //             } else {
    //               if (request.flag!.equalsIgnoreCase(Strings.reject)) {
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
    //               // getCreditNoteListData();
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

  showAssignStaffDialog(List<ApproveRejectCafDataList> item,
      String? staffStatus, BuildContext context, int? entityId) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return CafCustomerAssignDialog(
            cafCustomerAssignAction: this,
            itemsOrgLst: item,
            staffStatus: staffStatus,
            entityId: entityId,
          );
        });
  }

  @override
  void cafCustomerAssignBtnAction(
      {ApproveRejectCafDataList? selectedItem,
      bool? isStaffSelected,
      String? approveRejectStatus,
      int? entityId}) {
    Get.back();
    if (isStaffSelected == true) {
      // log("Staff is selected");
      if (approveRejectStatus!.equalsIgnoreCase(Strings.approve)) {
        // log("Staff is selected!!!!!!!!=>${Strings.approve}");
        assignStaffCreditNote(entityId, selectedItem!.id, true);
      } else if (approveRejectStatus.equalsIgnoreCase(Strings.reject)) {
        // log("Staff is selected!!!!!!!!=>${Strings.reject}");
        assignStaffCreditNote(entityId, selectedItem!.id, false);
      }
    } else {
      log("NotStaffis selected==>${approveRejectStatus}");
      if (approveRejectStatus!.equalsIgnoreCase("approved")) {
        assignEveryStaffCreditNote(entityId: entityId, isApprovedRequest: true);
      } else if (approveRejectStatus.equalsIgnoreCase("rejected")) {
        assignEveryStaffCreditNote(
            entityId: entityId, isApprovedRequest: false);
        // }
      }
    }
  }

  assignStaffCreditNote(
      int? entityId, int? nextAssignStaff, bool? isApproveRequest) {
    String apiUrl =
        "${UrlConstants.assignFromStaffCreditNoteList}?entityId=$entityId&eventName=CAF&nextAssignStaff=$nextAssignStaff&isApproveRequest=$isApproveRequest";
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
            getPACustomerList();
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

  assignEveryStaffCreditNote({int? entityId, bool? isApprovedRequest}) {
    String apiUrl =
        "${UrlConstants.creditNote_assign_every_staff}?entityId=$entityId&eventName=CAF&isApproveRequest=$isApprovedRequest";
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
            getPACustomerList();
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

  String safeString(Object? value) {
    final str = value?.toString() ?? "";
    return str.isNotEmpty ? str : "";
  }
}
