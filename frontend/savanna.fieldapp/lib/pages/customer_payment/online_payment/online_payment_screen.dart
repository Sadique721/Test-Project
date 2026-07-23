import 'dart:developer';

import 'package:savbill/pages/customer_payment/online_payment/online_payment_controller.dart';
import 'package:savbill/pages/customer_payment/online_payment/online_payment_item_list.dart';
import 'package:savbill/pages/customer_payment/response/online_payment_audit_res.dart';
import 'package:savbill/pages/dashboard/payment_invoice_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/custom_edit%20_note_dialog.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class OnlinePaymentScreen extends StatefulWidget {
  @override
  _OnlinePaymentListState createState() => _OnlinePaymentListState();
}

class _OnlinePaymentListState extends State<OnlinePaymentScreen> {
  final onlinePaymentController = Get.put(OnlinePaymentController());

  @override
  void initState() {
    super.initState();
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<OnlinePaymentController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: onlinePaymentController.isLoading),
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
            const SizedBox(height: Constant.SMALL_PADDING),
            Expanded(
              flex: 1,
              child: (onlinePaymentController.onlineAuditList != null &&
                      onlinePaymentController.onlineAuditList!.isNotEmpty)
                  ? Container(
                      padding:
                          const EdgeInsets.only(top: Constant.SMALL_PADDING),
                      margin: const EdgeInsets.only(
                          top: Constant.VERY_SMALL_PADDING),
                      child: ListView.builder(
                          scrollDirection: Axis.vertical,
                          itemCount:
                              onlinePaymentController.onlineAuditList!.length,
                          itemBuilder: (context, index) {
                            OnlineAuditData item =
                                onlinePaymentController.onlineAuditList![index];
                            return OnlinePaymentListItem(
                              index: index,
                              item: item,
                              userId: onlinePaymentController.userDetail!.userId!,
                              controller: onlinePaymentController,
                              onTapRetryPayment: () {
                                onlinePaymentController
                                    .retryPayment(int.parse(item.orderId));
                              },
                              onTapAddToWallet: () {
                                addToWalletDialog(int.parse(item.orderId));
                              },
                            );
                          }))
                  : noDataFound(),
            )
          ]),
    );
  }

  addToWalletDialog(int? orderId) async {
    showDialog(
      context: Get.context!,
      barrierDismissible: false,
      builder: (context) => CustomEditNoteDialog(
        title: Strings.transaction_no,
        controller: onlinePaymentController.transactionNumberController,
        onSave: () {
          Get.back();
          showDialog(
            context: context,
            builder: (BuildContext context) {
              return AlertDialogHelper(
                  title: Strings.app_name,
                  message: Strings.trancasction_no_delete_msg,
                  positiveBtnText: Strings.yes,
                  negativeBtnText: Strings.no,
                  positiveBtnClick: () {
                    Get.back();
                    onlinePaymentController.addToWalletAPI(
                        orderId,
                        onlinePaymentController
                            .transactionNumberController.text);
                  },
                  negativeBtnClick: () {
                    Get.back();
                  });
            },
          );
        },
        onCancel: () {
          Get.back();
        },
      ),
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
    return DynamicAppBar(
        Strings.online_payment_audit,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}
