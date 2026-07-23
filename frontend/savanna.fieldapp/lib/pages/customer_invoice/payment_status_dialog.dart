import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class PaymentStatusDialog extends StatefulWidget {
  final String titleMsg;

  const PaymentStatusDialog(
      {Key? key, required this.titleMsg})
      : super(key: key);

  @override
  _PaymentStatusState createState() => _PaymentStatusState();
}

class _PaymentStatusState extends State<PaymentStatusDialog> {
  @override
  Widget build(BuildContext context) {
    return Dialog(
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(10.0),
      ),
      // backgroundColor: Colors.transparent,
      child: contentBox(context),
    );
  }

  contentBox(context) {
    return Container(
      // decoration: BoxDecoration(
      //   shape: BoxShape.rectangle,
      //   color: AppTheme.colorWhite,
      //   borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
      // ),
      width: MediaQuery.of(context).size.width * 2,
      decoration: BoxDecoration(
          color: AppTheme.colorWhite,
          borderRadius: BorderRadius.circular(Constant.SMALL_PADDING)),
      child: Column(
          mainAxisSize: MainAxisSize.min,
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Container(
              color: AppTheme.colorPrimary,
              padding: const EdgeInsets.symmetric(
                  vertical: Constant.MEDIUM_PADDING,
                  horizontal: Constant.SCREEN_PADDING),
              child: Align(
                alignment: Alignment.centerLeft,
                child: CustomText(
                  title: "Payment Confirmation",
                  colors: AppTheme.colorWhite,
                  fontSize: AppTheme.large,
                  fontWeight: FontWeight.w500,
                ),
              ),
            ),
            const SizedBox(height: Constant.SMALL_PADDING),
            CustomText(
              title: widget.titleMsg,
              colors: AppTheme.colorBlack,
              fontSize: AppTheme.medium,
              fontWeight: FontWeight.w300,
              height: 1.35,
            ),
            const SizedBox(height: Constant.VERY_EXTRA_LARGE_PADDING),

            const SizedBox(height: Constant.LARGE_PADDING),
            InkWell(
              onTap: () {
                Get.back();
              },
              child: Container(
                padding: const EdgeInsets.symmetric(
                    vertical: Constant.SMALL_PADDING,
                    horizontal: Constant.SCREEN_PADDING),
                decoration: BoxDecoration(
                  color: AppTheme.colorGreen,
                  borderRadius: BorderRadius.circular(Constant.SMALL_PADDING)),
                child: CustomText(
                  title: Strings.cancel,
                  colors: AppTheme.colorWhite,
                  fontSize: AppTheme.medium,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
            const SizedBox(height: Constant.SMALL_PADDING),
          ]),
    );
  }
}
