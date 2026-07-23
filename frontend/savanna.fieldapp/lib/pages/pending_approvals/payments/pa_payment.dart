import 'package:savbill/pages/pending_approvals/model/request/payment_approve_reject_req.dart';
import 'package:savbill/pages/pending_approvals/model/response/approval_pending_payment_res.dart';
import 'package:savbill/pages/pending_approvals/payments/pa_payment_controller.dart';
import 'package:savbill/pages/pending_approvals/payments/pa_payment_item.dart';
import 'package:savbill/pages/pending_approvals/payments/pa_payment_status_dialog.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:intl/intl.dart';

class PendingApprovalPayment extends StatefulWidget {
  @override
  _PendingApprovalPaymentState createState() => _PendingApprovalPaymentState();
}

class _PendingApprovalPaymentState extends State<PendingApprovalPayment>
    implements PaymentStatusBtnAction {
  final paPaymentController = Get.put(PAPaymentController());

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<PAPaymentController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: paPaymentController.isLoading),
        ]);
      }),
    );
  }

  _body() {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: Container(
        width: MediaQuery.of(context).size.width,
        child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(
                height: Constant.SCREEN_PADDING,
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SCREEN_PADDING),
                child: CustomText(
                    title: Strings.payment_details,
                    colors: AppTheme.colorBlack,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.medium + 1,
                    fontWeight: FontWeight.w500),
              ),
              const SizedBox(
                height: Constant.MEDIUM_PADDING,
              ),
              Expanded(
                flex: 1,
                child: (paPaymentController.paymentList != null &&
                        paPaymentController.paymentList!.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            controller: paPaymentController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount:
                                paPaymentController.paymentList!.length + 1,
                            itemBuilder: (context, index) {
                              if (index ==
                                  paPaymentController.paymentList?.length) {
                                if (paPaymentController.isShowLoadMore) {
                                  return Padding(
                                    padding: const EdgeInsets.all(
                                        Constant.SMALL_PADDING),
                                    child: Center(
                                      child: SizedBox(
                                        width: Constant.SCREEN_PADDING,
                                        height: Constant.SCREEN_PADDING,
                                        child: CircularProgressIndicator(
                                          strokeWidth: 2.5,
                                          valueColor:
                                              AlwaysStoppedAnimation<Color>(
                                                  AppTheme.colorProgress),
                                          backgroundColor:
                                              AppTheme.colorProgressBg,
                                        ),
                                      ),
                                    ),
                                  );
                                } else {
                                  return Container();
                                }
                              } else {
                                ApprovalPendingPayment item =
                                    paPaymentController.paymentList![index];
                                return PendingApprovalPaymentItem(
                                  item: item,
                                  onTapApprove: () {
                                    showChangeStatusDialog(
                                        item, Strings.approve);
                                  },
                                  onTapReject: () {
                                    showChangeStatusDialog(
                                        item, Strings.reject);
                                  },
                                );
                              }
                            }),
                      )
                    : noDataFound(),
              ),
            ]),
      ),
    );
  }

  showChangeStatusDialog(ApprovalPendingPayment detail, String from) {
    String paydate = "";
    if (detail.paymentdate != null && detail.paymentdate!.isNotEmpty) {
      DateTime date =
          DateFormat(Constant.DATE_FORMAT).parse(detail.paymentdate!);
      paydate = DateFormat(Constant.API_DATE_FORMAT).format(date);
    }
    showDialog(
        context: context,
        barrierDismissible: false,
        builder: (BuildContext context) {
          return PAPaymentStatusDialog(
            from: from,
            paymentStatusBtnAction: this,
            paymentApproveRejectReq: PaymentApproveRejectReq(
                idlist: detail.id,
                customerid: detail.custId,
                paymode: detail.paymode,
                paystatus: detail.status,
                paytodate: paydate,
                referenceno: detail.receiptNo),
          );
        });
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        Strings.payment_pending_approvals,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  @override
  void paymentStatusBtnAction(
      {String? identifier, PaymentApproveRejectReq? request}) {
    Get.back();
    if (request != null && identifier != null && identifier.isNotEmpty) {
      paPaymentController.approveRejectPayment(identifier, request);
    }
  }
}
