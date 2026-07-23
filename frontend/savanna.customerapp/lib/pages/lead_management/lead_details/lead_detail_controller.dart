import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/customer_detail_option.dart';
import 'package:savbill/pages/customer/model/response/address_detail_response.dart';
import 'package:savbill/pages/lead_management/lead_system_provider.dart';
import 'package:savbill/pages/lead_management/model/lead_master_details_res.dart';
import 'package:savbill/pages/lead_management/model/lead_service_area_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/acl_constant.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/permission_service.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class LeadDetailController extends GetxController {
  bool isLoading = false;
  int eventId = 0;
  bool isShowLoadMore = false;
  bool isDashboardFlag = false;
  int page = 1;
  int? assignStaffParentId;

  Dio dio = Dio();
  GetStorage getStorage = GetStorage();
  String? progress = "0";
  UserDetail? userDetail;
  BuildContext? context;
  LeadMaster? leadMaster;
  LeadServiceAeraData? leadServiceAeraData;
  String? serviceAreaDATA, customerBill;
  bool? custInvoiceToOrg;
  AddressData? pinCodeData;
  String? valleyType;

  List<CustomerDetailOption> optionList = [];

  @override
  void onInit() {
    super.onInit();
    initPlatformState();
    context = Get.key.currentContext;

    if(PermissionService().hasAclPermission([AclSalesCRMs.LEAD_AUDIT_TRAIL]) == true ) {
      optionList.add(
          CustomerDetailOption(
              id: 1, title: Strings.audit_trial, icon: audit_trial));
    }

    if(PermissionService().hasAclPermission([AclSalesCRMs.LEAD_LEAD_STATUS]) == true ) {
      optionList.add(CustomerDetailOption(
          id: 2, title: Strings.lead_status, icon: status_icon));
    }

    if(PermissionService().hasAclPermission([AclSalesCRMs.LEAD_LEAD_NOTES]) == true ) {
      optionList.add(CustomerDetailOption(
          id: 3, title: Strings.lead_notes, icon: lead_notes));
    }

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
      if (arguments[Constant.LEAD_MASTER_ID] != null) {
        eventId = arguments[Constant.LEAD_MASTER_ID];
        getLeadDetailData(eventId);
      }
      if (arguments[Constant.LEAD_DASHBOARD_FLAG] != null) {
        isDashboardFlag = arguments[Constant.LEAD_DASHBOARD_FLAG];
      }
    }
  }

  getLeadDetailData(int eventId) {
    isLoading = true;
    update();
    LeadSystemProvider().getLeadDetailsById(
      eventId: eventId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              LeadMasterDetailsRes responseData =
                  LeadMasterDetailsRes.fromJson(map);
              if (responseData.responseCode == 200 ||
                  responseData.status == 200) {
                leadMaster = responseData.leadMaster;

                if (leadMaster!.serviceareaid != null) {
                  getLeadServiceArea(leadMaster!.serviceareaid);
                }

                if (leadMaster!.planMappingList!.isNotEmpty) {
                  customerBill = leadMaster!.planMappingList![0].billTo;
                  custInvoiceToOrg = leadMaster!.planMappingList![0].isInvoiceToOrg;
                } else {
                  customerBill = "-";
                }
                 //Area - PinCode
                if(leadMaster!.addressList!.isNotEmpty){
                  if(leadMaster!.addressList![0].areaId != null) {
                    getAreaDetail(leadMaster!.addressList![0].areaId);
                  }
                }

                if(leadMaster!.nextApproveStaffId == userDetail!.userId && leadMaster?.leadStatus != "Converted" && leadMaster?.leadStatus != "Rejected"){
                  optionList.add(CustomerDetailOption(
                      id: 4, title: Strings.followup, icon:audit_trial));
                }

              } else {
                // if (responseData.responseMessage!.isNotEmpty) {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
                // }
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
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  getLeadServiceArea(int? serviceAreaId) {
    isLoading = true;
    update();
    LeadSystemProvider().getLeadServiceArea(
      serviceAreaId: serviceAreaId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              LeadServiceAreaRes responseData =
                  LeadServiceAreaRes.fromJson(map);
              if ((responseData.responseCode == 0 ||responseData.responseCode == 200)) {
                leadServiceAeraData = responseData.data;
                if (leadServiceAeraData != null) {
                  serviceAreaDATA = leadServiceAeraData!.name;
                } else {
                  serviceAreaDATA = "-";
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
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  getAreaDetail(int? areaId) {
    isLoading = true;
    update();
    CustomerProvider().getAreaDetail(
      areaId: areaId!,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              AddressDetailResponse responseData =
                  AddressDetailResponse.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                pinCodeData = responseData.data;
                update();
              } else if (responseData.responseCode != null && responseData.responseCode == 404){
                log("Info==>>>${responseData.responseMessage}");
              }else {
                if (responseData.responseMessage != null &&
                    responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.INFO,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorBlueRView);
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
