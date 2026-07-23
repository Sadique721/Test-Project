import 'package:savbill/pages/dashboard/invoice_mapping_item.dart';
import 'package:savbill/pages/dashboard/model/response/payment_invoice_res.dart';
import 'package:savbill/pages/dashboard/payment_invoice_detail_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class PaymentInvoiceDetail extends StatefulWidget {
  @override
  _PaymentInvoiceDetailState createState() => _PaymentInvoiceDetailState();
}

class _PaymentInvoiceDetailState extends State<PaymentInvoiceDetail> {
  final paymentInvoiceDetailController =
      Get.put(PaymentInvoiceDetailController());

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
      child: GetBuilder<PaymentInvoiceDetailController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: SafeArea(
              child: _body(),
            ),
          ),
          ProgressBar(isLoader: paymentInvoiceDetailController.isLoading),
        ]);
      }), /**/
    );
  }

  _body() {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: Container(
        color: AppTheme.colorBG,
        width: MediaQuery.of(context).size.width,
        child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              Expanded(
                child: SingleChildScrollView(
                  child: Padding(
                    padding: const EdgeInsets.only(
                      top: Constant.SCREEN_PADDING,
                      left: Constant.SCREEN_PADDING,
                      right: Constant.SCREEN_PADDING,
                    ),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisAlignment: MainAxisAlignment.start,
                      children: [
                        (paymentInvoiceDetailController.paymentInvoice !=
                                    null &&
                                paymentInvoiceDetailController
                                    .paymentInvoice!.isNotEmpty)
                            ? Row(
                                children: [
                                  const SizedBox(
                                    width: Constant.VERY_SMALL_PADDING,
                                  ),
                                  Expanded(
                                    flex: 1,
                                    child: CustomText(
                                      title: Strings.document_no,
                                      colors: AppTheme.title_dark,
                                      textAlign: TextAlign.start,
                                      fontSize: AppTheme.verySmall + 1,
                                      fontWeight: FontWeight.w400,
                                      maxLines: 2,
                                    ),
                                  ),
                                  const SizedBox(
                                    width: Constant.VERY_SMALL_PADDING + 2,
                                  ),
                                  Expanded(
                                    flex: 1,
                                    child: CustomText(
                                      title: Strings.bill_amt,
                                      colors: AppTheme.title_dark,
                                      textAlign: TextAlign.start,
                                      fontSize: AppTheme.verySmall + 1,
                                      fontWeight: FontWeight.w400,
                                      maxLines: 2,
                                    ),
                                  ),
                                  const SizedBox(
                                    width: Constant.VERY_SMALL_PADDING + 2,
                                  ),
                                  Expanded(
                                    flex: 1,
                                    child: CustomText(
                                      title: Strings.adjusted_amount,
                                      colors: AppTheme.title_dark,
                                      textAlign: TextAlign.start,
                                      fontSize: AppTheme.verySmall + 1,
                                      fontWeight: FontWeight.w400,
                                      maxLines: 2,
                                    ),
                                  ),
                                  const SizedBox(
                                    width: Constant.VERY_SMALL_PADDING + 2,
                                  ),
                                  Expanded(
                                    flex: 1,
                                    child: CustomText(
                                      title: Strings.bill_date,
                                      colors: AppTheme.title_dark,
                                      textAlign: TextAlign.start,
                                      fontSize: AppTheme.verySmall + 1,
                                      fontWeight: FontWeight.w400,
                                      maxLines: 2,
                                    ),
                                  ),
                                  const SizedBox(
                                    width: Constant.VERY_SMALL_PADDING + 2,
                                  ),
                                ],
                              )
                            : Container(),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        (paymentInvoiceDetailController.paymentInvoice !=
                                    null &&
                                paymentInvoiceDetailController
                                    .paymentInvoice!.isNotEmpty)
                            ? ListView.builder(
                                physics: const NeverScrollableScrollPhysics(),
                                shrinkWrap: true,
                                itemCount: paymentInvoiceDetailController
                                    .paymentInvoice!.length,
                                itemBuilder: (BuildContext context, int index) {
                                  PaymentInvoice item =
                                      paymentInvoiceDetailController
                                          .paymentInvoice![index];
                                  return Container(
                                    margin: const EdgeInsets.only(
                                        top: Constant.VERY_SMALL_PADDING - 2),
                                    child: InvoiceMapItem(item: item),
                                  );
                                })
                            : noDataFound(),
                        const SizedBox(
                          height: Constant.EXTRA_LARGE_PADDING,
                        ),
                      ],
                    ),
                  ),
                ),
              ),
              (paymentInvoiceDetailController.paymentInvoice != null &&
                      paymentInvoiceDetailController.paymentInvoice!.isNotEmpty)
                  ? Row(
                      children: [
                        Expanded(
                          child: Padding(
                            padding: const EdgeInsets.only(
                              bottom: Constant.SCREEN_PADDING,
                            ),
                            child: CustomText(
                              title:
                                  "Total Adjusted Amount : ${paymentInvoiceDetailController.adjustedAmount}",
                              fontSize: AppTheme.medium,
                              fontWeight: FontWeight.w400,
                              colors: AppTheme.colorBlack,
                            ),
                          ),
                        ),
                      ],
                    )
                  : Container(),
            ]),
      ),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.invoice_detail, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }
}
