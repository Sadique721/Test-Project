import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/pages/service_management/service_audit/response/cust_service_audit_status_res.dart';
import 'package:savbill/pages/service_management/service_management_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class CustomerAuditServiceStatusController extends GetxController {
  bool isLoading = false;
  int serviceId = 0;
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;

  CustServiceAuditStatusResponse? custServiceAuditStatusResponse;
  CustomerDetail? customerDetail;
  List<ServiceAuditStatusContent>? serviceAuditStatusContentList = [];

  @override
  void onInit() {
    super.onInit();
    getArgumentData();

    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (custServiceAuditStatusResponse != null &&
            custServiceAuditStatusResponse!.totalPages != page) {
          if (custServiceAuditStatusResponse!.totalPages != page) {
            isShowLoadMore = true;
            page = page + 1;
            update();
            // getTeamHierarchyApprovalFlow(eventId);
            serviceAuditStatusCall(serviceId);
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

      if(arguments[Constant.SERVICE_ID] != null){
        serviceId = arguments[Constant.SERVICE_ID];
        serviceAuditStatusCall(serviceId);
      }

    }
    update();
  }

  serviceAuditStatusCall(int? serviceId) {
    isShowLoadMore = true;
    if (page == 1) {
      isLoading = true;
      serviceAuditStatusContentList?.clear();
    }
    update();
    ServiceManagementProvider().customerServiceAuditStatus(
      serviceId: serviceId,
      pageRequest:
          PageRequest(page: page, pageSize: Constant.PAGE_LOAD_DATA_LIMIT),
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (page == 1) {
            serviceAuditStatusContentList?.clear();
          }
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustServiceAuditStatusResponse responseData =
                  CustServiceAuditStatusResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.data != null &&
                    responseData.data!.content!.isNotEmpty) {
                  serviceAuditStatusContentList
                      ?.addAll(responseData.data!.content!);
                }
              } else {
                if (page == 1) {
                  serviceAuditStatusContentList?.clear();
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
          serviceAuditStatusContentList?.clear();
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
