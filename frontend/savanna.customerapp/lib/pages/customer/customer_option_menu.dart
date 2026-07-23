import 'dart:developer';

import 'package:savbill/pages/customer/model/customer_detail_option.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';

class CustomerOptionItemView extends StatelessWidget {
  CustomerDetailOption detail;
  int index;


  List textColor = [
    AppTheme.colorGreenRView,
    AppTheme.custNearLocationDark,
    AppTheme.custPaymentLinkDark,
    AppTheme.custAssignInventoryDark,
    AppTheme.custChangeStatusDark,
    AppTheme.colorRedRView,
    AppTheme.custUploadFileDark,
  ];

  CustomerOptionItemView({Key? key, required this.detail, required this.index})
      : super(key: key);

  @override
  Widget build(BuildContext context) {

    Color textColor = this.textColor[index % this.textColor.length];

    return Stack(alignment: Alignment.topCenter, children: <Widget>[
      Container(
        margin: const EdgeInsets.only(top: Constant.LARGE_PADDING, bottom: 0),
        constraints: const BoxConstraints(
          minWidth:
              Constant.TOP_MENU_OPTION_W + Constant.VERY_EXTRA_LARGE_PADDING,
        ),
        child: Material(
          color: AppTheme.colorWhite,
          elevation: 1.0,
          shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
          child: Padding(
            padding: const EdgeInsets.all(0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                FittedBox(
                  child: CustomText(
                    title: detail.title!,
                    colors: textColor,
                    textAlign: TextAlign.center,
                    fontSize: AppTheme.small,
                    fontWeight: FontWeight.w700,
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
      Positioned(
        bottom: 0,
        right: 0,
        child: Container(
          decoration: BoxDecoration(
            color: textColor,
            borderRadius: const BorderRadius.only(
                bottomRight: Radius.circular(Constant.BTN_ROUNDED_CORNER),
                topLeft: Radius.circular(Constant.BTN_ROUNDED_CORNER)),
          ),
          padding: const EdgeInsets.symmetric(
            vertical: Constant.VERY_SMALL_PADDING - 2,
            horizontal: Constant.SMALL_PADDING,
          ),
          child: Icon(
            Icons.arrow_forward_rounded,
            size: 10,
            color: AppTheme.colorWhite,
          ),
        ),
      ),
      Positioned(
        top: 0,
        child: Material(
          elevation: 2.0,
          shape: RoundedRectangleBorder(
              borderRadius:
                  BorderRadius.circular(Constant.RECENT_VIEW_SIZE / 2)),
          child: Container(
            padding: const EdgeInsets.all(Constant.VERY_SMALL_PADDING + 4),
            decoration: BoxDecoration(
              color:  AppTheme.colorWhite,
              borderRadius:
                  BorderRadius.circular(Constant.RECENT_VIEW_SIZE / 2),
            ),
            alignment: Alignment.center,
            width: Constant.RECENT_VIEW_SIZE,
            height: Constant.RECENT_VIEW_SIZE,
            child: Image.asset(
              detail.icon!,
              fit: BoxFit.contain,
              color: textColor,
            ),
          ),
        ),
      ),
    ]);
  }
}
