import 'package:savbill/pages/dashboard/model/response/payment_invoice_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

class InvoiceMapItem extends StatelessWidget {
  PaymentInvoice item;

  InvoiceMapItem({
    Key? key,
    required this.item,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    String billDate = "";
    if (item.billdate != null && item.billdate!.isNotEmpty) {
      DateTime date =
          DateFormat(Constant.DATE_TIME_FORMAT_API).parse(item.billdate!);
      billDate = DateFormat(Constant.DATE_FORMAT).format(date);
    }

    return Container(
      margin: const EdgeInsets.only(
        bottom: Constant.MEDIUM_PADDING,
      ),
      child: Material(
        color: AppTheme.colorWhite,
        elevation: 0.5,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          IntrinsicHeight(
            child: Row(
              children: [
                const SizedBox(
                  width: Constant.VERY_SMALL_PADDING,
                ),
                Expanded(
                  flex: 1,
                  child: CustomText(
                    title: item.docnumber,
                    colors: AppTheme.lable_noramal,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.verySmall + 1,
                    fontWeight: FontWeight.w400,
                    maxLines: 2,
                  ),
                ),
                const SizedBox(
                  width: Constant.VERY_SMALL_PADDING + 2,
                ),
                VerticalDivider(
                  color: AppTheme.title_dark,
                  thickness: 0.5,
                ),
                Expanded(
                  flex: 1,
                  child: CustomText(
                    title: item.totalamount ?? "-",
                    colors: AppTheme.lable_noramal,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.verySmall + 1,
                    fontWeight: FontWeight.w400,
                    maxLines: 2,
                  ),
                ),
                const SizedBox(
                  width: Constant.VERY_SMALL_PADDING + 2,
                ),
                VerticalDivider(
                  color: AppTheme.title_dark,
                  thickness: 0.5,
                ),
                Expanded(
                  flex: 1,
                  child: CustomText(
                    title: item.adjustedAmount ?? "-",
                    colors: AppTheme.lable_noramal,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.verySmall + 1,
                    fontWeight: FontWeight.w400,
                    maxLines: 2,
                  ),
                ),
                const SizedBox(
                  width: Constant.VERY_SMALL_PADDING + 2,
                ),
                VerticalDivider(
                  color: AppTheme.title_dark,
                  thickness: 0.5,
                ),
                Expanded(
                  flex: 1,
                  child: CustomText(
                    title: billDate.isNotEmpty ? billDate : "-",
                    colors: AppTheme.lable_noramal,
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
            ),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
        ]),
      ),
    );
  }
}
