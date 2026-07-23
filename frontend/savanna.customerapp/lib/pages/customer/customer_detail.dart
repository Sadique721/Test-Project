import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/change_discount/change_discount.dart';
import 'package:savbill/pages/change_plan/change_plan.dart';
import 'package:savbill/pages/connection_history/connection_history.dart';
import 'package:savbill/pages/customer/change_password_dialog.dart';
import 'package:savbill/pages/customer/credit_note_customer/customer_view_credit_note.dart';
import 'package:savbill/pages/customer/cust_notes/customer_notes_details.dart';
import 'package:savbill/pages/customer/customer_detail_controller.dart';
import 'package:savbill/pages/customer/customer_option_menu.dart';
import 'package:savbill/pages/customer/model/customer_detail_option.dart';
import 'package:savbill/pages/customer/model/request/change_customer_pwd_req.dart';
import 'package:savbill/pages/customer/model/response/cust_charge_details.dart';
import 'package:savbill/pages/customer/model/response/cust_mac_mappping_detail.dart';
import 'package:savbill/pages/customer/model/response/cust_plan_detail.dart';
import 'package:savbill/pages/customer/model/response/customer_quota_list_response.dart';
import 'package:savbill/pages/customer_change_status/change_status_list.dart';
import 'package:savbill/pages/customer_charge/charge_management.dart';
import 'package:savbill/pages/customer_inventory/inventory_detail.dart';
import 'package:savbill/pages/customer_invoice/customer_invoice.dart';
import 'package:savbill/pages/customer_ledger/customer_ledger.dart';
import 'package:savbill/pages/customer_payment/customer_paymentlist.dart';
import 'package:savbill/pages/customer_plan/customer_plan.dart';
import 'package:savbill/pages/customer_ticket/customer_ticket.dart';
import 'package:savbill/pages/revenue_report/cust_revenue_report.dart';
import 'package:savbill/pages/service_management/service_managment_view.dart';
import 'package:savbill/pages/shift_location/shift_location.dart';
import 'package:savbill/pages/workflow/cust_dunning_detail.dart';
import 'package:savbill/pages/workflow/cust_notification_detail.dart';
import 'package:savbill/pages/workflow/customer_audit_detail.dart';
import 'package:savbill/pages/workflow/customer_workflow_audit.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/acl_constant.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/permission_service.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class CustomerDetailScreen extends StatefulWidget {
  @override
  _CustomerDetailState createState() => _CustomerDetailState();
}

class _CustomerDetailState extends State<CustomerDetailScreen>
    implements ChangePasswordBtnAction {
  final customerDetailController = Get.put(CustomerDetailController());

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<CustomerDetailController>(builder: (controller) {
      return Stack(children: [
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: customerDetailController.isLoading),
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
      child: customerDetailController.customerDetail != null
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
                          itemCount: customerDetailController.optionList.length,
                          itemBuilder: (BuildContext context, int index) {
                            CustomerDetailOption detail =
                                customerDetailController.optionList[index];
                            return InkWell(
                                onTap: () {
                                  if (detail.id == 1) {
                                    openPlanDetailScreen();
                                  } else if (detail.id == 2) {
                                    openInvoiceDetailScreen();
                                  } else if (detail.id == 3) {
                                    openLedgerDetailScreen();
                                  } else if (detail.id == 4) {
                                    openPaymentListScreen();
                                  } else if (detail.id == 5) {
                                    openCustomerHistoryScreen();
                                  } else if (detail.id == 6) {
                                    openCustomerTicketsScreen();
                                  } else if (detail.id == 7) {
                                    openCustomerInventoryScreen();
                                  } else if (detail.id == 8) {
                                    openChangePlanScreen();
                                  } else if (detail.id == 9) {
                                    openChangeDiscountScreen();
                                  } else if (detail.id == 10) {
                                    changePasswordDialog(detail);
                                  } else if (detail.id == 11) {
                                    openChangeStatusScreen();
                                  } else if (detail.id == 12) {
                                    customerDetailController
                                        .getCustomerWalletBal();
                                  } else if (detail.id == 13) {
                                    openChargeManagementScreen();
                                  } else if (detail.id == 20) {
                                    openCustomerCreditNoteScreen();
                                  } else if (detail.id == 14) {
                                    openShiftLocationScreen();
                                  } else if (detail.id == 21) {
                                    openServiceManagementScreen();
                                  } else if (detail.id == 15) {
                                    openWorkFlowAuditScreen();
                                  } else if (detail.id == 16) {
                                    openAuditDetailScreen();
                                  } else if (detail.id == 17) {
                                    openDunningDetailScreen();
                                  } else if (detail.id == 18) {
                                    openNotificationDetailScreen();
                                  } else if (detail.id == 19) {
                                    openRevenueReportScreen();
                                  }
                                  else if(detail.id == 22){
                                    openCustomerNotesScreen();
                                  }
                                  else {
                                    Utils.showSnackbar(
                                        Strings.SUCCESS,
                                        Strings.under_development,
                                        AppTheme.colorWhite,
                                        AppTheme.colorGreen);
                                  }
                                },
                                child: Container(
                                  margin: EdgeInsets.only(
                                      left: index == 0
                                          ? Constant.SCREEN_PADDING
                                          : Constant.SMALL_PADDING,
                                      right: index ==
                                              customerDetailController
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
                            "${customerDetailController.customerDetail!.title} ${customerDetailController.customerDetail!.firstname} ${customerDetailController.customerDetail!.lastname}",
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
        custId: customerDetailController.customerId, remarks: "");
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
      Constant.CUSTOMER_DETAIL: customerDetailController.customerDetail,
      Constant.CUSTOMER_TYPE: customerDetailController.customerType,
      Constant.CUST_TYPE: "CUSTOMER",
    });

    if (result != null && result == true) {
      customerDetailController.getCustomerDetail();
    }
  }

  openServiceManagementScreen() async {
    log("customerDetailcustomerDetail===>${jsonEncode(customerDetailController.customerDetail)}");

    var result = await Get.to(() => ServiceManagementView(), arguments: {
      Constant.CUSTOMER_DETAIL: customerDetailController.customerDetail,
      Constant.CUSTOMER_TYPE: customerDetailController.customerType,
    });

    if (result != null && result == true) {
      customerDetailController.getCustomerDetail();
    }
  }

  openWorkFlowAuditScreen() async {
    var result = await Get.to(() => CustomerWorkFlowAudit(), arguments: {
      Constant.CUSTOMER_DETAIL: customerDetailController.customerDetail,
      Constant.CUSTOMER_TYPE: customerDetailController.customerType,
    });
    if (result != null && result == true) {
      customerDetailController.getCustomerDetail();
    }
  }

  openAuditDetailScreen() async {
    var result = await Get.to(() => CustomerAuditDetail(), arguments: {
      Constant.CUSTOMER_DETAIL: customerDetailController.customerDetail,
      Constant.CUSTOMER_TYPE: customerDetailController.customerType,
    });
    if (result != null && result == true) {
      customerDetailController.getCustomerDetail();
    }
  }

  openDunningDetailScreen() async {
    var result = await Get.to(CustomerDunningDetail(), arguments: {
      Constant.CUSTOMER_DETAIL: customerDetailController.customerDetail,
      Constant.CUSTOMER_TYPE: customerDetailController.customerType,
    });
    if (result != null && result == true) {
      customerDetailController.getCustomerDetail();
    }
  }

  openRevenueReportScreen() async {
    String name =
        "${customerDetailController.customerDetail!.title!} ${customerDetailController.customerDetail!.firstname!} ${customerDetailController.customerDetail!.lastname!}"; //firstname: 223344557, lastname
    var result = await Get.to(CustRevenueReport(), arguments: {
      Constant.CUSTOMER_ID: customerDetailController.customerDetail?.id,
      Constant.CUST_USERNAME: customerDetailController.customerDetail?.username,
      Constant.MV_ID: customerDetailController.customerDetail?.mvnoId,
      Constant.CUSTOMER_TYPE: customerDetailController.customerType,
      Constant.CUSTOMER_NAME: name
    });
  }

  openNotificationDetailScreen() async {
    var result = await Get.to(CustomerNotificationDetail(), arguments: {
      Constant.CUSTOMER_DETAIL: customerDetailController.customerDetail,
      Constant.CUSTOMER_TYPE: customerDetailController.customerType,
    });
    if (result != null && result == true) {
      customerDetailController.getCustomerDetail();
    }
  }

  openChargeManagementScreen() async {
    String name =
        "${customerDetailController.customerDetail!.title!} ${customerDetailController.customerDetail!.firstname!} ${customerDetailController.customerDetail!.lastname!}"; //firstname: 223344557, lastname
    var result = await Get.to(ChargeManagement(), arguments: {
      Constant.CUSTOMER_DETAIL: customerDetailController.customerDetail,
      Constant.CUSTOMER_TYPE: customerDetailController.customerType,
      Constant.CUSTOMER_PLAN_GRP_ID:
          customerDetailController.customerDetail!.planGroupId,
      Constant.CUSTOMER_NAME: name,
      Constant.CUSTOMER_PLAN_MAP:
          (customerDetailController.customerDetail!.planMappingList != null &&
                  customerDetailController
                      .customerDetail!.planMappingList!.isNotEmpty)
              ? customerDetailController.customerDetail!.planMappingList
              : []
    });
  }

  openCustomerCreditNoteScreen() async {
    String name =
        "${customerDetailController.customerDetail!.title!} ${customerDetailController.customerDetail!.firstname!} ${customerDetailController.customerDetail!.lastname!}"; //firstname: 223344557, lastname
    var result = await Get.to(CustomerViewCreditNote(), arguments: {
      Constant.CUSTOMER_DETAIL: customerDetailController.customerDetail,
      Constant.CUSTOMER_TYPE: customerDetailController.customerType,
    });
  }

  openChangeStatusScreen() async {
    String name =
        "${customerDetailController.customerDetail!.title!} ${customerDetailController.customerDetail!.custname!}"; //firstname: 223344557, lastname
    var result = await Get.to(CustomerChangeStatus(), arguments: {
      Constant.CUSTOMER_ID: customerDetailController.customerDetail?.id,
      Constant.CUSTOMER_TYPE: customerDetailController.customerType,
      Constant.CUSTOMER_NAME: name
    });
  }

  openChangeDiscountScreen() async {
    String name =
        "${customerDetailController.customerDetail!.title!} ${customerDetailController.customerDetail!.firstname!} ${customerDetailController.customerDetail!.lastname!}"; //firstname: 223344557, lastname
    var result = await Get.to(CustomerChangeDiscount(), arguments: {
      Constant.CUSTOMER_ID: customerDetailController.customerDetail?.id,
      Constant.CUSTOMER_TYPE: customerDetailController.customerType,
      Constant.CUSTOMER_NAME: name
    });
  }

  openChangePlanScreen() async {
    String name =
        "${customerDetailController.customerDetail!.title!} ${customerDetailController.customerDetail!.firstname!} ${customerDetailController.customerDetail!.lastname!}"; //firstname: 223344557, lastname
    var result = await Get.to(() => ChangePlan(), arguments: {
      Constant.CUSTOMER_DETAIL: customerDetailController.customerDetail!,
      Constant.CUSTOMER_NAME: name,
      Constant.CUSTOMER_PLAN_GRP_ID:
          customerDetailController.customerDetail!.planGroupId,
      Constant.SERVICE_AREA_ID:
          customerDetailController.customerDetail!.serviceAreaId,
      Constant.DISCOUNT: customerDetailController.customerDetail!.discount,
      Constant.CUSTOMER_TYPE: customerDetailController.customerDetail!.custtype,
      Constant.CUSTOMER_PLAN_MAP:
          (customerDetailController.customerDetail!.planMappingList != null &&
                  customerDetailController
                      .customerDetail!.planMappingList!.isNotEmpty)
              ? customerDetailController.customerDetail!.planMappingList
              : []
    });
  }

  openCustomerInventoryScreen() async {
    String name =
        "${customerDetailController.customerDetail!.title!} ${customerDetailController.customerDetail!.firstname!} ${customerDetailController.customerDetail!.lastname!}"; //firstname: 223344557, lastname
    var result = await Get.to(() => CustomerInventoryDetail(), arguments: {
      Constant.CUSTOMER_ID: customerDetailController.customerDetail?.id,
      Constant.CUSTOMER_NAME: name,
      Constant.CUSTOMER_FIRST_NAME:
          customerDetailController.customerDetail!.firstname!,
      Constant.SERVICE_AREA_ID: customerDetailController.serviceAreaId,
      Constant.CUSTOMER_TYPE: customerDetailController.customerType
    });
  }

  openCustomerTicketsScreen() async {
    String name =
        "${customerDetailController.customerDetail!.title!} ${customerDetailController.customerDetail!.firstname!} ${customerDetailController.customerDetail!.lastname!}"; //firstname: 223344557, lastname
    var result = await Get.to(CustomerTicketDetail(), arguments: {
      Constant.CUSTOMER_ID: customerDetailController.customerDetail?.id,
      Constant.CUSTOMER_DETAIL: customerDetailController.customerDetail,
      Constant.CUSTOMER_TYPE: customerDetailController.customerType,
      Constant.CUSTOMER_NAME: name
    });
  }

  openCustomerHistoryScreen() async {
    String name =
        "${customerDetailController.customerDetail!.title!} ${customerDetailController.customerDetail!.firstname!} ${customerDetailController.customerDetail!.lastname!}"; //firstname: 223344557, lastname
    var result = await Get.to(ConnectionHistory(), arguments: {
      Constant.CUSTOMER_ID: customerDetailController.customerDetail?.id,
      Constant.CUST_USERNAME: customerDetailController.customerDetail?.username,
      Constant.MV_ID: customerDetailController.customerDetail?.mvnoId,
      Constant.CUSTOMER_TYPE: customerDetailController.customerType,
      Constant.CUSTOMER_NAME: name
    });
  }

  openPaymentListScreen() async {
    String name =
        "${customerDetailController.customerDetail!.title!} ${customerDetailController.customerDetail!.firstname!} ${customerDetailController.customerDetail!.lastname!}"; //firstname: 223344557, lastname
    var result = await Get.to(CustomerPaymentList(), arguments: {
      Constant.CUSTOMER_ID: customerDetailController.customerDetail?.id,
      Constant.CUSTOMER_TYPE: customerDetailController.customerType,
      Constant.CUSTOMER_DETAIL: customerDetailController.customerDetail,
      Constant.CUSTOMER_NAME: name
    });
  }

  openLedgerDetailScreen() async {
    String name =
        "${customerDetailController.customerDetail!.title!} ${customerDetailController.customerDetail!.firstname!} ${customerDetailController.customerDetail!.lastname!}"; //firstname: 223344557, lastname
    var result = await Get.to(CustomerLedgerDetail(), arguments: {
      Constant.CUSTOMER_ID: customerDetailController.customerDetail?.id,
      Constant.CUSTOMER_TYPE: customerDetailController.customerType,
      Constant.CUSTOMER_NAME: name
    });
  }

  openInvoiceDetailScreen() async {
    String name =
        "${customerDetailController.customerDetail!.title!} ${customerDetailController.customerDetail!.firstname!} ${customerDetailController.customerDetail!.lastname!}"; //firstname: 223344557, lastname
    var result = await Get.to(CustomerInvoiceDetail(), arguments: {
      Constant.CUSTOMER_DETAIL: customerDetailController.customerDetail,
      Constant.CUSTOMER_ID: customerDetailController.customerDetail?.id,
      Constant.CUSTOMER_TYPE: customerDetailController.customerType,
      Constant.CUSTOMER_AREA_DETAIAL: customerDetailController.presentAddress,
      Constant.CUSTOMER_NAME: name
    });
  }

  openPlanDetailScreen() async {
    String name =
        "${customerDetailController.customerDetail!.title!} ${customerDetailController.customerDetail!.firstname!} ${customerDetailController.customerDetail!.lastname!}"; //firstname: 223344557, lastname
    var result = await Get.to(CustomerPlanDetail(), arguments: {
      Constant.CUSTOMER_ID: customerDetailController.customerDetail?.id,
      Constant.CUSTOMER_TYPE: customerDetailController.customerType,
      Constant.CUSTOMER_NAME: name,
      Constant.CUSTOMER_PLAN: customerDetailController.customerDetail!,
    });
  }

  basicDetailView() {
    String? name;
    if (customerDetailController.customerDetail?.title != null) {
      name = ("${customerDetailController.customerDetail?.title!}");
    }
    if (customerDetailController.customerDetail?.firstname != null) {
      name = ("$name ${customerDetailController.customerDetail?.firstname!}");
    }
    if (customerDetailController.customerDetail?.lastname != null) {
      name = ("$name ${customerDetailController.customerDetail?.lastname!}");
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
                          name ?? "-",
                          Strings.contact_person,
                          customerDetailController
                                  .customerDetail?.contactperson ??
                              "-"),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                          Strings.caf_no,
                          customerDetailController.customerDetail?.cafno ?? "-",
                          Strings.account_number,
                          customerDetailController.customerDetail?.acctno ??
                              "-"),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                          Strings.username,
                          customerDetailController.customerDetail?.username ??
                              "-",
                          Strings.status,
                          (customerDetailController.customerDetail?.status ==
                                      "Ingrace" ||
                                  customerDetailController
                                          .customerDetail?.status ==
                                      "INGRACE")
                              ? "InGrace"
                              : customerDetailController
                                  .customerDetail?.status),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.customer_type,
                          customerDetailController.customerDetail?.custtype ??
                              "-",
                          Strings.calendar_type,
                          customerDetailController
                                  .customerDetail?.calendarType ??
                              "-"),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                          Strings.feasibility,
                          "-",
                          Strings.first_activation_date,
                          customerDetailController
                                  .customerDetail?.firstActivationDate ??
                              "-"),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                          "${Strings.primary_mobile_number} (${customerDetailController.customerDetail?.countryCode})",
                          customerDetailController.customerDetail?.mobile ??
                              "-",
                          Strings.secondary_mobile_number,
                          customerDetailController.customerDetail?.altmobile ??
                              "-"),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                          Strings.tel_phone,
                          customerDetailController.customerDetail?.phone ?? "-",
                          Strings.fax_number,
                          customerDetailController.customerDetail?.fax ?? "-"),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                          Strings.created_by,
                          customerDetailController
                                  .customerDetail?.createdByName ??
                              "-",
                          Strings.email,
                          customerDetailController.customerDetail?.email ??
                              "-"),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                        "${Strings.bill_to} ${Strings.name}",
                        customerDetailController
                                .customerDetail!.planMappingList!.isNotEmpty
                            ? customerDetailController.customerDetail!
                                    .planMappingList![0].billTo ??
                                "-"
                            : "",
                        "${Strings.bill_to} ${Strings.address}",
                        "-",
                      ),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                        Strings.tin_pan_no,
                        customerDetailController.customerDetail?.pan ?? "-",
                        Strings.activated_by,
                        customerDetailController
                                .customerDetail?.activationByName ??
                            "-",
                      ),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                          Strings.account_status,
                          ""
                          "-",
                          Strings.customer_category,
                          customerDetailController
                                  .customerDetail?.custcategory ??
                              "-"),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                        Strings.customer_sub_type,
                        customerDetailController
                                .customerDetail?.customerSubType ??
                            "-",
                        Strings.customer_sector,
                        customerDetailController
                                .customerDetail?.customerSector ??
                            "-",
                      ),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                          Strings.customer_sub_sector,
                          customerDetailController
                                  .customerDetail?.customerSubSector ??
                              "-",
                          Strings.ezybill_id,
                          "" ?? "-"),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                        Strings.dob,
                        "" ?? "-",
                        Strings.dunning_enable,
                        "${customerDetailController.customerDetail?.isDunningEnable}" ??
                            "-",
                      ),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                        Strings.automatic_notify,
                        "${customerDetailController.customerDetail?.isNotificationEnable}" ??
                            "-",
                        Strings.last_payment_amount,
                        "${customerDetailController.customerDetail?.walletbalance}" ??
                            "-",
                      ),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                        Strings.next_quota_reset_date,
                        (customerDetailController
                                    .customerDetail!.nextQuotaResetDate !=
                                null)
                            ? "${customerDetailController.customerDetail!.nextQuotaResetDate}"
                            : "-",
                        Strings.mac_retention_date,
                        (customerDetailController
                                    .customerDetail!.nearestMacRetentionDate !=
                                null)
                            ? "${customerDetailController.customerDetail!.nearestMacRetentionDate}"
                            : "-",
                      ),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                        Strings.unit_no,
                        (customerDetailController.customerDetail?.blockNo !=
                                null)
                            ? "${customerDetailController.customerDetail?.blockNo}"
                            : "-",
                        Strings.available_balance,
                        "${customerDetailController.customerDetail!.planMappingList!.first.walletBalUsed}" ??
                            "-",
                      ),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                        Strings.expiry_date,
                        "${customerDetailController.customerDetail!.planMappingList!.first.expiryDate}" ??
                            "-",
                        Strings.last_payment_date,
                        (customerDetailController.customerDetail!.macaddress !=
                                null)
                            ? "${customerDetailController.customerDetail!.macaddress}"
                            : "-",
                      ),
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
      crossAxisAlignment: CrossAxisAlignment.center,
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Flexible(
          flex: 1,
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
        Flexible(
          flex: 1,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.end,
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
                        Strings.address,
                        (customerDetailController
                                    .customerDetail!.addressList!.isNotEmpty &&
                                customerDetailController.customerDetail!
                                        .addressList![0].landmark !=
                                    null)
                            ? customerDetailController
                                .customerDetail!.addressList![0].landmark
                            : "-",
                        "${Strings.service_area} ${Strings.name}",
                        customerDetailController
                                .customerDetail?.serviceareaName ??
                            "-",
                      ),
                      SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          "${Strings.branch} ${Strings.name} / ${Strings.partner}",
                          customerDetailController.customerDetail?.branchName ??
                              "-",
                          Strings.sub_area,
                          customerDetailController
                                  .buildingAndSubDetails?.name ??
                              "-"),
                      SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.building_name,
                          customerDetailController
                                  .buildingAndSubDetails?.buildingName ??
                              "-",
                          Strings.building_no,
                          customerDetailController.customerDetail!
                                  .addressList![0].buildingNumber ??
                              "-"),
                      SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.region,
                          customerDetailController.customerDetail?.aadhar ??
                              "-",
                          Strings.business_vertical,
                          customerDetailController.customerDetail?.passportNo ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                        Strings.municipality,
                        customerDetailController.presentAddress?.code ?? "-",
                        Strings.area,
                        customerDetailController.presentAddress?.name ?? "-",
                      ),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.city,
                          customerDetailController.presentAddress?.cityName ??
                              "-",
                          Strings.state,
                          customerDetailController.presentAddress?.stateName ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                        Strings.landmark,
                        "-",
                        Strings.valley_type,
                        customerDetailController.customerDetail?.valleyType ??
                            "-",
                      ),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                        Strings.latitude,
                        customerDetailController.customerDetail?.latitude ??
                            "-",
                        Strings.longitude,
                        customerDetailController.customerDetail?.longitude ??
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

  openCustomerNotesScreen() async {
    var result = await Get.to(CustomerNotesDetail(), arguments: {
      Constant.CUSTOMER_DETAIL: customerDetailController.customerDetail,
      Constant.CUSTOMER_TYPE: customerDetailController.customerType,
    });
    if (result != null && result == true) {
      customerDetailController.getCustomerDetail();
    }
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
                        customerDetailController.networkDetails?.popName ?? "-",
                        Strings.olt,
                        customerDetailController.networkDetails?.oltDeviceName ?? "-",
                      ),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.card_number,
                          "-",
                          Strings.master_db,
                          customerDetailController.networkDetails?.masterdbDeviceName ?? "",
                      ),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                        Strings.splitter_db,
                        customerDetailController
                                .networkDetails?.splitterDerviceName ??
                            "-",
                        Strings.mac_address,
                        customerDetailController
                                .networkDetails?.macAddress?.first ??
                            "-",
                      ),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.onu_serial_number,
                          customerDetailController
                                  .networkDetails?.onuSerialNumber?.first ??
                              "-",
                          Strings.external_onu_seraial,
                          customerDetailController
                              .networkDetails?.externalOnuSerialNumber
                              ?? "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.static_ip,
                          customerDetailController
                              .networkDetails?.staticOrPooledIP
                              ?? "-",
                          Strings.nas_ip,
                          customerDetailController.customerDetail?.nasPort ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                        Strings.nas_port,
                        customerDetailController.networkDetails?.nasPort ??
                            "-",
                        Strings.max_concurrent_session,
                        customerDetailController
                                .customerDetail?.maxconcurrentsession ??
                            "-",
                      ),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.network_profile,
                          customerDetailController
                                  .customerDetail?.networktype ?? "-",
                          Strings.location,
                          customerDetailController.customerDetail?.locations ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.is_parent_location,
                          "",
                          Strings.parent_quota_type,
                          customerDetailController
                                  .customerDetail?.parentQuotaType ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.ip_pool_name,
                          customerDetailController.networkDetails?.ipPoolNameBind
                              ?? "-",
                          Strings.vlan_id,
                          customerDetailController.customerDetail?.vlanid ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.framed_ip_address,
                          customerDetailController.networkDetails?.framedIp ??
                              "-",
                          Strings.framed_ipv6_address,
                          customerDetailController
                                  .customerDetail?.framedIpv6Address ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.framed_ipv6_prefix,
                          "",
                          Strings.framed_route,
                          customerDetailController
                                  .customerDetail?.framedroute ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.primary_dns,
                          customerDetailController.customerDetail?.primaryDNS ??
                              "-",
                          Strings.primary_ipv6_dns,
                          customerDetailController
                                  .customerDetail?.primaryIPv6DNS ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.secondary_dns,
                          customerDetailController
                                  .customerDetail?.secondaryDNS ??
                              "-",
                          Strings.secondary_ipv6_dns,
                          customerDetailController
                                  .customerDetail?.secondaryIPv6DNS ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.delagated_prefix,
                          customerDetailController
                                  .customerDetail?.delegatedprefix ??
                              "-",
                          Strings.nas_port_id,
                          customerDetailController.customerDetail?.nasPortId ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.mac_provision,
                          customerDetailController.customerDetail?.mac_provision.toString() ?? "-",
                          Strings.mac_auth,
                          customerDetailController.customerDetail?.mac_auth_enable.toString() ?? "-"
                          ),
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
                          customerDetailController
                                  .customerDetail?.serviceareaName ??
                              "-",
                          Strings.latitude,
                          customerDetailController.customerDetail?.latitude ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.longitude,
                          customerDetailController.customerDetail?.longitude ??
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
                          customerDetailController
                                  .customerDetail!.partnerName ??
                              "-",
                          Strings.sales_mark,
                          customerDetailController
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

    if (customerDetailController.paymentDetails?.amount != null) {
      amt = customerDetailController.paymentDetails?.amount.toString();
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
                        customerDetailController
                                .paymentDetails?.paymentreferenceno ??
                            "-",
                      ),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.payment_date,
                          customerDetailController
                                  .paymentDetails?.paymentdate ??
                              "-",
                          Strings.payment_mode,
                          customerDetailController.paymentDetails?.paymode ??
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
                          customerDetailController
                                  .paymentAddress?.fullAddress ??
                              "-",
                          Strings.pincode,
                          customerDetailController.paymentAddress?.code ?? "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.area,
                          customerDetailController.paymentAddress?.name ?? "-",
                          Strings.city,
                          customerDetailController.paymentAddress?.cityName ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.state,
                          customerDetailController.paymentAddress?.stateName ??
                              "-",
                          Strings.country,
                          customerDetailController
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
                          customerDetailController
                                  .permanentAddress?.fullAddress ??
                              "-",
                          Strings.pincode,
                          customerDetailController.permanentAddress?.code ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.area,
                          customerDetailController.permanentAddress?.name ??
                              "-",
                          Strings.city,
                          customerDetailController.permanentAddress?.cityName ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.state,
                          customerDetailController
                                  .permanentAddress?.stateName ??
                              "-",
                          Strings.country,
                          customerDetailController
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
                          customerDetailController
                                  .presentAddress?.fullAddress ??
                              "-",
                          Strings.pincode,
                          customerDetailController.presentAddress?.code ?? "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.area,
                          customerDetailController.presentAddress?.name ?? "-",
                          Strings.city,
                          customerDetailController.presentAddress?.cityName ??
                              "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.state,
                          customerDetailController.presentAddress?.stateName ??
                              "-",
                          Strings.country,
                          customerDetailController
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
            (customerDetailController.planMappingList != null &&
                    customerDetailController.planMappingList!.isNotEmpty)
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
            (customerDetailController.planMappingList != null &&
                    customerDetailController.planMappingList!.isNotEmpty)
                ? ListView.builder(
                    physics: const NeverScrollableScrollPhysics(),
                    scrollDirection: Axis.vertical,
                    shrinkWrap: true,
                    itemCount: customerDetailController.planMappingList?.length,
                    itemBuilder: (context, ii) {
                      PlanMappingDetail? items =
                          customerDetailController.planMappingList![ii];
                      int? lstLength =
                          customerDetailController.planMappingList?.length;
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
                          customerDetailController
                                  .customerDetail!.voicesrvtype ??
                              "-",
                          Strings.did_no,
                          customerDetailController.customerDetail!.didno ??
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
            (customerDetailController.custChargeList != null &&
                    customerDetailController.custChargeList!.isNotEmpty)
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
            (customerDetailController.custChargeList != null &&
                    customerDetailController.custChargeList!.isNotEmpty)
                ? ListView.builder(
                    physics: const NeverScrollableScrollPhysics(),
                    scrollDirection: Axis.vertical,
                    shrinkWrap: true,
                    itemCount: customerDetailController.custChargeList?.length,
                    itemBuilder: (context, ii) {
                      String? validity, price, actualPrice;
                      CustChargeDetails? items =
                          customerDetailController.custChargeList![ii];
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
                          customerDetailController.custChargeList?.length;
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
            (customerDetailController.custMacMapppingList != null &&
                    customerDetailController.custMacMapppingList!.isNotEmpty)
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
            (customerDetailController.custMacMapppingList != null &&
                    customerDetailController.custMacMapppingList!.isNotEmpty)
                ? ListView.builder(
                    physics: const NeverScrollableScrollPhysics(),
                    scrollDirection: Axis.vertical,
                    shrinkWrap: true,
                    itemCount:
                        customerDetailController.custMacMapppingList?.length,
                    itemBuilder: (context, ii) {
                      CustMacMapppingDetail? items =
                          customerDetailController.custMacMapppingList![ii];
                      int? lstLength =
                          customerDetailController.custMacMapppingList?.length;
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
            (customerDetailController.custQuotaList != null &&
                    customerDetailController.custQuotaList!.isNotEmpty)
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
            (customerDetailController.custQuotaList != null &&
                    customerDetailController.custQuotaList!.isNotEmpty)
                ? ListView.builder(
                    physics: const NeverScrollableScrollPhysics(),
                    scrollDirection: Axis.vertical,
                    shrinkWrap: true,
                    itemCount: customerDetailController.custQuotaList?.length,
                    itemBuilder: (context, ii) {
                      String? totalQuota,
                          usedQuota,
                          timeTotalQuota,
                          timeQuotaUsed;
                      CustQuotaDettail? items =
                          customerDetailController.custQuotaList![ii];
                      int? lstLength =
                          customerDetailController.custQuotaList?.length;

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
    return DynamicAppBar(Strings.customer_detail, '', AppTheme.colorPrimary,
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
      showDialog(
        context: context,
        builder: (BuildContext context) {
          return AlertDialogHelper(
              title: Strings.password_change_confirm,
              message: Strings.password_change_confirm_msg,
              positiveBtnText: Strings.yes,
              negativeBtnText: Strings.no,
              positiveBtnClick: () {
                Navigator.pop(context);
                // Get.back();
                customerDetailController
                    .changeCustomerPassword(changeCustomerPasswordReq!);
              },
              negativeBtnClick: () {
                // Get.back();
                Navigator.pop(context);
              });
        },
      );
    }
  }
}
