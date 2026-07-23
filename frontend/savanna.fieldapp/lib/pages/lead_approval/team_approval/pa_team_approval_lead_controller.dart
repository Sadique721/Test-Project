import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/credit_note/credit_note_provider.dart';
import 'package:savbill/pages/lead_approval/lead_approval_provider.dart';
import 'package:savbill/pages/lead_approval/model/la_assign_list_res.dart';
import 'package:savbill/pages/lead_management/lead_staff_assign_dialog.dart';
import 'package:savbill/pages/lead_management/lead_system_provider.dart';
import 'package:savbill/pages/lead_management/model/lead_approve_reject_req.dart';
import 'package:savbill/pages/lead_management/model/lead_approve_reject_staff_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

import '../../lead_management/create_lead_screen.dart';
import '../../lead_management/model/view_lead_response.dart';

class PATeamApprovalLeadController extends GetxController
    implements LeadAssignAction {
  bool isLoading = false;
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;
  GetStorage getStorage = GetStorage();
  List<LAAssignContent>? assignList = [];
  LAAssignListRes? assignListRes;
  UserDetail? userDetail;
  int? paymentId;
  List<ApproveRejectStaffLeadList>? approveRejectStaffLeadList = [];

  @override
  void onInit() {
    super.onInit();
    initPlatformState();

    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (assignListRes != null &&
            assignListRes!.leadMasterList!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          getPATeamApprovalLeadList();
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
    getPATeamApprovalLeadList();
  }

  getPATeamApprovalLeadList() {
    PageRequest request = PageRequest(page: page, pageSize: 10);
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    LeadApprovalsProvider().getLATeamApprovalLeadList(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              LAAssignListRes responseData = LAAssignListRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                assignListRes = responseData;
                if (page == 1) {
                  assignList?.clear();
                }
                if (responseData.leadMasterList!.content != null &&
                    responseData.leadMasterList!.content!.isNotEmpty) {
                  assignList?.addAll(responseData.leadMasterList!.content!);
                }
              } else {
                if (page == 1) {
                  assignList?.clear();
                }
                // if (responseData.message!.isNotEmpty) {
                //   Utils.showSnackbar(
                //       Strings.ERROR,
                //       responseData.message,
                //       AppTheme.colorWhite,
                //       AppTheme.colorRed);
                // }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (page == 1) {
            assignList?.clear();
          }
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        update();
      },
      onError: (ResponseModel error) {
        if (page == 1) {
          assignList?.clear();
        }
        _handleApiError(error);
      },
    );
  }

  approveRejectStaffLead(
      {required String? status,
      required String? remark,
      required BuildContext context,
      LAAssignContent? item}) {
    isLoading = true;
    update();
    LeadApproveRejectReq request = LeadApproveRejectReq(
      approveRequest: status!.equalsIgnoreCase(Strings.approve) ? true : false,
      buId: item!.buId,
      currentLoggedInStaffId: userDetail!.userId,
      firstname: item.firstname,
      id: item.id,
      mvnoId: userDetail!.mvnoId,
      remark: remark,
      serviceareaid: item.serviceareaid,
      flag: status.equalsIgnoreCase(Strings.approve) ? "Approve" : "Reject",
      nextTeamMappingId: item.nextTeamMappingId,
      status: item.leadStatus,
      username: item.username,
    );

    log("LeadApproveRejectReq>>>>${jsonEncode(request)}");

    LeadSystemProvider().approveRejectLeads(
      request: request,
      onSuccess: (responseModel) {
        isLoading = false;
        update();
        try {
          // Map<String, dynamic> map = responseModel.data;
          LeadApproveRejectStaffRes responseData =
              LeadApproveRejectStaffRes.fromJson(responseModel);
          if ((responseData.status != null && responseData.status == 200) ||
              (responseData.responseCode != null &&
                  responseData.responseCode == 200)) {
            if (responseData.data != null &&
                responseData.data == "FINAL_APPROVED") {
              openCreateAddLeadScreen(
                  Strings.lead_caf, LeadMasterListData(id: item.id), status);
            } else if (responseData.data != null &&
                responseData.data == "FINAL_REJECTED") {
              Utils.showSnackbar(Strings.SUCCESS, "Reject Successfully.",
                  AppTheme.colorWhite, AppTheme.colorGreen);
            } else if (responseData.data == null) {
              if (responseData.dataList != null &&
                  responseData.dataList!.isNotEmpty) {
                approveRejectStaffLeadList?.clear();
                approveRejectStaffLeadList?.addAll(responseData.dataList!);
                showAssignStaffDialog(responseData.dataList!,status,request);
                // showAssignStaffDialog(responseData.dataList!, status, request);
              } else {
                if (status.equalsIgnoreCase(Strings.reject)) {
                  Utils.showSnackbar(Strings.SUCCESS, "Reject Successfully.",
                      AppTheme.colorWhite, AppTheme.colorGreen);
                } else if (status.equalsIgnoreCase(Strings.approve)) {
                  Utils.showSnackbar(Strings.SUCCESS, "Approved Successfully.",
                      AppTheme.colorWhite, AppTheme.colorGreen);
                }
                // Get.back(result: true);
                // getCreditNoteListData();
              }
            }
          }
        } on Exception catch (e) {
          print(e.toString());
        }
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  openCreateAddLeadScreen(String? from, LeadMasterListData? leadViewContentData,
      String? approveRejectStatus) async {
    var result = await Get.to(CreateLeadScreen(), arguments: {
      Constant.FROM: from,
      Constant.LEAD_DETAIL: leadViewContentData,
      Constant.LEAD_STATUS: approveRejectStatus,
    });
  }

  showAssignStaffDialog(List<ApproveRejectStaffLeadList> item,
      String? staffStatus, LeadApproveRejectReq? requestData) {
    showDialog(
        context: Get.context!,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return LeadAssignDialog(
              leadAssignAction: this,
              itemsOrgLst: item,
              request: requestData,
              // entityId: caseId,
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

  assignLeadStaffNote(ApproveRejectStaffLeadList? selectedItem,
      bool? isApproveRequest, LeadApproveRejectReq? request) {
    LeadApproveRejectReq req = LeadApproveRejectReq();
    // if (selectedRejectedReason != null) {
    //   req.rejectedReasonMasterId = selectedRejectedReason!.id;
    // } else {
    //   req.rejectedReasonMasterId = null;
    // }
    req = request!;

    log("LeadApproveRejectReq>>>>>>${jsonEncode(req)}");

    String apiUrl =
        "${UrlConstants.assignFromStaffListForLead}?eventName=LEAD&nextAssignStaff=${selectedItem!.id}";

    log("assignFromStaffListForLead==>$apiUrl");
    isLoading = true;
    update();
    LeadSystemProvider().assignLeadStaff(
      url: apiUrl,
      request: req,
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
                Utils.showSnackbar(
                    Strings.SUCCESS,
                    responseData.responseMessage!,
                    AppTheme.colorWhite,
                    AppTheme.colorGreen);
                getPATeamApprovalLeadList();
              } else {
                Utils.showSnackbar(Strings.INFO, responseData.responseMessage!,
                    AppTheme.colorWhite, AppTheme.colorBlueRView);
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
        _handleApiError(error);
      },
    );
  }

  assignEveryStaffLead({int? entityId, bool? isApprovedRequest}) {
    String apiUrl =
        "${UrlConstants.creditNote_assign_every_staff}?entityId=$entityId&eventName=LEAD&isApproveRequest=$isApprovedRequest";
    isLoading = true;
    update();
    ViewCreditNoteProvider().assignCreditNoteEveryStaff(
      url: apiUrl,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.result != null) {
          try {
            Map<String, dynamic> map = responseModel.result;
            BaseResponse responseData = BaseResponse.fromJson(map);
            if ((responseData.status != null && responseData.status == 200) ||
                (responseData.responseCode != null &&
                    responseData.responseCode == 200)) {
              Utils.showSnackbar(Strings.SUCCESS, responseData.responseMessage!,
                  AppTheme.colorWhite, AppTheme.colorGreen);
              getPATeamApprovalLeadList();
            } else {
              Utils.showSnackbar(Strings.INFO, responseData.responseMessage!,
                  AppTheme.colorWhite, AppTheme.colorBlueRView);
            }
          } on Exception catch (e) {
            print(e.toString());
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

  @override
  void leadAssignBtnAction(
      {ApproveRejectStaffLeadList? selectedItem,
      bool? isStaffSelected,
      LeadApproveRejectReq? request,
      String? approveRejectStatus}) {
    Get.back();
    if (isStaffSelected == true) {
      // log("Staff is selected");
      if (approveRejectStatus!.equalsIgnoreCase(Strings.approve)) {
        // log("Staff is selected!!!!!!!!=>${Strings.approve}");
        assignLeadStaffNote(selectedItem, true, request);
      } else if (approveRejectStatus.equalsIgnoreCase(Strings.reject)) {
        // log("Staff is selected!!!!!!!!=>${Strings.reject}");
        assignLeadStaffNote(selectedItem, false, request);
      }
    } else {
      if (approveRejectStatus!.equalsIgnoreCase(Strings.approve)) {
        assignEveryStaffLead(entityId: request!.id, isApprovedRequest: true);
      } else if (approveRejectStatus.equalsIgnoreCase(Strings.reject)) {
        assignEveryStaffLead(entityId: request!.id, isApprovedRequest: false);
      }
    }
  }
}
