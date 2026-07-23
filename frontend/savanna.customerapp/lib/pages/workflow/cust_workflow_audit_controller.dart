import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer_inventory/inventory_provider.dart';
import 'package:savbill/pages/customer_inventory/request/team_hierarchy_approval_flow_res.dart';
import 'package:savbill/pages/customer_inventory/response/inventory_work_flow_res.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class WorkFlowAuditController extends GetxController {
  bool isLoading = false;
  int eventId = 0;
  String? eventName = "CAF";
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;
  List<TeamHierarchyDataList>? teamHierarchyDataList = [];

  List<WorkFlowAuditDataList>? workFlowAuditDataList = [];
  InventoryWorkFlowAuditRes? inventoryWorkFlowAuditRes;
  CustomerDetail? customerDetail;
  String? statusName = Strings.workflow_audit;
  @override
  void onInit() {
    super.onInit();
    getArgumentData();


    if (customerDetail != null){
      if(customerDetail!.status!.equalsIgnoreCase("NewActivation")){
        statusName = "${customerDetail!.title ??""} ${customerDetail!.custname} ${Strings.status}";
      }
    }

    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (inventoryWorkFlowAuditRes != null &&
            inventoryWorkFlowAuditRes!.totalPages != page) {
          if (inventoryWorkFlowAuditRes!.totalPages != page) {
            isShowLoadMore = true;
            page = page + 1;
            update();
            getTeamHierarchyApprovalFlow(eventId);
          }
        }
      }
    });
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {

      if(arguments[Constant.CUSTOMER_DETAIL] != null){
        customerDetail = arguments[Constant.CUSTOMER_DETAIL];
      }

      getTeamHierarchyApprovalFlow(customerDetail!.id);
    }
    update();
  }

  getTeamHierarchyApprovalFlow(int? eventId) {
    teamHierarchyDataList!.clear();
    isLoading = true;
    update();
    InventoryProvider().getTeamWorkFlowProgress(
      eventId: eventId,
      eventName:eventName,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TeamHierarchyApprovalFlowRes responseData =
              TeamHierarchyApprovalFlowRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  teamHierarchyDataList?.addAll(responseData.dataList!);
                }
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
        workFlowAuditApi(eventId);
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        workFlowAuditApi(eventId);
      },
    );
  }

  workFlowAuditApi(int? eventId) {
    // workFlowAuditDataList?.clear();
    // isLoading = true;
    isShowLoadMore = true;
    if (page == 1) {
      isLoading = true;
      workFlowAuditDataList?.clear();
    }
    update();
    InventoryProvider().inventoryWorkFlowAudit(
      request: PageRequest(page: page, pageSize: Constant.PAGE_LOAD_DATA_LIMIT),
      eventId: eventId,
      eventName : eventName,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (page == 1) {
            workFlowAuditDataList?.clear();
          }
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              InventoryWorkFlowAuditRes responseData =
              InventoryWorkFlowAuditRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  workFlowAuditDataList?.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  workFlowAuditDataList?.clear();
                }
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
        isShowLoadMore = false;
        isLoading = false;
        if (page == 1) {
          workFlowAuditDataList?.clear();
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