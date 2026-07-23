import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/response/address_detail_response.dart';
import 'package:savbill/pages/customer/model/response/plans_by_plan_group_id_res.dart';
import 'package:savbill/pages/customer_charge/charge_management_provider.dart';
import 'package:savbill/pages/customer_charge/response/add_charge_plan_detail.dart';
import 'package:savbill/pages/dashboard/model/response/final_amount_tax_res.dart';

import 'package:savbill/pages/dashboard/model/response/get_partner_detail_res.dart';
import 'package:savbill/pages/dashboard/model/response/show_tat_details_res.dart';

import 'package:savbill/pages/dashboard/model/response/ticket_followup_list_response.dart';
import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/pages/service_management/request/add_service_req.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_customer_details_update_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:intl/intl.dart';
import '../customer/model/response/postpaid_planlist_res.dart';

class TicketCustomerDetailController extends GetxController {
  bool isLoading = false;
  TicketDetail? ticketDetail;
  List<TicketAttachments>? attachmentList = [];
  List<FollowUpDetail>? followUpDetailList = [];
  List<CaseUpdateList>? caseUpdateList = [];
  List<PlanMappingList>? planMappingList =[];
  List<double>? finalAmountList = [];
  // List<WorkFlowAuditDataList>? workFlowAuditDataList = [];
  int customerId = 0;
  bool isShowLoadMore = false;
  int page = 1;

  Dio dio = Dio();
  GetStorage getStorage = GetStorage();
  String? progress = "0";
  String? customerBill;
  bool? custInvoiceToOrg;
  FlutterLocalNotificationsPlugin? flutterLocalNotificationsPlugin;
  BuildContext? context;
  ShowTATDetailsData? showTATDetailsData;
  TicketCustomersBasicDetail? customerDetail;
  GetPartnerlist? partnerList;
  AddressData? addressData;
  List<AddressList>? addressListData = [];
  bool? ifIndividualPlan = false, ifPlanGroup = false;

  List<PlansByPlanGroupIdPlanList>? plansByPlanGroupIdList = [];
  PlansByPlanGroupIdPlanList? selectPlanByPlanGroupList;
  int planDataLength = 0;
  int? planId;
  double? discount;
  List<PostpaidPlanDetail>? dataPlan = [];
  DateFormat apiDateFormat = DateFormat(Constant.DATE_FORMAT);
  @override
  void onInit() {
    super.onInit();
    getArgumentData();
    context = Get.key.currentContext;

    flutterLocalNotificationsPlugin = FlutterLocalNotificationsPlugin();
    const android = AndroidInitializationSettings('mipmap/ic_launcher');

    final ios = DarwinInitializationSettings(
        requestAlertPermission: true,
        requestBadgePermission: true,
        requestSoundPermission: true,
        // onDidReceiveLocalNotification: (int id, String? title, String? body, String? payload) async {}
    );
    final initSetting = InitializationSettings(android: android, iOS: ios);

    flutterLocalNotificationsPlugin!.initialize(initSetting);
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_DETAIL] != null) {
        customerId = arguments[Constant.CUSTOMER_DETAIL];
        getCustomerBasicDetail(customerId);
      }
    }
  }

  getCustomerBasicDetail(int customerId) {
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
              TicketCustomerDetailsUpdateRes responseData =
              TicketCustomerDetailsUpdateRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                customerDetail = responseData.customers;
                getPartnerDetail(customerDetail!.partnerid!);
                if (customerDetail!.addressList!.isNotEmpty) {
                  if (customerDetail!.addressList![0].addressType != null) {
                    getAreaDetail(customerDetail!.addressList![0].areaId);
                  }
                }



                addressListData = customerDetail!.addressList!
                    .where((element) =>
                        element.addressType!.equalsIgnoreCase("Payment"))
                    .cast<AddressList>()
                    .toList();

                if (customerDetail!.planMappingList!.isNotEmpty) {
                  customerBill = customerDetail!.planMappingList![0].billTo;
                  custInvoiceToOrg = customerDetail!.planMappingList![0].isInvoiceToOrg;
                }

                if(customerDetail!.plangroupid == true){
                  ifIndividualPlan = false;
                  ifPlanGroup = true;
                  getFindPlanGroupByIdData(customerDetail!.plangroupid);
                }else{
                  ifIndividualPlan = true;
                  ifPlanGroup = false;
                  planMappingList = customerDetail!.planMappingList;
                  while(planDataLength < customerDetail!.planMappingList!.length){
                    planId = customerDetail!.planMappingList![planDataLength].planId!;
                    if(customerDetail!.planMappingList![planDataLength].discount == null ||
                        customerDetail!.planMappingList![planDataLength].discount!.isNullOrEmpty()){
                      discount = 0.0;
                    }else{
                      discount = customerDetail!.planMappingList![planDataLength].discount;
                    }
                    // getPlanDetailFromPlanId(planId!);
                    planDataLength++;
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

  getPartnerDetail(int partnerId) {
    isLoading = true;
    update();
    CustomerProvider().getPartnerDetail(
      partnerId: partnerId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetPartnerDetailRes responseData =
                  GetPartnerDetailRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                partnerList = responseData.partnerlist;
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
        _handleApiPartnerError(error);
      },
    );
  }

  getAreaDetail(int? areaId) {
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
                  addressData = responseData.data;
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
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  Future<void> getPlanDetailFromPlanId(int customerPlanId) async {
    isLoading = true;
    update();
    ChargeManagementProvider().getCustomerPlanDetail(
      planId: customerPlanId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              AddChargePlanDetail responseData =
              AddChargePlanDetail.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.postPaidPlan != null) {
                  dataPlan?.add(responseData.postPaidPlan!);
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
        getOfferPriceWithTax(planId,discount);
        update();
        return true;
      },
      onError: (ResponseModel error) {
        getOfferPriceWithTax(planId,discount);
        _handleApiError(error);
        return true;
      },
    );
  }

  getOfferPriceWithTax(int? planId,double? discount) {
    isLoading = true;
    update();
    CustomerProvider().getOfferPriceWithTax(
      planId: planId,
      discount: discount,
      planGroupId: "",
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              FinalAmountTaxRes responseData =
              FinalAmountTaxRes.fromJson(map);
              if (responseData.result!.finalAmount != null) {
                finalAmountList!.add(responseData.result!.finalAmount!);
              } else {
                finalAmountList!.add(0.0);
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

  getFindPlanGroupByIdData(int? planGroupId) {
    isLoading = true;
    update();
    ChargeManagementProvider().getFindPlanGroupById(
      cusPlanGroupId: planGroupId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PlansByPlanGroupIdRes responseData =
              PlansByPlanGroupIdRes.fromJson(map);
              if (responseData.status != null && responseData.status == 200) {
                if (responseData.planList != null &&
                    responseData.planList!.isNotEmpty) {
                  plansByPlanGroupIdList!.addAll(responseData.planList!);
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

  _handleApiPartnerError(ResponseModel error) {
    isLoading = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } if (error.statusCode == 500) {
      Utils.showSnackbar(Strings.INFO, Strings.partner_service_up,
          AppTheme.colorWhite, AppTheme.colorBlueRView);
    }else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }


}
