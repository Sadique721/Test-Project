import 'dart:developer';

import 'package:dio/dio.dart';
import 'package:get_storage/get_storage.dart';
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

import '../customer_inventory/response/inventory_documentList_res.dart';
import '../customer_invoice/image_preview_screen.dart';

class WorkFlowAuditController extends GetxController {
  bool isLoading = false;
  int eventId = 0;
  String? eventName = "CAF";
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;

  GetStorage getStorage = GetStorage();
  List<TeamHierarchyDataList>? teamHierarchyDataList = [];

  List<WorkFlowAuditDataList>? workFlowAuditDataList = [];
  InventoryWorkFlowAuditRes? inventoryWorkFlowAuditRes;
  CustomerDetail? customerDetail;
  String? statusName = Strings.workflow_audit;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();

    if (customerDetail != null) {
      if (customerDetail!.status!.equalsIgnoreCase("NewActivation")) {
        statusName =
            "${customerDetail!.title ?? ""} ${customerDetail!.custname} ${Strings.status}";
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
      if (arguments[Constant.CUSTOMER_DETAIL] != null) {
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
      eventName: eventName,
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
      eventName: eventName,
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

  downloadFile(String? apiUrl, FileDetails items) async {
    isLoading = true;
    var url = "${apiUrl}";
    log("download fileurl==>>${apiUrl}");
    String token = "";
    if (getStorage.hasData(Constant.USER_TOKEN)) {
      token = await getStorage.read(Constant.USER_TOKEN);
    }
    Map<String, String> headers = {
      'Content-type': 'application/json; charset=UTF-8',
      'Accept': 'application/octet-stream',
      'Authorization': 'Bearer $token'
    };
    final dio = Dio();
    isLoading = true;
    try {
      final response = await dio.get(
        url,
        options: Options(responseType: ResponseType.bytes, headers: headers),
      );

      var type = "application/octet-stream"; // default type
      final uint = response.data;
      final magic = uint.sublist(0, 4);

      if (magic.every((b) => b == 0xff)) {
        type = "image/jpeg";
      } else if (magic[0] == 0x89 &&
          magic[1] == 0x50 &&
          magic[2] == 0x4e &&
          magic[3] == 0x47) {
        type = "image/png";
      } else if (magic[0] == 0x47 &&
          magic[1] == 0x49 &&
          magic[2] == 0x46 &&
          magic[3] == 0x38) {
        type = "image/gif";
      } else if (magic[0] == 0xd0 &&
          magic[1] == 0xcf &&
          magic[2] == 0x11 &&
          magic[3] == 0xe0) {
        type = "application/vnd.ms-excel";
      } else if (magic[0] == 0x25 &&
          magic[1] == 0x50 &&
          magic[2] == 0x44 &&
          magic[3] == 0x46) {
        type = "application/pdf";
      } else if (magic[0] == 0xd0 &&
          magic[1] == 0xcf &&
          magic[2] == 0x11 &&
          magic[3] == 0xe0) {
        type = "application/msword";
      }

      isLoading = true;
      final blob = response.data;
      final blobUrl = Uri.dataFromBytes(blob, mimeType: type).toString();
      await Get.to(() => ImagePreviewScreen(
            url: blobUrl,
            titleBarTitle: Strings.view_document,
          ));
      isLoading = false;
    } catch (e) {
      print("Error: $e");
      // Handle error
    }
  }
}
