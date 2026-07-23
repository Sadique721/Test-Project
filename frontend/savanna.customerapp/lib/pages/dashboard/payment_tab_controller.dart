import 'dart:convert';
import 'dart:developer';
import 'dart:io';
import 'package:savbill/pages/credit_note/credit_note_provider.dart';
import 'package:savbill/pages/credit_note/reassign_workflow_get_staff_dialog.dart';
import 'package:savbill/pages/credit_note/response/reassign_workflow_get_staff_res.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer/model/response/customer_list_response.dart';
import 'package:savbill/pages/dashboard/model/request/get_all_case_request.dart';
import 'package:savbill/pages/dashboard/model/response/payment_configuration_res.dart';
import 'package:savbill/pages/dashboard/model/response/payment_list_response.dart';
import 'package:savbill/pages/dashboard/model/response/payment_status_data.dart';
import 'package:savbill/pages/dashboard/payment_provider.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/pending_approvals/model/request/payment_approve_reject_req.dart';
import 'package:savbill/pages/pending_approvals/model/response/payment_change_status_res.dart';
import 'package:savbill/pages/pending_approvals/model/response/ticket_assign_staff_res.dart';
import 'package:savbill/pages/pending_approvals/pending_approvals_provider.dart';
import 'package:savbill/pages/pending_approvals/tickets/ticket_assign_dialog.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:file_utils/file_utils.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:intl/intl.dart';
import 'package:path_provider/path_provider.dart';
import 'package:path/path.dart';
import 'package:http/http.dart' as http;

class PaymentTabController extends GetxController
    implements TicketAssignAction, CreditReAssignWorkFlowAction {
  bool isLoading = false,
      isLoadingProgress = false,
      isFilterApply = false,
      filterViewOpen = false,
      checkBtnClickEvent = false,
      isLoadFilterData = false,
      isFirstTime = true,
    isClearStaffApproveId = false;

  int? entityId;
  GetStorage getStorage = GetStorage();
  List<PaymentDetail>? paymentsList = [];
  List<CustomerDetail>? customerList = [];
  List<ReassignWorkflowList>? reassignWorkFlowList = [];

  List<TicketAssignStaff>? ticketAssignStaffList = [];

  // List<CustomerList>? customerList = [];
  List<PaymentStatus>? paymentStatusList = [];

  CustomerDetail? selectedCustomer;
  PaymentStatus? selectedPaymentStatus;

  TextEditingController payFormDateController = TextEditingController();
  TextEditingController payToDateController = TextEditingController();
  TextEditingController chequeNoController = TextEditingController();
  TextEditingController invoiceNoController = TextEditingController();

  DateTime? selectedPayFromDate, selectedPayToDate;
  String? selectedPayFromDateApi = "", selectedPayToDateApi = "";
  String? currencySymbol;
  UserDetail? userDetail;
  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);
  DateFormat dateFormat = DateFormat(Constant.DATE_FORMAT);

  int? downloadId, paymentId;

  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;
  PaymentListResponse? paymentListResponse;

  String? status = "";
  int? cusId = -1;

  @override
  void onInit() {
    super.onInit();
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (paymentListResponse != null &&
            paymentListResponse?.pageDetails!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          getPaymentListData();
        }
      }
    });
    // initPlatformState();
    paymentStatusList?.clear();
    paymentStatusList?.add(PaymentStatus(status: Strings.pending,label: "${Strings.pending} (${Strings.collected}/${Strings.submitted})"));
    paymentStatusList?.add(PaymentStatus(status: Strings.approved,label: Strings.approved));
    paymentStatusList?.add(PaymentStatus(status: Strings.rejected,label: Strings.approved));
  }

  Future<void> initPlatformState() async {
    clearFilter();
    if (userDetail != null && userDetail?.userId != null) {
      getPaymentListData();
    } else {
      String strUserData = "";
      if (getStorage.hasData(Constant.USER_DATA)) {
        strUserData = await getStorage.read(Constant.USER_DATA);
      }
      if (!strUserData.isNullOrEmpty()) {
        userDetail = UserDetail.fromJson(jsonDecode(strUserData));
        update();
        if (userDetail != null && userDetail?.userId != null) {
          // getCustomerListData();
          getPaymentListData();
        }
      }
    }
  }

  applyFilter() {
    isFilterApply = true;
    filterViewOpen = false;
    update();
    getPaymentListData();
  }

  clearFilter() {
    selectedCustomer = null;
    selectedPaymentStatus = null;
    selectedPayFromDate = null;
    selectedPayToDate = null;
    selectedPayFromDateApi = "";
    selectedPayToDateApi = "";
    payFormDateController.clear();
    payToDateController.clear();
    chequeNoController.clear();
    invoiceNoController.clear();
    isFilterApply = false;
    filterViewOpen = false;
    update();
  }


  setBtnClickEvent(bool status) {
    checkBtnClickEvent = status;
    update();
  }

  getCustomerListData() {
    isLoadingProgress = true;
    update();
    GetAllCaseRequest getAllCaseRequest =
        GetAllCaseRequest(page: 1, pageSize: 100);
    PaymentProvider().getCustomerList(
      getAllCaseRequest: getAllCaseRequest,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerListResponse responseData =
                  CustomerListResponse.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.customerList != null &&
                    responseData.customerList!.isNotEmpty) {
                  customerList?.clear();
                  customerList?.addAll(responseData.customerList!);
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
        isLoadingProgress = false;
        update();
        if (isLoadFilterData == false) {
          isLoadFilterData = true;
          filterViewOpen = true;
        }
        // getPaymentListData();
      },
      onError: (ResponseModel error) {
        if (isLoadFilterData == false) {
          isLoadFilterData = true;
          filterViewOpen = true;
        }
        _handleApiError(error);
        // getPaymentListData();
        update();
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
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  getPaymentListData() {
    String apiUrl = UrlConstants.get_payment_list;

    if (isFilterApply) {
      // apiUrl = "$apiUrl";
      if (selectedCustomer != null) {
        cusId = selectedCustomer?.id;
        apiUrl = "${apiUrl}&customerid=$cusId";
      } else {
        apiUrl = "${apiUrl}&customerid=";
      }
      if (selectedPayFromDateApi!.isNotEmpty) {
        apiUrl = "${apiUrl}&payfromdate=$selectedPayFromDateApi";
      } else {
        apiUrl = "${apiUrl}&payfromdate=";
      }
      if (selectedPayToDateApi!.isNotEmpty) {
        apiUrl = "${apiUrl}&paytodate=$selectedPayToDateApi";
      } else {
        apiUrl = "${apiUrl}&paytodate=";
      }
      if (selectedPaymentStatus != null) {
        status = selectedPaymentStatus?.status;
        apiUrl = "${apiUrl}&paystatus=${status.toString().trim()}";
      } else {
        apiUrl = "${apiUrl}&paystatus=";
      }

      if (chequeNoController.text.isNotEmpty) {
        apiUrl = "${apiUrl}&chequeNo=${chequeNoController.text}";
      } else {
        apiUrl = "${apiUrl}&chequeNo=";
      }

      if (invoiceNoController.text.isNotEmpty) {
        apiUrl = "${apiUrl}&invoiceNumber=${invoiceNoController.text}";
      } else {
        apiUrl = "${apiUrl}&invoiceNumber=";
      }

      if (userDetail != null) {
        apiUrl = "${apiUrl}&staff=${isClearStaffApproveId == true ? "":userDetail!.userId}&approveId=${isClearStaffApproveId == true ? "" : userDetail!.userId}&paymode=&branchname=&buID=&referenceno=&receiptNo=&chequedate=&paydetails1=&destinationBank=&partnerName=&serviceAreaId=&page=$page&pageSize=${Constant.PAGE_LOAD_DATA_LIMIT_FULL}";
      }

      if (apiUrl == UrlConstants.get_payment_list) {
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
      if (selectedCustomer != null) {
        cusId = selectedCustomer?.id;
        apiUrl = "${apiUrl}&customerid=$cusId";
      } else {
        apiUrl = "${apiUrl}&customerid=";
      }
      if (selectedPayFromDateApi!.isNotEmpty) {
        apiUrl = "${apiUrl}&payfromdate=$selectedPayFromDateApi";
      } else {
        apiUrl = "${apiUrl}&payfromdate=";
      }
      if (selectedPayToDateApi!.isNotEmpty) {
        apiUrl = "${apiUrl}&paytodate=$selectedPayToDateApi";
      } else {
        apiUrl = "${apiUrl}&paytodate=";
      }
      if (selectedPaymentStatus != null) {
        status = selectedPaymentStatus?.status;
        apiUrl = "${apiUrl}&paystatus=${status.toString().trim()}";
      } else {
        apiUrl = "${apiUrl}&paystatus=";
      }

      if (chequeNoController.text.isNotEmpty) {
        apiUrl = "${apiUrl}&chequeNo=${chequeNoController.text}";
      } else {
        apiUrl = "${apiUrl}&chequeNo=";
      }

      if (invoiceNoController.text.isNotEmpty) {
        apiUrl = "${apiUrl}&invoiceNumber=${invoiceNoController.text}";
      } else {
        apiUrl = "${apiUrl}&invoiceNumber=";
      }
      // apiUrl = "${apiUrl}&staff=${userDetail!.userId}&approveId=${userDetail!.userId}&paymode=&branchname=&buID=&referenceno=&receiptNo=&chequedate=&paydetails1=&destinationBank=&partnerName=&serviceAreaId=&page=$page&pageSize=${Constant.PAGE_LOAD_DATA_LIMIT}";
      apiUrl = "${apiUrl}&staff=${userDetail!.userId}&approveId=${userDetail!.userId}&paymode=&branchname=&buID=&referenceno=&receiptNo=&chequedate=&paydetails1=&destinationBank=&partnerName=&serviceAreaId=&page=$page&pageSize=${Constant.PAGE_LOAD_DATA_LIMIT_FULL}";
      isLoading = true;
    }/* else {
      if (selectedCustomer != null) {
        cusId = selectedCustomer?.id;
        apiUrl = "${apiUrl}&customerid=$cusId";
      } else {
        apiUrl = "${apiUrl}&customerid=";
      }
      if (selectedPayFromDateApi!.isNotEmpty) {
        apiUrl = "${apiUrl}&payfromdate=$selectedPayFromDateApi";
      } else {
        apiUrl = "${apiUrl}&payfromdate=";
      }
      if (selectedPayToDateApi!.isNotEmpty) {
        apiUrl = "${apiUrl}&paytodate=$selectedPayToDateApi";
      } else {
        apiUrl = "${apiUrl}&paytodate=";
      }
      if (selectedPaymentStatus != null) {
        status = selectedPaymentStatus?.status;
        apiUrl = "${apiUrl}&paystatus=$status";
      } else {
        apiUrl = "${apiUrl}&paystatus=";
      }

      if (chequeNoController.text.isNotEmpty) {
        apiUrl = "${apiUrl}&chequeNo=${chequeNoController.text}";
      } else {
        apiUrl = "${apiUrl}&chequeNo=";
      }

      if (invoiceNoController.text.isNotEmpty) {
        apiUrl = "${apiUrl}&invoiceNumber=${invoiceNoController.text}";
      } else {
        apiUrl = "${apiUrl}&invoiceNumber=";
      }
      apiUrl =
      "${apiUrl}&staff=${userDetail!.userId}&approveId=${userDetail!.userId}&paymode=&branchname=&buID=&referenceno=&receiptNo=&chequedate=&paydetails1=&destinationBank=&partnerName=&serviceAreaId=&page=$page&pageSize=${Constant.PAGE_LOAD_DATA_LIMIT}";
      isLoadingProgress = true;
    }*/
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    // update();
    
    log("getPaymentList==>${apiUrl}");
    PaymentProvider().getPaymentList(
      url: apiUrl,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        isShowLoadMore = false;
        isLoadingProgress = false;
        isFirstTime = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              paymentListResponse = PaymentListResponse.fromJson(map);
              if (paymentListResponse!.status == 200) {
                if (paymentListResponse!.creditDocumentPojoList != null &&
                    paymentListResponse!.creditDocumentPojoList!.isNotEmpty) {
                  paymentsList?.clear();
                  paymentsList
                      ?.addAll(paymentListResponse!.creditDocumentPojoList!);
                } else {
                  paymentsList?.clear();
                }
              } else {
                if (paymentListResponse!.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      paymentListResponse!.responseMessage,
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

        getSystemConfigurationData(Strings.currency_payment);
      },
      onError: (ResponseModel error) {
        isShowLoadMore = false;
        isLoadingProgress = false;
        isLoading = false;
        getSystemConfigurationData(Strings.currency_payment);
        _handleApiError(error);
      },
    );
  }

  approveRejectPayment(String status, PaymentApproveRejectReq request) {
    isLoadingProgress = true;
    update();
    PendingApprovalsProvider().approveRejectPayment(
      status: status.toLowerCase(),
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isLoadingProgress = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PaymentChangeStatusRes responseData =
                  PaymentChangeStatusRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200)) {
                if (responseData.payment!.dataList != null &&
                    responseData.payment!.dataList!.isNotEmpty) {
                  ticketAssignStaffList?.clear();
                  ticketAssignStaffList
                      ?.addAll(responseData.payment!.dataList!);
                  showAssignStaffDialog(
                      responseData.payment!.dataList!, status);
                } else {
                  if (status.equalsIgnoreCase(Strings.reject)) {
                    Utils.showSnackbar(Strings.SUCCESS, "Reject Successfully.",
                        AppTheme.colorWhite, AppTheme.colorGreen);
                  } else {
                    Utils.showSnackbar(
                        Strings.SUCCESS,
                        "Approved Successfully.",
                        AppTheme.colorWhite,
                        AppTheme.colorGreen);
                    isFilterApply = true;
                    Get.back(result: true);
                    getPaymentListData();
                    isFilterApply = false;
                  }
                  // Get.back(result: true);
                  // getCreditNoteListData();
                }

                // if (status.equalsIgnoreCase(Strings.reject)) {
                //   if (responseData.payment != null &&
                //       responseData.payment!.dataList != null &&
                //       responseData.payment!.dataList!.isNotEmpty) {
                //     paymentId = request.idlist;
                //     update();
                //     showAssignStaffDialog(responseData.payment!.dataList!,status);
                //   } else {
                //     getPaymentListData();
                //   }
                // } else {
                //   getPaymentListData();
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

  // showAssignStaffDialog(List<TicketAssignStaff> item) {
  showAssignStaffDialog(List<TicketAssignStaff> item, String? staffStatus) {
    showDialog(
        context: Get.context!,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return TicketAssignDialog(
              ticketAssignAction: this,
              itemsOrgLst: item,
              staffStatus: staffStatus);
        });
  }

  @override
  // void ticketAssignBtnAction({TicketAssignStaff? selectedItem}) {
  void ticketAssignBtnAction(
      {TicketAssignStaff? selectedItem,
      bool? isStaffSelected,
      String? approveRejectStatus}) {
    Get.back();
    // if (selectedItem != null && paymentId != null) {
    //   assignTicket(selectedItem.createdById!);
    // }
    if (isStaffSelected == true) {
      if (approveRejectStatus!.equalsIgnoreCase(Strings.approve)) {
        assignTicket(entityId, selectedItem!.id, true);
      } else if (approveRejectStatus.equalsIgnoreCase(Strings.reject)) {
        assignTicket(entityId, selectedItem!.id, false);
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

  assignEveryStaffCreditNote({int? entityId, bool? isApprovedRequest}) {
    String apiUrl =
        "${UrlConstants.creditNote_assign_every_staff}?entityId=$entityId&eventName=PAYMENT&isApproveRequest=$isApprovedRequest";
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
            getPaymentListData();
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

  // assignTicket(int staffId) {
  assignTicket(int? entityId, int? nextAssignStaff, bool? isApproveRequest) {
    isLoadingProgress = true;
    update();
    PendingApprovalsProvider().approveRejectTicket(
      entityId: entityId!,
      eventName: "PAYMENT",
      approveReject: isApproveRequest!,
      // assignId: staffId,
      assignId: nextAssignStaff,
      onSuccess: (ResponseModel responseModel) {
        isLoadingProgress = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                paymentId = null;
                update();
                if (responseData.message != null &&
                    responseData.message!.isNotEmpty) {
                  Utils.showSnackbar(Strings.SUCCESS, responseData.message,
                      AppTheme.colorWhite, AppTheme.colorGreen);
                }
                getPaymentListData();
              } else {
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
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  /* approveRejectPaymentRequest(
      PaymentApproveRejectReq? paymentApproveRejectReq, String apiUrl) {
    isLoadingProgress = true;
    update();
    PaymentProvider().approveRejectPaymentRequest(
      url: apiUrl,
      paymentApproveRejectReq: paymentApproveRejectReq,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PaymentListResponse responseData =
                  PaymentListResponse.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                getPaymentListData();
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
        isLoadingProgress = false;
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }*/

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

              getPaymentListData();
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

  downloadFile() async {
    try {
      String fileName = convertCurrentDateTimeToString();
      final directory = await getApplicationDocumentsDirectory();
      String dirloc = '${directory.path}/';
      FileUtils.mkdir([dirloc]);
      downloadFileApiCall(
          UrlConstants.payment_receipt_url + downloadId!.toString(),
          "$dirloc$fileName.pdf");
    } catch (e) {
      print("$e");
    }
  }

  downloadFileApiCall(String url, String savePath) {
    isLoadingProgress = true;
    update();
    PaymentProvider().downloadFile(
      fileUrl: url,
      savePath: savePath,
      onSuccess: (ResponseModel responseModel) async {
        isLoadingProgress = false;
        update();
        if (responseModel.statusCode == 200) {
          if (!savePath.isNullOrEmpty()) {
            // await savePat
            // OpenFile.open(savePath);
            // OpenFilex.open(savePath);
          }
        } else {
          if (responseModel.message != null &&
              responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
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
    if (response.statusCode == 200) {
      try {
          final bytes = response.bodyBytes;
          var directory;
          if (Platform.isIOS) {
            directory = await getDownloadsDirectory();
          } else {
            directory = "/storage/emulated/0/Download/";
          }

          var file = File('$directory/$customerName$filename.pdf');
          await file.writeAsBytes(bytes, flush: true);
          Utils.showSnackbar(Strings.SUCCESS,
              "File Downloaded Successfully Please Open Download Folder!!",
              AppTheme.colorWhite, AppTheme.colorGreen);
          isLoading = false;
        } catch (e) {
        log("messageException===>$e");
        Utils.showSnackbar(Strings.INFO,
            "Receipt generation Fail",
            AppTheme.colorWhite, AppTheme.colorGreen);
      }
    }else{

    }
    // return pFile;
  }


  String convertCurrentDateTimeToString() {
    String formattedDateTime =
        DateFormat('yyyyMMdd_kkmmss').format(DateTime.now()).toString();
    return "bill_" + formattedDateTime;
  }

  _handleApiError(ResponseModel error) {
    isLoading = false;
    isLoadingProgress = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
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
            getPaymentListData();
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
          entityId, "PAYMENT", selectedItem.id, remarkController!.text);
    }
  }
}
