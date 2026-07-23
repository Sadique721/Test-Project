import 'package:savbill/pages/customer_invoice/invoice_payment_details_controller.dart';
import 'package:savbill/pages/customer_invoice/response/invoice_payment_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

class InvoicePaymentItem extends StatelessWidget {
  InvoicePaymentList item;
  InvoicePaymentListController? controller;

  InvoicePaymentItem({Key? key, required this.item, required this.controller})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(
        bottom: Constant.MEDIUM_PADDING,
      ),
      child: Material(
        color: item.isSelected == true ? AppTheme.colorYellowBtn : AppTheme.colorWhite,
        elevation: 0.5,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
        child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const SizedBox(
                height: Constant.VERY_SMALL_PADDING,
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SMALL_PADDING,
                    vertical: Constant.VERY_SMALL_PADDING),
                child: basicDetailItem(
                    Strings.reference_no,
                    (item.referenceno != null && item.referenceno!.isNotEmpty)
                        ? "${item.referenceno}"
                        : "-",
                    Strings.payment_date,
                    (item.paymentdate != null && item.paymentdate!.isNotEmpty)
                        ? "${item.paymentdate}"
                        : "-"),
              ),
              const SizedBox(
                height: Constant.VERY_SMALL_PADDING,
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SMALL_PADDING,
                    vertical: Constant.VERY_SMALL_PADDING),
                child: basicDetailItem(
                    Strings.payment_amount,
                    (item.amount != null && item.amount.toString().isNotEmpty)
                        ? "${item.amount.toString()}"
                        : "-",
                    Strings.adjusted_amount,
                    (item.adjustedAmount != null &&
                            item.adjustedAmount.toString().isNotEmpty)
                        ? "${item.adjustedAmount.toString()}"
                        : "-"),
              ),
              const SizedBox(
                height: Constant.VERY_SMALL_PADDING,
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SMALL_PADDING,
                    vertical: Constant.VERY_SMALL_PADDING),
                child: basicDetailItem(
                    Strings.remaining_amount,
                    controller!
                        .remaningAmountV(item.amount, item.adjustedAmount)
                        .toString(),
                    Strings.payment_mode,
                    (item.paymode != null && item.paymode!.isNotEmpty)
                        ? "${item.paymode}"
                        : "-"),
              ),
              const SizedBox(
                height: Constant.VERY_SMALL_PADDING,
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SMALL_PADDING,
                    vertical: Constant.VERY_SMALL_PADDING),
                child: basicDetailItem(
                    Strings.type,
                    (item.type != null && item.type!.isNotEmpty)
                        ? "${item.type}"
                        : "-",
                    "",
                    ""),
              ),
              const SizedBox(
                height: Constant.VERY_SMALL_PADDING,
              ),
            ]),
      ),
    );
  }

  remaningAmount() {
    double? remaningValue =
        (double.parse(item.amount) - double.parse(item.remainingAmount));
    return remaningValue.toString();
  }

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

  basicDetailItem(
      String title1, String? value1, String title2, String? value2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        Expanded(
          flex: 2,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title1),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(
                  value1,
                  item.isSelected == true ?
                       AppTheme.colorWhite
                      : AppTheme.title_dark),
            ],
          ),
        ),
        Expanded(
          flex: 1,
          child: Padding(
            padding: const EdgeInsets.only(right: Constant.SMALL_PADDING),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                titleWidget(title2),
                const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                valueWidget(value2, item.isSelected == true ?
                AppTheme.colorWhite
                    : AppTheme.title_dark),
              ],
            ),
          ),
        ),
      ],
    );
  }

  titleWidget(String title) {
    return CustomText(
      title: title,
      colors: item.isSelected == true ? AppTheme.colorLightGrey : AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.verySmall,
      fontWeight: FontWeight.w400,
      maxLines: 2,
    );
  }

  valueWidget(String? value, Color txtColors) {
    return CustomText(
      title: value!.isNotEmpty ? value : "",
      colors:  txtColors,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.normal,
      maxLines: 2,
    );
  }




}
