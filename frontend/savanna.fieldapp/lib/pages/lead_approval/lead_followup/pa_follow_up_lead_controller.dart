import 'dart:convert';

import 'package:savbill/pages/lead_approval/lead_approval_provider.dart';
import 'package:savbill/pages/lead_approval/model/la_assign_list_res.dart';
import 'package:savbill/pages/lead_approval/model/la_follow_up_lead_list_res.dart';
import 'package:savbill/pages/lead_management/lead_system_provider.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
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

class PAFollowUpLeadController extends GetxController {
  bool isLoading = false;
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;
  GetStorage getStorage = GetStorage();
  // List<LAAssignContent>? assignList = [];
  List<FollowUpList>? followUpList = [];
  LAFollowUpLeadListRes? followUpLeadListRes;
  UserDetail? userDetail;
  int? paymentId;

  @override
  void onInit() {
    super.onInit();
    initPlatformState();

    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (followUpLeadListRes != null && followUpLeadListRes!.followUpList!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          getPAFollowUpLeadList();
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
    getPAFollowUpLeadList();
  }

  getPAFollowUpLeadList() {
    PageRequest request = PageRequest(page: page, pageSize: 10);
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    LeadApprovalsProvider().getLAFollowUpApprovalLeadList(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              LAFollowUpLeadListRes responseData = LAFollowUpLeadListRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                followUpLeadListRes = responseData;
                if (page == 1) {
                  followUpList?.clear();
                }
                  if (responseData.followUpList!.content != null &&
                      responseData.followUpList!.content!.isNotEmpty) {
                    followUpList?.addAll(responseData.followUpList!.content!);
                  }
              } else {
                if (page == 1) {
                  followUpList?.clear();
                }
                if (responseData.message!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      responseData.message,
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
            followUpList?.clear();
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
          followUpList?.clear();
        }
        _handleApiError(error);
      },
    );
  }


  closeRemarkFollowUp({int? followUpId,String?remark}) {
    isLoading = true;
    update();
    LeadSystemProvider().leadCloseFollowUp(
      followUpId: followUpId,
      remark: remark,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if ((responseData.responseCode != null &&
                  responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                getPAFollowUpLeadList();
                Utils.showSnackbar(Strings.SUCCESS, responseData.message,
                    AppTheme.colorWhite, AppTheme.colorGreen);
              } else {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
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
