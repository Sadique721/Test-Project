import 'dart:developer';

import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer_caf/customer_caf_invoice/customer_caf_invoice_controller.dart';
import 'package:savbill/pages/customer_caf/response/customer_caf_invoice_details_res.dart';
import 'package:savbill/pages/customer_caf_invoice/cust_caf_invoice_detail_screen.dart';
import 'package:savbill/pages/customer_invoice/cust_invoice_detail/cust_invoice_detail_screen.dart';
import 'package:savbill/pages/dashboard/model/response/invoice_list_response.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/status_bg_view.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import 'package:intl/intl.dart';

class CustomerCafInvoiceViewItem extends StatelessWidget {
  Invoicesearchlist item;
  CustomerDetail? customerDetail;
  int index;
  CustomerCafInvoiceController? controller;
  final Function()? onTapDocumentGenerate;
  final Function()? onTapDownload;
  final Function()? onTapOpenTicketInvoice;
  final Function()? onTapInvoice;
  final Function()? onTapPrintInvoice;
  final Function()? onTapCancelRegenerate;
  final Function()? onTapReprintInvoice;
  final Function()? onTapInvoicePayment;

  CustomerCafInvoiceViewItem({
    Key? key,
    required this.index,
    required this.customerDetail,
    required this.item,
    required this. controller,
    required this.onTapDocumentGenerate,
    required this.onTapDownload,
    required this.onTapOpenTicketInvoice,
    required this.onTapInvoice,
    required this.onTapPrintInvoice,
    required this.onTapCancelRegenerate,
    required this.onTapReprintInvoice,
    required this.onTapInvoicePayment,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    DateTime date = DateFormat(Constant.DATE_TIME_FORMAT_API).parse(item.billdate!);
    bool? isPaymentButton = false;
  if(item.adjustedAmount!= null){
    isPaymentButton = (item.totalamount! - item.adjustedAmount! >= 1);
  }else{
    isPaymentButton = (item.totalamount! - 0 >= 1);
  }


    String startDt = DateFormat(Constant.API_DATE_FORMAT).format(date);
    return Container(
      padding: const EdgeInsets.symmetric(
        vertical: Constant.SMALL_PADDING,
        horizontal: Constant.SMALL_PADDING + 2,
      ),
      child: Material(
        color: AppTheme.colorWhite,
        elevation: 0.5,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
        child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              // Padding(
              //   padding: const EdgeInsets.symmetric(
              //       horizontal: Constant.SMALL_PADDING + 2),
              //   child: basicDetailItem(
              //     Strings.name,
              //     (item.customerName != null && item.customerName!.isNotEmpty)
              //         ? item.customerName!
              //         : "-",
              //     Strings.invoice_no,
              //     (item.docnumber != null && item.docnumber!.isNotEmpty)
              //         ? item.docnumber!
              //         : "-",
              //   ),
              // ),


              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SMALL_PADDING + 2),
                child: Row(
                  mainAxisSize: MainAxisSize.max,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  crossAxisAlignment: CrossAxisAlignment.center,
                  children: [
                    Expanded(
                      flex: 1,
                      child: InkWell(
                        // onTap: title1.equalsIgnoreCase(Strings.name) ? onTapDetail : null,
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.start,
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            titleWidget(Strings.name),
                            const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                            valueWidget(
                                (item.customerName != null && item.customerName!.isNotEmpty)
                                    ? item.customerName!
                                    : "-",
                                Strings.name.equalsIgnoreCase(Strings.name)
                                    ? AppTheme.colorPrimary
                                    : AppTheme.title_dark),
                          ],
                        ),
                      ),
                    ),
                    Column(
                      mainAxisAlignment: MainAxisAlignment.start,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        titleWidget(Strings.invoice_no),
                        const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                        InkWell(
                          onTap: () async {
                            await Get.to(CustomerCAFInvoiceDetailScreen(), arguments: {
                              Constant.CUSTOMER_DETAIL: customerDetail,
                              Constant.INVOICE_DETAIL: item,
                            });

                          },
                          child: valueWidget((item.docnumber != null && item.docnumber!.isNotEmpty)
                              ? item.docnumber!
                              : "-", Strings.invoice_no.equalsIgnoreCase(Strings.invoice_no)
                              ? AppTheme.colorPrimary
                              : AppTheme.title_dark),
                        )

                      ],
                    ),
                  ],
                ),
              ),
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SMALL_PADDING),
                child: basicDetailItem(
                  Strings.purchase_by,
                  (item.createdByName != null && item.createdByName!.isNotEmpty)
                      ? item.createdByName!
                      : "-",
                  Strings.bill_to,
                  (item.billableToName != null &&
                          item.billableToName!.isNotEmpty)
                      ? item.billableToName!
                      : "-",
                ),
              ),
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SMALL_PADDING),
                child: basicDetailItem(
                  Strings.payment_owner,
                  // (item.paymentowner != null && item.paymentowner!.isNotEmpty)
                  //     ? item.paymentowner!
                  //     : "-",
                  "-",
                  Strings.bill_date,
                  (item.billdate != null && item.billdate!.isNotEmpty)
                      ? startDt
                      : "-",
                ),
              ),
              const SizedBox(
                height: Constant.MEDIUM_PADDING,
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.VERY_SMALL_PADDING),
                child: Row(
                  mainAxisSize: MainAxisSize.max,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Column(
                      children: [
                        titleWidget(Strings.bill_run_status),
                        const SizedBox(
                          height: Constant.VERY_SMALL_PADDING,
                        ),
                        statusBgView(
                          status: item.billrunstatus!,
                          bgColor: AppTheme.colorGreen,
                          textColor: AppTheme.colorWhite,
                          fontSize: AppTheme.small,
                          fontWeight: FontWeight.normal,
                        ),
                      ],
                    ),
                    Column(
                      children: [
                        titleWidget(Strings.payment_status),
                        const SizedBox(
                          height: Constant.VERY_SMALL_PADDING,
                        ),
                        statusBgView(
                          status: item.paymentStatus ?? "Pending",
                          bgColor: AppTheme.statusClosedGreen,
                          textColor: AppTheme.colorWhite,
                          fontSize: AppTheme.small,
                          fontWeight: FontWeight.normal,
                        ),
                      ],
                    )
                  ],
                ),
              ),
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              Divider(
                color: AppTheme.title_dark,
                height: 1,
              ),
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SMALL_PADDING),
                child: Row(
                    mainAxisAlignment: MainAxisAlignment.end,
                    // crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      !customerDetail!.status!.equalsIgnoreCase("Terminate") &&
                          (item.billrunstatus!= null && item.billrunstatus!.equalsIgnoreCase("Generated"))
                          ? buttonView(generateSvg, AppTheme.colorPrimary,
                              AppTheme.colorWhite, onTapDocumentGenerate!)
                          : buttonView(pdfSvg, AppTheme.colorPrimary,
                          AppTheme.colorWhite, onTapDownload!),
                      !customerDetail!.status!.equalsIgnoreCase("Terminate") &&
                              item.billrunstatus!.equalsIgnoreCase("Generated")
                          ? const SizedBox(
                              width: Constant.MEDIUM_PADDING,
                            )
                          : const SizedBox(
                              width: Constant.MEDIUM_PADDING,
                            ),

                      !item.billrunstatus!.equalsIgnoreCase("Cancelled")
                          ? buttonView(
                              openPrintInvoiceSvg,
                          AppTheme.colorPrimary,
                          AppTheme.colorWhite,
                              onTapPrintInvoice!)
                          : const SizedBox.shrink(),
                      !item.billrunstatus!.equalsIgnoreCase("Cancelled")
                          ? const SizedBox(
                              width: Constant.MEDIUM_PADDING,
                            )
                          : const SizedBox.shrink(),

                      buttonView(reprintInvoiceSvg,AppTheme.colorPrimary,
                          AppTheme.colorWhite, onTapReprintInvoice!),
                      const SizedBox(
                        width: Constant.MEDIUM_PADDING,
                      ),

                      isPaymentButton  && !item.billrunstatus!.equalsIgnoreCase("Cancelled")?
                      InkWell(
                        onTap: onTapInvoicePayment,
                        child: Container(
                            height: Constant.BTN_HEIGHT_M - 5,
                            alignment: Alignment.center,
                            padding: const EdgeInsets.all(Constant.SMALL_PADDING - 1),
                            decoration:BoxDecoration(borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),color: AppTheme.colorPrimary),
                            child: Row(
                              children: [
                                SvgPicture.asset(
                                  ticketPromiseToPaySvg,
                                  height: Constant.ICON_SIZE + 1,
                                  width: Constant.ICON_SIZE + 1,
                                  color: AppTheme.colorWhite,
                                  fit: BoxFit.fitWidth,
                                ),
                                const SizedBox(
                                  width: Constant.VERY_SMALL_PADDING,
                                ),
                                CustomText(title: Strings.pay,fontSize: AppTheme.small-2,),
                              ],
                            )),
                      ) : const SizedBox.shrink(),

                    ]),
              ),
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              Divider(
                color: AppTheme.title_dark,
                height: 1,
              ),
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              IntrinsicHeight(
                child: Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.spaceAround,
                  children: [
                    Expanded(
                      flex: 1,
                      child: Column(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            CustomText(
                              title: "${controller!.currencySymbol} ${item.totalamount ?? 00.00.toStringAsFixed(2)}",
                              fontSize: AppTheme.large,
                              maxLines: 2,
                              colors: AppTheme.colorGreen,
                              textAlign: TextAlign.start,
                              fontWeight: FontWeight.w600,
                            ),
                            const SizedBox(
                              height: Constant.SMALL_PADDING,
                            ),
                            CustomText(
                              title: Strings.total_amount,
                              fontSize: AppTheme.verySmall,
                              colors: AppTheme.title_dark,
                              textAlign: TextAlign.start,
                              fontWeight: FontWeight.normal,
                            ),
                          ]),
                    ),
                    VerticalDivider(
                      color: AppTheme.title_dark,
                      thickness: 0.4,
                    ),
                    Expanded(
                      flex: 1,
                      child: Column(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            CustomText(
                              title: item.adjustedAmount!= null ? "${controller!.currencySymbol} ${item.adjustedAmount ?? 00.00.toStringAsFixed(2)}" : "${controller!.currencySymbol} 00.00",
                              fontSize: AppTheme.large,
                              maxLines: 2,
                              colors: AppTheme.colorPrimary,
                              textAlign: TextAlign.start,
                              fontWeight: FontWeight.w600,
                            ),
                            const SizedBox(
                              height: Constant.SMALL_PADDING,
                            ),
                            CustomText(
                              title: Strings.adjusted_amount,
                              fontSize: AppTheme.verySmall,
                              colors: AppTheme.title_dark,
                              textAlign: TextAlign.start,
                              fontWeight: FontWeight.normal,
                            ),
                          ]),
                    ),
                  ],
                ),
              ),
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
            ]),
      ),
    );
  }

  buttonView(String btnName, Color bgColor, Color txtColor, Function() onTap) {
    return InkWell(
      onTap: onTap,
      child: Material(
        elevation: 3.0,
        color: bgColor,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
        child: Container(
          height: Constant.BTN_HEIGHT_M - 5,
          width: Constant.BTN_HEIGHT_M - 5,
          alignment: Alignment.center,
          padding: const EdgeInsets.all(Constant.SMALL_PADDING - 1),
          child: SvgPicture.asset(
            btnName,
            height: Constant.ICON_SIZE + 5,
            width: Constant.ICON_SIZE + 5,
            color: txtColor,
            fit: BoxFit.fitWidth,
          ),
        ),
      ),
    );
  }

  basicDetailItem(
      String title1, String? value1, String title2, String? value2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        Expanded(
          flex: 1,
          child: InkWell(
            // onTap: title1.equalsIgnoreCase(Strings.name) ? onTapDetail : null,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                titleWidget(title1),
                const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                valueWidget(
                    value1,
                    title1.equalsIgnoreCase(Strings.name)
                        ? AppTheme.colorPrimary
                        : AppTheme.title_dark),
              ],
            ),
          ),
        ),
        Column(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            titleWidget(title2),
            const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
            valueWidget(value2, AppTheme.title_dark),
          ],
        ),
      ],
    );
  }

  titleWidget(String title) {
    return CustomText(
      title: title,
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.verySmall,
      fontWeight: FontWeight.w400,
      maxLines: 2,
    );
  }

  valueWidget(String? value, Color txtColors) {
    return CustomText(
      title: value!.isNotEmpty ? value : "",
      colors: txtColors,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.normal,
      maxLines: 2,
    );
  }
}
