import 'dart:developer';
import 'package:savbill/pages/credit_note/response/credit_note_res.dart';
import 'package:savbill/pages/customer/credit_note_customer/customer_view_credit_note_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';

class CustomerCreditViewListItem extends StatelessWidget {
  CreditNoteDetailsList item;
  int index, userId;
  CustomerCreditNoteController? controller;
  bool? isShowBtn;
  String? currency;
  bool showStatusBtn = true,
      showDownloadBtn = true,
      showApproveBtn = true,
      showPickBtn = true,
      showReassignBtn = true;

  CustomerCreditViewListItem(
      {Key? key,
      required this.index,
      required this.item,
      required this.userId,
      this.controller,
      this.isShowBtn,
      this.currency})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    String paymentDate = "", status = "";
    Color? statusTxtColor = AppTheme.statusPending,
        typeBgColor = AppTheme.statusUnAssignGray;

    if (item.paymentdate != null && item.paymentdate!.isNotEmpty) {
      paymentDate = item.paymentdate!;
    }
    if (item.status!.equalsIgnoreCase(Strings.pending) ||
        item.status!.equalsIgnoreCase(Strings.rejected)) {
      showDownloadBtn = false;
    } else {
      showDownloadBtn = true;
    }

    if (item.status!.equalsIgnoreCase(Strings.fully_adjusted) ||
        item.status!.equalsIgnoreCase(Strings.advance.toLowerCase()) ||
        item.status!.equalsIgnoreCase(Strings.approved.toLowerCase()) ||
        item.status!.equalsIgnoreCase("Partially Adjusted") ||
        item.status!.equalsIgnoreCase("Partialy Adjusted") ||
        item.status!.equalsIgnoreCase(Strings.rejected.toLowerCase()) ||
        item.approverid != null) {
      showPickBtn = false;
    } else {
      showPickBtn = true;
    }

/*______________________________ approve & rejected ________________________________________*/

    if (item.status!.equalsIgnoreCase(Strings.fully_adjusted) ||
        item.status!.equalsIgnoreCase(Strings.advance.toLowerCase()) ||
        item.status!.equalsIgnoreCase(Strings.approved.toLowerCase()) ||
        item.status!.equalsIgnoreCase(Strings.rejected.toLowerCase()) ||
        item.status!.equalsIgnoreCase("Partialy Adjusted") ||
        item.status!.equalsIgnoreCase(Strings.partialy_adjusted) ||
        item.approverid != controller?.userDetail?.userId) {
      showApproveBtn = false;
    } else {
      showApproveBtn = true;
    }

/*_____________________________ reassign ______________________________________*/

    if (item.status!.isNotEmpty) {
      if (item.status!.equalsIgnoreCase(Strings.pending.toLowerCase())) {
        statusTxtColor = AppTheme.statusPending;
        status = Strings.generated;
      } else if (item.status!.equalsIgnoreCase(Strings.approved) ||
          item.status!.equalsIgnoreCase(Strings.fully_adjusted)) {
        statusTxtColor = AppTheme.statusApprove;
        status = Strings.adjusted;
      } else if (item.status!.equalsIgnoreCase(Strings.partialy_adjusted)) {
        statusTxtColor = AppTheme.statusApprove;
        status = Strings.partialy_adjusted;
      } else if (item.status!.equalsIgnoreCase(Strings.rejected)) {
        statusTxtColor = AppTheme.statusReject;
        status = Strings.rejected;
      }
      // status = item.status!;
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
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  Expanded(
                    flex: 1,
                    child: InkWell(
                      onTap: (){},
                      child: CustomText(
                          title: item.customerName ?? "",
                          colors: AppTheme.colorAccent,
                          textAlign: TextAlign.start,
                          maxLines: 2,
                          fontSize: AppTheme.small,
                          fontWeight: FontWeight.w500),
                    ),
                  ),
                  Flexible(
                    flex: 1,
                    child: Container(
                      decoration: BoxDecoration(
                          color: statusTxtColor,
                          borderRadius:
                              BorderRadius.circular(Constant.MEDIUM_PADDING)),
                      padding: const EdgeInsets.symmetric(
                          vertical: Constant.STATUS_PADDING_LR,
                          horizontal: Constant.SMALL_PADDING),
                      child: CustomText(
                          title: status,
                          colors: AppTheme.colorWhite,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.small,
                          fontWeight: FontWeight.w500),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Row(
              children: [
                Expanded(
                  flex: 1,
                  child: Padding(
                    padding: const EdgeInsets.symmetric(
                        horizontal: Constant.SMALL_PADDING),
                    child: CustomText(
                      title:
                          "${isShowBtn == false ? Strings.payment_by : Strings.create_by} : ${isShowBtn == false ? item.customerName ?? "" : item.createbyname ?? ""}",
                      colors: isShowBtn == false
                          ? AppTheme.lable_noramal
                          : AppTheme.lable_noramal,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.verySmall + 1,
                      fontWeight: FontWeight.w400,
                    ),
                  ),
                ),
                Expanded(
                  flex: 1,
                  child: Padding(
                    padding: const EdgeInsets.symmetric(
                        horizontal: Constant.SMALL_PADDING),
                    child: CustomText(
                      title:
                          "${Strings.credit_date} : ${item.paymentdate ?? ""}",
                      colors: AppTheme.lable_noramal,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.verySmall + 1,
                      fontWeight: FontWeight.w400,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Row(
              children: [
                Expanded(
                  flex: 1,
                  child: Padding(
                    padding: const EdgeInsets.symmetric(
                        horizontal: Constant.SMALL_PADDING),
                    child: Row(
                      children: [
                        CustomText(
                          title: "${Strings.amount} : ",
                          colors: AppTheme.lable_noramal,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.verySmall + 1,
                          fontWeight: FontWeight.w400,
                        ),
                        InkWell(
                          onTap: (){},
                          child: CustomText(
                            title: "$currency${item.amount ?? ""}",
                            colors: AppTheme.colorAccent,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.verySmall + 1,
                            fontWeight: FontWeight.w400,
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
                Expanded(
                  flex: 1,
                  child: Padding(
                    padding: const EdgeInsets.symmetric(
                        horizontal: Constant.SMALL_PADDING),
                    child: CustomText(
                      title:
                          "${Strings.credit_note_no} : ${item.documentno ?? ""}",
                      colors: AppTheme.lable_noramal,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.verySmall + 1,
                      fontWeight: FontWeight.w400,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Row(
              children: [
                Expanded(
                  flex: 1,
                  child: Padding(
                    padding: const EdgeInsets.symmetric(
                        horizontal: Constant.SMALL_PADDING),
                    child: CustomText(
                      title:
                          "${Strings.reference_no} : ${item.referenceno ?? ""}",
                      colors: AppTheme.lable_noramal,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.verySmall + 1,
                      fontWeight: FontWeight.w400,
                    ),
                  ),
                ),
                Expanded(
                  flex: 1,
                  child: Padding(
                    padding: const EdgeInsets.symmetric(
                        horizontal: Constant.SMALL_PADDING),
                    child: CustomText(
                      title: "${Strings.remarks} : ${item.remarks ?? ""}",
                      colors: AppTheme.lable_noramal,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.verySmall + 1,
                      fontWeight: FontWeight.w400,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(
              height: Constant.MEDIUM_PADDING,
            ),

          ],
        ),
      ),
    );
  }
}
