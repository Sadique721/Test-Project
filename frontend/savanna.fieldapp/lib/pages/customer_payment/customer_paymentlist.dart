import 'package:savbill/pages/customer_payment/customer_paymentlist_controller.dart';
import 'package:savbill/pages/customer_payment/online_payment/online_payment_screen.dart';
import 'package:savbill/pages/dashboard/model/response/payment_list_response.dart';
import 'package:savbill/pages/dashboard/payment_invoice_detail.dart';
import 'package:savbill/pages/dashboard/payment_list_item.dart';
import 'package:savbill/pages/dashboard/record_payment.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/acl_constant.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/permission_service.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../util/utils.dart';

class CustomerPaymentList extends StatefulWidget {
  @override
  _CustomerPaymentListState createState() => _CustomerPaymentListState();
}

class _CustomerPaymentListState extends State<CustomerPaymentList> {
  final customerPaymentListController =
      Get.put(CustomerPaymentListController());

  @override
  void initState() {
    super.initState();
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<CustomerPaymentListController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: customerPaymentListController.isLoading),
      ]);
    });
  }

  _body() {
    return Container(
      color: AppTheme.colorBG,
      width: MediaQuery.of(context).size.width,
      child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            Container(
              padding: const EdgeInsets.only(
                  top: Constant.SCREEN_PADDING,
                  left: Constant.SCREEN_PADDING,
                  right: Constant.SCREEN_PADDING),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Expanded(
                      child: CustomText(
                          title: customerPaymentListController.customerName,
                          colors: AppTheme.colorBlack,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.medium + 1,
                          fontWeight: FontWeight.w500)),
                  const SizedBox(
                    width: Constant.VERY_SMALL_PADDING,
                  ),
                  PermissionService().hasAclPermission([customerPaymentListController.customerType!.equalsIgnoreCase('Prepaid')
                      ? AclPreCustConstants.PRE_CUST_PAYMENT_RECORD
                      : AclPostCustConstants.POST_CUST_PAYMENT_RECORD]) == true ?
                  (customerPaymentListController
                      .customerDetail!.status!
                      .equalsIgnoreCase("Terminate") ||
                      customerPaymentListController.customerDetail!.status!
                          .equalsIgnoreCase("isDisable"))
                      ? const SizedBox.shrink() : Container(
                    height: Constant.BTN_HEIGHT_M,
                    alignment: Alignment.topRight,
                    child: FloatingActionButton.extended(
                      backgroundColor: AppTheme.colorPrimary,
                      onPressed: () {
                        openOnlinePaymentScreen();
                      },
                      label: CustomText(
                        title: Strings.online_payment_audit,
                        fontSize: AppTheme.small-1,
                      ),
                      icon: Icon(
                        Icons.payment,
                        size: Constant.ICON_SIZE_M-2,
                        color: AppTheme.colorWhite,
                      ),
                    ),
                  ) : SizedBox.shrink(),

                  const SizedBox(
                    width: Constant.SMALL_PADDING,
                  ),

                  PermissionService().hasAclPermission([customerPaymentListController.customerType!.equalsIgnoreCase('Prepaid')
                      ? AclPreCustConstants.PRE_CUST_PAYMENT_RECORD
                      : AclPostCustConstants.POST_CUST_PAYMENT_RECORD]) == true ?
                  (customerPaymentListController.customerDetail!.status!
                              .equalsIgnoreCase("Terminate") ||
                          customerPaymentListController.customerDetail!.status!
                              .equalsIgnoreCase("isDisable"))
                      ? const SizedBox.shrink()
                      : Material(
                          color: AppTheme.colorWhite,
                          elevation: 2,
                          shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(20)),
                          child: InkWell(
                            onTap: () {
                              openRecordPaymentScreen();
                            },
                            child: Container(
                              decoration: BoxDecoration(
                                color: AppTheme.colorPrimary,
                                borderRadius:
                                    const BorderRadius.all(Radius.circular(20)),
                              ),
                              padding: const EdgeInsets.all(6),
                              child: Icon(
                                Icons.add,
                                color: AppTheme.colorWhite,
                                size: 22,
                              ),
                            ),
                          ),
                        ) : SizedBox.shrink(),
                ],
              ),
            ),
            const SizedBox(height: Constant.MEDIUM_PADDING),
            Expanded(
              flex: 1,
              child: (customerPaymentListController.paymentDetail != null &&
                      customerPaymentListController.paymentDetail!.isNotEmpty)
                  ? ListView.builder(
                      scrollDirection: Axis.vertical,
                      itemCount:
                          customerPaymentListController.paymentDetail!.length,
                      itemBuilder: (context, index) {
                        PaymentDetail item =
                            customerPaymentListController.paymentDetail![index];
                        return PaymentViewListItem(
                          index: index,
                          item: item,
                          userId:
                              customerPaymentListController.userDetail!.userId!,
                          controller: customerPaymentListController,
                        );
                      })
                  : noDataFound(),
            ),
          ]),
    );
  }

  openInvoiceDetailScreen(int id) async {
    Get.to(PaymentInvoiceDetail(), arguments: {
      Constant.ID: id,
    });
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.payment_details, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  openRecordPaymentScreen() async {
    bool chkRefresh = await Get.to(RecordPayment(), arguments: {
      Constant.FROM: Strings.customer_payment,
      Constant.CUSTOMER_DETAIL: customerPaymentListController.customerDetail,
    });
    if (chkRefresh) {
      Utils.showSnackbar(Strings.SUCCESS, "Payment created successfully.",
          AppTheme.colorWhite, AppTheme.colorGreen);
      customerPaymentListController.getCustomerInvoiceDetail();
    }
  }

  openOnlinePaymentScreen() async {
    bool chkRefresh = await Get.to(OnlinePaymentScreen(), arguments: {
      Constant.FROM: Strings.customer_payment,
      Constant.CUSTOMER_DETAIL: customerPaymentListController.customerDetail,
    });
    if (chkRefresh) {
      customerPaymentListController.getCustomerInvoiceDetail();
    }
  }
}
