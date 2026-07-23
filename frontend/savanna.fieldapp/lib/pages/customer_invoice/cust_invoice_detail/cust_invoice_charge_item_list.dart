import 'package:savbill/pages/customer_invoice/cust_invoice_detail/cust_invoice_detail_controller.dart';
import 'package:savbill/pages/customer_invoice/cust_invoice_detail/model/cust_invoice_detail_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';

class CustInvoiceChargeItem extends StatelessWidget {
  DebitDocDetail item;
  CustomerInvoiceDetailController? controller;

  CustInvoiceChargeItem({
    Key? key,
    required this.item,
    required this.controller,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    // String actionDate = "";
    // if (item.actionDateTime != null && item.actionDateTime!.isNotEmpty) {
    //   DateTime date =
    //   DateFormat(Constant.DATE_TIME_FORMAT_API).parse(item.actionDateTime!);
    //   actionDate =
    //       DateFormat("${Constant.DATE_FORMAT} ${Constant.APP_TIME_FORMAT}")
    //           .format(date);
    // }
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
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Expanded(
                    child: CustomText(
                        title: item.chargename != null &&
                                item.chargename!.isNotEmpty
                            ? item.chargename
                            : "",
                        colors: AppTheme.title_dark,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.small,
                        maxLines: 2,
                        height: 1,
                        fontWeight: FontWeight.w500)),
              ],
            ),
          ),
          Divider(
            color: AppTheme.title_dark,
            thickness: 0.5,
            height: Constant.MEDIUM_PADDING,
          ),
          const SizedBox(
            height: Constant.VERY_SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                Strings.sub_total,
                (item.subtotal != null)
                    ? item.subtotal!.toStringAsFixed(2)
                    : "-",
                Strings.discount,
                (item.discount != null) ? item.discount.toString() : "-"),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                Strings.tax,
                (item.tax != null) ? item.tax!.toStringAsFixed(2) : "-",
                Strings.total_amount,
                (item.totalamount != null)
                    ? item.totalamount!.toStringAsFixed(2)
                    : "-"),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                Strings.description,
                (item.description != null && item.description!.isNotEmpty)
                    ? item.description!
                    : "-",
                "",
                "-"),
          ),
        ]),
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
          // flex: 1,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title1),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value1),
            ],
          ),
        ),
        Expanded(
          // flex: 1,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
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
      title: value!.isNotEmpty ? value : "",
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.normal,
      maxLines: 2,
    );
  }
}
