import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/credit_note/credit_note_provider.dart';
import 'package:savbill/pages/credit_note/reassign_workflow_get_staff_dialog.dart';
import 'package:savbill/pages/credit_note/response/reassign_workflow_get_staff_res.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/shift_location/request/approve_customer_address_req.dart';
import 'package:savbill/pages/shift_location/response/new_address_shift_location_res.dart';
import 'package:savbill/pages/shift_location/response/shift_location_approve_reject_res.dart';
import 'package:savbill/pages/shift_location/shift_location_assign_dialog.dart';
import 'package:savbill/pages/shift_location/shift_location_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

import '../customer/customer_detail_controller.dart';
import '../customer_caf/customer_caf_detail/customer_caf_detail_controller.dart';

class ShiftLocationController extends GetxController implements CreditReAssignWorkFlowAction,ShiftLocationAssignAction {
  bool isLoading = false;
  CustomerDetail? customerDetail;
  NewcustomerAddress? newCustomerAddressData;
  List<NewcustomerAddress>? newCustomerAddressDatList = [];
  List<ShiftLocationApproveDataList>? shiftLocationApproveRejList = [];
  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;
  String newFormatDate = "",
      pickBtnDisableFlag = "";
  List<ReassignWorkflowList>? reassignWorkFlowList = [];
  String? customerType;
  String? custType;
  int? entityId;
  bool disableShiftButton = false;
  @override
  void onInit() {
    super.onInit();
    initPlatformState();
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
      if (arguments[Constant.CUSTOMER_DETAIL] != null) {
        customerDetail = arguments[Constant.CUSTOMER_DETAIL];
        getNewAddressShiftLocation();
      }
      if(arguments[Constant.CUSTOMER_TYPE] != null){
        customerType = arguments[Constant.CUSTOMER_TYPE];
      }
      if(arguments[Constant.CUST_TYPE] != null){
        custType = arguments[Constant.CUST_TYPE];
      }
    }
    update();
  }

  getNewAddressShiftLocation() {
    isLoading = true;
    update();
    ShiftLocationProvider().getShiftLocationNewAddressData(
      customerId: customerDetail!.id!,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              NewAddressShiftLocationRes response =
              NewAddressShiftLocationRes.fromJson(map);
              if (response.status == 200) {
                if (response.newcustomerAddress != null ||
                    !response.newcustomerAddress!.isNullOrEmpty()) {
                  // newCustomerAddressData = response;
                  newCustomerAddressDatList = response.newcustomerAddress;

                  if (newCustomerAddressDatList!.isNotEmpty) {
                    disableShiftButton = newCustomerAddressDatList!.any(
                            (item) => item.version == "IN_TRANSIT"
                    );
                  }
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
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        log("ResponseModel==>${error.statusCode}");
        isLoading = false;
        _handleApiCustomError(error);
      },
    );
  }

  getApproveCustomerShiftLocationAddress(String? status,ApproveCustomerAddressReq? request,BuildContext context) {
    isLoading = true;
    update();
    ShiftLocationProvider().approveCustomerAddressShiftLocation(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          log("HeaderStatusCode ${responseModel.statusCode}");
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ShiftLocationApproveRejectRes responseData = ShiftLocationApproveRejectRes.fromJson(map);
              if ((responseData.result!.responseCode != null && responseData.result!.responseCode == 0)) {
                if (responseData.result!.dataList != null &&
                    responseData.result!.dataList!.isNotEmpty) {
                  shiftLocationApproveRejList?.clear();
                  shiftLocationApproveRejList?.addAll(responseData.result!.dataList!);
                  showAssignStaffDialog(responseData.result!.dataList!,status,context);
                } else {
                  if (status!.equalsIgnoreCase(Strings.reject)) {
                    Utils.showSnackbar(Strings.SUCCESS, Strings.rejectedSuccessfully,
                        AppTheme.colorWhite, AppTheme.colorGreen);
                    getNewAddressShiftLocation();
                  } else {
                    Utils.showSnackbar(
                        Strings.SUCCESS,
                        Strings.approvedSuccessfully,
                        AppTheme.colorWhite,
                        AppTheme.colorGreen);
                    getNewAddressShiftLocation();
                  }
                  // Get.back(result: true);
                  // getCreditNoteListData();
                }
                if (status!.equalsIgnoreCase(Strings.approve)) {
                  if (Get.isRegistered<CustomerCafDetailController>()) {
                    final customerCafDetailController = Get.find<CustomerCafDetailController>();
                    customerCafDetailController.getCustomerDetail();
                  }
                  if (Get.isRegistered<CustomerDetailController>()) {
                    final customerDetailController = Get.find<CustomerDetailController>();
                    customerDetailController.getCustomerDetail();
                  }
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
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        log("errorMessage=>>$error");
        _handleApiError(error);
      },
    );
  }


  showAssignStaffDialog(List<ShiftLocationApproveDataList> item,String? staffStatus,BuildContext context) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return ShiftLocationAssignDialog(
              shiftLocationAssignAction: this,
              itemsOrgLst: item,
              staffStatus:  staffStatus);
        });
  }


  reassignWorkflowGetStaff(int? entityId, String? eventName) {
    String apiUrl =
        "${UrlConstants.creditNote_reassign_workflow_get_staff_list}?entityId=$entityId&eventName=$eventName";
    isLoading = true;
    update();
    ViewCreditNoteProvider().creditNoteReassignWorkflowGetStaffList(
      url: apiUrl,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ReassignWorkflowGetStaffRes responseData =
              ReassignWorkflowGetStaffRes.fromJson(map);
              if ((responseData.responseCode != null &&
                  responseData.responseCode == 200)) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  reassignWorkFlowList?.clear();
                  reassignWorkFlowList?.addAll(responseData.dataList!);
                  showReAssignWorkFlowGetStaffDialog(responseData.dataList!);
                } else {
                  Utils.showSnackbar(
                      Strings.INFO,
                      responseData.responseMessage ?? "",
                      AppTheme.colorWhite,
                      AppTheme.colorBlueRView);
                }
              }else if(responseData.responseCode == 417){
                Utils.showSnackbar(
                    Strings.INFO,
                    responseData.responseMessage ?? "",
                    AppTheme.colorWhite,
                    AppTheme.colorBlueRView);
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

  reassignWorkflowAssignCall(
      int? entityId, String? eventName, int? assignToStaffId, String? remark) {
    String apiUrl =
        "${UrlConstants.creditNote_reassign_workflow_get_staff_list}?entityId=$entityId&eventName=$eventName&assignToStaffId=$assignToStaffId&remark=$remark";
    isLoading = true;
    update();
    ViewCreditNoteProvider().reassignWorkflowApprove(
      url: apiUrl,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          try {
            Utils.showSnackbar(Strings.SUCCESS, Strings.successfully,
                AppTheme.colorWhite, AppTheme.colorGreen);
            getNewAddressShiftLocation();
          } on Exception catch (e) {
            print(e.toString());
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


  assignStaffShiftLocation(
      {int? entityId,
      int? nextAssignStaff,
      bool? isApproveRequest,
      String? eventName}) {
    String apiUrl =
        "${UrlConstants.assignFromStaffCreditNoteList}?entityId=$entityId&eventName=$eventName&nextAssignStaff=$nextAssignStaff&isApproveRequest=$isApproveRequest";
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
            getNewAddressShiftLocation();
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

  assignEveryStaffShiftLocation({int? entityId,bool? isApprovedRequest,String? eventName}) {
    String apiUrl =
        "${UrlConstants.creditNote_assign_every_staff}?entityId=$entityId&eventName=$eventName&isApproveRequest=$isApprovedRequest";
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
            getNewAddressShiftLocation();
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

  showReAssignWorkFlowGetStaffDialog(List<ReassignWorkflowList> item) {
    showDialog(
        context: Get.context!,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return CreditReAssignWorkFlowDialog(
              creditReassignWorkflowAction: this, itemsOrgLst: item);
        });
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

  _handleApiCustomError(ResponseModel error) {

    isLoading = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    }else if (error.statusCode == 417 ) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }

  @override
  void creditReAssignWorkFlowBtnAction({ReassignWorkflowList? selectedItem, TextEditingController? remarkController}) {
    Get.back();
    if (selectedItem != null && entityId != null) {
      // assignEveryStaffCreditNote(entityId);
      reassignWorkflowAssignCall(
          entityId, "SHIFT_LOCATION", selectedItem.id, remarkController!.text);
    }
  }

  @override
  void shiftLocationAssignBtnAction({ShiftLocationApproveDataList? selectedItem, bool? isStaffSelected, String? approveRejectStatus}) {
    Get.back();
    if(isStaffSelected == true){
      // log("Staff is selected");
      if(approveRejectStatus!.equalsIgnoreCase(Strings.approve)){
        // log("Staff is selected!!!!!!!!=>${Strings.approve}");
        assignStaffShiftLocation(entityId: newCustomerAddressData!.id,nextAssignStaff: selectedItem!.id,isApproveRequest: true,eventName: "SHIFT_LOCATION");
      }else if(approveRejectStatus.equalsIgnoreCase(Strings.reject)) {
        // log("Staff is selected!!!!!!!!=>${Strings.reject}");
        assignStaffShiftLocation(entityId: newCustomerAddressData!.id,nextAssignStaff: selectedItem!.id,isApproveRequest: false,eventName: "SHIFT_LOCATION");
      }
    }else{
      // log("Not Staff is selected");
      if(approveRejectStatus!.equalsIgnoreCase(Strings.approve)) {
        // log("Not Staff is selected!!!!!!!!=>${Strings.approve}");
        assignEveryStaffShiftLocation(entityId: newCustomerAddressData!.id, isApprovedRequest: true,eventName: "SHIFT_LOCATION");
      }else if(approveRejectStatus.equalsIgnoreCase(Strings.reject)){
        // log("Not Staff is selected!!!!!!!!=>${Strings.reject}");
        assignEveryStaffShiftLocation(entityId: newCustomerAddressData!.id, isApprovedRequest: false,eventName: "SHIFT_LOCATION");
      }
    }
  }
}
