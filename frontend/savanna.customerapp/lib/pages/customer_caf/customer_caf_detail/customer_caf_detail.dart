import 'package:savbill/pages/change_plan/change_plan.dart';
import 'package:savbill/pages/connection_history/connection_history.dart';
import 'package:savbill/pages/customer/change_password_dialog.dart';
import 'package:savbill/pages/customer/credit_note_customer/customer_view_credit_note.dart';
import 'package:savbill/pages/customer/customer_option_menu.dart';
import 'package:savbill/pages/customer/model/customer_detail_option.dart';
import 'package:savbill/pages/customer/model/request/change_customer_pwd_req.dart';
import 'package:savbill/pages/customer/model/response/cust_charge_details.dart';
import 'package:savbill/pages/customer/model/response/cust_mac_mappping_detail.dart';
import 'package:savbill/pages/customer/model/response/cust_plan_detail.dart';
import 'package:savbill/pages/customer/model/response/customer_quota_list_response.dart';
import 'package:savbill/pages/customer_caf/caf_customer_plan/caf_customer_plan.dart';
import 'package:savbill/pages/customer_caf/caf_notes/caf_notes_detail.dart';
import 'package:savbill/pages/customer_caf/caf_service_management/caf_service_managment_view.dart';
import 'package:savbill/pages/customer_caf/customer_caf_change_discount/caf_change_discount.dart';
import 'package:savbill/pages/customer_caf/customer_caf_detail/customer_caf_detail_controller.dart';
import 'package:savbill/pages/customer_caf/customer_caf_invoice/customer_caf_invoice.dart';
import 'package:savbill/pages/customer_caf/customer_caf_payment/customer_paymentlist.dart';
import 'package:savbill/pages/customer_caf/customer_change_plan/change_plan_caf_screen.dart';
import 'package:savbill/pages/customer_caf/followup/caf_follow_up/caf_follow_up.dart';
import 'package:savbill/pages/customer_change_status/change_status_list.dart';
import 'package:savbill/pages/customer_charge/charge_management.dart';
import 'package:savbill/pages/customer_inventory/inventory_detail.dart';
import 'package:savbill/pages/customer_ledger/customer_ledger.dart';
import 'package:savbill/pages/customer_payment/customer_paymentlist.dart';
import 'package:savbill/pages/customer_ticket/customer_ticket.dart';
import 'package:savbill/pages/revenue_report/cust_revenue_report.dart';
import 'package:savbill/pages/shift_location/shift_location.dart';
import 'package:savbill/pages/workflow/cust_dunning_detail.dart';
import 'package:savbill/pages/workflow/cust_notification_detail.dart';
import 'package:savbill/pages/workflow/customer_audit_detail.dart';
import 'package:savbill/pages/workflow/customer_workflow_audit.dart';
import 'package:savbill/routes/app_routes.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:get/get.dart';

class CustomerCafDetailScreen extends StatefulWidget {
  @override
  _CustomerDetailState createState() => _CustomerDetailState();
}

class _CustomerDetailState extends State<CustomerCafDetailScreen>
    implements ChangePasswordBtnAction {
  final customerCafDetailController = Get.put(CustomerCafDetailController());

  _backScreen() {
    if (customerCafDetailController.isCustApproval == true) {
      Get.offAllNamed(AppRoutes.CUSTOMER_CAF_LIST, arguments: {
        Constant.CUSTOMER_TYPE: Strings.prepaid,
      });
    } else {
      Get.back();
    }
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<CustomerCafDetailController>(builder: (controller) {
      return Stack(children: [
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: customerCafDetailController.isLoading),
      ]);
    });
  }

  _body() {
    return Container(
      width: MediaQuery.of(context).size.width,
      height: MediaQuery.of(context).size.height,
      margin: const EdgeInsets.only(
        top: Constant.SMALL_PADDING,
      ),
      color: AppTheme.colorBG,
      child: customerCafDetailController.customerDetail != null
          ? SingleChildScrollView(
              physics: const ScrollPhysics(),
              child: Column(
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    /*const SizedBox(
                      height: Constant.MEDIUM_PADDING,
                    ),*/
                    Container(
                      margin: const EdgeInsets.only(
                          top: Constant.SMALL_PADDING,
                          left: Constant.SCREEN_PADDING),
                      child: CustomText(
                        title: "Customer Summary & Option Menu",
                        fontSize: AppTheme.medium,
                        colors: AppTheme.title_dark,
                        textAlign: TextAlign.start,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    Container(
                      margin: const EdgeInsets.only(
                        top: Constant.SMALL_PADDING,
                      ),
                      height: Constant.TOP_MENU_OPTION,
                      child: ListView.builder(
                          scrollDirection: Axis.horizontal,
                          itemCount:
                              customerCafDetailController.optionList.length,
                          itemBuilder: (BuildContext context, int index) {
                            CustomerDetailOption detail =
                                customerCafDetailController.optionList[index];
                            return InkWell(
                                onTap: () {
                                  if (detail.id == 1) {
                                    // openPlanDetailScreen();
                                    openWorkFlowAuditScreen();
                                  } else if (detail.id == 2) {
                                    openPlanDetailScreen();
                                    // openInvoiceDetailScreen();
                                  } else if (detail.id == 3) {
                                    // openLedgerDetailScreen();
                                    openInvoiceDetailScreen();
                                  }
                                  /*else if (detail.id == 4) {
                                    openLedgerDetailScreen();
                                  }else if (detail.id == 5) {
                                    openPaymentListScreen();
                                  } else if (detail.id == 5) {
                                    openCustomerHistoryScreen();
                                  } else if (detail.id == 6) {
                                    openCustomerTicketsScreen();
                                  } else if (detail.id == 7) {
                                    openCustomerInventoryScreen();
                                  } else if (detail.id == 8) {
                                    // openChangePlanScreen();

                                    Utils.showSnackbar(
                                        Strings.SUCCESS,
                                        Strings.under_development,
                                        AppTheme.colorWhite,
                                        AppTheme.colorGreen);
                                  } */
                                  else if (detail.id == 4) {
                                    openPaymentListScreen();
                                  } else if (detail.id == 5) {
                                    openCustomerInventoryScreen();
                                  } else if (detail.id == 6) {
                                    openChangeDiscountScreen();
                                  }
                                  /*else if (detail.id == 8) {
                                    // changePasswordDialog(detail);
                                    customerCafDetailController
                                        .getCustomerWalletBal();
                                  }*/
                                  else if (detail.id == 7) {
                                    openChargeManagementScreen();
                                    // openChangeStatusScreen();
                                  }
                                  /*else if (detail.id == 10) {
                                    openShiftLocationScreen();
                                  } */
                                  else if (detail.id == 8) {
                                    openFollowUpScreen();
                                    // openShiftLocationScreen();
                                  } else if (detail.id == 9) {
                                    openServiceManagementScreen();
                                    // openCustomerCreditNoteScreen();
                                  } else if (detail.id == 10) {
                                    openChangePlanScreen();
                                    // Utils.showSnackbar(
                                    //     Strings.SUCCESS,
                                    //     Strings.under_development,
                                    //     AppTheme.colorWhite,
                                    //     AppTheme.colorGreen);
                                  } else if (detail.id == 11) {
                                    openCustomerNotesScreen();
                                  }else if (detail.id == 12){
                                    openShiftLocationScreen();
                                  }
                                },
                                child: Container(
                                  margin: EdgeInsets.only(
                                      left: index == 0
                                          ? Constant.SCREEN_PADDING
                                          : Constant.SMALL_PADDING,
                                      right: index ==
                                              customerCafDetailController
                                                      .optionList.length -
                                                  1
                                          ? Constant.SCREEN_PADDING
                                          : Constant.SMALL_PADDING,
                                      bottom: 2),
                                  child: CustomerOptionItemView(
                                    detail: detail,
                                    index: index,
                                  ),
                                ));
                          }),
                    ),
                    const SizedBox(
                      height: Constant.MEDIUM_PADDING,
                    ),
                    Container(
                      margin: const EdgeInsets.only(
                          top: Constant.SMALL_PADDING,
                          left: Constant.SCREEN_PADDING),
                      child: CustomText(
                        title:
                            "${customerCafDetailController.customerDetail?.title ?? ""} ${customerCafDetailController.customerDetail?.firstname ?? ""} ${customerCafDetailController.customerDetail?.lastname ?? " "}",
                        fontSize: AppTheme.medium,
                        colors: AppTheme.title_dark,
                        textAlign: TextAlign.start,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    basicDetailView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    serviceAreaDetails(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    networkDetailView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    /*subscriberLocationDetailView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    businessPartnerDetailView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    paymentDetailView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    presentAddressDetailView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    paymentAddressDetailView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    permanentAddressDetailView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    planDetailView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    additionalServiceDetailView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    chargeDetailView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    macMapppingListView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    customerQuotaDetailsView(),
                    const SizedBox(
                      height: Constant.MEDIUM_PADDING,
                    ),*/
                  ]),
            )
          : noDataFound(),
    );
  }

  changePasswordDialog(CustomerDetailOption detail) {
    ChangeCustomerPasswordReq request = ChangeCustomerPasswordReq(
        custId: customerCafDetailController.customerId, remarks: "");
    showDialog(
        context: context,
        barrierDismissible: false,
        builder: (BuildContext context) {
          return ChangePasswordDialog(
            changePasswordBtnAction: this,
            changeCustomerPasswordReq: request,
          );
        });
  }

  openShiftLocationScreen() async {
    var result = await Get.to(ShiftLocation(), arguments: {
      Constant.CUSTOMER_DETAIL: customerCafDetailController.customerDetail,
      Constant.CUSTOMER_TYPE: customerCafDetailController.customerType,
      Constant.CUST_TYPE: "CUSTOMER_CAF",
    });

    if (result != null && result == true) {
      customerCafDetailController.getCustomerDetail();
    }
  }

  openFollowUpScreen() async {
    var result = await Get.to(CafFollowUp(), arguments: {
      Constant.CUSTOMER_DETAIL: customerCafDetailController.customerDetail,
      Constant.CUSTOMER_TYPE: customerCafDetailController.customerType,
    });

    if (result != null && result == true) {
      customerCafDetailController.getCustomerDetail();
    }
  }

  openServiceManagementScreen() async {
    var result = await Get.to(CafServiceManagementView(), arguments: {
      Constant.CUSTOMER_DETAIL: customerCafDetailController.customerDetail,
      Constant.CUSTOMER_TYPE: customerCafDetailController.customerType,
    });

    if (result != null && result == true) {
      customerCafDetailController.getCustomerDetail();
    }
  }

  openWorkFlowAuditScreen() async {
    var result = await Get.to(CustomerWorkFlowAudit(), arguments: {
      Constant.CUSTOMER_DETAIL: customerCafDetailController.customerDetail,
      Constant.CUSTOMER_TYPE: customerCafDetailController.customerType,
    });
    if (result != null && result == true) {
      customerCafDetailController.getCustomerDetail();
    }
  }

  openAuditDetailScreen() async {
    var result = await Get.to(CustomerAuditDetail(), arguments: {
      Constant.CUSTOMER_DETAIL: customerCafDetailController.customerDetail,
      Constant.CUSTOMER_TYPE: customerCafDetailController.customerType,
    });
    if (result != null && result == true) {
      customerCafDetailController.getCustomerDetail();
    }
  }

  openDunningDetailScreen() async {
    var result = await Get.to(CustomerDunningDetail(), arguments: {
      Constant.CUSTOMER_DETAIL: customerCafDetailController.customerDetail,
      Constant.CUSTOMER_TYPE: customerCafDetailController.customerType,
    });
    if (result != null && result == true) {
      customerCafDetailController.getCustomerDetail();
    }
  }

  openRevenueReportScreen() async {
    String name =
        "${customerCafDetailController.customerDetail!.title ?? ""} ${customerCafDetailController.customerDetail!.firstname ?? ""} ${customerCafDetailController.customerDetail!.lastname ?? ""}"; //firstname: 223344557, lastname
    var result = await Get.to(CustRevenueReport(), arguments: {
      Constant.CUSTOMER_ID: customerCafDetailController.customerDetail?.id,
      Constant.CUSTOMER_TYPE: customerCafDetailController.customerType,
      Constant.CUST_USERNAME:
          customerCafDetailController.customerDetail?.username,
      Constant.MV_ID: customerCafDetailController.customerDetail?.mvnoId,
      Constant.CUSTOMER_NAME: name
    });
  }

  openNotificationDetailScreen() async {
    var result = await Get.to(CustomerNotificationDetail(), arguments: {
      Constant.CUSTOMER_DETAIL: customerCafDetailController.customerDetail,
      Constant.CUSTOMER_TYPE: customerCafDetailController.customerType,
    });
    if (result != null && result == true) {
      customerCafDetailController.getCustomerDetail();
    }
  }

  openChargeManagementScreen() async {
    String name =
        "${customerCafDetailController.customerDetail!.title ?? ""} ${customerCafDetailController.customerDetail!.firstname ?? ""} ${customerCafDetailController.customerDetail!.lastname ?? ""}"; //firstname: 223344557, lastname
    var result = await Get.to(ChargeManagement(), arguments: {
      Constant.CUSTOMER_DETAIL: customerCafDetailController.customerDetail,
      Constant.CUSTOMER_TYPE: customerCafDetailController.customerType,
      Constant.CUSTOMER_PLAN_GRP_ID:
          customerCafDetailController.customerDetail!.planGroupId,
      Constant.CUSTOMER_NAME: name,
      Constant.CUSTOMER_PLAN_MAP:
          (customerCafDetailController.customerDetail!.planMappingList !=
                      null &&
                  customerCafDetailController
                      .customerDetail!.planMappingList!.isNotEmpty)
              ? customerCafDetailController.customerDetail!.planMappingList
              : []
    });
  }

  openCustomerCreditNoteScreen() async {
    String name =
        "${customerCafDetailController.customerDetail!.title ?? ""} ${customerCafDetailController.customerDetail!.firstname ?? ""} ${customerCafDetailController.customerDetail!.lastname ?? ""}"; //firstname: 223344557, lastname
    var result = await Get.to(CustomerViewCreditNote(), arguments: {
      Constant.CUSTOMER_DETAIL: customerCafDetailController.customerDetail,
      Constant.CUSTOMER_TYPE: customerCafDetailController.customerType,
    });
  }

  openChangeStatusScreen() async {
    String name =
        "${customerCafDetailController.customerDetail!.title ?? ""} ${customerCafDetailController.customerDetail!.custname ?? ""}"; //firstname: 223344557, lastname
    var result = await Get.to(CustomerChangeStatus(), arguments: {
      Constant.CUSTOMER_ID: customerCafDetailController.customerDetail?.id,
      Constant.CUSTOMER_TYPE: customerCafDetailController.customerType,
      Constant.CUSTOMER_NAME: name
    });
  }

  openChangeDiscountScreen() async {
    String name =
        "${customerCafDetailController.customerDetail!.title ?? ""} ${customerCafDetailController.customerDetail!.firstname ?? ""} ${customerCafDetailController.customerDetail!.lastname ?? ""}"; //firstname: 223344557, lastname
    var result = await Get.to(CustomerCafChangeDiscount(), arguments: {
      Constant.CUSTOMER_ID: customerCafDetailController.customerDetail?.id,
      Constant.CUSTOMER_TYPE: customerCafDetailController.customerType,
      Constant.CUSTOMER_NAME: name
    });
  }

  openChangePlanScreen() async {
    String name =
        "${customerCafDetailController.customerDetail!.title ?? ""} ${customerCafDetailController.customerDetail!.firstname ?? ""} ${customerCafDetailController.customerDetail!.lastname ?? ""}"; //firstname: 223344557, lastname
    // var result = await Get.to(ChangePlan(), arguments: {
    var result = await Get.to(ChangePlanCafScreen(), arguments: {
      Constant.CUSTOMER_DETAIL: customerCafDetailController.customerDetail!,
      // Constant.CUSTOMER_TYPE: customerCafDetailController.customerType,
      Constant.CUSTOMER_NAME: name,
      Constant.CUSTOMER_PLAN_GRP_ID:
          customerCafDetailController.customerDetail!.planGroupId,
      Constant.SERVICE_AREA_ID:
          customerCafDetailController.customerDetail!.serviceAreaId,
      Constant.DISCOUNT: customerCafDetailController.customerDetail!.discount,
      Constant.CUSTOMER_TYPE:
          customerCafDetailController.customerDetail!.custtype,
      Constant.CUSTOMER_PLAN_MAP:
          (customerCafDetailController.customerDetail!.planMappingList !=
                      null &&
                  customerCafDetailController
                      .customerDetail!.planMappingList!.isNotEmpty)
              ? customerCafDetailController.customerDetail!.planMappingList
              : []
    });
  }

  openCustomerNotesScreen() async {
    var result = await Get.to(CafNotesDetail(), arguments: {
      Constant.CUSTOMER_DETAIL: customerCafDetailController.customerDetail,
      Constant.CUSTOMER_TYPE: customerCafDetailController.customerType,
    });
    if (result != null && result == true) {
      customerCafDetailController.getCustomerDetail();
    }
  }

  openCustomerInventoryScreen() async {
    String name =
        "${customerCafDetailController.customerDetail!.title ?? ""} ${customerCafDetailController.customerDetail!.firstname ?? ""} ${customerCafDetailController.customerDetail!.lastname ?? ""}"; //firstname: 223344557, lastname
    var result = await Get.to(() => CustomerInventoryDetail(), arguments: {
      Constant.CUSTOMER_ID: customerCafDetailController.customerDetail?.id,
      Constant.CUSTOMER_TYPE: customerCafDetailController.customerType,
      Constant.CUSTOMER_NAME: name,
      Constant.SERVICE_AREA_ID: customerCafDetailController.serviceAreaId,
      Constant.CUSTOMER_TYPE: customerCafDetailController.customerType
    });
  }

  openCustomerTicketsScreen() async {
    String name =
        "${customerCafDetailController.customerDetail!.title ?? ""} ${customerCafDetailController.customerDetail!.firstname ?? ""} ${customerCafDetailController.customerDetail!.lastname ?? ""}"; //firstname: 223344557, lastname
    var result = await Get.to(CustomerTicketDetail(), arguments: {
      Constant.CUSTOMER_ID: customerCafDetailController.customerDetail?.id,
      Constant.CUSTOMER_TYPE: customerCafDetailController.customerType,
      Constant.CUSTOMER_DETAIL: customerCafDetailController.customerDetail,
      Constant.CUSTOMER_NAME: name
    });
  }

  openCustomerHistoryScreen() async {
    String name =
        "${customerCafDetailController.customerDetail!.title ?? ""} ${customerCafDetailController.customerDetail!.firstname ?? ""} ${customerCafDetailController.customerDetail!.lastname ?? ""}"; //firstname: 223344557, lastname
    var result = await Get.to(ConnectionHistory(), arguments: {
      Constant.CUSTOMER_ID: customerCafDetailController.customerDetail?.id,
      Constant.CUSTOMER_TYPE: customerCafDetailController.customerType,
      Constant.CUST_USERNAME:
          customerCafDetailController.customerDetail?.username,
      Constant.MV_ID: customerCafDetailController.customerDetail?.mvnoId,
      Constant.CUSTOMER_NAME: name
    });
  }

  openPaymentListScreen() async {
    String name =
        "${customerCafDetailController.customerDetail!.title ?? ""} ${customerCafDetailController.customerDetail!.firstname ?? ""} ${customerCafDetailController.customerDetail!.lastname ?? ""}"; //firstname: 223344557, lastname
    var result = await Get.to(CustomerCAFPaymentList(), arguments: {
      Constant.CUSTOMER_ID: customerCafDetailController.customerDetail?.id,
      Constant.CUSTOMER_TYPE: customerCafDetailController.customerType,
      Constant.CUSTOMER_NAME: name
    });
  }

  openLedgerDetailScreen() async {
    String name =
        "${customerCafDetailController.customerDetail!.title ?? ""} ${customerCafDetailController.customerDetail!.firstname ?? ""} ${customerCafDetailController.customerDetail!.lastname ?? ""}"; //firstname: 223344557, lastname
    var result = await Get.to(CustomerLedgerDetail(), arguments: {
      Constant.CUSTOMER_ID: customerCafDetailController.customerDetail?.id,
      Constant.CUSTOMER_TYPE: customerCafDetailController.customerType,
      Constant.CUSTOMER_NAME: name
    });
  }

  openInvoiceDetailScreen() async {
    String name =
        "${customerCafDetailController.customerDetail!.title ?? ""} ${customerCafDetailController.customerDetail!.firstname ?? ""} ${customerCafDetailController.customerDetail!.lastname ?? ""}"; //firstname: 223344557, lastname
    var result = await Get.to(CustomerCafInvoiceDetail(), arguments: {
      Constant.CUSTOMER_DETAIL: customerCafDetailController.customerDetail,
      Constant.CUSTOMER_TYPE: customerCafDetailController.customerType,
      Constant.CUSTOMER_ID: customerCafDetailController.customerDetail?.id,
      Constant.CUSTOMER_AREA_DETAIAL: customerCafDetailController.presentAddress,
      Constant.CUSTOMER_NAME: name
    });
  }

  openPlanDetailScreen() async {
    String name =
        "${customerCafDetailController.customerDetail!.title ?? ""} ${customerCafDetailController.customerDetail!.firstname ?? ""} ${customerCafDetailController.customerDetail!.lastname ?? ""}"; //firstname: 223344557, lastname
    var result = await Get.to(CustomerCafPlanDetail(), arguments: {
      Constant.CUSTOMER_ID: customerCafDetailController.customerDetail?.id,
      Constant.CUSTOMER_TYPE: customerCafDetailController.customerType,
      Constant.CUSTOMER_NAME: name,
      Constant.CUSTOMER_PLAN: customerCafDetailController.customerDetail!,
    });
  }

  basicDetailView() {
    String? name = "";
    if (customerCafDetailController.customerDetail!.title != null) {
      name = (customerCafDetailController.customerDetail?.title ?? "");
    }
    if (customerCafDetailController.customerDetail!.firstname != null) {
      name =
          ("$name ${customerCafDetailController.customerDetail?.firstname ?? ""}");
    }
    if (customerCafDetailController.customerDetail!.lastname != null) {
      name =
          ("$name ${customerCafDetailController.customerDetail?.lastname ?? ""}");
    }
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.basic_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: true,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.basic_details,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          shape: const Border(),
          onExpansionChanged: ((newState) {}),
          children: <Widget>[
            Container(
              width: Get.width,
              height: 1.5,
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(6),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.grey.withOpacity(0.4),
                      blurRadius: 1.5,
                      spreadRadius: 1.5,
                    ),
                  ]),
            ),
            Padding(
              padding: const EdgeInsets.only(
                  top: Constant.EXPANTABLE_ITEM_MARGIN,
                  left: Constant.EXPANTABLE_ITEM_MARGIN,
                  right: Constant.EXPANTABLE_ITEM_MARGIN,
                  bottom: 0),
              child: Container(
                alignment: Alignment.topLeft,
                padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      basicDetailItem(
                          Strings.name,
                          name,
                          Strings.contact_person,
                          customerCafDetailController
                                  .customerDetail?.contactperson ??
                              "-"),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                          Strings.caf_no,
                          customerCafDetailController.customerDetail?.cafno ??
                              "-",
                          Strings.account_number,
                          customerCafDetailController.customerDetail?.acctno ??
                              "-"),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                          Strings.username,
                          customerCafDetailController
                                  .customerDetail?.username ??
                              "-",
                          Strings.status,
                          (customerCafDetailController.customerDetail?.status ==
                                      "Ingrace" ||
                                  customerCafDetailController
                                          .customerDetail?.status ==
                                      "INGRACE")
                              ? "InGrace"
                              : customerCafDetailController
                                  .customerDetail?.status),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.customer_type,
                          customerCafDetailController
                                  .customerDetail?.custtype ??
                              "-",
                          Strings.calendar_type,
                          customerCafDetailController
                                  .customerDetail?.calendarType ??
                              "-"),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                          Strings.bill_day,
                          customerCafDetailController.customerDetail?.billday
                                  ?.toString() ??
                              "-",
                          Strings.next_bill_date,
                          customerCafDetailController
                                  .customerDetail!.nextBillDate ??
                              "-"),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                        Strings.first_activation_date,
                        customerCafDetailController
                                .customerDetail?.firstActivationDate ??
                            "-",
                        "${Strings.primary_mobile_number} (${customerCafDetailController.customerDetail?.countryCode})",
                        customerCafDetailController.customerDetail?.mobile ??
                            "-",
                      ),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                        Strings.secondary_mobile_number,
                        customerCafDetailController.customerDetail?.altmobile ??
                            "-",
                        Strings.tel_phone,
                        customerCafDetailController.customerDetail?.phone ??
                            "-",
                      ),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                          Strings.fax_number,
                          customerCafDetailController.customerDetail?.fax ??
                              "-",
                          Strings.address,
                          customerCafDetailController
                                  .customerDetail!.addressList!.isNotEmpty
                              ? customerCafDetailController
                                  .customerDetail?.addressList![0].landmark
                              : ""),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                        Strings.email,
                        customerCafDetailController.customerDetail?.email ??
                            "-",
                        "${Strings.bill_to} ${Strings.name}",
                        customerCafDetailController
                                .customerDetail!.planMappingList!.isNotEmpty
                            ? customerCafDetailController.customerDetail!
                                    .planMappingList![0].billTo ??
                                "-"
                            : "",
                      ),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                        "${Strings.bill_to} ${Strings.address}",
                        "-",
                        Strings.pan_no,
                        customerCafDetailController.customerDetail!.pan ?? "-",
                      ),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                        Strings.activated_by,
                        customerCafDetailController
                                .customerDetail!.dunningActivateFor ??
                            "-",
                        Strings.account_status,
                        ""
                        "-",
                      ),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                        Strings.customer_new_category,
                        customerCafDetailController
                                .customerDetail!.custcategory ??
                            "-",
                        Strings.customer_sub_type,
                        customerCafDetailController
                                .customerDetail!.customerSubType ??
                            "-",
                      ),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                        Strings.customer_sector,
                        customerCafDetailController
                                .customerDetail!.customerSector ??
                            "-",
                        Strings.customer_sub_sector,
                        customerCafDetailController
                                .customerDetail!.customerSubSector ??
                            "-",
                      ),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                        Strings.ezybill_id,
                        "" ?? "-",
                        Strings.dob,
                        "" ?? "-",
                      ),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                        Strings.dunning_enable,
                        "${customerCafDetailController.customerDetail!.isDunningEnable}" ??
                            "-",
                        Strings.automatic_notify,
                        "${customerCafDetailController.customerDetail!.isNotificationEnable}" ??
                            "-",
                      ),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                        Strings.disable_self_care,
                        "" ?? "-",
                        Strings.disable_renew,
                        "",
                      ),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                        Strings.create_by,
                        "${customerCafDetailController.customerDetail?.createdByName}" ??
                            "",
                        Strings.unit_no,
                        "${customerCafDetailController.customerDetail?.blockNo}" ??
                            "",
                      ),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING),
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }

  basicDetailItem(
      String title1, String? value1, String title2, String? value2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisAlignment: MainAxisAlignment.start,
      children: [
        Expanded(
          flex: 3,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title1),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value1),
            ],
          ),
        ),
        Expanded(
          flex: 2,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title2),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value2),
            ],
          ),
        ),
      ],
    );
  }

  serviceAreaDetails() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.service_area),
          maintainState: true,
          shape: const Border(),
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: false,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: "${Strings.service_area} ${Strings.details}",
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          children: <Widget>[
            Container(
              width: Get.width,
              height: 1.5,
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(6),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.grey.withOpacity(0.4),
                      blurRadius: 1.5,
                      spreadRadius: 1.5,
                    ),
                  ]),
            ),
            Padding(
              padding: const EdgeInsets.only(
                  top: Constant.EXPANTABLE_ITEM_MARGIN,
                  left: Constant.EXPANTABLE_ITEM_MARGIN,
                  right: Constant.EXPANTABLE_ITEM_MARGIN,
                  bottom: 0),
              child: Container(
                alignment: Alignment.topLeft,
                padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      basicDetailItem(
                          "${Strings.service_area} ${Strings.name}",
                          customerCafDetailController
                                  .customerDetail!.serviceareaName ??
                              "-",
                          "${Strings.branch} ${Strings.name} / ${Strings.partner}",
                          customerCafDetailController
                                  .customerDetail!.branchName ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.region,
                          customerCafDetailController.customerDetail?.aadhar ??
                              "-",
                          Strings.business_vertical,
                          customerCafDetailController
                                  .customerDetail?.passportNo ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                        Strings.municipality,
                        customerCafDetailController.presentAddress?.code ?? "-",
                        Strings.area,
                        customerCafDetailController.presentAddress?.name ?? "-",
                      ),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.city,
                          (customerCafDetailController.presentAddress != null &&
                                  customerCafDetailController
                                          .presentAddress!.cityName !=
                                      null)
                              ? customerCafDetailController
                                  .presentAddress!.cityName
                              : "-",
                          Strings.state,
                          (customerCafDetailController.presentAddress != null &&
                                  customerCafDetailController
                                          .presentAddress!.stateName !=
                                      null)
                              ? customerCafDetailController
                                  .presentAddress!.stateName
                              : "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.sub_area,

                          customerCafDetailController
                              .presentAddress!.subarea ?? "-",
                          // (customerCafDetailController.presentAddress != null &&
                          //         customerCafDetailController
                          //                 .presentAddress!.subarea !=
                          //             null)
                          //     ? customerCafDetailController
                          //         .presentAddress!.subarea
                          //     : "-",
                          Strings.building_name,
                          (customerCafDetailController.presentAddress != null &&
                                  customerCafDetailController
                                          .presentAddress!.buildingName !=
                                      null)
                              ? customerCafDetailController
                                  .presentAddress!.buildingName
                              : "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.building_no,
                          (customerCafDetailController.presentAddress != null &&
                                  customerCafDetailController
                                          .presentAddress!.buildingNumber !=
                                      null)
                              ? customerCafDetailController
                                  .presentAddress!.buildingNumber
                              : "-",
                          "-",
                          "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                        Strings.landmark,
                        customerCafDetailController.presentAddress != null
                            ? customerCafDetailController
                                .presentAddress!.landmark
                            : "-",
                        Strings.valley_type,
                        customerCafDetailController
                                .customerDetail!.valleyType ??
                            "-",
                      ),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                        Strings.latitude,
                        customerCafDetailController.customerDetail!.latitude ??
                            "-",
                        Strings.longitude,
                        customerCafDetailController.customerDetail!.longitude ??
                            "-",
                      ),
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }

  networkDetailView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.contact_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: false,
          shape: const Border(),
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.network_location_details,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          children: <Widget>[
            Container(
              width: Get.width,
              height: 1.5,
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(6),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.grey.withOpacity(0.4),
                      blurRadius: 1.5,
                      spreadRadius: 1.5,
                    ),
                  ]),
            ),
            Padding(
              padding: const EdgeInsets.only(
                  top: Constant.EXPANTABLE_ITEM_MARGIN,
                  left: Constant.EXPANTABLE_ITEM_MARGIN,
                  right: Constant.EXPANTABLE_ITEM_MARGIN,
                  bottom: 0),
              child: Container(
                alignment: Alignment.topLeft,
                padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      basicDetailItem(
                          Strings.pop,
                          customerCafDetailController.customerDetail!.popName ??
                              "-",
                          Strings.olt,
                          customerCafDetailController.customerDetail!.oltName ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.card_number,
                          "-",
                          Strings.master_db,
                          customerCafDetailController
                                  .customerDetail!.masterdbName ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.splitter_db,
                          customerCafDetailController
                                  .customerDetail!.splitterName ??
                              "-",
                          Strings.mac_address,
                          customerCafDetailController
                                  .customerDetail!.macaddress ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(Strings.onu_serial_number, "-",
                          Strings.external_onu_seraial, "-" ?? "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.static_ip,
                          customerCafDetailController
                                  .customerDetail!.framedIp ??
                              "-",
                          Strings.nas_ip,
                          customerCafDetailController.customerDetail!.nasPort ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.nas_port,
                          customerCafDetailController.customerDetail!.nasPort ??
                              "-",
                          Strings.max_current_session,
                          "-" ?? ""),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(Strings.network_profile, "-", "-", "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }

  subscriberLocationDetailView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.subscriber_location_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: false,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.subscriber_location_details,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          children: <Widget>[
            Container(
              width: Get.width,
              height: 1.5,
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(6),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.grey.withOpacity(0.4),
                      blurRadius: 1.5,
                      spreadRadius: 1.5,
                    ),
                  ]),
            ),
            Padding(
              padding: const EdgeInsets.only(
                  top: Constant.EXPANTABLE_ITEM_MARGIN,
                  left: Constant.EXPANTABLE_ITEM_MARGIN,
                  right: Constant.EXPANTABLE_ITEM_MARGIN,
                  bottom: 0),
              child: Container(
                alignment: Alignment.topLeft,
                padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      basicDetailItem(
                          // Strings.service_area,
                          Strings.hierarchy,
                          customerCafDetailController
                                  .customerDetail!.serviceareaName ??
                              "-",
                          Strings.latitude,
                          customerCafDetailController
                                  .customerDetail!.latitude ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.longitude,
                          customerCafDetailController
                                  .customerDetail!.longitude ??
                              "-",
                          "-",
                          "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }

  businessPartnerDetailView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.business_partner_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: false,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.business_partner_details,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          children: <Widget>[
            Container(
              width: Get.width,
              height: 1.5,
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(6),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.grey.withOpacity(0.4),
                      blurRadius: 1.5,
                      spreadRadius: 1.5,
                    ),
                  ]),
            ),
            Padding(
              padding: const EdgeInsets.only(
                  top: Constant.EXPANTABLE_ITEM_MARGIN,
                  left: Constant.EXPANTABLE_ITEM_MARGIN,
                  right: Constant.EXPANTABLE_ITEM_MARGIN,
                  bottom: 0),
              child: Container(
                alignment: Alignment.topLeft,
                padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      basicDetailItem(
                          Strings.partner,
                          customerCafDetailController
                                  .customerDetail!.partnerName ??
                              "-",
                          Strings.sales_mark,
                          customerCafDetailController
                                  .customerDetail!.salesremark ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }

  paymentDetailView() {
    String? amt;

    if (customerCafDetailController.paymentDetails?.amount != null) {
      amt = customerCafDetailController.paymentDetails?.amount.toString();
    }

    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.payment_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: false,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.payment_details,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          children: <Widget>[
            Container(
              width: Get.width,
              height: 1.5,
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(6),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.grey.withOpacity(0.4),
                      blurRadius: 1.5,
                      spreadRadius: 1.5,
                    ),
                  ]),
            ),
            Padding(
              padding: const EdgeInsets.only(
                  top: Constant.EXPANTABLE_ITEM_MARGIN,
                  left: Constant.EXPANTABLE_ITEM_MARGIN,
                  right: Constant.EXPANTABLE_ITEM_MARGIN,
                  bottom: 0),
              child: Container(
                alignment: Alignment.topLeft,
                padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      basicDetailItem(
                        Strings.amount,
                        amt ?? "-",
                        Strings.reference_no,
                        customerCafDetailController
                                .paymentDetails?.paymentreferenceno ??
                            "-",
                      ),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.payment_date,
                          customerCafDetailController
                                  .paymentDetails?.paymentdate ??
                              "-",
                          Strings.payment_mode,
                          customerCafDetailController.paymentDetails?.paymode ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }

  paymentAddressDetailView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.payment_address_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: false,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.payment_address_details,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          children: <Widget>[
            Container(
              width: Get.width,
              height: 1.5,
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(6),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.grey.withOpacity(0.4),
                      blurRadius: 1.5,
                      spreadRadius: 1.5,
                    ),
                  ]),
            ),
            Padding(
              padding: const EdgeInsets.only(
                  top: Constant.EXPANTABLE_ITEM_MARGIN,
                  left: Constant.EXPANTABLE_ITEM_MARGIN,
                  right: Constant.EXPANTABLE_ITEM_MARGIN,
                  bottom: 0),
              child: Container(
                alignment: Alignment.topLeft,
                padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      basicDetailItem(
                          Strings.address,
                          customerCafDetailController
                                  .paymentAddress?.fullAddress ??
                              "-",
                          Strings.pincode,
                          customerCafDetailController.paymentAddress?.code ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.area,
                          customerCafDetailController.paymentAddress?.name ??
                              "-",
                          Strings.city,
                          customerCafDetailController
                                  .paymentAddress?.cityName ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.state,
                          customerCafDetailController
                                  .paymentAddress?.stateName ??
                              "-",
                          Strings.country,
                          customerCafDetailController
                                  .paymentAddress?.countryName ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }

  permanentAddressDetailView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.permanent_address_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: false,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.permanent_address_details,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          children: <Widget>[
            Container(
              width: Get.width,
              height: 1.5,
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(6),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.grey.withOpacity(0.4),
                      blurRadius: 1.5,
                      spreadRadius: 1.5,
                    ),
                  ]),
            ),
            Padding(
              padding: const EdgeInsets.only(
                  top: Constant.EXPANTABLE_ITEM_MARGIN,
                  left: Constant.EXPANTABLE_ITEM_MARGIN,
                  right: Constant.EXPANTABLE_ITEM_MARGIN,
                  bottom: 0),
              child: Container(
                alignment: Alignment.topLeft,
                padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      basicDetailItem(
                          Strings.address,
                          customerCafDetailController
                                  .permanentAddress?.fullAddress ??
                              "-",
                          Strings.pincode,
                          customerCafDetailController.permanentAddress?.code ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.area,
                          customerCafDetailController.permanentAddress?.name ??
                              "-",
                          Strings.city,
                          customerCafDetailController
                                  .permanentAddress?.cityName ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.state,
                          customerCafDetailController
                                  .permanentAddress?.stateName ??
                              "-",
                          Strings.country,
                          customerCafDetailController
                                  .permanentAddress?.countryName ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }

  presentAddressDetailView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.present_address_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: false,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.present_address_details,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          children: <Widget>[
            Container(
              width: Get.width,
              height: 1.5,
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(6),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.grey.withOpacity(0.4),
                      blurRadius: 1.5,
                      spreadRadius: 1.5,
                    ),
                  ]),
            ),
            Padding(
              padding: const EdgeInsets.only(
                  top: Constant.EXPANTABLE_ITEM_MARGIN,
                  left: Constant.EXPANTABLE_ITEM_MARGIN,
                  right: Constant.EXPANTABLE_ITEM_MARGIN,
                  bottom: 0),
              child: Container(
                alignment: Alignment.topLeft,
                padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      basicDetailItem(
                          Strings.address,
                          customerCafDetailController
                                  .presentAddress?.fullAddress ??
                              "-",
                          Strings.pincode,
                          customerCafDetailController.presentAddress?.code ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.area,
                          customerCafDetailController.presentAddress?.name ??
                              "-",
                          Strings.city,
                          customerCafDetailController
                                  .presentAddress?.cityName ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.state,
                          customerCafDetailController
                                  .presentAddress?.stateName ??
                              "-",
                          Strings.country,
                          customerCafDetailController
                                  .presentAddress?.countryName ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }

  planDetailView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.plan_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: false,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.plan_details,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          children: <Widget>[
            (customerCafDetailController.planMappingList != null &&
                    customerCafDetailController.planMappingList!.isNotEmpty)
                ? Container(
                    width: Get.width,
                    height: 1.5,
                    decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(6),
                        boxShadow: [
                          BoxShadow(
                            color: Colors.grey.withOpacity(0.4),
                            blurRadius: 1.5,
                            spreadRadius: 1.5,
                          ),
                        ]),
                  )
                : Container(),
            (customerCafDetailController.planMappingList != null &&
                    customerCafDetailController.planMappingList!.isNotEmpty)
                ? ListView.builder(
                    physics: const NeverScrollableScrollPhysics(),
                    scrollDirection: Axis.vertical,
                    shrinkWrap: true,
                    itemCount:
                        customerCafDetailController.planMappingList?.length,
                    itemBuilder: (context, ii) {
                      PlanMappingDetail? items =
                          customerCafDetailController.planMappingList![ii];
                      int? lstLength =
                          customerCafDetailController.planMappingList?.length;
                      return Padding(
                        padding: EdgeInsets.only(
                            top: (ii == 0)
                                ? Constant.SMALL_PADDING
                                : Constant.EXPANTABLE_ITEM_MARGIN,
                            left: Constant.EXPANTABLE_ITEM_MARGIN,
                            right: Constant.EXPANTABLE_ITEM_MARGIN,
                            bottom: (ii == (lstLength! - 1))
                                ? Constant.EXPANTABLE_ITEM_MARGIN
                                : 0),
                        child: InkWell(
                          onTap: () async {},
                          child: Container(
                            decoration: BoxDecoration(
                              color: AppTheme.expantableItemBg,
                              border:
                                  Border.all(color: AppTheme.expantableItemBg),
                              borderRadius: const BorderRadius.all(
                                Radius.circular(3),
                              ),
                            ),
                            child: Padding(
                              padding:
                                  const EdgeInsets.all(Constant.SMALL_PADDING),
                              child: Column(
                                mainAxisAlignment: MainAxisAlignment.start,
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  basicDetailItem(
                                      Strings.service,
                                      items.service ?? "-",
                                      Strings.plan_name,
                                      items.planName ?? "-"),
                                ],
                              ),
                            ),
                          ),
                        ),
                      );
                    })
                : Container(),
          ],
        ),
      ),
    );
  }

  additionalServiceDetailView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.additional_service_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: false,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.additional_service_details,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          children: <Widget>[
            Container(
              width: Get.width,
              height: 1.5,
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(6),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.grey.withOpacity(0.4),
                      blurRadius: 1.5,
                      spreadRadius: 1.5,
                    ),
                  ]),
            ),
            Padding(
              padding: const EdgeInsets.only(
                  top: Constant.EXPANTABLE_ITEM_MARGIN,
                  left: Constant.EXPANTABLE_ITEM_MARGIN,
                  right: Constant.EXPANTABLE_ITEM_MARGIN,
                  bottom: 0),
              child: Container(
                alignment: Alignment.topLeft,
                padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      basicDetailItem(
                          Strings.voice_service_type,
                          customerCafDetailController
                                  .customerDetail!.voicesrvtype ??
                              "-",
                          Strings.did_no,
                          customerCafDetailController.customerDetail!.didno ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }

  chargeDetailView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.charge_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: false,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.charge_details,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          children: <Widget>[
            (customerCafDetailController.custChargeList != null &&
                    customerCafDetailController.custChargeList!.isNotEmpty)
                ? Container(
                    width: Get.width,
                    height: 1.5,
                    decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(6),
                        boxShadow: [
                          BoxShadow(
                            color: Colors.grey.withOpacity(0.4),
                            blurRadius: 1.5,
                            spreadRadius: 1.5,
                          ),
                        ]),
                  )
                : Container(),
            (customerCafDetailController.custChargeList != null &&
                    customerCafDetailController.custChargeList!.isNotEmpty)
                ? ListView.builder(
                    physics: const NeverScrollableScrollPhysics(),
                    scrollDirection: Axis.vertical,
                    shrinkWrap: true,
                    itemCount:
                        customerCafDetailController.custChargeList?.length,
                    itemBuilder: (context, ii) {
                      String? validity, price, actualPrice;
                      CustChargeDetails? items =
                          customerCafDetailController.custChargeList![ii];
                      if (items.validity != null) {
                        validity = items.validity?.toString();
                      }
                      if (items.price != null) {
                        price = items.price?.toString();
                      }
                      if (items.actualprice != null) {
                        actualPrice = items.actualprice?.toString();
                      }
                      int? lstLength =
                          customerCafDetailController.custChargeList?.length;
                      return Padding(
                        padding: EdgeInsets.only(
                            top: (ii == 0)
                                ? Constant.SMALL_PADDING
                                : Constant.EXPANTABLE_ITEM_MARGIN,
                            left: Constant.EXPANTABLE_ITEM_MARGIN,
                            right: Constant.EXPANTABLE_ITEM_MARGIN,
                            bottom: (ii == (lstLength! - 1))
                                ? Constant.EXPANTABLE_ITEM_MARGIN
                                : 0),
                        child: InkWell(
                          onTap: () async {},
                          child: Container(
                            decoration: BoxDecoration(
                              color: AppTheme.expantableItemBg,
                              border:
                                  Border.all(color: AppTheme.expantableItemBg),
                              borderRadius: const BorderRadius.all(
                                Radius.circular(3),
                              ),
                            ),
                            child: Padding(
                              padding:
                                  const EdgeInsets.all(Constant.SMALL_PADDING),
                              child: Column(
                                mainAxisAlignment: MainAxisAlignment.start,
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  basicDetailItem(
                                      Strings.charge_type,
                                      items.chargetype ?? "-",
                                      Strings.validity,
                                      validity ?? "-"),
                                  const SizedBox(
                                      height: Constant.SMALL_PADDING),
                                  basicDetailItem(Strings.price, price ?? "-",
                                      Strings.actual_price, actualPrice ?? "-"),
                                  const SizedBox(
                                      height: Constant.SMALL_PADDING),
                                  basicDetailItem(Strings.charge_date,
                                      items.chargeDateString ?? "-", "-", "-"),
                                ],
                              ),
                            ),
                          ),
                        ),
                      );
                    })
                : Container(),
          ],
        ),
      ),
    );
  }

  macMapppingListView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.mac_mpappping_list),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: false,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.mac_mpappping_list,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          children: <Widget>[
            (customerCafDetailController.custMacMapppingList != null &&
                    customerCafDetailController.custMacMapppingList!.isNotEmpty)
                ? Container(
                    width: Get.width,
                    height: 1.5,
                    decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(6),
                        boxShadow: [
                          BoxShadow(
                            color: Colors.grey.withOpacity(0.4),
                            blurRadius: 1.5,
                            spreadRadius: 1.5,
                          ),
                        ]),
                  )
                : Container(),
            (customerCafDetailController.custMacMapppingList != null &&
                    customerCafDetailController.custMacMapppingList!.isNotEmpty)
                ? ListView.builder(
                    physics: const NeverScrollableScrollPhysics(),
                    scrollDirection: Axis.vertical,
                    shrinkWrap: true,
                    itemCount:
                        customerCafDetailController.custMacMapppingList?.length,
                    itemBuilder: (context, ii) {
                      CustMacMapppingDetail? items =
                          customerCafDetailController.custMacMapppingList![ii];
                      int? lstLength = customerCafDetailController
                          .custMacMapppingList?.length;
                      return Padding(
                        padding: EdgeInsets.only(
                            top: (ii == 0)
                                ? Constant.SMALL_PADDING
                                : Constant.EXPANTABLE_ITEM_MARGIN,
                            left: Constant.EXPANTABLE_ITEM_MARGIN,
                            right: Constant.EXPANTABLE_ITEM_MARGIN,
                            bottom: (ii == (lstLength! - 1))
                                ? Constant.EXPANTABLE_ITEM_MARGIN
                                : 0),
                        child: InkWell(
                          onTap: () async {},
                          child: Container(
                            decoration: BoxDecoration(
                              color: AppTheme.expantableItemBg,
                              border:
                                  Border.all(color: AppTheme.expantableItemBg),
                              borderRadius: const BorderRadius.all(
                                Radius.circular(3),
                              ),
                            ),
                            child: Padding(
                              padding:
                                  const EdgeInsets.all(Constant.SMALL_PADDING),
                              child: Column(
                                mainAxisAlignment: MainAxisAlignment.start,
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  macAddressItem(Strings.mac_address,
                                      items.macAddress ?? "-"),
                                ],
                              ),
                            ),
                          ),
                        ),
                      );
                    })
                : Container(),
          ],
        ),
      ),
    );
  }

  macAddressItem(String title1, String? value1) {
    return Row(
        mainAxisSize: MainAxisSize.max,
        crossAxisAlignment: CrossAxisAlignment.center,
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          titleWidget(title1),
          const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
          valueWidget(value1),
        ]);
  }

  customerQuotaDetailsView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.customer_quota_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: false,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.customer_quota_details,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          children: <Widget>[
            (customerCafDetailController.custQuotaList != null &&
                    customerCafDetailController.custQuotaList!.isNotEmpty)
                ? Container(
                    width: Get.width,
                    height: 1.5,
                    decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(6),
                        boxShadow: [
                          BoxShadow(
                            color: Colors.grey.withOpacity(0.4),
                            blurRadius: 1.5,
                            spreadRadius: 1.5,
                          ),
                        ]),
                  )
                : Container(),
            (customerCafDetailController.custQuotaList != null &&
                    customerCafDetailController.custQuotaList!.isNotEmpty)
                ? ListView.builder(
                    physics: const NeverScrollableScrollPhysics(),
                    scrollDirection: Axis.vertical,
                    shrinkWrap: true,
                    itemCount:
                        customerCafDetailController.custQuotaList?.length,
                    itemBuilder: (context, ii) {
                      String? totalQuota,
                          usedQuota,
                          timeTotalQuota,
                          timeQuotaUsed;
                      CustQuotaDettail? items =
                          customerCafDetailController.custQuotaList![ii];
                      int? lstLength =
                          customerCafDetailController.custQuotaList?.length;

                      if (items.totalQuota != null) {
                        totalQuota = items.totalQuota.toString();
                      }

                      if (items.usedQuota != null) {
                        usedQuota = items.usedQuota.toString();
                      }
                      if (items.timeTotalQuota != null) {
                        timeTotalQuota = items.timeTotalQuota.toString();
                      }
                      if (items.timeQuotaUsed != null) {
                        timeQuotaUsed = items.timeQuotaUsed.toString();
                      }

                      return Padding(
                        padding: EdgeInsets.only(
                            top: (ii == 0)
                                ? Constant.SMALL_PADDING
                                : Constant.EXPANTABLE_ITEM_MARGIN,
                            left: Constant.EXPANTABLE_ITEM_MARGIN,
                            right: Constant.EXPANTABLE_ITEM_MARGIN,
                            bottom: (ii == (lstLength! - 1))
                                ? Constant.EXPANTABLE_ITEM_MARGIN
                                : 0),
                        child: InkWell(
                          onTap: () async {},
                          child: Container(
                            decoration: BoxDecoration(
                              color: AppTheme.expantableItemBg,
                              border:
                                  Border.all(color: AppTheme.expantableItemBg),
                              borderRadius: const BorderRadius.all(
                                Radius.circular(3),
                              ),
                            ),
                            child: Padding(
                              padding:
                                  const EdgeInsets.all(Constant.SMALL_PADDING),
                              child: Column(
                                mainAxisAlignment: MainAxisAlignment.start,
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  basicDetailItem(
                                      Strings.plan_name,
                                      items.planName ?? "-",
                                      Strings.quota_type,
                                      items.quotaType ?? "-"),
                                  const SizedBox(
                                      height: Constant.SMALL_PADDING),
                                  basicDetailItem(
                                      Strings.total_quota,
                                      totalQuota ?? "-",
                                      Strings.used_quota,
                                      usedQuota ?? "-"),
                                  const SizedBox(
                                      height: Constant.SMALL_PADDING),
                                  basicDetailItem(
                                      Strings.quota_unit,
                                      items.quotaUnit ?? "-",
                                      Strings.time_total_quota,
                                      timeTotalQuota ?? "-"),
                                  const SizedBox(
                                      height: Constant.SMALL_PADDING),
                                  basicDetailItem(
                                      Strings.time_quota_used,
                                      timeQuotaUsed ?? "-",
                                      Strings.time_quota_unit,
                                      items.timeQuotaUnit ?? "-"),
                                ],
                              ),
                            ),
                          ),
                        ),
                      );
                    })
                : Container(),
          ],
        ),
      ),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.customer_caf_detail, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  titleWidget(String title) {
    return CustomText(
      title: title,
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w700,
      maxLines: 2,
    );
  }

  valueWidget(String? value) {
    return CustomText(
      title: value!.isNotEmpty ? value : "-",
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w400,
      maxLines: 2,
    );
  }

  @override
  void changePasswordBtnAction(
      {String? identifier,
      ChangeCustomerPasswordReq? changeCustomerPasswordReq}) {
    // api call
    Get.back();
    if (identifier != null && identifier.equalsIgnoreCase(Strings.submit)) {
      customerCafDetailController
          .changeCustomerPassword(changeCustomerPasswordReq!);
    }
  }
}
