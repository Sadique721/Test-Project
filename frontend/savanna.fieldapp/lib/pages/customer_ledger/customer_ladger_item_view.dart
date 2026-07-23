import 'dart:math';

import 'package:savbill/pages/customer_ledger/customer_ledger_controller.dart';
import 'package:savbill/pages/customer_ledger/response/customer_ledger_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';

class CustomerLadgerViewItem extends StatelessWidget {
  LedgerDebitCreditDetail item;
  int index;
  CustomerLedgerController controller;

  CustomerLadgerViewItem(
      {Key? key,
      required this.index,
      required this.item,
      required this.controller})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    String? first;
      if (item.invoiceNo != null && item.invoiceNo!.isNotEmpty) {
        for (var element in item.invoiceNo!) {
          if (element == null){
            first: "-";
          }else {
            first  = element;
          }
        }
      } else {
        first = "-";
      }
    return Card(
      margin: const EdgeInsets.only(
        top: Constant.SMALL_PADDING,
        left: Constant.SCREEN_PADDING,
        right: Constant.SCREEN_PADDING,
        bottom: Constant.SMALL_PADDING,
      ),
      elevation: 2,
      color: AppTheme.colorWhite,
      child: Padding(
        padding: const EdgeInsets.symmetric(
          vertical: Constant.SMALL_PADDING,
          horizontal: Constant.SMALL_PADDING,
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    RichText(
                      text: TextSpan(
                          text: "${item.transcategory} ",
                          style: TextStyle(
                            color: AppTheme.title_dark,
                            fontSize: AppTheme.small + 1,
                            fontWeight: FontWeight.w500,
                          ),
                          children: [
                            TextSpan(
                              text: "#${item.transtype}",
                              style: TextStyle(
                                color: AppTheme.colorBlueRView,
                                fontSize: AppTheme.verySmall,
                                fontWeight: FontWeight.w500,
                              ),
                            ),
                          ]),
                    ),
                    const SizedBox(
                      height: 2,
                    ),
                    CustomText(
                        title:
                            "${Strings.receipt_no}/${Strings.credit_no}: ${item.paymentRefNo ?? "-"}",
                        colors: AppTheme.colorBlack,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.verySmall - 1,
                        fontWeight: FontWeight.w400),
                  ],
                ),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.end,
                  children: [
                    CustomText(
                        title: item.cREATEDATE,
                        colors: AppTheme.colorBlack,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.verySmall,
                        fontWeight: FontWeight.w400),
                    const SizedBox(
                      height: 2,
                    ),
                    CustomText(
                        title: first,
                        colors: AppTheme.colorBlack,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.verySmall - 1,
                        fontWeight: FontWeight.w400),
                  ],
                )
              ],
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Row(
              children: [
                Expanded(
                  flex: 1,
                  child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        CustomText(
                          title: Strings.category,
                          fontSize: AppTheme.verySmall,
                          colors: AppTheme.title_dark,
                          textAlign: TextAlign.start,
                          fontWeight: FontWeight.normal,
                        ),
                        const SizedBox(
                          height: Constant.VERY_SMALL_PADDING,
                        ),
                        CustomText(
                          title: item.transcategory! ?? "",
                          fontSize: AppTheme.verySmall,
                          colors: AppTheme.lable_noramal,
                          textAlign: TextAlign.start,
                          fontWeight: FontWeight.normal,
                        ),
                      ]),
                ),
                Expanded(
                  flex: 1,
                  child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        CustomText(
                          title: item.remarks != null ? Strings.remarks : "",
                          fontSize: AppTheme.verySmall,
                          colors: AppTheme.title_dark,
                          textAlign: TextAlign.start,
                          fontWeight: FontWeight.normal,
                        ),
                        const SizedBox(
                          height: Constant.VERY_SMALL_PADDING,
                        ),
                        CustomText(
                          title: item.remarks ?? "",
                          fontSize: AppTheme.verySmall,
                          maxLines: 2,
                          overflow: TextOverflow.ellipsis,
                          colors: AppTheme.lable_noramal,
                          textAlign: TextAlign.start,
                          fontWeight: FontWeight.normal,
                        ),
                      ]),
                ),
              ],
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
                            title:
                                "${controller.currencySymbol} ${item.amount ?? 0}",
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
                            title: item.transtype!.equalsIgnoreCase("DR")
                                ? "${Strings.debit} ${Strings.amount}"
                                : "${Strings.credit} ${Strings.amount}",
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
                            title:
                                "${controller.currencySymbol} ${item.balAmount ?? 0}",
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
                            title: Strings.bal_amount,
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
          ],
        ),
      ),
    );
  }
}
