import 'package:savbill/pages/customer_payment/customer_paymentlist_controller.dart';
import 'package:savbill/pages/dashboard/model/response/payment_list_response.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/status_bg_view.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

class PaymentViewListItem extends StatelessWidget {
  PaymentDetail item;
  int index, userId;
  CustomerPaymentListController? controller;
  bool showStatusBtn = false, showDownloadBtn = false;

  PaymentViewListItem(
      {Key? key,
      required this.index,
      required this.item,
      required this.userId,
      this.controller})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    String paymentDate = "", status = "", paymode = "";
    Color? statusTxtColor = AppTheme.statusPending,
        typeBgColor = AppTheme.statusUnAssignGray;
    if (item.paymentdate != null && item.paymentdate!.isNotEmpty) {
      paymentDate = item.paymentdate!;
    }

    if ((item.approverid != null && userId == item.approverid) &&
        (item.status!.isNotEmpty &&
            item.status!.equalsIgnoreCase(Strings.pending))) {
      showStatusBtn = true;
    } else {
      showStatusBtn = false;
    }
    if ((item.status!.isNotEmpty &&
        !item.status!.equalsIgnoreCase(Strings.pending))) {
      showDownloadBtn = true;
    } else {
      showDownloadBtn = false;
    }

    if (item.paymode!.isNotEmpty) {
      if (item.paymode!.equalsIgnoreCase(Strings.online)) {
        paymode = Strings.online;
        typeBgColor = AppTheme.colorPrimary;
      } else if (item.paymode!.equalsIgnoreCase(Strings.cheque)) {
        paymode = Strings.cheque;
        typeBgColor = AppTheme.statusClosedGreen;
      } else if (item.paymode!.equalsIgnoreCase(Strings.cash)) {
        paymode = Strings.cash;
        typeBgColor = AppTheme.statusUnAssignGray;
      } else {
        paymode = item.paymode!;
        typeBgColor = AppTheme.statusUnAssignGray;
      }
    }

    if (item.status!.isNotEmpty) {
      if (item.status!.equalsIgnoreCase(Strings.pending.toLowerCase()) &&
          item.nextTeamHierarchyMappingId == null) {
        statusTxtColor = AppTheme.statusPending;
        status = Strings.collected;
      } else if (item.status!
              .equalsIgnoreCase(Strings.approved.toLowerCase()) ||
          item.status!.equalsIgnoreCase(Strings.fully_adjusted) ||
          item.status!.equalsIgnoreCase(Strings.advance.toLowerCase()) ||
          item.status!.equalsIgnoreCase("Partialy Adjusted") ||
          item.status!.equalsIgnoreCase(Strings.partialy_adjusted)) {
        statusTxtColor = AppTheme.statusApprove;
        status = Strings.verified;
      } else if (item.status!
          .equalsIgnoreCase(Strings.rejected.toLowerCase())) {
        statusTxtColor = AppTheme.statusReject;
        status = Strings.rejected;
      } else if (item.status!.equalsIgnoreCase(Strings.pending.toLowerCase()) ||
          item.nextTeamHierarchyMappingId != null) {
        statusTxtColor = AppTheme.statusPending;
        status = Strings.submitted;
      } else {
        status = item.status!;
      }
    }

    return Container(
      margin: const EdgeInsets.only(
        left: Constant.SCREEN_PADDING,
        right: Constant.SCREEN_PADDING,
        bottom: Constant.MEDIUM_PADDING,
      ),
      child: Material(
        color: AppTheme.colorWhite,
        elevation: 0.5,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Expanded(
                    flex: 1,
                    child: Row(
                      children: [
                        statusBgView(
                          status: paymode,
                          bgColor: typeBgColor,
                          textColor: AppTheme.colorWhite,
                          fontSize: AppTheme.small,
                          fontWeight: FontWeight.normal,
                        ),
                      ],
                    ),
                  ),
                  // CustomText(
                  //     title: status,
                  //     colors: statusTxtColor,
                  //     textAlign: TextAlign.start,
                  //     fontSize: AppTheme.small,
                  //     fontWeight: FontWeight.w500),

                  Padding(
                    padding: const EdgeInsets.symmetric(
                        horizontal: Constant.VERY_SMALL_PADDING,
                        vertical: Constant.VERY_SMALL_PADDING),
                    child: Container(
                      padding: const EdgeInsets.symmetric(
                          horizontal: Constant.SMALL_PADDING,
                          vertical: Constant.VERY_SMALL_PADDING),
                      decoration: BoxDecoration(
                        borderRadius:
                            BorderRadius.circular(Constant.LARGE_PADDING),
                        color: statusTxtColor,
                      ),
                      child: CustomText(
                          title: status,
                          colors: AppTheme.colorWhite,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.small,
                          maxLines: 2,
                          height: 1,
                          fontWeight: FontWeight.w500),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            /* Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: RichText(
                maxLines: 2,
                softWrap: true,
                text: TextSpan(
                  text:
                  "#${isShowBtn == true ? item.referenceno ?? "" : item.receiptNo ?? ""} ",
                  style: TextStyle(
                    fontWeight: FontWeight.bold,
                    fontSize: AppTheme.small + 1,
                    color: AppTheme.title_dark,
                  ),
                ),
              ),
            ),*/
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: basicDetailItem(
                  Strings.receipt_no,
                  "#${item.creditdocumentno ?? ""}",
                  Strings.payment_by,
                  (item.paymentBy != null && item.paymentBy!.isNotEmpty)
                      ? item.paymentBy
                      : "-"),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: basicDetailItem(
                Strings.payment_amount,
                "${controller!.currencySymbol}${item.amount ?? ""}  ",
                Strings.invoice_no,
                "${item.invoiceNumber ?? ""}",
              ),
            ),

            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: basicDetailItem(
                Strings.TDS,
                "${item.tdsamount ?? ""}  ",
                Strings.ABBS,
                "${item.abbsAmount ?? ""}",
              ),
            ),

            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: basicDetailItem(
                Strings.bank_name,
                "${item.bankName ?? ""}  ",
                Strings.cheque_no,
                "${item.paydetails2 ?? ""}  ",
              ),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: basicDetailItem(
                Strings.source,
                "${item.onlinesource ?? ""}  ",
                Strings.unsettled_amount,
                "${controller!.currencySymbol}${item.unsettledAmount ?? ""}",
              ),
            ),

            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: basicDetailItem(
                Strings.payment_date,
                "${item.paymentdate ?? ""}  ",
                Strings.remarks,
                "${item.remarks ?? ""}  ",
              ),
            ),
            // cardButtonRow(),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),

            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: Row(
                mainAxisSize: MainAxisSize.max,
                crossAxisAlignment: CrossAxisAlignment.end,
                children: [
                  Expanded(
                    flex: 1,
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      crossAxisAlignment: CrossAxisAlignment.end,
                      children: [
                        item.filename != null
                            ? Material(
                                elevation: 3.0,
                                color: AppTheme.colorPrimary,
                                shape: RoundedRectangleBorder(
                                    borderRadius: BorderRadius.circular(
                                        Constant.BTN_ROUNDED_CORNER)),
                                child: InkWell(
                                  onTap: (){
                                    // controller!.custDownloadInvoice(item.id, item.filename,item.custId);
                                    controller!.custDownloadInvoice(Strings.payment,
                                        "${UrlConstants.custInvoiceDownload}/${item.id}/${item.custId}",item.customerName);
                                  },
                                  child: Container(
                                    height: Constant.BTN_HEIGHT_M- 5,
                                    width: Constant.BTN_HEIGHT_M -5,
                                    alignment: Alignment.center,
                                    padding: const EdgeInsets.all(
                                        Constant.VERY_SMALL_PADDING - 1),
                                    child: SvgPicture.asset(
                                      pdfSvg,
                                      height: Constant.ICON_SIZE + 5,
                                      width: Constant.ICON_SIZE + 5,
                                      color: AppTheme.colorWhite,
                                      fit: BoxFit.fitWidth,
                                    ),
                                  ),
                                ),
                              )
                            : const SizedBox.shrink()
                      ],
                    ),
                  ),
                  const SizedBox(width: Constant.SMALL_PADDING,)
                ],
              ),
            ),
            // cardButtonRow(),
            const SizedBox(
              height: Constant.SMALL_PADDING,
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
      children: [
        Expanded(
          flex: 3,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
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
            mainAxisAlignment: MainAxisAlignment.center,
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

  valueWidget(String? value) {
    return CustomText(
      title: value!.isNotEmpty ? value : "-",
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.normal,
      maxLines: 2,
    );
  }

  // cardButtonRow() {
  //   return Row(mainAxisAlignment: MainAxisAlignment.end, children: [
  //     showDownloadBtn
  //         ? buttonView(fileDownloadSvg, AppTheme.custUploadFileLight,
  //             AppTheme.custUploadFileDark, onDownloadTap!)
  //         : Container(),
  //     showDownloadBtn
  //         ? const SizedBox(
  //             width: Constant.SMALL_PADDING,
  //           )
  //         : Container(),
  //     showStatusBtn
  //         ? buttonView(checkSvg, AppTheme.custEditLight, AppTheme.custEditDark,
  //             onApproveTap!)
  //         : Container(),
  //     showStatusBtn
  //         ? const SizedBox(
  //             width: Constant.SMALL_PADDING,
  //           )
  //         : Container(),
  //     showStatusBtn
  //         ? buttonView(cancelSvg, AppTheme.custDeleteLight,
  //             AppTheme.custDeleteDark, onRejectTap!)
  //         : Container(),
  //     showStatusBtn
  //         ? const SizedBox(
  //             width: Constant.SMALL_PADDING,
  //           )
  //         : Container(),
  //     buttonView(invoiceDetailSvg, AppTheme.custPaymentLinkLight,
  //         AppTheme.custPaymentLinkDark, onInvoiceTap!),
  //     const SizedBox(
  //       width: Constant.SMALL_PADDING,
  //     ),
  //     (isShowBtn != null && isShowBtn == true)
  //         ? buttonView(auditStatusSvg, AppTheme.custAssignInventoryLight,
  //             AppTheme.custAssignInventoryDark, onAuditStatusTap!)
  //         : Container(),
  //     (isShowBtn != null && isShowBtn == true)
  //         ? const SizedBox(
  //             width: Constant.SMALL_PADDING,
  //           )
  //         : Container(),
  //   ]);
  // }

  buttonView(String btnName, Color bgColor, Color txtColor, Function() onTap) {
    return InkWell(
      onTap: onTap,
      child: Material(
        elevation: 1.5,
        color: bgColor,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
        child: Container(
          height: Constant.BTN_HEIGHT_M - 10,
          width: Constant.BTN_HEIGHT_M - 10,
          alignment: Alignment.center,
          padding: const EdgeInsets.all(Constant.SMALL_PADDING - 1),
          child: SvgPicture.asset(
            btnName,
            height: Constant.ICON_SIZE,
            width: Constant.ICON_SIZE,
            color: txtColor,
            fit: BoxFit.fill,
          ),
        ),
      ),
    );
  }
}
