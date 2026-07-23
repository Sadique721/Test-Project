import 'dart:async';
import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/credit_note/credit_note_provider.dart';
import 'package:savbill/pages/credit_note/response/reassign_workflow_get_staff_res.dart';
import 'package:savbill/pages/customer/change_customer_status_dialog.dart';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/customer_search_data.dart';
import 'package:savbill/pages/customer/model/request/add_notes_req.dart';
import 'package:savbill/pages/customer/model/request/change_customer_status_req.dart';
import 'package:savbill/pages/customer/model/request/custmer_list_request.dart';
import 'package:savbill/pages/customer/model/request/filters.dart';
import 'package:savbill/pages/customer/model/request/nearby_devices_req.dart';
import 'package:savbill/pages/customer/model/request/send_payment_link_req.dart';
import 'package:savbill/pages/customer/model/response/change_customer_status_res.dart';
import 'package:savbill/pages/customer/model/response/cust_invoice_payment_link_res.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer/model/response/customer_list_response.dart';
import 'package:savbill/pages/customer/model/response/customer_status_list_res.dart';
import 'package:savbill/pages/customer/model/response/delete_customer_res.dart';
import 'package:savbill/pages/customer/model/response/nearby_devices_res.dart';
import 'package:savbill/pages/customer/nearby_device_dialog.dart';
import 'package:savbill/pages/customer_caf/caf_customer_approve_reject_dialog.dart';
import 'package:savbill/pages/customer_caf/caf_customer_staff_assign_dialog.dart';
import 'package:savbill/pages/customer_caf/customer_reassign_work_flow_dialog.dart';
import 'package:savbill/pages/customer_caf/response/approve_reject_caf_customer_res.dart';
import 'package:savbill/pages/customer_caf/response/custome_doc_pending_res.dart';
import 'package:savbill/pages/customer_caf/response/customer_caf_drop_down_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/routes/app_routes.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:intl/intl.dart';
import 'package:url_launcher/url_launcher.dart';

class CustomerCafListController extends GetxController
    implements
        CafCustomerAssignAction,
        CustomerReAssignWorkFlowAction,
        CafCustomerApproveRejectBtnAction {
  bool isLoading = false;
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;
  GetStorage getStorage = GetStorage();

  List<CustomerDetail>? customerList = [];
  CustomerListResponse? customerListResponse;
  String type = Strings.prepaid;
  int? entityId;
  bool isFilterApply = false, filterViewOpen = false;
  TextEditingController searchController = TextEditingController();
  TextEditingController startDateController = TextEditingController();
  TextEditingController endDateController = TextEditingController();
  TextEditingController notesController = TextEditingController();
  TextEditingController searchStaffController = TextEditingController();
  TextEditingController searchAssignStaffController = TextEditingController();
  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);
  DateTime? selectedStartDate, selectedEndDate;
  List<CustomerSearchData>? searchCategory = [];
  CustomerSearchData? selectedSearchCategory;
  List<CustomerStatusDetail>? statusList = [];
  CustomerStatusDetail? selectedStatusList;


  List<CustomerCafDropDownStaffList>? customerAllStaffList = [];
  CustomerCafDropDownStaffList? selectedCustomerAllStaffList;

  List<DropdownDetail>? custTypeList = [];
  DropdownDetail? selectedCustType;

  // List<CloseCafContentList>? closeCafContentList = [];
  // List<RejectSubReasonDtoList>? rejectSubReasonDtoList = [];
  // RejectSubReasonDtoList? selectedRejectedSubReason;
  // RxBool isRejectedSubReason = false .obs;
  // CloseCafContentList? selectRejectedReason;
  bool checkBtnClickEvent = false;
  UserDetail? userDetail;
  List<ApproveRejectCafDataList>? approveRejectCafCustomerListList = [];
  String? loggedInUser;

  // String? remainTimeTAT;

  Timer? cafRemainTimeSubscription;

  List<ReassignWorkflowList>? reassignWorkFlowList = [];

  @override
  void onInit() {
    super.onInit();
    searchCategory?.clear();
    searchCategory?.add(CustomerSearchData(
        text: Strings.firstname, value: Strings.name.toLowerCase()));
    searchCategory?.add(CustomerSearchData(
        text: Strings.username, value: Strings.username.toLowerCase()));
    searchCategory?.add(CustomerSearchData(
        text: Strings.fullname, value: Strings.fullname.toLowerCase()));
    searchCategory?.add(CustomerSearchData(
        text: Strings.email, value: Strings.email.toLowerCase()));
    searchCategory?.add(
        CustomerSearchData(text: 'Phone', value: Strings.mobile.toLowerCase()));
    searchCategory?.add(CustomerSearchData(
        text: Strings.service, value: Strings.service.toLowerCase()));
    searchCategory?.add(CustomerSearchData(
        text: Strings.plan, value: Strings.plan.toLowerCase()));
    searchCategory
        ?.add(CustomerSearchData(text: 'Plan Group', value: 'planGroup'));
    searchCategory?.add(
        CustomerSearchData(text: 'Service Area', value: 'serviceareaName'));
    searchCategory
        ?.add(CustomerSearchData(text: 'Mac Address', value: 'macaddress'));
    searchCategory?.add(CustomerSearchData(
        text: Strings.status, value: Strings.status.toLowerCase()));
    searchCategory
        ?.add(CustomerSearchData(text: 'CAF Status', value: 'cafStatus'));
    searchCategory?.add(CustomerSearchData(
        text: Strings.any, value: Strings.any.toLowerCase()));
    searchCategory
        ?.add(CustomerSearchData(text: 'PartnerName', value: 'partnerName'));
    searchCategory
        ?.add(CustomerSearchData(text: 'Branch', value: 'branchName'));
    searchCategory
        ?.add(CustomerSearchData(text: 'Customer Type', value: 'custtype'));
    searchCategory
        ?.add(CustomerSearchData(text: 'Circuit Name', value: 'circuitName'));
    searchCategory?.add(CustomerSearchData(
        text: 'Current Assigned Staff', value: 'currentAssigneeName'));
    searchCategory?.add(CustomerSearchData(
        text: 'Current Assigned Team', value: 'currentAssignedTeam'));
    searchCategory?.add(
        CustomerSearchData(text: 'CAF Created Date', value: 'cafCreatedDate'));
    searchCategory?.add(CustomerSearchData(text: 'CAF Number', value: 'cafNo'));
    searchCategory
        ?.add(CustomerSearchData(text: 'Static IPr', value: 'staticIp'));
    searchCategory?.add(CustomerSearchData(
        text: 'Inventory Serial Number', value: 'inventorySerial'));
    searchCategory?.add(
        CustomerSearchData(text: 'Plan Expiry Date', value: 'expiryDate'));
    searchCategory?.add(CustomerSearchData(
        text: 'Framed_Ip_Address', value: 'framedIpAddress'));
    searchCategory?.add(CustomerSearchData(
        text: 'Subscription Mode', value: 'subscriptionMode'));
    searchCategory?.add(CustomerSearchData(text: 'Param1', value: 'param1'));
    searchCategory?.add(CustomerSearchData(text: 'Param2', value: 'param2'));
    searchCategory?.add(CustomerSearchData(text: 'Param3', value: 'param3'));
    searchCategory?.add(CustomerSearchData(text: 'Param4', value: 'param4'));
    searchCategory?.add(CustomerSearchData(text: 'Account No', value: 'accountNumber'));


    custTypeList!.clear();
    custTypeList!.add(DropdownDetail(
        id: "Parent",
        text: "Parent",
        type: "custparent"));
    custTypeList!.add(DropdownDetail(
        id: "Child",
        text: "Child",
        type: "custchild"));
    custTypeList!.add(DropdownDetail(
        id: "Individual",
        text: "Individual",
        type: "custindividual"));

    selectedSearchCategory = searchCategory![17];
    initPlatformState();
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (customerListResponse != null &&
            customerListResponse?.pageDetails!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          getCustomerListData();
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

  setBtnClickEvent(bool status) {
    checkBtnClickEvent = status;
    update();
  }

  void moveToDashboard() async {
    Get.offAllNamed(AppRoutes.DASHBOARD);
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_TYPE] != null) {
        type = arguments[Constant.CUSTOMER_TYPE];
        // getCustomerListData();
        getCustomerAll();
      }
    }
  }

  applyFilter() {
    // if (selectedSearchCategory == null ||
    //     searchController.text.isNullOrEmpty() )
    if (selectedSearchCategory == null) {
      isFilterApply = false;
      filterViewOpen = true;
      update();
      Utils.showSnackbar(Strings.ERROR, "Please select or enter filter option.",
          AppTheme.colorWhite, AppTheme.colorRed);
      return;
    }
    isFilterApply = true;
    filterViewOpen = false;
    page = 1;
    update();
    getCustomerListData();
  }

  clearFilter() {
    selectedSearchCategory = null;
    selectedStatusList = null;
    selectedCustomerAllStaffList = null;
    searchController.clear();
    page = 1;
    isFilterApply = false;
    filterViewOpen = false;
    update();
    getCustomerListData();
  }

  changeCustomerStatusUpPopup(
      int index,
      ChangeCustomerStatusBtnAction changeCustomerStatusBtnAction,
      CustomerCafListController controller) {
    if (statusList != null && statusList!.isNotEmpty) {
      // showDialog(
      //     context: Get.context!,
      //     barrierDismissible: false,
      //     builder: (BuildContext context) {
      //       return ChangeCustomerStatusDialog(
      //           changeCustomerStatusBtnAction: changeCustomerStatusBtnAction,
      //           custDetail: customerList![index],
      //           statusList: statusList!,
      //           controller: controller);
      //     });
    } else {
      // getCustomerStatusList(index, changeCustomerStatusBtnAction, controller);
    }
  }

  // getCustomerAllApiCall() {
  //   isLoading = true;
  //   update();
  //   CustomerProvider().getCustomerAll(
  //     onSuccess: (ResponseModel responseModel) {
  //       isLoading = false;
  //       update();
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           try {
  //             Map<String, dynamic> map = responseModel.result;
  //             CustomerCafDropDownRes responseData = CustomerCafDropDownRes.fromJson(map);
  //             if (responseData.responseCode == 200) {
  //               if (responseData.dataList!.isNotEmpty) {
  //                 customerCAFDropDownStaffList!.addAll(responseData.dataList!);
  //
  //                 for (CustomerCafDropDownStaffList element in customerCAFDropDownStaffList!) {
  //                   if (userDetail!.userId == element.id) {
  //                     selectedCustomerCAFDropDownStaffList = element;
  //                     applyFilter();
  //                   }
  //                 }
  //               }
  //             } else {
  //               if (responseData.responseMessage!.isNotEmpty) {
  //                 Utils.showSnackbar(
  //                     Strings.ERROR,
  //                     responseData.responseMessage,
  //                     AppTheme.colorWhite,
  //                     AppTheme.colorRed);
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
  //       update();
  //     },
  //     onError: (ResponseModel error) {
  //       _handleApiError(error);
  //     },
  //   );
  // }

  getCustomerListData() {
    CustomerListRequest customerReq = CustomerListRequest(
        page: page,
        pageSize: Constant.PAGE_LOAD_DATA_LIMIT,
        sortBy: "",
        sortOrder: 1,
        status: "NewActivation");

    if (isFilterApply) {
      List<Filters>? filters = [];
      filters.add(Filters(
          filterColumn: selectedSearchCategory?.value,
          filterCondition: "and",
          filterDataType: "",
          filterOperator: "equalto",
          // filterValue: searchController.text,
          filterValue: selectedCustomerAllStaffList != null ? selectedCustomerAllStaffList!.username : searchController.text));
      customerReq.filters = filters;
    }
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    CustomerProvider().getCustomerList(
      type: type,
      isSearch: isFilterApply,
      customerListRequest: customerReq,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerListResponse responseData =
                  CustomerListResponse.fromJson(map);

              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                customerListResponse = responseData;
                if (page == 1) {
                  customerList?.clear();
                }
                if (responseData.customerList != null &&
                    responseData.customerList!.isNotEmpty) {
                  customerList?.addAll(responseData.customerList!);
                }
              } else if (responseData.status == 204) {
                if (page == 1) {
                  customerList?.clear();
                }
              }else{
                if (page == 1) {
                  customerList?.clear();
                }
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
          if (page == 1) {
            customerList?.clear();
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
          customerList?.clear();
        }
        _handleApiErrorSearchData(error);
      },
    );
  }

  sendPaymentLinkToCustomer(int? custId) {
    isLoading = true;
    update();
    SendPaymentLinkReq req = SendPaymentLinkReq(custId: custId);
    CustomerProvider().sendPaymentLinkCustomer(
      sendPaymentLinkReq: req,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.msg != null && responseData.msg!.isNotEmpty) {
                  Utils.showSnackbar(Strings.SUCCESS, responseData.msg!,
                      AppTheme.colorWhite, AppTheme.colorGreen);
                } else {
                  Utils.showSnackbar(Strings.SUCCESS, Strings.successfully,
                      AppTheme.colorWhite, AppTheme.colorGreen);
                }
              } else {
                if (responseData.msg!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.msg,
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
        print("Post Response Data ==> ${error}");
        _handleApiError(error);
      },
    );
  }

  getCustomerStatusList() {
    isLoading = true;
    update();
    CustomerProvider().getCustomerStatusType(
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerStatusListRes responseData =
                  CustomerStatusListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList!.isNotEmpty) {
                  statusList?.clear();
                  // statusList?.addAll(responseData.dataList!);
                  statusList = responseData.dataList!
                      .where((status) =>
                          status.value !=  "Active" &&
                          status.value != "InActive" &&
                          status.value != "Reject" &&
                          status.value != "Rejected" &&
                          status.value != "Suspend" &&
                          status.value != "Terminate")
                      .toList();
                  update();
                  // changeCustomerStatusUpPopup(index, changeCustomerStatusBtnAction, controller);
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

  changeCustomerStatusList(int custId, String status, String? remark) {
    isLoading = true;
    update();
    ChangeCustomerStatusReq request = ChangeCustomerStatusReq(
        id: custId, status: status, remark: remark, rf: "bss");
    CustomerProvider().changeCustomerStatus(
      remark: remark,
      changeCustomerStatusReq: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ChangeCustomerStatusRes responseData =
                  ChangeCustomerStatusRes.fromJson(map);
              if (responseData.status == 200) {
                Utils.showSnackbar(Strings.SUCCESS, responseData.customer,
                    AppTheme.colorWhite, AppTheme.colorGreen);
              } else {
                if (responseData.customer!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.customer,
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
        _handleApiErrorCustom(error);
      },
    );
  }

  getNearByDevices(Position currentPosition) {
    isLoading = true;
    update();
    NearbyDevicesReq request = NearbyDevicesReq(
        latitude: currentPosition.latitude.toString(),
        longitude: currentPosition.longitude.toString());
    CustomerProvider().getNearByDevices(
      nearbyDevicesReq: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              NearbyDevicesRes responseData = NearbyDevicesRes.fromJson(map);
              if (responseData.status != null && responseData.status == 200) {
                if (responseData.locations != null &&
                    responseData.locations!.isNotEmpty) {
                  nearByDeviceDialog(
                      Strings.near_location, responseData.locations!);
                }
              } else {
                if (responseData.error != null &&
                    responseData.error!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.error,
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
        /* List<NearByDeviceDetail> locations = [];
        NearByDeviceDetail data = NearByDeviceDetail(
            address: "A-404, The First, Vastrapur, Ahemedabad, Gujarat, India.",
            latitude: "23.1456",
            longitude: "72.685",
            name: "Vastrapur",
            distance: 5,
            networkDeviceId: 12);
        locations.add(data);
        locations.add(data);
        locations.add(data);
        nearByDeviceDialog(Strings.near_location, locations);*/
        _handleApiErrorNearByDevice(error);
      },
    );
  }

  deleteCustomer(int? customerId, int index) {
    isLoading = true;
    update();
    CustomerProvider().deleteCustomer(
      customerId: customerId!,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              DeleteCustomerRes responseData = DeleteCustomerRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.message!.isNotEmpty) {
                  Utils.showSnackbar(Strings.SUCCESS, responseData.message,
                      AppTheme.colorWhite, AppTheme.colorGreen);
                }
                if (customerList!.isNotEmpty) {
                  customerList!.removeAt(index);
                }
              } else {
                if (responseData.eRROR!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.eRROR,
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

  approveRejectCafCustomer(
      {required String? status,
      required String? remark,
      required CustomerDetail? customerDetail,
      required BuildContext context}) {
    isLoading = true;
    update();
    CustomerProvider().approveCustomerCAF(
      custCafID: customerDetail!.id,
      nextStaffID: null,
      approveFlag: status,
      remark: remark,
      staffID: userDetail!.userId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ApproveRejectCafCustomerRes responseData =
                  ApproveRejectCafCustomerRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.result!.dataList != null &&
                    responseData.result!.dataList!.isNotEmpty) {
                  approveRejectCafCustomerListList?.clear();
                  approveRejectCafCustomerListList
                      ?.addAll(responseData.result!.dataList!);
                  showAssignStaffDialog(responseData.result!.dataList!, status,
                      context, customerDetail.id,searchStaffController);
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
                  applyFilter();
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

  onReActivateService(int? customerId) {
    isLoading = true;
    update();
    CustomerProvider().reactivateService(
      custID: customerId!,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                Utils.showSnackbar(Strings.SUCCESS, "Re-activate Successfully",
                    AppTheme.colorWhite, AppTheme.colorGreen);
                getCustomerListData();
              } else {
                if (responseData.error!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.error,
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

  isCustomerDocumentPendingCall(
      int? customerId, BuildContext context, CustomerDetail? customerList) {
    isLoading = true;
    update();
    CustomerProvider().customerDocumentPending(
      custID: customerId!,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerDocPendingRes responseData =
                  CustomerDocPendingRes.fromJson(map);
              if (responseData.responseCode == 200 ||
                  responseData.responseCode == 0) {
                if (responseData.data == true) {
                  Utils.showSnackbar(
                      Strings.INFO,
                      "Customer cannot activate. Document Verification Pending",
                      AppTheme.colorWhite,
                      AppTheme.colorBlueRView);
                } else {
                  addRemarkCafCustomerDialog(
                      Get.context!, Strings.approve, customerList);
                }
              } else {
                if (responseData.error!.isNotEmpty) {
                  Utils.showSnackbar(Strings.INFO, responseData.error,
                      AppTheme.colorWhite, AppTheme.colorBlueRView);
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.INFO, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorBlueRView);
          }
        }
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  customerInvoicePaymentLinkCall(int? customerId, bool? isRenew) {
    isLoading = true;
    update();
    CustomerProvider().customerInvoicePaymentLink(
      customerId: customerId!,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustInvoicePaymentLinkRes responseData =
                  CustInvoicePaymentLinkRes.fromJson(map);
              if (responseData.responseCode == 200 ||
                  responseData.status == 200) {
                final payData = responseData.data;
                if (responseData.data == null) {
                  Utils.showSnackbar(
                      Strings.INFO,
                      "No Unpaid Invoice Found for this Customer",
                      AppTheme.colorWhite,
                      AppTheme.colorBlueRView);
                } else if (responseData.data != null) {
                  final paymentUrl =
                      "${UrlConstants.PAYMENT_RECEIPT_URL}/#/customer/payMethod/$payData";
                  _launchUrl(Uri.parse(paymentUrl));
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

  Future<void> _launchUrl(Uri _url) async {
    if (!await launchUrl(_url)) {
      throw Exception('Could not launch $_url');
    }
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

  _handleApiErrorSearchData(ResponseModel error) {
    isLoading = false;
    isShowLoadMore = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (error.statusCode == 500) {
      Utils.showSnackbar(Strings.INFO, Strings.no_data_found,
          AppTheme.colorWhite, AppTheme.colorBlueRView);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }

  _handleApiErrorNearByDevice(ResponseModel error) {
    isLoading = false;
    isShowLoadMore = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (error.statusCode == 500) {
      Utils.showSnackbar(Strings.INFO, "No Splitter Profile available",
          AppTheme.colorWhite, AppTheme.colorBlueRView);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }

  _handleApiErrorCustom(ResponseModel error) {
    isLoading = false;
    isShowLoadMore = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (error.statusCode == Constant.CODE_NO_TRY_CATCH) {
      Utils.showSnackbar(Strings.INFO, "Change status already in process.",
          AppTheme.colorWhite, AppTheme.colorBlueRView);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }

  nearByDeviceDialog(String title, List<NearByDeviceDetail> locations) {
    showDialog(
        context: Get.context!,
        barrierDismissible: false,
        builder: (BuildContext context) {
          return NearbyDeviceDialog(title: title, detailsInfo: locations);
        });
  }

  showAssignStaffDialog(List<ApproveRejectCafDataList> item,
      String? staffStatus, BuildContext context, int? entityId,TextEditingController? searchController ) {
    showDialog(
        context: Get.context!,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return CafCustomerAssignDialog(
            cafCustomerAssignAction: this,
            itemsOrgLst: item,
            staffStatus: staffStatus,
            entityId: entityId,
            controller: searchController,

          );
        });
  }


  addNoteCallApi(int? custId) {
    isLoading = true;
    update();
    AddNotesReq req = AddNotesReq(id: 0, custId: custId, notes: notesController.text);
    CustomerProvider().addNoteApi(
      addNotesReq: req,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.msg != null && responseData.msg!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.add_notes_successfully,
                      responseData.msg!,
                      AppTheme.colorWhite,
                      AppTheme.colorGreen);
                } else {
                  Utils.showSnackbar(Strings.SUCCESS, Strings.successfully,
                      AppTheme.colorWhite, AppTheme.colorGreen);
                }
                Get.back();
              } else {
                if (responseData.msg!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.msg,
                      AppTheme.colorWhite, AppTheme.colorRed);
                }
                Get.back();
              }
            } on Exception catch (e) {
              print(e.toString());
              Get.back();
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
          Get.back();
        }
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        Get.back();
      },
    );
  }

  @override
  void cafCustomerAssignBtnAction(
      {ApproveRejectCafDataList? selectedItem,
      bool? isStaffSelected,
      String? approveRejectStatus,
      int? entityId}) {
    Get.back();
    if (isStaffSelected == true) {
      // log("Staff is selected");
      if (approveRejectStatus!.equalsIgnoreCase(Strings.approve)) {
        // log("Staff is selected!!!!!!!!=>${Strings.approve}");
        assignStaffCreditNote(entityId, selectedItem!.id, true);
      } else if (approveRejectStatus.equalsIgnoreCase(Strings.reject)) {
        // log("Staff is selected!!!!!!!!=>${Strings.reject}");
        assignStaffCreditNote(entityId, selectedItem!.id, false);
      }
    } else {
      log("NotStaffis selected==>${approveRejectStatus}");
      if (approveRejectStatus!.equalsIgnoreCase("approved")) {
        assignEveryStaffCreditNote(entityId: entityId, isApprovedRequest: true);
      } else if (approveRejectStatus.equalsIgnoreCase("rejected")) {
        assignEveryStaffCreditNote(
            entityId: entityId, isApprovedRequest: false);
        // }
      }
    }
  }

  assignStaffCreditNote(
      int? entityId, int? nextAssignStaff, bool? isApproveRequest) {
    String apiUrl =
        "${UrlConstants.assignFromStaffCreditNoteList}?entityId=$entityId&eventName=CAF&nextAssignStaff=$nextAssignStaff&isApproveRequest=$isApproveRequest";
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
            // applyFilter();
            getCustomerAll();
            Utils.showSnackbar(Strings.SUCCESS, Strings.successfully,
                AppTheme.colorWhite, AppTheme.colorGreen);
            // getCustomerListData();
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
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  assignEveryStaffCreditNote({int? entityId, bool? isApprovedRequest}) {
    String apiUrl =
        "${UrlConstants.creditNote_assign_every_staff}?entityId=$entityId&eventName=CAF&isApproveRequest=$isApprovedRequest";
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
            // applyFilter();
            getCustomerAll();
            Utils.showSnackbar(Strings.SUCCESS, Strings.successfully,
                AppTheme.colorWhite, AppTheme.colorGreen);
            // getCustomerListData();
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
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  pickUpCreditNote(int? entityId) {
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
                getCustomerListData();
              } else {
                if (responseData.responseMessage != null &&
                    responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.INFO,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorBlueRView);
                }
                if (responseData.ERROR != null &&
                    responseData.ERROR!.isNotEmpty) {
                  Utils.showSnackbar(Strings.INFO, responseData.ERROR,
                      AppTheme.colorWhite, AppTheme.colorBlueRView);
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
                  showReAssignWorkFlowGetStaffDialog(responseData.dataList!,searchAssignStaffController);
                } else {
                  Utils.showSnackbar(
                      Strings.INFO,
                      responseData.responseMessage ?? "",
                      AppTheme.colorWhite,
                      AppTheme.colorBlueRView);
                }
              }

              getCustomerListData();
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

  showReAssignWorkFlowGetStaffDialog(List<ReassignWorkflowList> item,TextEditingController? assignSearchController ) {
    showDialog(
        context: Get.context!,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return CustomerReAssignWorkFlowDialog(
              customerReassignWorkflowAction: this, itemsOrgLst: item,textEditingController: assignSearchController,);
        });
  }

  @override
  void customerReAssignWorkFlowBtnAction(
      {ReassignWorkflowList? selectedItem,
      TextEditingController? remarkController}) {
    Get.back();
    if (selectedItem != null && entityId != null) {
      // assignEveryStaffCreditNote(entityId);
      reassignWorkflowAssignCall(
          entityId, "CAF", selectedItem.id, remarkController!.text);
    }
  }

  reassignWorkflowAssignCall(int? entityId, String? eventName, int? assignToStaffId, String? remark) {
    String apiUrl = "${UrlConstants.reassignWorkflow}?entityId=$entityId&eventName=$eventName&assignToStaffId=$assignToStaffId&remark=$remark";
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
            getCustomerListData();
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

  addRemarkCafCustomerDialog(BuildContext context, String? pageName,
      CustomerDetail? customerDetail) async {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return CafCustomerApproveRejectDialog(
            pageName: pageName,
            cafCustomerApproveRejectBtnAction: this,
            customerDetail: customerDetail,
            // caseId: item.caseId,
          );
        });
  }

  @override
  void cafCustomerApproveRejectStatus(
      {String? identifier,
      TextEditingController? remarkController,
      BuildContext? context,
      CustomerDetail? customerDetail}) {
    Get.back();
    if (identifier != null && identifier.equalsIgnoreCase(Strings.approve)) {
      approveRejectCafCustomer(
          status: Strings.approved.toLowerCase(),
          remark: remarkController!.text,
          customerDetail: customerDetail,
          context: context!);
    } else if (identifier != null &&
        identifier.equalsIgnoreCase(Strings.reject)) {
      approveRejectCafCustomer(
          status: Strings.rejected.toLowerCase(),
          remark: remarkController!.text,
          customerDetail: customerDetail,
          context: context!);
    }
  }

  getCustomerAll() {
    isLoading = true;
    update();
    CustomerProvider().getCustomerDropDownList(
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerCafDropDownRes responseData = CustomerCafDropDownRes.fromJson(map);
              log("map==>${jsonEncode(responseData)}");
              if (responseData.responseCode == 200) {
                customerAllStaffList = responseData.dataList;
                for (CustomerCafDropDownStaffList element in customerAllStaffList!) {
                  if (userDetail!.userId == element.id) {
                    selectedCustomerAllStaffList = element;
                    applyFilter();
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
}
