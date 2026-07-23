import 'dart:convert';
import 'dart:developer';
import 'dart:io';
import 'package:savbill/pages/credit_note/credit_assign_dialog.dart';
import 'package:savbill/pages/credit_note/credit_note_provider.dart';
import 'package:savbill/pages/credit_note/reassign_workflow_get_staff_dialog.dart';
import 'package:savbill/pages/credit_note/response/credit_note_res.dart';
import 'package:savbill/pages/credit_note/response/reassign_workflow_get_staff_res.dart';
import 'package:savbill/pages/dashboard/model/response/payment_configuration_res.dart';
import 'package:savbill/pages/dashboard/payment_provider.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/pending_approvals/model/request/payment_approve_reject_req.dart';
import 'package:savbill/pages/pending_approvals/model/response/payment_change_status_res.dart';
import 'package:savbill/pages/pending_approvals/model/response/ticket_assign_staff_res.dart';
import 'package:savbill/pages/pending_approvals/pending_approvals_provider.dart';
import 'package:savbill/routes/app_routes.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:intl/intl.dart';
import '../../webservices/url_constants.dart';
import '../dashboard/model/response/payment_status_data.dart';
import 'create_credit_controller.dart';
import 'package:http/http.dart' as http;
import 'package:path_provider/path_provider.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:path/path.dart';

class CreditNoteController extends GetxController
    implements TicketAssignAction, CreditReAssignWorkFlowAction {
  bool isLoading = false,
      isShowLoadMore = false,
      isLoadingProgress = false,
      isFilterApply = false,
      filterViewOpen = false,
      checkBtnClickEvent = false,
      isFirstTime = true,
      isLoadFilterData = false;
  DateTime? selectedPayFromDate, selectedPayToDate;
  DateFormat dateFormat = DateFormat(Constant.DATE_FORMAT);
  String? selectedPayFromDateApi = "", selectedPayToDateApi = "";
  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);
  String? currencySymbol;

  TextEditingController creditFormDateController = TextEditingController();
  TextEditingController creditToDateController = TextEditingController();
  TextEditingController referenceController = TextEditingController();
  TextEditingController invoiceNumberController = TextEditingController();
  TextEditingController mobileNumberController = TextEditingController();
  TextEditingController documentNumberController = TextEditingController();
  PaymentStatus? selectedPaymentStatus;
  final createCreditController = Get.put(CreateCreditController());
  int tabIndex = 0;
  ScrollController? controller;
  int page = 1;
  GetStorage getStorage = GetStorage();

  List<CreditNoteDetailsList>? creditNoteList = [];
  CreditNoteDetailsList? creditNoteListResponse;
  CreditNoteResponse? creditNoteResponse;
  List<PaymentStatus>? creditStatusList = [];
  UserDetail? userDetail;

  int? entityId;

  List<TicketAssignStaff>? ticketAssignStaffList = [];
  List<ReassignWorkflowList>? reassignWorkFlowList = [];

  @override
  void onInit() {
    getArgumentData();
    creditStatusList?.clear();
    creditStatusList?.add(PaymentStatus(status: Strings.adjusted));
    creditStatusList?.add(PaymentStatus(status: Strings.generated));
    creditStatusList?.add(PaymentStatus(status: Strings.partialy_adjusted));
    // getCreditNoteListData();
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (creditNoteResponse != null &&
            creditNoteResponse!.pageDetails!.totalPages != page) {
          // if (inventoryWorkFlowAuditRes!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          getCreditNoteListData();
          update();
        }
      }
    });
  }

  getArgumentData() async {
    var arguments = Get.arguments;
    if (arguments != null) {}
    String strUserData = "";
    if (getStorage.hasData(Constant.USER_DATA)) {
      strUserData = await getStorage.read(Constant.USER_DATA);
    }
    if (!strUserData.isNullOrEmpty()) {
      userDetail = UserDetail.fromJson(jsonDecode(strUserData));
      update();
    }

    // getCreditNoteListData();
    getSystemConfigurationData(Strings.currency_payment);
    update();
  }

  setBtnClickEvent(bool status) {
    checkBtnClickEvent = status;
    update();
  }

  clearFilter() {
    selectedPaymentStatus = null;
    selectedPayFromDate = null;
    selectedPayToDate = null;
    selectedPayFromDateApi = "";
    selectedPayToDateApi = "";
    creditFormDateController.clear();
    creditToDateController.clear();
    referenceController.clear();
    invoiceNumberController.clear();
    mobileNumberController.clear();
    documentNumberController.clear();
    isFilterApply = false;
    filterViewOpen = false;
    update();
    isLoading = true;
    getCreditNoteListData();
  }

  applyFilter() {
    isFilterApply = true;
    filterViewOpen = false;
    update();
    getCreditNoteListData();
  }

  void moveToDashboard() async {
    Get.offAllNamed(AppRoutes.DASHBOARD);
  }

  getCreditNoteListData() {
    String apiUrl = UrlConstants.get_creditNote_list;
    if (isFilterApply) {
      int? cusId = -1;
      String? status = "";
      // apiUrl = "$apiUrl";
      if (createCreditController.selectedCustomer != null) {
        cusId = createCreditController.selectedCustomer?.id;
        apiUrl = "${apiUrl}&customerid=$cusId";
      }

      if (selectedPayFromDateApi!.isNotEmpty) {
        apiUrl = "${apiUrl}&payfromdate=$selectedPayFromDateApi";
      }

      if (selectedPayToDateApi!.isNotEmpty) {
        apiUrl = "${apiUrl}&paytodate=$selectedPayToDateApi";
      }

      if (selectedPaymentStatus != null) {
        status = selectedPaymentStatus?.status;

        log("status=>>>>>$status");
        if (status!.equalsIgnoreCase("Adjusted")) {
          apiUrl = "${apiUrl}&paystatus=Fully Adjusted";
        } else if (status.equalsIgnoreCase("Generated")) {
          apiUrl = "${apiUrl}&paystatus=pending";
        } else if (status.equalsIgnoreCase("Partially Adjusted")) {
          apiUrl = "${apiUrl}&paystatus=Partially Adjuste";
        }
        // apiUrl = "${apiUrl}&paystatus=$status";
      }

      if (referenceController.text.isNotEmpty) {
        apiUrl = "${apiUrl}&referenceno=${referenceController.text}";
      }

      if (invoiceNumberController.text.isNotEmpty) {
        apiUrl = "${apiUrl}&invoiceNumber=${invoiceNumberController.text}";
      }

      if (mobileNumberController.text.isNotEmpty) {
        apiUrl = "${apiUrl}&mobileNumber=${mobileNumberController.text}";
      }

      if (documentNumberController.text.isNotEmpty) {
        apiUrl =
            "${apiUrl}&creditDocumentNumber=${documentNumberController.text}";
      }

      if (apiUrl == UrlConstants.get_creditNote_list) {
        isFilterApply = false;
        filterViewOpen = true;
        update();
        Utils.showSnackbar(
            Strings.ERROR,
            "Please select at-least one filter option.",
            AppTheme.colorWhite,
            AppTheme.colorRed);
        return;
      }
    }
    if (isFirstTime) {
      isLoading = true;
    } else {
      isLoadingProgress = true;
    }

    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }

    if (page == 1) {
      isLoading = true;
      creditNoteList?.clear();
    }
    update();
    ViewCreditNoteProvider().getCreditNoteList(
      url: "${apiUrl}&page=$page&pageSize=5",
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        isLoadingProgress = false;
        isFirstTime = false;
        update();
        if (responseModel.statusCode == 200) {
          if (page == 1) {
            creditNoteList?.clear();
          }
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CreditNoteResponse responseData =
                  CreditNoteResponse.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.creditDocumentPojoList != null &&
                    responseData.creditDocumentPojoList!.isNotEmpty) {
                  creditNoteResponse = responseData;
                  log("creditNoteResponse==>>>${jsonEncode(responseData)}");
                  if (page == 1) {
                    creditNoteList?.clear();
                  }
                  creditNoteList!.addAll(responseData.creditDocumentPojoList!);
                } else {
                  creditNoteList?.clear();
                }
              } else {
                if (page == 1) {
                  creditNoteList?.clear();
                }
                if (responseData.message!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.message,
                      AppTheme.colorWhite, AppTheme.colorRed);
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (page == 1) {
            creditNoteList?.clear();
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
          creditNoteList?.clear();
        }
        _handleApiError(error);
      },
    );
  }

  getSystemConfigurationData(String type) {
    isLoading = true;
    update();
    PaymentProvider().getSystemConfiguration(
      type: type,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PaymentConfigurationRes responseData =
                  PaymentConfigurationRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.data != null) {
                  if (responseData.data!.name!.isNotEmpty &&
                      type.equalsIgnoreCase(Strings.currency_payment)) {
                    currencySymbol = responseData.data!.value;
                  }
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
        getCreditNoteListData();
      },
      onError: (ResponseModel error) {
        getCreditNoteListData();
        _handleApiError(error);
      },
    );
  }

  approveRejectCreditPayment(String status, PaymentApproveRejectReq request,BuildContext context) {
    isLoading = true;
    update();
    PendingApprovalsProvider().approveRejectPayment(
      status: status.toLowerCase(),
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PaymentChangeStatusRes responseData = PaymentChangeStatusRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200)) {
                if (responseData.payment!.dataList != null &&
                    responseData.payment!.dataList!.isNotEmpty) {
                  ticketAssignStaffList?.clear();
                  ticketAssignStaffList?.addAll(responseData.payment!.dataList!);
                  showAssignStaffDialog(responseData.payment!.dataList!,status,context);
                }
                else {
                  if (status.equalsIgnoreCase(Strings.reject)) {
                    Utils.showSnackbar(Strings.SUCCESS,
                        "Reject Successfully.",
                        AppTheme.colorWhite,
                        AppTheme.colorGreen);
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
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  assignEveryStaffCreditNote({int? entityId,bool? isApprovedRequest}) {
    String apiUrl =
        "${UrlConstants.creditNote_assign_every_staff}?entityId=$entityId&eventName=CREDIT_NOTE&isApproveRequest=$isApprovedRequest";
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
            getCreditNoteListData();
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


  assignStaffCreditNote(int? entityId,int? nextAssignStaff,bool? isApproveRequest) {
    String apiUrl =
        "${UrlConstants.assignFromStaffCreditNoteList}?entityId=$entityId&eventName=CREDIT_NOTE&nextAssignStaff=$nextAssignStaff&isApproveRequest=$isApproveRequest";
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
            getCreditNoteListData();
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

  pickUpCreditNote(int? entityId) {
    String apiUrl =
        "${UrlConstants.creditNote_pick_up_flow}?entityId=$entityId&eventName=CREDIT_NOTE";
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
                getCreditNoteListData();
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

              getCreditNoteListData();
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
            getCreditNoteListData();
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

  showAssignStaffDialog(List<TicketAssignStaff> item,String? staffStatus,BuildContext context) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return CreditAssignDialog(
            ticketAssignAction: this,
            itemsOrgLst: item,
            staffStatus:  staffStatus);
        });
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

  @override
  void ticketAssignBtnAction({TicketAssignStaff? selectedItem,bool? isStaffSelected,String? approveRejectStatus}) {
    Get.back();
    if(isStaffSelected == true){
      // log("Staff is selected");
      if(approveRejectStatus!.equalsIgnoreCase(Strings.approve)){
        // log("Staff is selected!!!!!!!!=>${Strings.approve}");
            assignStaffCreditNote(entityId,selectedItem!.id,true);
      }else if(approveRejectStatus.equalsIgnoreCase(Strings.reject)) {
        // log("Staff is selected!!!!!!!!=>${Strings.reject}");
          assignStaffCreditNote(entityId,selectedItem!.id,false);
      }
    }
    else{
      // log("Not Staff is selected");
      if(approveRejectStatus!.equalsIgnoreCase(Strings.approve)) {
        // log("Not Staff is selected!!!!!!!!=>${Strings.approve}");
        assignEveryStaffCreditNote(entityId: entityId, isApprovedRequest: true);
      }else if(approveRejectStatus.equalsIgnoreCase(Strings.reject)){
        // log("Not Staff is selected!!!!!!!!=>${Strings.reject}");
        assignEveryStaffCreditNote(entityId: entityId, isApprovedRequest: false);
      }
    }
    // if (selectedItem != null && entityId != null) {
    //   if(isStaffSelected == true){
    //     assignStaffCreditNote(entityId,selectedItem.id,true);
    //   }else if(isStaffSelected == false) {
    //     assignStaffCreditNote(entityId,selectedItem.id,false);
    //   }else{
    //     assignEveryStaffCreditNote(entityId: entityId,isApprovedRequest : true);
    //   }
    // }
  }



  Future<File?> fileDownloading(String? pageTitle, String? networkPathUrl,String? customerName) async {
    isLoading = true;
    var url = "$networkPathUrl";
    final filename = basename(url);
    String token = "";
    if (getStorage.hasData(Constant.USER_TOKEN)) {
      token = await getStorage.read(Constant.USER_TOKEN);
    }
    Map<String, String> headers = {
      'Content-type': 'application/json; charset=UTF-8',
      'Accept': 'application/json',
      'Authorization': 'Bearer $token'
    };

    log("Url===>>>${url}");

    final response = await http.get(Uri.parse(url), headers: headers);
    final bytes = response.bodyBytes;
    // var dir = await getApplicationDocumentsDirectory();
    var directory;
    if (Platform.isIOS) {
      directory = await getDownloadsDirectory();
    } else {
      directory = "/storage/emulated/0/Download/";
    }

    var file = File('$directory/$customerName$filename.pdf');
    await file.writeAsBytes(bytes, flush: true);
    Utils.showSnackbar(Strings.SUCCESS, "File Downloaded Successfully Please Open Download Folder!!",
        AppTheme.colorWhite, AppTheme.colorGreen);
    isLoading = false;
    // return pFile;
  }

  @override
  void creditReAssignWorkFlowBtnAction(
      {ReassignWorkflowList? selectedItem,
      TextEditingController? remarkController}) {
    Get.back();
    if (selectedItem != null && entityId != null) {
      // assignEveryStaffCreditNote(entityId);
      reassignWorkflowAssignCall(
          entityId, "CREDIT_NOTE", selectedItem.id, remarkController!.text);
    }
  }
}
