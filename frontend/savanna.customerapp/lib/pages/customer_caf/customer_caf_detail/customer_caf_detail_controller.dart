import 'dart:async';
import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/customer_detail_option.dart';
import 'package:savbill/pages/customer/model/request/change_customer_pwd_req.dart';
import 'package:savbill/pages/customer/model/request/cust_wallet_bal_req.dart';
import 'package:savbill/pages/customer/model/response/address_detail_response.dart';
import 'package:savbill/pages/customer/model/response/cust_address_detail.dart';
import 'package:savbill/pages/customer/model/response/cust_charge_details.dart';
import 'package:savbill/pages/customer/model/response/cust_detail_response.dart';
import 'package:savbill/pages/customer/model/response/cust_mac_mappping_detail.dart';
import 'package:savbill/pages/customer/model/response/cust_payment_detail.dart';
import 'package:savbill/pages/customer/model/response/cust_plan_detail.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer/model/response/customer_quota_list_response.dart';
import 'package:savbill/pages/customer/model/response/customer_wallet_bal_res.dart';
import 'package:savbill/pages/customer/model/response/service_area_detail_res.dart';
import 'package:savbill/pages/customer_caf/response/building_sub_area_name_res.dart';
import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/response/pop_detail_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/acl_constant.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/permission_service.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class CustomerCafDetailController extends GetxController {
  bool isLoading = false;
  CustomerDetail? customerDetail;
  int customerId = 0;
  int serviceAreaId = 0;
  List<CustQuotaDettail>? custQuotaList = [];
  List<CustMacMapppingDetail>? custMacMapppingList = [];
  List<CustChargeDetails>? custChargeList = [];
  CustPaymentDetail? paymentDetails;
  CustAddressDetail? presentAddress, paymentAddress, permanentAddress;
  List<PlanMappingDetail>? planMappingList = [];
  List<CustomerDetailOption> optionList = [];
  int? popId;
  ServiceAreaDetailData? serviceAreaDetail;
  String? customerType;
  bool? isCustApproval = false;

  late Timer timer;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;

    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_ID] != null) {
        customerId = arguments[Constant.CUSTOMER_ID];
        getCustomerDetail();
      }
      // if (arguments[Constant.SERVICE_AREA_ID] != null) {
      //   serviceAreaId = arguments[Constant.SERVICE_AREA_ID];
      // }
      if (arguments[Constant.CUSTOMER_TYPE] != null) {
        customerType = arguments[Constant.CUSTOMER_TYPE];
      }

      if (arguments[Constant.CUST_APPROVAL] != null) {
        isCustApproval = arguments[Constant.CUST_APPROVAL];
      }
      // log("serviceAreaId>>>${serviceAreaId}");
    }
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
                if (customerDetail != null &&
                    customerDetail!.paymentDetails != null) {
                  paymentDetails = customerDetail?.paymentDetails;
                }

                serviceAreaId = customerDetail!.serviceAreaId!;

                if (customerDetail != null &&
                    customerDetail!.custMacMapppingList != null &&
                    customerDetail!.custMacMapppingList!.isNotEmpty) {
                  custMacMapppingList = customerDetail!.custMacMapppingList!;
                }

                if (customerDetail != null && customerDetail!.popid != null) {
                  // Future.delayed(Duration(microseconds: 500),(){
                  if (customerDetail!.popid! > 0) {
                    viewPopDetail(customerDetail!.popid);
                  }
                  update();
                  // });
                }

                custChargeList?.clear();
                if (customerDetail != null &&
                    customerDetail!.overChargeList != null &&
                    customerDetail!.overChargeList!.isNotEmpty) {
                  custChargeList?.addAll(customerDetail!.overChargeList!);
                }
                if (customerDetail != null &&
                    customerDetail!.indiChargeList != null &&
                    customerDetail!.indiChargeList!.isNotEmpty) {
                  custChargeList?.addAll(customerDetail!.indiChargeList!);
                }
                planMappingList?.clear();
                if (customerDetail != null &&
                    customerDetail!.planMappingList != null &&
                    customerDetail!.planMappingList!.isNotEmpty) {
                  planMappingList?.addAll(customerDetail!.planMappingList!);
                }

                if (customerDetail != null &&
                    customerDetail!.addressList != null &&
                    customerDetail!.addressList!.isNotEmpty) {
                  customerDetail!.addressList!.forEach((element) async {
                    if (element.addressType != null &&
                        element.addressType!.isNotEmpty) {
                      if (element.addressType!.equalsIgnoreCase("Present")) {
                        presentAddress = element;
                        getAreaDetail(presentAddress?.areaId, "Present");
                      }

                      if (element.addressType!.equalsIgnoreCase("Payment")) {
                        paymentAddress = element;
                        getAreaDetail(paymentAddress?.areaId, "Payment");
                      }

                      if (element.addressType!.equalsIgnoreCase("Permanent")) {
                        permanentAddress = element;
                        getAreaDetail(permanentAddress?.areaId, "Permanent");
                      }
                      update();
                    }
                  });
                }
              } else {
                if (responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
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
        manageCustomerOption();
        // getCustomerQuotaDetail();
      },
      onError: (ResponseModel error) {
        // getCustomerQuotaDetail();
        // _handleApiError(error);
        _handleApiCustomerDetailsError(error);
      },
    );
  }

  hasCustomerInvoiceTypeIndependent() {
    // return (customerDetail!.planMappingList!
    //     .where(
    //         (element) => element.invoiceType!.equalsIgnoreCase("Independent"))
    //     .isNotEmpty);
    return customerDetail!.planMappingList!
        .where((element) =>
    element.invoiceType != null &&
        element.invoiceType!.equalsIgnoreCase("Independent"))
        .isNotEmpty;
  }

  manageCustomerOption() {
    bool showChildOption = false;
    if (customerDetail != null &&
        (customerDetail!.parentCustomerId == null ||
            hasCustomerInvoiceTypeIndependent()) &&
        (!customerDetail!.status!.equalsIgnoreCase("Terminate"))) {
      showChildOption = true;
    } else {
      showChildOption = false;
    }
    if(PermissionService().hasAclPermission([customerType!.equalsIgnoreCase('Prepaid')
        ? AclPreCustConstants.PRE_CUST_CAF_CUST_STATUS
        : AclPostCustConstants.POST_CUST_CAF_STATUS]) == true) {
      optionList.add(CustomerDetailOption(
          id: 1, title: Strings.customer_status, icon: plan_detail));
    }

    if(PermissionService().hasAclPermission([customerType!.equalsIgnoreCase('Prepaid')
        ? AclPreCustConstants.PRE_CUST_CAF_CUST_PLANS
        : AclPostCustConstants.POST_CUST_CAF_PLANS]) == true) {
      optionList.add(CustomerDetailOption(
          id: 2, title: Strings.customer_plan, icon: plan_detail));
    }

    if(PermissionService().hasAclPermission([customerType!.equalsIgnoreCase('Prepaid')
        ? AclPreCustConstants.PRE_CUST_CAF_INVOICES
        : AclPostCustConstants.POST_CUST_CAF_INVOICES]) == true) {
      optionList.add(CustomerDetailOption(
          id: 3, title: Strings.invoice, icon: invoice_detail));
    }

    // optionList.add(CustomerDetailOption(
    //     id: 4, title: Strings.ledger, icon: ledger_detail));
    // if (showChildOption) {

    if(PermissionService().hasAclPermission([customerType!.equalsIgnoreCase('Prepaid')
        ? AclPreCustConstants.PRE_CUST_CAF_PAYMENT
        : AclPostCustConstants.POST_CUST_CAF_PAYMENT]) == true) {
      optionList.add(CustomerDetailOption(
          id: 4, title: Strings.payments, icon: payment_detail));
    }

    // }
    // optionList.add(CustomerDetailOption(
    //     id: 6,
    //     title: Strings.connection_history,
    //     icon: connection_history_detail));
    // optionList.add(CustomerDetailOption(
    //     id: 7, title: Strings.tickets, icon: tickets_detail));
    if(PermissionService().hasAclPermission([customerType!.equalsIgnoreCase('Prepaid')
        ? AclPreCustConstants.PRE_CUST_CAF_INVENTORY
        : AclPostCustConstants.POST_CUST_CAF_INVENTORY]) == true) {
      optionList.add(CustomerDetailOption(
          id: 5, title: Strings.inventory, icon: inventory_detail));
    }
    // if (showChildOption) {
    //   optionList.add(CustomerDetailOption(
    //       id: 8, title: Strings.change_plan, icon: change_plan_detail));
    // }
    if(PermissionService().hasAclPermission([customerType!.equalsIgnoreCase('Prepaid')
        ? AclPreCustConstants.PRE_CUST_CAF_CHANGE_DISCOUNT
        : AclPostCustConstants.POST_CUST_CAF_CHANGE_DISCOUNT]) == true) {
      optionList.add(CustomerDetailOption(
          id: 6, title: Strings.change_discount, icon: change_discount_detail));
    }
    // optionList.add(CustomerDetailOption(
    //     id: 10, title: Strings.change_password, icon: change_password_detail));
    // optionList.add(CustomerDetailOption(
    //     id: 11, title: Strings.change_status, icon: change_status_detail));
    // if (showChildOption) {
    //   optionList.add(CustomerDetailOption(
    //       id: 8, title: Strings.wallet, icon: wallet_detail));
    // }

    if(PermissionService().hasAclPermission([customerType!.equalsIgnoreCase('Prepaid')
        ? AclPreCustConstants.PRE_CUST_CAF_CHARGE
        : AclPostCustConstants.POST_CUST_CAF_CHARGE]) == true) {
      optionList.add(CustomerDetailOption(
          id: 7,
          title: Strings.charge_management_new,
          icon: charge_management));
    }
    // optionList.add(CustomerDetailOption(
    //     id: 20, title: Strings.credit_note, icon: shift_location));
    optionList.add(CustomerDetailOption(
        id:12, title: Strings.shift_location, icon: shift_location));


    if(PermissionService().hasAclPermission([customerType!.equalsIgnoreCase('Prepaid')
        ? AclPreCustConstants.PRE_CUST_CAF_FOLLOW_UP
        : AclPostCustConstants.POST_CUST_CAF_FOLLOW_UP]) == true) {
      optionList.add(CustomerDetailOption(
          id: 8, title: Strings.followup, icon: shift_location));
    }

    if(PermissionService().hasAclPermission([customerType!.equalsIgnoreCase('Prepaid')
        ? AclPreCustConstants.PRE_CUST_CAF_SERVICE
        : AclPostCustConstants.POST_CUST_CAF_SERVICE]) == true) {
      optionList.add(CustomerDetailOption(
          id: 9, title: Strings.service_management_new, icon: shift_location));
    }

    if(PermissionService().hasAclPermission([customerType!.equalsIgnoreCase('Prepaid')
        ? AclPreCustConstants.PRE_CUST_CAF_SERVICE_CHANGE_PLAN
        : AclPostCustConstants.POST_CUST_CAF_SERVICE_CHANGE_PLAN]) == true) {
      optionList.add(CustomerDetailOption(
          id: 10, title: Strings.change_plan, icon: change_plan_detail));
    }

    // optionList.add(CustomerDetailOption(
    //     id: 19, title: Strings.revenue_report, icon: shift_location));
    //
    // optionList.add(CustomerDetailOption(
    //     id: 15, title: Strings.workflow_audit, icon: shift_location));
    // optionList.add(CustomerDetailOption(
    //     id: 16, title: Strings.audit_detials, icon: shift_location));
    //
    // optionList.add(CustomerDetailOption(
    //     id: 17, title: Strings.dunning_management, icon: shift_location));
    // optionList.add(CustomerDetailOption(
    //     id: 18, title: Strings.notification_management, icon: shift_location));
    if(PermissionService().hasAclPermission([customerType!.equalsIgnoreCase('Prepaid')
        ? AclPreCustConstants.CUSTOMER_CAF_NOTES
        : AclPostCustConstants.POST_CUST_CHARGE_CREATE]) == true) {
      optionList.add(CustomerDetailOption(
          id: 11, title: Strings.customer_caf_notes, icon: openTicket));
    }
    update();
  }

  getCustomerQuotaDetail() {
    custQuotaList!.clear();
    isLoading = true;
    update();
    CustomerProvider().getCustomerQuotaDetail(
      customerId: customerId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerQuotaListResponse responseData =
                  CustomerQuotaListResponse.fromJson(map);
              if (responseData.status != null && responseData.status == 200) {
                custQuotaList?.clear();
                if (responseData.custQuotaList != null &&
                    responseData.custQuotaList!.isNotEmpty) {
                  custQuotaList?.addAll(responseData.custQuotaList!);
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
              print("custQuotaList>>> $e");
            }
          }
        } else {
          log("custQuotaList>>> statusError");
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        update();
      },
      onError: (ResponseModel error) {
        log("custQuotaList>>>--->... ${error}");
        _handleApiError(error);
      },
    );
  }

  viewPopDetail(int? popId) async {
    isLoading = true;
    update();
    InventoryManagementProvider().viewPopDetail(
      popId: popId,
      onSuccess: (ResponseModel responseModel) {
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PopDetailRes responseData = PopDetailRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.data != null) {
                  // popDetailData = responseData.data;
                  customerDetail!.popName = responseData.data!.name;
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
        // getServiceAreaDetail();
      },
      onError: (ResponseModel error) async {
        // getServiceAreaDetail();
        _handleApiError(error);
      },
    );
  }

  getServiceAreaDetail() {
    isLoading = true;
    update();
    CustomerProvider().getServiceAreaDetail(
      id: serviceAreaId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ServiceAreaDetailRes responseData =
                  ServiceAreaDetailRes.fromJson(map);
              if ((responseData.responseCode != null && responseData.responseCode == 200) || (responseData.responseCode != null &&  responseData.responseCode == 0)) {
                serviceAreaDetail = responseData.data;
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
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  getAreaDetail(int? areaId, String type) {
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
                if (responseData.data != null) {

                  log("AddressDetailResponse==>${jsonEncode(responseData.data)}");
                  if (type.equalsIgnoreCase("Present")) {
                    presentAddress?.name = responseData.data?.name;
                    presentAddress?.cityName = responseData.data?.cityName;
                    presentAddress?.stateName = responseData.data?.stateName;
                    presentAddress?.countryName = responseData.data?.countryName;
                    // presentAddress?.subarea = responseData.data?.;
                    presentAddress?.code = responseData.data?.code;
                  }
                  if (type.equalsIgnoreCase("Payment")) {
                    paymentAddress?.name = responseData.data?.name;
                    paymentAddress?.cityName = responseData.data?.cityName;
                    paymentAddress?.stateName = responseData.data?.stateName;
                    paymentAddress?.countryName =
                        responseData.data?.countryName;
                    paymentAddress?.code = responseData.data?.code;
                  }

                  if (type.equalsIgnoreCase("Permanent")) {
                    permanentAddress?.name = responseData.data?.name;
                    permanentAddress?.cityName = responseData.data?.cityName;
                    permanentAddress?.stateName = responseData.data?.stateName;
                    permanentAddress?.countryName =
                        responseData.data?.countryName;
                    permanentAddress?.code = responseData.data?.code;
                  }
                }
                update();
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
        getBuildingSubAreaName();
      },
      onError: (ResponseModel error) {
        getBuildingSubAreaName();
        _handleApiError(error);
      },
    );
  }

  getCustomerWalletBal() {
    CustomerWalletReq request = CustomerWalletReq(custId: customerId);
    isLoading = true;
    update();
    CustomerProvider().getCustomerWalletBal(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerWalletRes responseData = CustomerWalletRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.customerWalletDetails != null) {
                  showCustomerBalDialog(
                      responseData.customerWalletDetails!.toString());
                } else {
                  showCustomerBalDialog("0");
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


  changeCustomerPassword(ChangeCustomerPasswordReq request) {
    isLoading = true;
    update();
    CustomerProvider().changeCustomerPassword(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if ((responseData.responseCode != null &&
                  responseData.responseCode == 200)) {
                if (responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.SUCCESS,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorGreen);
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

  getBuildingSubAreaName() {
    isLoading = true;
    update();
    CustomerProvider().getBuildingAndSubAreaName(
      customerId: customerId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BuildingSubAreaNameRes responseData = BuildingSubAreaNameRes.fromJson(map);
              if (responseData.status == 200|| responseData.responseCode == 200) {
                if (responseData.data != null) {
                  presentAddress?.subarea = responseData.data?.name;
                  presentAddress?.buildingName = responseData.data?.buildingName;
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

  showCustomerBalDialog(String bal) {
    showDialog(
      context: Get.context!,
      builder: (BuildContext context) {
        return AlertDialogHelper(
            title:
                "${customerDetail!.firstname!} ${customerDetail!.lastname!} ${Strings.wallet}",
            message: "Wallet Balance : $bal",
            positiveBtnText: Strings.ok,
            negativeBtnText: "",
            positiveBtnClick: () {
              Get.back();
            },
            negativeBtnClick: () {
              Get.back();
            });
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
