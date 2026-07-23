import 'package:savbill/pages/dashboard/model/response/payment_team_hierarchy_res.dart';
import 'package:savbill/pages/dashboard/model/response/workflow_audit_res.dart';
import 'package:savbill/pages/dashboard/payment_provider.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class PaymentAuditController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();
  List<TeamHierarchyDetail>? teamHierarchyList = [];
  int? paymentId;
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;
  int tabIndex = 0;
  WorkflowAuditRes? workflowAuditRes;
  List<WorkflowAuditDetail>? workflowAuditList = [];
  int currentStep = 0;

  @override
  void onInit() {
    super.onInit();
    controller = ScrollController();
    getArgumentData();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (workflowAuditRes != null && workflowAuditRes?.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          getWorkflowAuditList();
        }
      }
    });
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.ID] != null) {
        paymentId = arguments[Constant.ID];
      }
    }
    update();
    if (paymentId != null) {
      getPaymentTeamHierarchy();
    }
  }

  getPaymentTeamHierarchy() {
    teamHierarchyList!.clear();
    isLoading = true;
    update();
    PaymentProvider().paymentTeamHierarchy(
      id: paymentId!,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PaymentTeamHierarchyRes responseData =
                  PaymentTeamHierarchyRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  teamHierarchyList!.addAll(responseData.dataList!);
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
        getWorkflowAuditList();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getWorkflowAuditList();
      },
    );
  }

  getWorkflowAuditList() {
    PageRequest normalRequest = PageRequest(page: page, pageSize: 10);
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    PaymentProvider().paymentWorkflowDetail(
      id: paymentId!,
      request: normalRequest,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              WorkflowAuditRes responseData = WorkflowAuditRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                workflowAuditRes = responseData;
                if (page == 1) {
                  workflowAuditList!.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  workflowAuditList!.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  workflowAuditList!.clear();
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
            workflowAuditList!.clear();
          }
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        if (page == 1) {
          workflowAuditList!.clear();
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
