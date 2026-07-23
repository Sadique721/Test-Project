import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/change_discount/change_discount_provider.dart';
import 'package:savbill/pages/change_discount/discount_reasssign_work_flow_dialog.dart';
import 'package:savbill/pages/change_discount/discount_staff_list_dialog.dart';
import 'package:savbill/pages/change_discount/request/discount_approve_reject_req.dart';
import 'package:savbill/pages/change_discount/request/discount_update_req.dart';
import 'package:savbill/pages/change_discount/response/change_discount_list.dart';
import 'package:savbill/pages/change_discount/response/cust_approve_change_discount_res.dart';
import 'package:savbill/pages/credit_note/credit_note_provider.dart';
import 'package:savbill/pages/credit_note/response/reassign_workflow_get_staff_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:intl/intl.dart';

class CustomerCafDiscountController extends GetxController
    implements DiscountAssignAction, DiscountReAssignWorkFlowAction {
  bool isLoading = false;
  UserDetail? userDetail;
  List<DiscountDetails>? discountList = [];
  int customerId = 0;
  String customerName = "";
  GetStorage getStorage = GetStorage();
  List<DiscountDataList>? discountStaffList = [];
  List<DropdownDetail>? newDiscountList = [];
  DropdownDetail? selectedNewDiscountType;
  TextEditingController currentDiscountExpiryDate = TextEditingController();
  TextEditingController newDiscountExpiryDate = TextEditingController();
  TextEditingController remarksController = TextEditingController();
  DateFormat dateFormat = DateFormat(Constant.DATE_FORMAT);
  DateFormat apiDateTimeFormat = DateFormat(Constant.API_DATE_FORMAT);
  DateTime? selectedNewDiscountDateTime;
  DateTime? selectedCurrentDiscountDateTime;
  List<ReassignWorkflowList>? reassignWorkFlowList = [];
  List<DiscountUpdateData>? discountUpdateDataList = [];
  DateFormat apiDateStandardFormat =
      DateFormat(Constant.DATE_TIME_FORMAT_API_US);
  String currentDateTime = "", newCurrentDateTime = "";
  DiscountUpdateData? discountUpdateData;
  bool? isPickButton = false,
      isAuditDiscountButton = false,
      isApproveButton = false,
      isRejectedButton = false,
      isReassignShiftLocation = false;
  int? entityId;
  String discountDateTimeCurrent = "";

  RxString? currentDiscountDate = "".obs;

  RxBool isDisable = false.obs;

  @override
  void onInit() {
    super.onInit();

    newDiscountList!.add(DropdownDetail(
        id: Strings.onetime.toUpperCase(),
        text: Strings.onetime,
        type: Strings.discount_type));
    newDiscountList!.add(DropdownDetail(
        id: Strings.recurring.toUpperCase(),
        text: Strings.recurring,
        type: Strings.discount_type));
    DateTime now = DateTime.now();
    discountDateTimeCurrent = apiDateTimeFormat.format(now);
    // newDiscountExpiryDate.text = dateFormat.format(now);
    selectedNewDiscountType = newDiscountList![0];
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
        //customerId = 304;
        getCustomerDiscountDetail();
      }
    }
    update();
  }

  getCustomerDiscountDetail() {
    isLoading = true;
    discountList!.clear();
    update();
    ChangeDiscountProvider().getCustomerCafDiscountList(
      id: customerId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ChangeDiscountList responseData =
                  ChangeDiscountList.fromJson(map);
              if (responseData.discountDetails != null &&
                  responseData.discountDetails!.isNotEmpty) {
                for (var element in responseData.discountDetails!) {
                  if (element.newDiscount != null) {
                    element.newDiscount = element.newDiscount.toString();
                  }
                }
                discountList!.addAll(responseData.discountDetails!);

                if (discountList![0].discountType != null &&
                    discountList![0].discountType.isNotEmpty) {
                  for (DropdownDetail element in newDiscountList!) {
                    if (element.id!
                        .equalsIgnoreCase(discountList![0].discountType)) {
                      selectedNewDiscountType = element;
                      break;
                    }
                  }
                }
                if (discountList != null && discountList!.isNotEmpty) {
                  if(discountList![0].discountExpiryDate != null &&
                  discountList![0].discountExpiryDate!.isNotEmpty) {
                    DateTime currentDate = DateFormat(Constant.API_DATE_FORMAT)
                        .parse(discountList![0].discountExpiryDate!);
                    selectedCurrentDiscountDateTime = currentDate;
                    currentDiscountExpiryDate.text =
                        dateFormat.format(currentDate);
                    currentDateTime = apiDateTimeFormat.format(currentDate);

                    DateTime newCurrentDate = DateFormat(
                        Constant.API_DATE_FORMAT)
                        .parse(discountList![0].newDiscountExpiryDate!);
                    selectedNewDiscountDateTime = newCurrentDate;
                    newDiscountExpiryDate.text =
                        dateFormat.format(newCurrentDate);
                    newCurrentDateTime =
                        apiDateTimeFormat.format(newCurrentDate);
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
        _handleApiError(error);
      },
    );
  }

  updateCustomerDiscount() {
    bool valid = true;
    for (var element in discountList!) {
      if (double.parse(element.newDiscount!) > 100 ||
          double.parse(element.newDiscount!) < 0) {
        valid = false;
        break;
      }
    }
    if (valid == false) {
      Utils.showSnackbar(Strings.ERROR, "Please enter valid discount value",
          AppTheme.colorWhite, AppTheme.colorRed);
      return;
    }
    isLoading = true;
    update();

    discountUpdateDataList!.add(discountUpdateData!);
    ChangeDiscountProvider().updateCustomerDiscountList(
      id: customerId,
      request: discountUpdateDataList,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ChangeDiscountList responseData =
                  ChangeDiscountList.fromJson(map);
              discountList!.clear();
              if (responseData.discountDetails != null &&
                  responseData.discountDetails!.isNotEmpty) {
                discountList!.addAll(responseData.discountDetails!);
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
        getCustomerDiscountDetail();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  pickUpCustomerDiscount(int? entityId) {
    String apiUrl =
        "${UrlConstants.creditNote_pick_up_flow}?entityId=$entityId&eventName=CAF";
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
                // getCreditNoteListData();
                getCustomerDiscountDetail();
              } else {
                if (responseData.responseMessage != null &&
                    responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorRed);
                }
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

  approveRejectDiscountChange(
    DiscountApproveRejectReq request,
    String? status,
    BuildContext? context,
  ) {
    isLoading = true;
    update();
    ChangeDiscountProvider().approveRejectChangeDiscountService(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustApproveChangeDiscountRes responseData =
                  CustApproveChangeDiscountRes.fromJson(map);
              if ((responseData.responseCode != null &&
                  responseData.responseCode == 200)) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  discountStaffList?.clear();
                  discountStaffList?.addAll(responseData.dataList!);
                  log("discountStaffList==>>>");
                  showAssignStaffDialog(responseData.dataList!, status);
                } else {
                  if (status!.equalsIgnoreCase(Strings.reject.toLowerCase())) {
                    Utils.showSnackbar(
                      Strings.SUCCESS,
                      "Reject Successfully.",
                      AppTheme.colorWhite,
                      AppTheme.colorGreen,
                    );
                  } else {
                    Utils.showSnackbar(
                      Strings.SUCCESS,
                      "Approved Successfully.",
                      AppTheme.colorWhite,
                      AppTheme.colorGreen,
                    );
                  }
                  // Get.back(result: true);
                  getCustomerDiscountDetail();
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

  assignEveryStaffCreditNote({int? entityId, bool? isApprovedRequest}) {
    String apiUrl =
        "${UrlConstants.creditNote_assign_every_staff}?entityId=$entityId&eventName=CUSTOMER_DISCOUNT&isApproveRequest=$isApprovedRequest";
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
            getCustomerDiscountDetail();
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
              }

              getCustomerDiscountDetail();
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
          return DiscountReAssignWorkFlowDialog(
              discountReAssignWorkFlowAction: this, itemsOrgLst: item);
        });
  }

  assignStaffCreditNote(
      int? entityId, int? nextAssignStaff, bool? isApproveRequest) {
    String apiUrl =
        "${UrlConstants.assignFromStaffCreditNoteList}?entityId=$entityId&eventName=CUSTOMER_DISCOUNT&nextAssignStaff=$nextAssignStaff&isApproveRequest=$isApproveRequest";
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
            getCustomerDiscountDetail();
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

  showAssignStaffDialog(List<DiscountDataList> item, String? staffStatus) {
    showDialog(
        context: Get.overlayContext!,
        barrierDismissible: true,
        builder: (_) {
          return DiscountAssignDialog(
            discountAssignAction: this,
            itemsOrgLst: item,
            staffStatus: staffStatus,
          );
        });
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
            getCustomerDiscountDetail();
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

  @override
  void discountAssignBtnAction(
      {DiscountDataList? selectedItem,
      bool? isStaffSelected,
      String? approveRejectStatus}) {
    Get.back();
    if (isStaffSelected == true) {
      if (approveRejectStatus!.equalsIgnoreCase(Strings.approve)) {
        assignStaffCreditNote(entityId, selectedItem!.id, true);
      } else if (approveRejectStatus.equalsIgnoreCase(Strings.reject)) {
        assignStaffCreditNote(entityId, selectedItem!.id, false);
      }
    } else {
      if (approveRejectStatus!.equalsIgnoreCase(Strings.approve)) {
        assignEveryStaffCreditNote(entityId: entityId, isApprovedRequest: true);
      } else if (approveRejectStatus.equalsIgnoreCase(Strings.reject)) {
        assignEveryStaffCreditNote(
            entityId: entityId, isApprovedRequest: false);
      }
    }
  }

  @override
  void discountReAssignWorkFlowBtnAction(
      {ReassignWorkflowList? selectedItem,
      TextEditingController? remarkController}) {
    Get.back();
    if (selectedItem != null && entityId != null) {
      // assignEveryStaffCreditNote(entityId);
      reassignWorkflowAssignCall(entityId, "CUSTOMER_DISCOUNT", selectedItem.id,
          remarkController!.text);
    }
  }
}
