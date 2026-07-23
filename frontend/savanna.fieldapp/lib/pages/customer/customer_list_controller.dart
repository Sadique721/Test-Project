import 'dart:convert';
import 'dart:developer';

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
import 'package:savbill/pages/customer/model/response/radius_check_status_res.dart';
import 'package:savbill/pages/customer/nearby_device_dialog.dart';
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
import 'package:url_launcher/url_launcher.dart';

class CustomerListController extends GetxController {
  bool isLoading = false;
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;
  GetStorage getStorage = GetStorage();

  List<CustomerDetail>? customerList = [];
  List<String>? usernameList = [];
  CustomerListResponse? customerListResponse;
  String type = Strings.prepaid;

  bool isFilterApply = false, filterViewOpen = false;
  TextEditingController searchController = TextEditingController();
  TextEditingController notesController = TextEditingController();
  List<CustomerSearchData>? searchCategory = [];
  CustomerSearchData? selectedSearchCategory;
  List<CustomerStatusDetail>? statusList = [];

  bool checkBtnClickEvent = false;

  @override
  void onInit() {
    super.onInit();
    searchCategory?.clear();
    searchCategory?.add(CustomerSearchData(
        text: Strings.name, value: Strings.name.toLowerCase()));
    searchCategory?.add(CustomerSearchData(
        text: Strings.username, value: Strings.username.toLowerCase()));
    searchCategory?.add(CustomerSearchData(
        text: Strings.email, value: Strings.email.toLowerCase()));
    searchCategory?.add(CustomerSearchData(
        text: Strings.mobile, value: Strings.mobile.toLowerCase()));
    searchCategory?.add(CustomerSearchData(
        text: Strings.any, value: Strings.any.toLowerCase()));

    selectedSearchCategory = searchCategory![4];

    getArgumentData();
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
        clearFilter();
      }
    }
  }

  applyFilter() {
    if (selectedSearchCategory == null
        // searchController.text.isNullOrEmpty()
        ) {
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
      CustomerListController controller) {
    if (statusList != null && statusList!.isNotEmpty) {
      showDialog(
          context: Get.context!,
          barrierDismissible: false,
          builder: (BuildContext context) {
            return ChangeCustomerStatusDialog(
                changeCustomerStatusBtnAction: changeCustomerStatusBtnAction,
                custDetail: customerList![index],
                statusList: statusList!,
                controller: controller);
          });
    } else {
      getCustomerStatusList(index, changeCustomerStatusBtnAction, controller);
    }
  }

  getCustomerListData() {
    CustomerListRequest customerReq = CustomerListRequest(
        page: page, pageSize: Constant.PAGE_LOAD_DATA_LIMIT, status: "Active");

    if (isFilterApply && searchController.text.trim().isNotEmpty) {
      customerReq.filters = [
        Filters(
          filterColumn: selectedSearchCategory?.value,
          filterCondition: "and",
          filterDataType: "",
          filterOperator: "equalto",
          filterValue: searchController.text.trim(),
        )
      ];
    } else {
      customerReq.filters = []; // VERY IMPORTANT
    }

    // if (isFilterApply) {
    //   List<Filters>? filters = [];
    //   filters.add(Filters(
    //       filterColumn: selectedSearchCategory?.value,
    //       filterCondition: "and",
    //       filterDataType: "",
    //       filterOperator: "equalto",
    //       filterValue: searchController.text.trim()));
    //   customerReq.filters = filters;
    // }
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    usernameList!.clear();
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

                  for (CustomerDetail element in customerList!) {
                    usernameList!.add(element.username!);
                  }
                }

                getCustomerCheckStatus();
              } else {
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
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
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

  getCustomerCheckStatus() {
    isLoading = true;
    update();
    Map<String, List<String>?> userMap = {"users": usernameList};

    CustomerProvider().getCustomerCheckStatus(
      usernameList: userMap,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              RadiusCheckStatusRes responseData =
                  RadiusCheckStatusRes.fromJson(map);
              if (responseData.status != null && responseData.status == 200) {
                if (responseData.liveusers!.isNotEmpty ||
                    responseData.liveusers != null) {
                  for (var customer in customerList!) {
                    customer.connectionMode = responseData.liveusers!
                            .contains(customer.username ?? "")
                        ? "Online"
                        : "Offline";
                  }
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
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
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
          if (responseModel.message != Strings.something_wrong) {
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

  getCustomerStatusList(
      int index,
      ChangeCustomerStatusBtnAction changeCustomerStatusBtnAction,
      CustomerListController controller) {
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
                  statusList?.addAll(responseData.dataList!);
                  update();
                  changeCustomerStatusUpPopup(
                      index, changeCustomerStatusBtnAction, controller);
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
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
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
                clearFilter();
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
                  // final paymentUrl = "${UrlConstants.PAYMENT_RECEIPT_URL}/#/customer/payMethod/$payData?isRenew=$isRenew";
                  final paymentUrl =
                      "${UrlConstants.PAYMENT_RECEIPT_URL}/#/customer/payMethod/$payData";
                  _launchUrl(Uri.parse(paymentUrl));
                }
              } else {
                Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorBlueRView);
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

  customerRenewPaymentLinkCall(int? customerId, bool? isRenew) {
    isLoading = true;
    update();
    CustomerProvider().customerRenewPaymentLink(
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
                if (responseData.data == null || responseData.data == "") {
                  Utils.showSnackbar(Strings.INFO, Strings.something_went_wrong,
                      AppTheme.colorWhite, AppTheme.colorBlueRView);
                } else if (responseData.data != null) {
                  // final paymentUrl = "${UrlConstants.PAYMENT_RECEIPT_URL}/#/customer/payMethod/$payData?isRenew=$isRenew";
                  final paymentUrl =
                      "${UrlConstants.PAYMENT_RECEIPT_URL}/#/customer/payMethod/$payData";
                  _launchUrl(Uri.parse(paymentUrl));
                }
              } else {
                Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorBlueRView);
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

  addNoteCallApi(int? custId) {
    isLoading = true;
    update();
    AddNotesReq req =
        AddNotesReq(id: 0, custId: custId, notes: notesController.text);

    log("AddNotesReq==>${jsonEncode(req)}");

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
                Get.back();
                if (responseData.message != null &&
                    responseData.message!.isNotEmpty) {
                  log("message=>${responseData.message}");
                  Utils.showSnackbar(Strings.INFO, responseData.message,
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
        print("Post Response Data ==> ${error}");
        _handleApiError(error);
        Get.back();
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

  _handleApiErrorSearchData(ResponseModel error) {
    isLoading = false;
    isShowLoadMore = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (error.statusCode == 500) {
      Utils.showSnackbar(Strings.INFO, Strings.no_data_found,
          AppTheme.colorWhite, AppTheme.colorBlueRView);
    } else if (error.statusCode == 404) {
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
}
