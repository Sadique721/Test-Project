import 'package:savbill/pages/credit_note/response/customer_credit_res.dart';
import 'package:flutter/material.dart';

import '../../theme/app_theme.dart';
import '../../util/constant.dart';
import '../../widgets/coustom_text.dart';

class CustomerViewItem extends StatelessWidget {
  CustomerCreditList item;
  int index;

  List colorArr = [
    AppTheme.colorGreenRView,
    AppTheme.colorRedRView,
    AppTheme.colorBlueRView,
    AppTheme.colorYellowRView
  ];

  CustomerViewItem({
    Key? key,
    required this.index,
    required this.item,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: EdgeInsets.only(
        top: index == 0 ? 0 : Constant.MEDIUM_PADDING,
      ),
      elevation: 2,
      color: AppTheme.colorWhite,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(
            width: double.infinity,
            height: 2,
            decoration: BoxDecoration(
              color: colorArr[index % colorArr.length],
              borderRadius: const BorderRadius.only(
                topLeft: Radius.circular(Constant.BTN_ROUNDED_CORNER),
                topRight: Radius.circular(Constant.BTN_ROUNDED_CORNER),
              ),
            ),
          ),
          const SizedBox(height: Constant.MEDIUM_PADDING),
          Padding(
              padding: const EdgeInsets.only(left: Constant.MEDIUM_PADDING),
              child: CustomText(
                  title: "${item.name}",
                  colors: AppTheme.title_dark,
                  textAlign: TextAlign.start,
                  fontSize: AppTheme.small,
                  height: 1,
                  fontWeight: FontWeight.w500)),
          const SizedBox(height: Constant.MEDIUM_PADDING),
          Padding(
              padding: const EdgeInsets.only(left: Constant.MEDIUM_PADDING),
              child: CustomText(
                  title: "${item.username}",
                  colors: AppTheme.lable_noramal,
                  textAlign: TextAlign.start,
                  fontSize: AppTheme.small,
                  height: 1,
                  fontWeight: FontWeight.w500)),
          const SizedBox(height: Constant.MEDIUM_PADDING),
        ],
      ),
    );
  }
}