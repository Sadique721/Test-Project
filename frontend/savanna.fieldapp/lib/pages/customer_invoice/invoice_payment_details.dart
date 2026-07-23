import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/customer_invoice/invoice_payment_details_controller.dart';
import 'package:savbill/pages/customer_invoice/invoice_payment_item.dart';
import 'package:savbill/pages/customer_invoice/request/invoice_payment_adjust_req.dart';
import 'package:savbill/pages/customer_invoice/response/invoice_payment_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class InvoicePaymentDetails extends StatefulWidget {
  @override
  _InvoicePaymentState createState() => _InvoicePaymentState();
}

class _InvoicePaymentState extends State<InvoicePaymentDetails> {
  final invoicePaymentController = Get.put(InvoicePaymentListController());
  final GlobalKey<ScaffoldState> _deviceListKey = GlobalKey();



  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<InvoicePaymentListController>(builder: (controller) {
      return Stack(children: [
        Scaffold(
          appBar: _appBar(),
          body: Scaffold(
            key: _deviceListKey,
            backgroundColor: AppTheme.colorBG,
            body: _body(),
          ),
        ),
        ProgressBar(isLoader: invoicePaymentController.isLoading),
      ]);
    });
  }

  _body() {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: Container(
        width: MediaQuery
            .of(context)
            .size
            .width,
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
                child:    CustomText(
                    title: "${invoicePaymentController.customerName} ${ Strings.payment_details}",
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
                child: (invoicePaymentController.invoicePaymentList != null &&
                    invoicePaymentController.invoicePaymentList!.isNotEmpty)
                    ? Padding(
                  padding: const EdgeInsets.symmetric(
                      horizontal: Constant.SCREEN_PADDING,vertical: Constant.SMALL_PADDING),
                  child: ListView.builder(
                      // controller: invoicePaymentController.controller,
                      scrollDirection: Axis.vertical,
                      itemCount:
                      invoicePaymentController.invoicePaymentList!.length + 1,
                      itemBuilder: (context, index) {
                        if (index == invoicePaymentController.invoicePaymentList?.length) {
                          if (invoicePaymentController.isShowLoadMore) {
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
                          InvoicePaymentList item =
                          invoicePaymentController.invoicePaymentList![index];
                          return InkWell(
                            onTap: () {
                              for (var f in  invoicePaymentController.invoicePaymentList!) {
                                if (f.id == item.id!) {
                                  if (f.isSelected == null) {
                                    f.isSelected = true;
                                  } else {
                                    f.isSelected = !f.isSelected!;
                                  }
                                  break;
                                }
                              }
                              setState(() {
                                invoicePaymentController.selectedInvoicePaymentList = invoicePaymentController.invoicePaymentList;
                              });
                            },
                            child: InvoicePaymentItem(
                              item: item,
                              controller : invoicePaymentController
                            ),
                          );
                        }
                      }),
                )
                    : noDataFound(),
              ),
              Row(
                children: [
                  Expanded(
                    child: SimpleButton(
                      onTap: () {
                        validateForm();
                      },
                      radius: 0,
                      height: Constant.BOTTOM_BTN_HEIGHT,
                      bgColors: AppTheme.colorGreen,
                      borderColors: AppTheme.colorGreen,
                      child: CustomText(
                        title: Strings.manual_adjusted,
                        fontSize: AppTheme.medium,
                        fontWeight: FontWeight.w400,
                      ),
                    ),
                  ),
                  Expanded(
                    child: SimpleButton(
                      onTap: () {
                        _backScreen();
                      },
                      radius: 0,
                      height: Constant.BOTTOM_BTN_HEIGHT,
                      bgColors: AppTheme.colorRed,
                      borderColors: AppTheme.colorRed,
                      child: CustomText(
                        title: Strings.cancel,
                        fontSize: AppTheme.medium,
                        fontWeight: FontWeight.w400,
                      ),
                    ),
                  ),
                ],
              ),
            ]),
      ),
    );
  }

  validateForm() {
    List<InvoicePaymentList> selectedItem = [];
    for (var element in invoicePaymentController.invoicePaymentList!) {
      invoicePaymentController.creditDocumentList!.clear();
      if (element.isSelected != null && element.isSelected == true) {
        invoicePaymentController.creditDocumentList!.add(CreditDocumentList(id: element.id,amount: element.amount));
        selectedItem.add(element);
        log("creditDocumentList>> ${json.encode(invoicePaymentController.creditDocumentList)}");
      }
    }
    if (selectedItem.isNotEmpty) {
      invoicePaymentController.invoicePaymentAdjustApi();
      invoicePaymentController.update();
    } else {
      Utils.showSnackbar(Strings.ERROR, "Please select at-least one item",
          AppTheme.colorWhite, AppTheme.colorRed);
    }
  }

  _appBar() {
    return DynamicAppBar(
        Strings.payment_details,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  noDataFound() {
    return const NoDataFound();
  }


}