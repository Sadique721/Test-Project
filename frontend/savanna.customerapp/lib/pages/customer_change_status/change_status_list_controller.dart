import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/credit_note/credit_note_provider.dart';
import 'package:savbill/pages/credit_note/reassign_workflow_get_staff_dialog.dart';
import 'package:savbill/pages/credit_note/response/reassign_workflow_get_staff_res.dart';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/response/cust_detail_response.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer_change_status/change_status_provider.dart';
import 'package:savbill/pages/customer_change_status/cust_change_staus_assign_dialog.dart';
import 'package:savbill/pages/customer_change_status/request/cust_terminate_approve_reject_req.dart';
import 'package:savbill/pages/customer_change_status/response/customer_change_status_res.dart';
import 'package:savbill/pages/customer_change_status/response/customer_terminate_approve_reject_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_staff_detail_res.dart';
import 'package:savbill/pages/ticket_system/ticket_system_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

import '../../webservices/base_response.dart';
import '../../webservices/url_constants.dart';

class ChangeStatusListController extends GetxController
    implements ChangeStatusAssignAction, CreditReAssignWorkFlowAction {
  bool isLoading = false;
  List<CustomerTerminateApproveRejectDataList>? customerChangeStatusDataList =
      [];
  List<ChangeStatusDetail>? changeStatusDetail = [];
  int customerId = 0;
  String customerName = "";
  List<ReassignWorkflowList>? reassignWorkFlowList = [];
  int? entityId;
  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;
  String? assignStaffByName;

  CustomerDetail? customerDetail;

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
      if (arguments[Constant.CUSTOMER_NAME] != null) {
        customerName = arguments[Constant.CUSTOMER_NAME];
      }
      if (arguments[Constant.CUSTOMER_ID] != null) {
        customerId = arguments[Constant.CUSTOMER_ID];
        // customerId = 301;
        getCustomerDetail();
      }
    }
    update();
  }

  getCustomerDetail() {
    isLoading = true;
    update();
    CustomerProvider().getCustomerDetail(
      customerId: customerId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustDetailResponse responseData =
              CustDetailResponse.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                customerDetail = responseData.customers;
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
        getCustomerChangeStatusDetail();
      },
      onError: (ResponseModel error) {
        getCustomerChangeStatusDetail();
        _handleApiCustomerDetailsError(error);
      },
    );
  }

  getCustomerChangeStatusDetail() {
    isLoading = true;
    changeStatusDetail!.clear();
    update();
    ChangeStatusProvider().getChangeStatusList(
      custId: customerId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerChangeStatusRes responseData =
              CustomerChangeStatusRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.customer != null &&
                    responseData.customer!.isNotEmpty) {
                  changeStatusDetail!.addAll(responseData.customer!);
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
        getByCustomerId();
      },
      onError: (ResponseModel error) {
        getByCustomerId();
        _handleApiError(error);
      },
    );
  }

  getByCustomerId() {
    isLoading = true;
    update();
    TicketSystemProvider().getTicketStaffDetail(
      staffId: userDetail?.userId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TicketStaffDetailRes responseData =
                  TicketStaffDetailRes.fromJson(map);
              if (responseData.status != null && responseData.status == 200) {
                if (responseData.staff != null) {
                  assignStaffByName = responseData.staff!.username;
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
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }

  // pickModelOpen(ChangeStatusDetail? item) {
  //   isLoading = true;
  //   changeStatusDetail!.clear();
  //   update();
  //   ChangeStatusProvider().getChangeStatusList(
  //     custId: customerId,
  //     onSuccess: (ResponseModel responseModel) {
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           try {
  //             Map<String, dynamic> map = responseModel.result;
  //             CustomerChangeStatusRes responseData =
  //                 CustomerChangeStatusRes.fromJson(map);
  //             if (responseData.status == 200) {
  //               if (responseData.customer != null &&
  //                   responseData.customer!.isNotEmpty) {
  //                 changeStatusDetail!.addAll(responseData.customer!);
  //               }
  //             }
  //           } on Exception catch (e) {
  //             print(e.toString());
  //           }
  //         }
  //       } else {
  //         if (responseModel.message!.isNotEmpty) {
  //           Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
  //               AppTheme.colorWhite, AppTheme.colorRed);
  //         }
  //       }
  //       isLoading = false;
  //       update();
  //     },
  //     onError: (ResponseModel error) {
  //       _handleApiError(error);
  //     },
  //   );
  // }

  pickModelOpen(int? entityId) {
    String apiUrl =
        "${UrlConstants.creditNote_pick_up_flow}?entityId=$entityId&eventName=TERMINATION";
    isLoading = true;
    update();
    ViewCreditNoteProvider().assignCreditNoteEveryStaff(
      url: apiUrl,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                Utils.showSnackbar(
                    Strings.SUCCESS,
                    responseData.responseMessage,
                    AppTheme.colorWhite,
                    AppTheme.colorGreen);
                getCustomerChangeStatusDetail();
              } if (responseData.responseCode == 417) {
                Utils.showSnackbar(
                    Strings.INFO,
                    responseData.responseMessage,
                    AppTheme.colorWhite,
                    AppTheme.colorBlueRView);
              }else {
                // if (responseData.responseMessage != null &&
                //     responseData.responseMessage!.isNotEmpty) {
                //   Utils.showSnackbar(
                //       Strings.ERROR,
                //       responseData.responseMessage,
                //       AppTheme.colorWhite,
                //       AppTheme.colorRed);
                // }
                if (responseData.ERROR != null &&
                    responseData.ERROR!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.ERROR,
                      AppTheme.colorWhite, AppTheme.colorRed);
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

  _handleApiErrorCustom(ResponseModel error) {
    isLoading = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (error.statusCode == Constant.EXPECTION_FAIL_STATUS) {
      Utils.showSnackbar(
          Strings.INFO, error.message,
          AppTheme.colorWhite,
          AppTheme.colorBlueRView);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }

  handleApiError(ResponseModel error) {
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

  getApproveCustomerChangeStatusApproveReject(String? status, String? remarks,
      CustomerTerminateApproveRejectReq? request, BuildContext? context) {
    isLoading = true;
    update();
    ChangeStatusProvider().customerTerminationApproveRejectStatus(
      customerId: customerId,
      status: status,
      remarks: remarks,
      request: request,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerTerminateApproveRejectRes responseData =
                  CustomerTerminateApproveRejectRes.fromJson(map);
              if ((responseData.result!.responseCode != null &&
                  responseData.result!.responseCode == 0)) {
                if (responseData.result!.dataList != null &&
                    responseData.result!.dataList!.isNotEmpty) {
                  customerChangeStatusDataList?.clear();
                  customerChangeStatusDataList
                      ?.addAll(responseData.result!.dataList!);
                  showAssignStaffDialog(
                    customerChangeStatusDataList!,
                    status,
                  );
                } else {
                  if (status!.equalsIgnoreCase(Strings.reject)) {
                    Utils.showSnackbar(Strings.SUCCESS, "Reject Successfully.",
                        AppTheme.colorWhite, AppTheme.colorGreen);
                  } else {
                    Utils.showSnackbar(
                        Strings.SUCCESS,
                        "Approved Successfully.",
                        AppTheme.colorWhite,
                        AppTheme.colorGreen);
                  }
                  // Get.back(result: true);
                  // getCreditNoteListData();
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
        _handleApiErrorCustom(error);
      },
    );
  }

  reassignWorkflowGetStaff(int? entityId, String? eventName) {
    String apiUrl =
        "${UrlConstants.creditNote_reassign_workflow_get_staff_list}?entityId=$entityId&eventName=$eventName&remark=";
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
                  responseData.responseCode == 0)) {
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
              } else if ((responseData.responseCode != null &&
                  responseData.responseCode == 417)) {
                Utils.showSnackbar(
                    Strings.INFO,
                    responseData.responseMessage ?? "",
                    AppTheme.colorWhite,
                    AppTheme.colorBlueRView);
              }
              getCustomerChangeStatusDetail();
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

  showReAssignWorkFlowGetStaffDialog(List<ReassignWorkflowList> item) {
    showDialog(
        context: Get.context!,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return CreditReAssignWorkFlowDialog(
              creditReassignWorkflowAction: this, itemsOrgLst: item);
        });
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
            // getNewAddressShiftLocation();
            getCustomerChangeStatusDetail();
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

  assignEveryStaffShiftLocation(
      {int? entityId, bool? isApprovedRequest, String? eventName}) {
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
            // getNewAddressShiftLocation();
            getCustomerChangeStatusDetail();
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

  showAssignStaffDialog(
      List<CustomerTerminateApproveRejectDataList> item, String? staffStatus) {
    showDialog(
        context: Get.context!,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return ChangeStatusAssignDialog(
              changeStatusAssignAction: this,
              itemsOrgLst: item,
              staffStatus: staffStatus);
        });
  }

  @override
  void changeStatusAssignBtnAction(
      {CustomerTerminateApproveRejectDataList? selectedItem,
      bool? isStaffSelected,
      String? approveRejectStatus}) {
    Get.back();

    log("approveRejectStatus==>${approveRejectStatus}");

    if (isStaffSelected == true) {
      log("Staff is selected");
      if (approveRejectStatus!.equalsIgnoreCase(Strings.approve)) {
        log("Staff is selected!!!!!!!!=>${Strings.approve}");
        assignStaffShiftLocation(
            entityId: customerId,
            nextAssignStaff: selectedItem!.id,
            isApproveRequest: true,
            eventName: "TERMINATION");
      } else if (approveRejectStatus.equalsIgnoreCase(Strings.reject)) {
        log("Staff is selected!!!!!!!!=>${Strings.reject}");
        assignStaffShiftLocation(
            entityId: customerId,
            nextAssignStaff: selectedItem!.id,
            isApproveRequest: false,
            eventName: "TERMINATION");
      }
    } else {
      if (approveRejectStatus!.equalsIgnoreCase(Strings.approved)) {
        // log("Not Staff is selected!!!!!!!!=>${Strings.rejected}");
        assignEveryStaffShiftLocation(
            entityId: customerId,
            isApprovedRequest: true,
            eventName: "TERMINATION");
      } else if (approveRejectStatus.equalsIgnoreCase(Strings.rejected)) {
        // log("Not Staff is selected!!!!!!!!=>${Strings.rejected}");
        assignEveryStaffShiftLocation(
            entityId: customerId,
            isApprovedRequest: false,
            eventName: "TERMINATION");
      }
    }
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
            getCustomerChangeStatusDetail();
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

  @override
  void creditReAssignWorkFlowBtnAction(
      {ReassignWorkflowList? selectedItem,
      TextEditingController? remarkController}) {
    Get.back();
    if (selectedItem != null && entityId != null) {
      // assignEveryStaffCreditNote(entityId);
      reassignWorkflowAssignCall(
          entityId, "TERMINATION", selectedItem.id, remarkController!.text);
    }
  }


  _handleApiCustomerDetailsError(ResponseModel error) {
    isLoading = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (error.statusCode == Constant.CODE_NO_TRY_CATCH) {
      Utils.showSnackbar(Strings.INFO, Strings.data_not_available,
          AppTheme.colorWhite, AppTheme.colorBlueRView);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }
}
