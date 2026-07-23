
import 'dart:convert';

import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer_inventory/inventory_provider.dart';
import 'package:savbill/pages/customer_inventory/request/team_hierarchy_approval_flow_res.dart';
import 'package:savbill/pages/customer_inventory/response/inventory_work_flow_res.dart';
import 'package:savbill/pages/lead_management/lead_system_provider.dart';
import 'package:savbill/pages/lead_management/model/lead_master_details_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class LeadStatusWorkFlowController extends GetxController {
  bool isLoading = false;
  bool isShowLoadMore = false;
  int eventId = 0;
  String? eventName = "LEAD";
  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;
  ScrollController? controller;
  int page = 1;
  List<WorkFlowAuditDataList>? workFlowAuditDataList = [];
  InventoryWorkFlowAuditRes? inventoryWorkFlowAuditRes;
  CustomerDetail? customerDetail;
  List<TeamHierarchyDataList>? teamHierarchyDataList = [];
  LeadMaster? leadMaster;
  int? buId,mvnoId;
  @override
  void onInit() {
    super.onInit();
    initPlatformState();
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (inventoryWorkFlowAuditRes != null &&
            inventoryWorkFlowAuditRes!.totalPages != page) {
            isShowLoadMore = true;
            page = page + 1;
            update();
            workFlowAuditApi(eventId);
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
    }
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {

      if(arguments[Constant.LEAD_MASTER_ID] != null){
        eventId = arguments[Constant.LEAD_MASTER_ID];
      }
      getLeadDetails(eventId);
    }
    update();
  }

  getLeadDetails(int? eventId) {
    isLoading = true;
    update();
    LeadSystemProvider().getLeadDetailsById(
      eventId: eventId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              LeadMasterDetailsRes responseData =
              LeadMasterDetailsRes.fromJson(map);
              if (responseData.responseCode == 200 ||
                  responseData.status == 200) {
                leadMaster = responseData.leadMaster;

                buId = leadMaster?.buId ?? 0;
                if(leadMaster?.nextTeamMappingId != null){
                  getLeadTeamHierarchyApprovalFlow(buId: buId,nextTeamMappingId: leadMaster?.nextTeamMappingId);
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
        workFlowAuditApi(eventId);
        _handleApiError(error);
      },
    );
  }

  workFlowAuditApi(int? eventId) {
   PageRequest pageRequest =  PageRequest(page: page, pageSize: Constant.PAGE_LOAD_DATA_LIMIT);
    // isLoading = true;
    // isShowLoadMore = true;
   if (!isShowLoadMore) {
     isLoading = true;
     update();
   }
    // if (page == 1) {
    //   isLoading = true;
    //   workFlowAuditDataList?.clear();
    // }
    // update();
    InventoryProvider().inventoryWorkFlowAudit(
      request: pageRequest,
      eventId: eventId,
      eventName : eventName,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {

          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              InventoryWorkFlowAuditRes responseData =
              InventoryWorkFlowAuditRes.fromJson(map);
              if (responseData.responseCode == 200) {
                inventoryWorkFlowAuditRes = responseData;
                if (page == 1) {
                  workFlowAuditDataList?.clear();
                }
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
          if (page == 1) {
            workFlowAuditDataList?.clear();
          }
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        // isLoading = false;
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


  getLeadTeamHierarchyApprovalFlow({int? buId, int? nextTeamMappingId}) {
    teamHierarchyDataList!.clear();
    isLoading = true;
    update();
    LeadSystemProvider().getLeadStatusProgress(
      buId: buId,
      mvNoId: userDetail?.mvnoId ?? 0,
      nextTeamMappingId :nextTeamMappingId,
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
      },
      onError: (ResponseModel error) {
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